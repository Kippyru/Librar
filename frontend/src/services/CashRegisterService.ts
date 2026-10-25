import { axiosClient } from './axiosClient';

export type CashRegisterStatus = 'OPEN' | 'CLOSED';

export interface CashRegisterResponse {
    id: number;
    sucursalId: number;
    userId: number;
    openingAmount: number;
    closingAmount: number | null;
    expectedAmount: number | null;
    difference: number | null;
    openedAt: string;
    closedAt: string | null;
    status: CashRegisterStatus;
    notes: string | null;
}

export const cashRegisterService = {
    // Get the open register of a sucursal
    getCurrent: async (
        sucursalId: number
    ): Promise<CashRegisterResponse> => {
        const response = await axiosClient.get<CashRegisterResponse>(
            '/api/cashregister/current',
            { params: { sucursalId } }
        );

        return response.data;
    },

    // Open a register
    open: async (sucursalId: number, requestData: unknown) => {
        const response = await axiosClient.post(
            '/api/cashregister/open',
            requestData,
            { params: { sucursalId } }
        );
        return response.data;
    },

    // Get the current count
    count: async (cashRegisterId: number) => {
        const response = await axiosClient.get(
            `/api/cashregister/${cashRegisterId}/count`
        );
        return response.data;
    },

    // Close the register
    close: async (cashRegisterId: number, requestData: unknown) => {
        const response = await axiosClient.post(
            `/api/cashregister/${cashRegisterId}/close`,
            requestData
        );
        return response.data;
    },

    // Register a movement
    registerMovement: async (
        cashRegisterId: number,
        requestData: unknown
    ) => {
        const response = await axiosClient.post(
            `/api/cash-registers/${cashRegisterId}/movements`,
            requestData
        );
        return response.data;
    },

    // List register movements
    getMovements: async (cashRegisterId: number) => {
        const response = await axiosClient.get(
            `/api/cash-registers/${cashRegisterId}/movements`
        );
        return response.data;
    },
};