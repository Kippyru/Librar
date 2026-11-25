export type PaymentMethod =
    | 'EFECTIVO'
    | 'TRANSFERENCIA'
    | 'TARJETA'
    | 'MIXTO';

export type SaleStatus = 'COMPLETED' | 'CANCELED';

export type CashMovementType = 'CASH_IN' | 'CASH_OUT';

export type CashMovementReason =
    | 'SALE'
    | 'REFUND'
    | 'DEPOSIT'
    | 'WITHDRAWAL'
    | 'ADJUSTMENT';

export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface SalesReportItem {
    saleId: number;
    date: string;
    username: string;
    paymentMethod: PaymentMethod;
    totalAmount: number;
    status: SaleStatus;
}

export interface PaymentSummary {
    paymentMethod: PaymentMethod;
    totalAmount: number;
    transactions: number;
}

export interface CashReportSummary {
    totalIncome: number;
    totalExpenses: number;
    netMovement: number;
    totalMovements: number;
}

export interface CashMovementReportItem {
    id: number;
    occurredAt: string;
    cashRegisterId: number;
    sucursal: string;
    username: string;
    type: CashMovementType;
    reason: CashMovementReason;
    description: string | null;
    amount: number;
}

export interface ReportResponse {
    totalSales: number;
    totalSalesAmount: number;
    averageSale: number;
    sales: PageResponse<SalesReportItem>;
    payments: PaymentSummary[];
    cashSummary: CashReportSummary;
    cashMovements: PageResponse<CashMovementReportItem>;
}

export interface ReportFilters {
    startDate: string;
    endDate: string;
    sucursalId?: number;
    salesPage: number;
    salesSize: number;
    movementsPage: number;
    movementsSize: number;
}