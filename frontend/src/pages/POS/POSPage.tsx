import { useEffect, useMemo, useRef, useState } from 'react';
import {
    Alert,
    Button,
    Card,
    Col,
    Divider,
    Empty,
    Input,
    InputNumber,
    message,
    Modal,
    Row,
    Select,
    Space,
    Table,
    Tag,
    Typography,
    type InputRef,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { DeleteOutlined, PlusOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import { posService, type Product, type SaleRequest, type PaymentMethod, type IndividualPaymentMethod, } from '../../services/POSService';
import { userService, type UserResponse } from "../../services/userService";
import { cashRegisterService, type CashRegisterResponse } from "../../services/CashRegisterService";

const { Title, Text } = Typography;
const { Search } = Input;

interface CartItem {
    product: Product;
    quantity: number;
}

const formatCurrency = (amount: number): string =>
    new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS',
    }).format(amount);

const POSPage = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [cart, setCart] = useState<CartItem[]>([]);
    const [query, setQuery] = useState('');
    const [paymentMethod, setPaymentMethod] =
        useState<PaymentMethod>('EFECTIVO');

    const [loadingProducts, setLoadingProducts] = useState(false);
    const [previewVisible, setPreviewVisible] = useState(false);
    const toCentsForDisplay = (amount: number) => Math.round(amount * 100);

    const [user, setUser] = useState<UserResponse | null>(null);
    const [loadingContext, setLoadingContext] = useState(true);
    const [contextError, setContextError] = useState<string | null>(null);
    const [submittingSale, setSubmittingSale] = useState(false);
    //ete es para el barcode search
    const searchInputRef = useRef<InputRef>(null);

    const [mixedPayments, setMixedPayments] = useState<
        Record<IndividualPaymentMethod, number>
    >({
        EFECTIVO: 0,
        TRANSFERENCIA: 0,
        TARJETA: 0,
    });

    const mixedPaymentTotal =
        mixedPayments.EFECTIVO +
        mixedPayments.TRANSFERENCIA +
        mixedPayments.TARJETA;

    const selectedPayments = (
        Object.entries(mixedPayments) as [
            IndividualPaymentMethod,
            number
        ][]
    ).filter(([, amount]) => amount > 0);

    //y ete para enfocar el imput cuando se pone el pos
    useEffect(() => {
        searchInputRef.current?.focus();
    }, []);

    const total = useMemo(
        () =>
            cart.reduce(
                (sum, item) =>
                    sum + item.product.sellingPrice * item.quantity,
                0,
            ),
        [cart],
    );

    const searchProducts = async (value: string): Promise<Product[]> => {
        setLoadingProducts(true);

        try {
            const result = await posService.searchProducts(value);
            setProducts(result);
            return result;
        } catch (error) {
            console.error(error);
            message.error('Error al buscar productos.');
            return [];
        } finally {
            setLoadingProducts(false);
        }
    };

    useEffect(() => {
        void searchProducts('');
    }, []);

    const addToCart = (product: Product) => {
        setCart((currentCart) => {
            const existingItem = currentCart.find(
                (item) => item.product.id === product.id,
            );

            if (existingItem) {
                return currentCart.map((item) =>
                    item.product.id === product.id
                        ? { ...item, quantity: item.quantity + 1 }
                        : item,
                );
            }

            return [...currentCart, { product, quantity: 1 }];
        });

        message.success(`${product.name} agregado al carrito.`);
    };

    const handleSearch = async (value: string) => {
        const normalizedQuery = value.trim();

        if (!normalizedQuery) {
            await searchProducts('');
            searchInputRef.current?.focus();
            return;
        }

        setQuery(normalizedQuery);

        const result = await searchProducts(normalizedQuery);

        //el lector puede devolver el código seguido de Enter, pero no tengo lector pos asi que nu she si va a andar
        //pero si anda solo se agrega si encontra el barcode exacto
        const exactMatch = result.find(
            (product) => product.barcode.trim() === normalizedQuery,
        );

        if (exactMatch) {
            addToCart(exactMatch);
        } else if (result.length === 0) {
            message.warning('No se encontraron productos.');
        }

        //dejamos listo el buscador para el prox escaneo
        searchInputRef.current?.focus();
    };

    const updateQuantity = (productId: number, quantity: number | null) => {
        if (quantity == null || !Number.isInteger(quantity) || quantity < 1) {
            return;
        }

        setCart((currentCart) =>
            currentCart.map((item) =>
                item.product.id === productId
                    ? { ...item, quantity }
                    : item,
            ),
        );
    };

    const removeFromCart = (productId: number) => {
        setCart((currentCart) =>
            currentCart.filter((item) => item.product.id !== productId),
        );
    };

    const [cashRegister, setCashRegister] = useState<CashRegisterResponse | null>(null);

    const confirmSale = async () => {
        if (!user || !cashRegister) {
            message.error('No se pudo identificar al usuario o la caja.');
            return;
        }

        if (cashRegister.status !== 'OPEN') {
            message.error('La caja no está abierta.');
            return;
        }

        if (cart.length === 0) {
            message.warning('El carrito está vacío.');
            return;
        }

        // Trabajamos en centavos para evitar errores típicos de decimales.
        const toCents = (amount: number) => Math.round(amount * 100);

        let payments: SaleRequest['payments'];

        if (paymentMethod === 'MIXTO') {
            if (selectedPayments.length < 2) {
                message.error(
                    'Un pago mixto requiere al menos dos medios de pago.'
                );
                return;
            }

            const enteredTotal = selectedPayments.reduce(
                (sum, [, amount]) => sum + toCents(amount),
                0
            );

            if (enteredTotal !== toCents(total)) {
                message.error(
                    'Los importes de pago deben coincidir con el total de la venta.'
                );
                return;
            }

            payments = selectedPayments.map(([method, amount]) => ({
                paymentMethod: method,
                amount: Number(amount.toFixed(2)),
            }));
        } else {
            payments = [{
                paymentMethod: paymentMethod as IndividualPaymentMethod,
                amount: Number(total.toFixed(2)),
            }];
        }

        const request: SaleRequest = {
            sucursalId: cashRegister.sucursalId,
            cashRegisterId: cashRegister.id,
            paymentMethod,
            payments,
            details: cart.map((item) => ({
                productId: item.product.id,
                quantity: item.quantity,
            })),
        };

        setSubmittingSale(true);

        try {
            const sale = await posService.registerSale(user.id, request);

            message.success(
                `Venta #${sale.id} registrada correctamente. Total: ${formatCurrency(sale.totalAmount)}`
            );

            setCart([]);
            setPreviewVisible(false);
            setPaymentMethod('EFECTIVO');
            setMixedPayments({
                EFECTIVO: 0,
                TRANSFERENCIA: 0,
                TARJETA: 0,
            });

            await searchProducts(query);
        } catch (error) {
            console.error('Error al registrar la venta:', error);

            message.error(
                'No se pudo registrar la venta. Revisá el estado antes de reintentar.'
            );
        } finally {
            setSubmittingSale(false);
        }
    };

    useEffect(() => {
        const loadPOSContext = async () => {
            try {
                setLoadingContext(true);
                setContextError(null);

                const currentUser = await userService.getMe();
                setUser(currentUser);

                const currentCashRegister =
                    await cashRegisterService.getCurrent(
                        currentUser.sucursal
                    );

                if (currentCashRegister.status !== 'OPEN') {
                    throw new Error('La caja no está abierta.');
                }

                setCashRegister(currentCashRegister);
            } catch (error) {
                console.error('Error al cargar el contexto del POS:', error);

                setContextError(
                    'No se pudo obtener el usuario o la caja abierta.'
                );
            } finally {
                setLoadingContext(false);
            }


        };

        loadPOSContext();
    }, []);

    const productColumns: ColumnsType<Product> = [
        {
            title: 'Código',
            dataIndex: 'barcode',
            key: 'barcode',
            width: 140,
        },
        {
            title: 'Producto',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Precio',
            dataIndex: 'sellingPrice',
            key: 'sellingPrice',
            render: (price: number) => formatCurrency(price),
            width: 130,
        },
        {
            title: 'Acción',
            key: 'action',
            width: 110,
            render: (_, product) => (
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => addToCart(product)}
                >
                    Agregar
                </Button>
            ),
        },
    ];

    const cartColumns: ColumnsType<CartItem> = [
        {
            title: 'Producto',
            key: 'product',
            render: (_, item) => (
                <Space direction="vertical" size={0}>
                    <Text strong>{item.product.name}</Text>
                    <Text type="secondary">
                        {formatCurrency(item.product.sellingPrice)} c/u
                    </Text>
                </Space>
            ),
        },
        {
            title: 'Cantidad',
            key: 'quantity',
            width: 120,
            render: (_, item) => (
                <InputNumber
                    min={1}
                    precision={0}
                    value={item.quantity}
                    onChange={(value) =>
                        updateQuantity(item.product.id, value)
                    }
                    style={{ width: 85 }}
                />
            ),
        },
        {
            title: 'Subtotal',
            key: 'subtotal',
            width: 130,
            render: (_, item) =>
                formatCurrency(item.product.sellingPrice * item.quantity),
        },
        {
            title: '',
            key: 'actions',
            width: 55,
            render: (_, item) => (
                <Button
                    danger
                    type="text"
                    aria-label={`Eliminar ${item.product.name}`}
                    icon={<DeleteOutlined />}
                    onClick={() => removeFromCart(item.product.id)}
                />
            ),
        },
    ];

    return (
        <div style={{ padding: 24, maxWidth: 1500, margin: '0 auto' }}>
            <Space align="center" size="middle">
                <ShoppingCartOutlined style={{ fontSize: 28 }} />
                <Title level={2} style={{ margin: 0 }}>
                    Punto de Venta
                </Title>
            </Space>

            

            <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
                <Col xs={24} xl={14}>
                    <Card title="Buscar productos">
                        <Search
                            ref={searchInputRef}
                            placeholder="Nombre o código de barras..."
                            allowClear
                            enterButton="Buscar"
                            value={query}
                            onChange={(event) => setQuery(event.target.value)}
                            onSearch={(value) => void handleSearch(value)}
                            loading={loadingProducts}
                        />

                        <Table<Product>
                            style={{ marginTop: 16 }}
                            rowKey="id"
                            columns={productColumns}
                            dataSource={products}
                            loading={loadingProducts}
                            pagination={{ pageSize: 6 }}
                            locale={{
                                emptyText: (
                                    <Empty description="No hay productos para mostrar" />
                                ),
                            }}
                            scroll={{ x: 550 }}
                        />
                    </Card>
                </Col>

                <Col xs={24} xl={10}>
                    <Card
                        title={
                            <Space>
                                <ShoppingCartOutlined />
                                Carrito
                                <Tag>{cart.length} productos</Tag>
                            </Space>
                        }
                    >
                        {cart.length === 0 ? (
                            <Empty description="Todavía no agregaste productos" />
                        ) : (
                            <Table<CartItem>
                                rowKey={(item) => item.product.id}
                                columns={cartColumns}
                                dataSource={cart}
                                pagination={false}
                                size="small"
                                scroll={{ x: 400 }}
                            />
                        )}

                        <Divider />

                        <Row justify="space-between" align="middle">
                            <Col>
                                <Text type="secondary">Total estimado</Text>
                                <Title level={3} style={{ margin: 0 }}>
                                    {formatCurrency(total)}
                                </Title>
                            </Col>

                            <Col>
                                <Button
                                    danger
                                    disabled={cart.length === 0}
                                    onClick={() => setCart([])}
                                >
                                    Vaciar
                                </Button>
                            </Col>
                        </Row>

                        <Divider />

                        <Text strong>Medio de pago</Text>
                        <Select<PaymentMethod>
                            aria-label="Medio de pago"
                            style={{ width: '100%', marginTop: 8 }}
                            value={paymentMethod}
                            onChange={setPaymentMethod}
                            options={[
                                { value: 'EFECTIVO', label: 'Efectivo' },
                                {
                                    value: 'TRANSFERENCIA',
                                    label: 'Transferencia',
                                },
                                { value: 'TARJETA', label: 'Tarjeta' },
                                { value: 'MIXTO', label: 'Pago mixto' },
                            ]}
                        />
                        {paymentMethod === 'MIXTO' && (
                            <Card
                                size="small"
                                title="Distribución del pago"
                                style={{ marginTop: 12 }}
                            >
                                {(
                                    [
                                        ['EFECTIVO', 'Efectivo'],
                                        ['TRANSFERENCIA', 'Transferencia'],
                                        ['TARJETA', 'Tarjeta'],
                                    ] as [IndividualPaymentMethod, string][]
                                ).map(([method, label]) => (
                                    <Row
                                        key={method}
                                        align="middle"
                                        justify="space-between"
                                        style={{ marginBottom: 12 }}
                                    >
                                        <Col>{label}</Col>
                                        <Col>
                                            <InputNumber
                                                min={0}
                                                precision={2}
                                                step={100}
                                                value={mixedPayments[method]}
                                                prefix="$"
                                                style={{ width: 160 }}
                                                onChange={(value) =>
                                                    setMixedPayments((current) => ({
                                                        ...current,
                                                        [method]: value ?? 0,
                                                    }))
                                                }
                                            />
                                        </Col>
                                    </Row>
                                ))}

                                <Divider />

                                <Row justify="space-between">
                                    <Text>Importe distribuido</Text>
                                    <Text strong>
                                        {formatCurrency(mixedPaymentTotal)}
                                    </Text>
                                </Row>

                                <Row justify="space-between">
                                    <Text>Restante</Text>
                                    <Text
                                        type={
                                            toCentsForDisplay(mixedPaymentTotal) ===
                                                toCentsForDisplay(total)
                                                ? 'success'
                                                : 'danger'
                                        }
                                    >
                                        {formatCurrency(total - mixedPaymentTotal)}
                                    </Text>
                                </Row>
                            </Card>
                        )}

                        <Button
                            type="primary"
                            size="large"
                            block
                            style={{ marginTop: 16 }}
                            disabled={
                                cart.length === 0 ||
                                (
                                    paymentMethod === 'MIXTO' &&
                                    (
                                        selectedPayments.length < 2 ||
                                        toCentsForDisplay(mixedPaymentTotal) !==
                                        toCentsForDisplay(total)
                                    )
                                )
                            }
                            onClick={() => setPreviewVisible(true)}
                        >
                            Revisar venta
                        </Button>
                    </Card>
                </Col>
            </Row>

            <Modal
                title="Vista previa de la venta"
                open={previewVisible}
                onCancel={() => setPreviewVisible(false)}
                footer={[
                    <Button
                        key="back"
                        disabled={submittingSale}
                        onClick={() => setPreviewVisible(false)}
                    >
                        Volver
                    </Button>,
                    <Button
                        key="confirm"
                        type="primary"
                        loading={submittingSale}
                        disabled={
                            loadingContext ||
                            !user ||
                            !cashRegister ||
                            cashRegister.status !== 'OPEN' ||
                            cart.length === 0
                        }
                        onClick={() => void confirmSale()}
                    >
                        Confirmar venta
                    </Button>,
                ]}
            >
                <Alert
                    type="info"
                    showIcon
                    message="Esta es una vista previa"
                    description="Todavía no se registra la venta ni se realiza ningún cobro."
                />

                <Divider />

                {cart.map((item) => (
                    <Row
                        key={item.product.id}
                        justify="space-between"
                        style={{ marginBottom: 12 }}
                    >
                        <Col flex="auto">
                            <Text strong>{item.product.name}</Text>
                            <br />
                            <Text type="secondary">
                                {item.quantity} ×{' '}
                                {formatCurrency(item.product.sellingPrice)}
                            </Text>
                        </Col>

                        <Col>
                            <Text>
                                {formatCurrency(
                                    item.product.sellingPrice * item.quantity,
                                )}
                            </Text>
                        </Col>
                    </Row>
                ))}

                <Divider />

                <Row justify="space-between">
                    <Col>
                        <Text strong>Total estimado</Text>
                    </Col>
                    <Col>
                        <Title level={4} style={{ margin: 0 }}>
                            {formatCurrency(total)}
                        </Title>
                    </Col>
                </Row>

                <Row justify="space-between" style={{ marginTop: 12 }}>
                    <Col>
                        <Text type="secondary">Medio de pago</Text>
                    </Col>
                    <Col>
                        <Text>
                            {{
                                EFECTIVO: 'Efectivo',
                                TRANSFERENCIA: 'Transferencia',
                                TARJETA: 'Tarjeta',
                                MIXTO: 'Pago mixto',
                            }[paymentMethod]}
                        </Text>
                    </Col>
                </Row>

                {paymentMethod === 'MIXTO' && (
                    <div style={{ marginTop: 12 }}>
                        {selectedPayments.map(([method, amount]) => (
                            <Row
                                key={method}
                                justify="space-between"
                                style={{ marginBottom: 6 }}
                            >
                                <Col>
                                    <Text type="secondary">
                                        {{
                                            EFECTIVO: 'Efectivo',
                                            TRANSFERENCIA: 'Transferencia',
                                            TARJETA: 'Tarjeta',
                                        }[method]}
                                    </Text>
                                </Col>
                                <Col>
                                    <Text>{formatCurrency(amount)}</Text>
                                </Col>
                            </Row>
                        ))}
                    </div>
                )}
            </Modal>
        </div>
    );
};

export default POSPage;