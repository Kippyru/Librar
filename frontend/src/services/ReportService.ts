import { axiosClient } from './axiosClient';
import type { ReportFilters, ReportResponse } from '../types/report';

export const reportService = {
    getReport: async (
        filters: ReportFilters
    ): Promise<ReportResponse> => {
        const response = await axiosClient.get<ReportResponse>(
            '/api/reports',
            {
                params: filters,
            }
        );

        return response.data;
    },
};