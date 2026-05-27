import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';

export const useGetSalesStats = (startDate?: string, endDate?: string) => {
  return useQuery({
    queryKey: ['salesStats', startDate, endDate],
    queryFn: () => dashboardService.getSalesStats(startDate, endDate),
  });
};

export const useGetReportSummary = (startDate?: string, endDate?: string) => {
  return useQuery({
    queryKey: ['reportSummary', startDate, endDate],
    queryFn: () => dashboardService.getReportSummary(startDate, endDate),
  });
};
