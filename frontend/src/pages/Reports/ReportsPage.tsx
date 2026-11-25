import { useCallback, useEffect, useState } from 'react';
import {
    Alert,
    Button,
    Card,
    Col,
    DatePicker,
    Empty,
    Row,
    Select,
    Space,
    Statistic,
    Table,
    Tag,
    Typography,
    message,
} from 'antd';

import type { ColumnsType } from 'antd/es/table';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';

import {
    DownloadOutlined,
    SearchOutlined,
    ReloadOutlined,
} from '@ant-design/icons';

import './ReportsPage.css';

import { reportService } from '../../services/ReportService';
import { sucursalService } from '../../services/SucursalService';

import type { SucursalResponse } from '../../services/SucursalService';

import type {
    CashMovementReportItem,
    CashMovementReason,
    CashMovementType,
    PaymentMethod,
    PaymentSummary,
    ReportFilters,
    ReportResponse,
    SaleStatus,
    SalesReportItem,
} from '../../types/report';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

const PAGE_SIZE = 10;
const EXPORT_PAGE_SIZE = 100;

const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS',
    }).format(value ?? 0);
};

const formatDateTime = (value: string) => {
    return dayjs(value).format('DD/MM/YYYY HH:mm');
};

const paymentMethodLabels: Record<PaymentMethod, string> = {
    EFECTIVO: 'Efectivo',
    TRANSFERENCIA: 'Transferencia',
    TARJETA: 'Tarjeta',
    MIXTO: 'Mixto',
};

const saleStatusLabels: Record<SaleStatus, string> = {
    COMPLETED: 'Completada',
    CANCELED: 'Cancelada',
};

const cashMovementTypeLabels: Record<CashMovementType, string> = {
    CASH_IN: 'Ingreso',
    CASH_OUT: 'Egreso',
};

const cashMovementReasonLabels: Record<CashMovementReason, string> = {
    SALE: 'Venta',
    REFUND: 'Devolución',
    DEPOSIT: 'Depósito',
    WITHDRAWAL: 'Retiro',
    ADJUSTMENT: 'Ajuste',
};

const escapeCsvValue = (value: unknown): string => {
    const text = String(value ?? '');
    return `"${text.replace(/"/g, '""')}"`;
};

const downloadCsv = (
    filename: string,
    headers: string[],
    rows: unknown[][]
) => {
    const content = [
        headers.map(escapeCsvValue).join(';'),
        ...rows.map(row => row.map(escapeCsvValue).join(';')),
    ].join('\r\n');

    // BOM para que Excel reconozca correctamente UTF-8.
    const blob = new Blob(['\uFEFF' + content], {
        type: 'text/csv;charset=utf-8;',
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');

    link.href = url;
    link.download = filename;
    link.click();

    URL.revokeObjectURL(url);
};

const ReportsPage = () => {
    const [loading, setLoading] = useState(false);
    const [exportingSales, setExportingSales] = useState(false);
    const [exportingMovements, setExportingMovements] = useState(false);

    const [report, setReport] = useState<ReportResponse | null>(null);
    const [sucursales, setSucursales] = useState<SucursalResponse[]>([]);

    const [dateRange, setDateRange] = useState<[Dayjs, Dayjs]>([
        dayjs().startOf('month'),
        dayjs(),
    ]);

    const [selectedSucursal, setSelectedSucursal] = useState<
        number | undefined
    >();

    const [appliedFilters, setAppliedFilters] = useState<{
        startDate: string;
        endDate: string;
        sucursalId?: number;
    }>({
        startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
        endDate: dayjs().format('YYYY-MM-DD'),
    });

    const [salesPage, setSalesPage] = useState(0);
    const [salesSize, setSalesSize] = useState(PAGE_SIZE);

    const [movementsPage, setMovementsPage] = useState(0);
    const [movementsSize, setMovementsSize] = useState(PAGE_SIZE);

    // Cargar sucursales.
    useEffect(() => {
        const loadSucursales = async () => {
            try {
                const data = await sucursalService.list();

                setSucursales(
                    data.filter(sucursal => sucursal.active)
                );
            } catch (error) {
                console.error('Error loading sucursales:', error);
                message.error('No se pudieron cargar las sucursales.');
            }
        };

        loadSucursales();
    }, []);

    // Obtener el reporte.
    const loadReport = useCallback(async () => {
        setLoading(true);

        try {
            const filters: ReportFilters = {
                ...appliedFilters,
                salesPage,
                salesSize,
                movementsPage,
                movementsSize,
            };

            const data = await reportService.getReport(filters);
            setReport(data);
        } catch (error) {
            console.error('Error loading report:', error);
            message.error('No se pudo cargar el reporte.');
        } finally {
            setLoading(false);
        }
    }, [
        appliedFilters,
        salesPage,
        salesSize,
        movementsPage,
        movementsSize,
    ]);

    useEffect(() => {
        loadReport();
    }, [loadReport]);

    // Aplicar filtros.
    const handleApplyFilters = () => {
        if (!dateRange || !dateRange[0] || !dateRange[1]) {
            message.warning('Seleccioná un rango de fechas.');
            return;
        }

        if (dateRange[0].isAfter(dateRange[1], 'day')) {
            message.warning('La fecha inicial no puede ser posterior a la final.');
            return;
        }

        setSalesPage(0);
        setMovementsPage(0);

        setAppliedFilters({
            startDate: dateRange[0].format('YYYY-MM-DD'),
            endDate: dateRange[1].format('YYYY-MM-DD'),
            sucursalId: selectedSucursal,
        });
    };

    // Consultar todas las páginas para exportar, respetando el límite
    // de 100 registros por página del backend.
    const getAllReportRows = async () => {
        const firstPage = await reportService.getReport({
            ...appliedFilters,
            salesPage: 0,
            salesSize: EXPORT_PAGE_SIZE,
            movementsPage: 0,
            movementsSize: EXPORT_PAGE_SIZE,
        });

        const sales: SalesReportItem[] = [...firstPage.sales.content];
        const movements: CashMovementReportItem[] = [
            ...firstPage.cashMovements.content,
        ];

        const maxPages = Math.max(
            firstPage.sales.totalPages,
            firstPage.cashMovements.totalPages
        );

        for (let page = 1; page < maxPages; page++) {
            const response = await reportService.getReport({
                ...appliedFilters,
                salesPage: page,
                salesSize: EXPORT_PAGE_SIZE,
                movementsPage: page,
                movementsSize: EXPORT_PAGE_SIZE,
            });

            if (page < firstPage.sales.totalPages) {
                sales.push(...response.sales.content);
            }

            if (page < firstPage.cashMovements.totalPages) {
                movements.push(...response.cashMovements.content);
            }
        }

        return { sales, movements };
    };

    // Exportar todas las ventas.
    const handleExportSales = async () => {
        setExportingSales(true);

        try {
            const { sales } = await getAllReportRows();

            if (sales.length === 0) {
                message.warning('No hay ventas para exportar.');
                return;
            }

            downloadCsv(
                'reporte-ventas.csv',
                [
                    'ID Venta',
                    'Fecha',
                    'Usuario',
                    'Método de pago',
                    'Importe',
                    'Estado',
                ],
                sales.map(sale => [
                    sale.saleId,
                    formatDateTime(sale.date),
                    sale.username,
                    paymentMethodLabels[sale.paymentMethod],
                    sale.totalAmount,
                    saleStatusLabels[sale.status],
                ])
            );

            message.success('Reporte de ventas exportado.');
        } catch (error) {
            console.error('Error exporting sales:', error);
            message.error('No se pudo exportar el reporte de ventas.');
        } finally {
            setExportingSales(false);
        }
    };

    // Exportar todos los movimientos de caja.
    const handleExportMovements = async () => {
        setExportingMovements(true);

        try {
            const { movements } = await getAllReportRows();

            if (movements.length === 0) {
                message.warning('No hay movimientos para exportar.');
                return;
            }

            downloadCsv(
                'reporte-movimientos-caja.csv',
                [
                    'ID',
                    'Fecha',
                    'Caja',
                    'Sucursal',
                    'Usuario',
                    'Tipo',
                    'Motivo',
                    'Descripción',
                    'Importe',
                ],
                movements.map(movement => [
                    movement.id,
                    formatDateTime(movement.occurredAt),
                    movement.cashRegisterId,
                    movement.sucursal,
                    movement.username,
                    cashMovementTypeLabels[movement.type],
                    cashMovementReasonLabels[movement.reason],
                    movement.description,
                    movement.amount,
                ])
            );

            message.success('Reporte de movimientos exportado.');
        } catch (error) {
            console.error('Error exporting movements:', error);
            message.error('No se pudo exportar el reporte de movimientos.');
        } finally {
            setExportingMovements(false);
        }
    };

    // Columnas de ventas.
    const salesColumns: ColumnsType<SalesReportItem> = [
        {
            title: 'Venta',
            dataIndex: 'saleId',
            key: 'saleId',
            width: 90,
        },
        {
            title: 'Fecha',
            dataIndex: 'date',
            key: 'date',
            render: (value: string) => formatDateTime(value),
            width: 160,
        },
        {
            title: 'Usuario',
            dataIndex: 'username',
            key: 'username',
        },
        {
            title: 'Método de pago',
            dataIndex: 'paymentMethod',
            key: 'paymentMethod',
            render: (value: PaymentMethod) => paymentMethodLabels[value] ?? value,
        },
        {
            title: 'Importe',
            dataIndex: 'totalAmount',
            key: 'totalAmount',
            align: 'right',
            render: (value: number) => formatCurrency(value),
        },
        {
            title: 'Estado',
            dataIndex: 'status',
            key: 'status',
            render: (value: SaleStatus) => (
                <Tag color={value === 'COMPLETED' ? 'success' : 'error'}>
                    {saleStatusLabels[value] ?? value}
                </Tag>
            ),
        },
    ];

    // Columnas de movimientos de caja.
    const movementColumns: ColumnsType<CashMovementReportItem> = [
        {
            title: 'Fecha',
            dataIndex: 'occurredAt',
            key: 'occurredAt',
            render: (value: string) => formatDateTime(value),
            width: 160,
        },
        {
            title: 'Caja',
            dataIndex: 'cashRegisterId',
            key: 'cashRegisterId',
        },
        {
            title: 'Sucursal',
            dataIndex: 'sucursal',
            key: 'sucursal',
        },
        {
            title: 'Usuario',
            dataIndex: 'username',
            key: 'username',
        },
        {
            title: 'Tipo',
            dataIndex: 'type',
            key: 'type',
            render: (value: CashMovementType) => (
                <Tag color={value === 'CASH_IN' ? 'success' : 'error'}>
                    {cashMovementTypeLabels[value] ?? value}
                </Tag>
            ),
        },
        {
            title: 'Motivo',
            dataIndex: 'reason',
            key: 'reason',
            render: (value: CashMovementReason) =>
                cashMovementReasonLabels[value] ?? value,
        },
        {
            title: 'Descripción',
            dataIndex: 'description',
            key: 'description',
            render: (value: string | null) => value || '-',
        },
        {
            title: 'Importe',
            dataIndex: 'amount',
            key: 'amount',
            align: 'right',
            render: (value: number) => formatCurrency(value),
        },
    ];

    const paymentSummary: PaymentSummary[] = report?.payments ?? [];

    return (
        <div className="reports-page">
            <div className="reports-header">
                <div>
                    <Title level={2}>Reportes</Title>
                    <Text type="secondary">
                        Resumen de ventas y movimientos de caja
                    </Text>
                </div>

                <Button
                    icon={<ReloadOutlined />}
                    onClick={loadReport}
                    loading={loading}
                >
                    Actualizar
                </Button>
            </div>

            {/* Filtros */}
            <Card className="reports-filters">
                <Row gutter={[16, 16]} align="bottom">
                    <Col xs={24} md={10} lg={8}>
                        <Text strong>Período</Text>

                        <RangePicker
                            value={dateRange}
                            onChange={(dates) => {
                                if (dates?.[0] && dates?.[1]) {
                                    setDateRange([dates[0], dates[1]]);
                                }
                            }}
                            format="DD/MM/YYYY"
                            style={{ width: '100%', marginTop: 8 }}
                            allowClear={false}
                        />
                    </Col>

                    <Col xs={24} md={8} lg={8}>
                        <Text strong>Sucursal</Text>

                        <Select
                            allowClear
                            showSearch
                            optionFilterProp="label"
                            placeholder="Todas las sucursales"
                            value={selectedSucursal}
                            onChange={setSelectedSucursal}
                            options={sucursales.map(sucursal => ({
                                label: sucursal.name,
                                value: sucursal.id,
                            }))}
                            style={{ width: '100%', marginTop: 8 }}
                        />
                    </Col>

                    <Col xs={24} md={6} lg={8}>
                        <Button
                            type="primary"
                            icon={<SearchOutlined />}
                            onClick={handleApplyFilters}
                            loading={loading}
                            block
                        >
                            Aplicar filtros
                        </Button>
                    </Col>
                </Row>
            </Card>

            {report && (
                <>
                    {/* Indicadores de ventas */}
                    <Title level={4}>Resumen de ventas</Title>

                    <Row gutter={[16, 16]}>
                        <Col xs={24} sm={12} lg={8}>
                            <Card>
                                <Statistic
                                    title="Ventas completadas"
                                    value={report.totalSales}
                                />
                            </Card>
                        </Col>

                        <Col xs={24} sm={12} lg={8}>
                            <Card>
                                <Statistic
                                    title="Facturación"
                                    value={report.totalSalesAmount}
                                    formatter={(value) =>
                                        formatCurrency(Number(value))
                                    }
                                />
                            </Card>
                        </Col>

                        <Col xs={24} sm={12} lg={8}>
                            <Card>
                                <Statistic
                                    title="Promedio por venta"
                                    value={report.averageSale}
                                    formatter={(value) =>
                                        formatCurrency(Number(value))
                                    }
                                />
                            </Card>
                        </Col>
                    </Row>

                    {/* Resumen de pagos */}
                    <Card
                        title="Resumen por método de pago"
                        className="reports-section"
                    >
                        {paymentSummary.length === 0 ? (
                            <Empty description="No hay pagos registrados en este período." />
                        ) : (
                            <Row gutter={[16, 16]}>
                                {paymentSummary.map(payment => (
                                    <Col
                                        xs={24}
                                        sm={12}
                                        lg={6}
                                        key={payment.paymentMethod}
                                    >
                                        <Card size="small">
                                            <Statistic
                                                title={
                                                    paymentMethodLabels[
                                                    payment.paymentMethod
                                                    ] ?? payment.paymentMethod
                                                }
                                                value={payment.totalAmount}
                                                formatter={(value) =>
                                                    formatCurrency(Number(value))
                                                }
                                            />

                                            <Text type="secondary">
                                                {payment.transactions}{' '}
                                                {payment.transactions === 1
                                                    ? 'transacción'
                                                    : 'transacciones'}
                                            </Text>
                                        </Card>
                                    </Col>
                                ))}
                            </Row>
                        )}
                    </Card>

                    {/* Resumen de caja */}
                    <Title level={4}>Resumen de movimientos de caja</Title>

                    <Row gutter={[16, 16]}>
                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title="Total de ingresos"
                                    value={report.cashSummary.totalIncome}
                                    formatter={(value) =>
                                        formatCurrency(Number(value))
                                    }
                                    valueStyle={{ color: '#389e0d' }}
                                />
                            </Card>
                        </Col>

                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title="Total de egresos"
                                    value={report.cashSummary.totalExpenses}
                                    formatter={(value) =>
                                        formatCurrency(Number(value))
                                    }
                                    valueStyle={{ color: '#cf1322' }}
                                />
                            </Card>
                        </Col>

                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title="Movimiento neto"
                                    value={report.cashSummary.netMovement}
                                    formatter={(value) =>
                                        formatCurrency(Number(value))
                                    }
                                />
                            </Card>
                        </Col>

                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title="Cantidad de movimientos"
                                    value={report.cashSummary.totalMovements}
                                />
                            </Card>
                        </Col>
                    </Row>

                    {/* Tabla de ventas */}
                    <Card
                        className="reports-section"
                        title="Detalle de ventas"
                        extra={
                            <Button
                                icon={<DownloadOutlined />}
                                onClick={handleExportSales}
                                loading={exportingSales}
                            >
                                Exportar CSV
                            </Button>
                        }
                    >
                        <Table<SalesReportItem>
                            rowKey="saleId"
                            columns={salesColumns}
                            dataSource={report.sales.content}
                            loading={loading}
                            scroll={{ x: 800 }}
                            locale={{
                                emptyText: 'No hay ventas para este período.',
                            }}
                            pagination={{
                                current: salesPage + 1,
                                pageSize: salesSize,
                                total: report.sales.totalElements,
                                showSizeChanger: true,
                                pageSizeOptions: [10, 20, 50, 100],
                                showTotal: total => `${total} ventas`,
                                onChange: (page, pageSize) => {
                                    setSalesPage(page - 1);

                                    if (pageSize !== salesSize) {
                                        setSalesSize(pageSize);
                                        setSalesPage(0);
                                    }
                                },
                            }}
                        />
                    </Card>

                    {/* Tabla de movimientos */}
                    <Card
                        className="reports-section"
                        title="Detalle de movimientos de caja"
                        extra={
                            <Button
                                icon={<DownloadOutlined />}
                                onClick={handleExportMovements}
                                loading={exportingMovements}
                            >
                                Exportar CSV
                            </Button>
                        }
                    >
                        <Table<CashMovementReportItem>
                            rowKey="id"
                            columns={movementColumns}
                            dataSource={report.cashMovements.content}
                            loading={loading}
                            scroll={{ x: 1100 }}
                            locale={{
                                emptyText: 'No hay movimientos para este período.',
                            }}
                            pagination={{
                                current: movementsPage + 1,
                                pageSize: movementsSize,
                                total: report.cashMovements.totalElements,
                                showSizeChanger: true,
                                pageSizeOptions: [10, 20, 50, 100],
                                showTotal: total => `${total} movimientos`,
                                onChange: (page, pageSize) => {
                                    setMovementsPage(page - 1);

                                    if (pageSize !== movementsSize) {
                                        setMovementsSize(pageSize);
                                        setMovementsPage(0);
                                    }
                                },
                            }}
                        />
                    </Card>
                </>
            )}

            {!report && !loading && (
                <Alert
                    type="info"
                    showIcon
                    message="No hay información para mostrar."
                    description="Seleccioná un período y aplicá los filtros para consultar los reportes."
                />
            )}
        </div>
    );
};

export default ReportsPage;