package com.uit.nhom7.KiemThuPhanMem.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.uit.nhom7.KiemThuPhanMem.util.error.StorageException;

@Service
public class DatabaseBackupService {
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            .withZone(ZoneId.systemDefault());

    private final DataSource dataSource;
    private final Path backupDirectory;

    public DatabaseBackupService(
            DataSource dataSource,
            @Value("${techsales.backup.directory:storage/backups}") String backupDirectory) {
        this.dataSource = dataSource;
        this.backupDirectory = Path.of(backupDirectory);
    }

    public BackupResult backupToLocalStorage() {
        try {
            Files.createDirectories(backupDirectory);
            String fileName = "techsales_%s.sql.gz".formatted(BACKUP_TIMESTAMP_FORMATTER.format(Instant.now()));
            Path backupFile = backupDirectory.resolve(fileName).normalize();
            if (!backupFile.startsWith(backupDirectory.toAbsolutePath().normalize())
                    && backupDirectory.isAbsolute()) {
                throw new StorageException("Invalid backup path");
            }

            try (Connection connection = dataSource.getConnection();
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(Files.newOutputStream(backupFile))) {
                StringBuilder header = new StringBuilder();
                header.append("-- TechSales database backup").append(System.lineSeparator());
                header.append("-- Created at: ").append(Instant.now()).append(System.lineSeparator());
                header.append("SET FOREIGN_KEY_CHECKS=0;").append(System.lineSeparator()).append(System.lineSeparator());
                gzipOutputStream.write(header.toString().getBytes(StandardCharsets.UTF_8));
                for (String tableName : getTableNames(connection)) {
                    dumpTable(connection, tableName, gzipOutputStream);
                }
                gzipOutputStream.write("SET FOREIGN_KEY_CHECKS=1;".getBytes(StandardCharsets.UTF_8));
            }

            long size = Files.size(backupFile);
            String checksum = calculateSha256(backupFile);
            if (!verifyBackup(backupFile, size, checksum)) {
                throw new StorageException("Backup integrity check failed");
            }
            return new BackupResult(fileName, backupFile.toAbsolutePath().toString(), size, checksum);
        } catch (IOException | SQLException ex) {
            throw new StorageException("Cannot create database backup: " + ex.getMessage(), ex);
        }
    }

    private List<String> getTableNames(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        List<String> tableNames = new ArrayList<>();
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), null, "%", new String[] { "TABLE" })) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if (tableName != null && !tableName.startsWith("flyway_")) {
                    tableNames.add(tableName);
                }
            }
        }
        tableNames.sort(String::compareToIgnoreCase);
        return tableNames;
    }

    private void dumpTable(Connection connection, String tableName, GZIPOutputStream outputStream) throws SQLException, IOException {
        String safeTableName = quoteIdentifier(tableName);
        outputStream.write(("-- Table: " + tableName + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + safeTableName)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                StringBuilder insert = new StringBuilder("INSERT INTO ");
                insert.append(safeTableName).append(" (");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        insert.append(", ");
                    }
                    insert.append(quoteIdentifier(metaData.getColumnName(i)));
                }
                insert.append(") VALUES (");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        insert.append(", ");
                    }
                    insert.append(toSqlLiteral(resultSet.getObject(i)));
                }
                insert.append(");").append(System.lineSeparator());
                outputStream.write(insert.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
    }

    private String quoteIdentifier(String identifier) {
        return "`" + identifier.replace("`", "``") + "`";
    }

    private String toSqlLiteral(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Number || value instanceof Boolean || value instanceof BigDecimal) {
            return value.toString();
        }
        if (value instanceof Timestamp timestamp) {
            return "'" + timestamp.toInstant().toString().replace("'", "''") + "'";
        }
        if (value instanceof byte[] bytes) {
            return "X'" + HexFormat.of().formatHex(bytes).toUpperCase(Locale.ROOT) + "'";
        }
        return "'" + value.toString().replace("'", "''") + "'";
    }

    private String calculateSha256(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream inputStream = new DigestInputStream(Files.newInputStream(file), digest)) {
                inputStream.transferTo(java.io.OutputStream.nullOutputStream());
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new StorageException("SHA-256 is not supported", ex);
        }
    }

    private boolean verifyBackup(Path backupFile, long size, String checksum) {
        if (size <= 0 || checksum == null || checksum.isBlank()) {
            return false;
        }
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(backupFile));
                BufferedReader reader = new BufferedReader(
                        new java.io.InputStreamReader(gzipInputStream, StandardCharsets.UTF_8))) {
            return reader.lines().findFirst()
                    .map(line -> line.contains("TechSales database backup"))
                    .orElse(false);
        } catch (IOException ex) {
            return false;
        }
    }

    public record BackupResult(
            String fileName,
            String absolutePath,
            long sizeBytes,
            String checksumSha256) {
    }
}
