import { axiosClient } from './axiosClient';

export type PaymentMethod =
    | 'EFECTIVO'
    | 'TRANSFERENCIA'
    | 'TARJETA'
    | 'MIXTO';

export type IndividualPaymentMethod = Exclude<
    PaymentMethod,
    'MIXTO'
>;

export interface SalePaymentRequest {
    paymentMethod: IndividualPaymentMethod;
    amount: number;
}


export interface SaleDetailRequest {
    productId: number;
    quantity: number;
}

export interface SaleRequest {
    sucursalId: number;
    cashRegisterId: number;
    paymentMethod: PaymentMethod;
    details: {
        productId: number;
        quantity: number;
    }[];
    payments: SalePaymentRequest[];
}

export interface SaleDetailResponse {
    id: number;
    productId: number;
    quantity: number;
    unitPrice: number;
    subtotal: number;
}

export interface SaleResponse {
    id: number;
    userId: number;
    sucursalId: number;
    cashRegisterId: number;
    totalAmount: number;
    paymentMethod: SaleRequest['paymentMethod'];
    status: string;
    createdAt: string;
    details: SaleDetailResponse[];
}
export interface Product {
    id: number;
    barcode: string;
    name: string;
    description?: string;
    purchasePrice: number;
    sellingPrice: number;
    minimumStock: number;
    category?: number;
    supplier?: number;
    imagenUrl?: string;
    active?: boolean;
}

export const posService = {
    searchProducts: async (query: string): Promise<Product[]> => {
        const normalizedQuery = query.trim();

        const response = normalizedQuery
            ? await axiosClient.get<Product[]>('/api/product/search', {
                params: { q: normalizedQuery },
            })
            : await axiosClient.get<Product[]>('/api/product/list');

        return response.data;
    },

    registerSale: async (
        userId: number,
        request: SaleRequest
    ): Promise<SaleResponse> => {
        const response = await axiosClient.post<SaleResponse>(
            `/api/sales/user/${userId}`,
            request
        );

        return response.data;
    },
};
