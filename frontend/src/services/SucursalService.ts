import { axiosClient } from './axiosClient';

export interface SucursalResponse {
    id: number;
    name: string;
    address: string;
    phone: string;
    active: boolean;
}

export const sucursalService = {
    list: async (): Promise<SucursalResponse[]> => {
        const response = await axiosClient.get<SucursalResponse[]>(
            '/api/sucursal/list'
        );

        return response.data;
    },
};