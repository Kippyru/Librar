import { axiosClient } from './axiosClient';

export const cashRegisterService = {

    // get the open register of a sucursal
    getCurrent: async (sucursalId) => {
        const response = await axiosClient.get(`/api/cashregister/current`, {
            params: { sucursalId: sucursalId } // Axios lo convierte automáticamente a ?sucursalId=X
        });
        return response.data;
    },

    // open a Register
    open: async (sucursalId, requestData) => {
        const response = await axiosClient.post(`/api/cashregister/open`, requestData, {
            params: { sucursalId: sucursalId }
        });
        return response.data;
    },

    // get the current arqueo/conteo of the running Register
    count: async (cashRegisterId) => {
        const response = await axiosClient.get(`/api/cashregister/${cashRegisterId}/count`);
        return response.data;
    },

    // close the Register
    close: async (cashRegisterId, requestData) => {
        const response = await axiosClient.post(`/api/cashregister/${cashRegisterId}/close`, requestData);
        return response.data;
    },

    // manually register a new enter/exit movement
    registerMovement: async (cashRegisterId, requestData) => {
        const response = await axiosClient.post(`/api/cash-registers/${cashRegisterId}/movements`, requestData);
        return response.data;
    },

    // list the whole history of movements of the Register
    getMovements: async (cashRegisterId) => {
        const response = await axiosClient.get(`/api/cash-registers/${cashRegisterId}/movements`);
        return response.data;
    }
};