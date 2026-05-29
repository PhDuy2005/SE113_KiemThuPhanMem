import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

function walk(dir, callback) {
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? walk(dirPath, callback) : callback(path.join(dir, f));
    });
}

const targetDir = path.join(__dirname, 'src');

walk(targetDir, function(filePath) {
    if (filePath.endsWith('.tsx') || filePath.endsWith('.ts')) {
        let content = fs.readFileSync(filePath, 'utf8');
        let original = content;

        // Case 1: onError: () => toast.error('message')
        content = content.replace(/onError:\s*\(\)\s*=>\s*toast\.error\((['"`][^'"`]+['"`])\)/g, "onError: (err: any) => toast.error(err?.message || $1)");
        
        // Case 2: onError: (err) => toast.error('message') or (error: any)
        content = content.replace(/onError:\s*\(\s*([a-zA-Z0-9_]+)(?:\s*:\s*any)?\s*\)\s*=>\s*toast\.error\((['"`][^'"`]+['"`])\)/g, "onError: ($1: any) => toast.error($1?.message || $2)");

        // Case 3: toast.error('message') inside a catch block
        content = content.replace(/catch\s*\(\s*([a-zA-Z0-9_]+)\s*\)\s*\{\s*[\s\S]*?toast\.error\((['"`][^'"`]+['"`])\)/g, (match, errName, msg) => {
            return match.replace(msg, `${errName}?.message || ${msg}`);
        });

        // Some specific places where it's on a new line:
        // onError: () => { toast.error('msg') }
        content = content.replace(/onError:\s*\(\)\s*=>\s*\{\s*toast\.error\((['"`][^'"`]+['"`])\)/g, "onError: (err: any) => {\n        toast.error(err?.message || $1)");

        if (content !== original) {
            fs.writeFileSync(filePath, content, 'utf8');
            console.log('Fixed: ' + filePath);
        }
    }
});
