import { axiosClient } from "./axiosClient";

export interface UserResponse {
    id: number;
    username: string;
    completeName: string;
    role: number;
    sucursal: number;
    active: boolean;
}

export const userService = {
    getMe: async (): Promise<UserResponse> => {
        const response = await axiosClient.get<UserResponse>(
            "/api/users/me"
        );

        return response.data;
    },
};
