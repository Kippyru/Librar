import React, {useState, useEffect} from 'react';
import {Card, Button, Typography, Space, Table, Tag, message, Modal, Form, InputNumber, Input, Select} from 'antd';
import {cashRegisterService} from '../../services/cashRegisterService'; // Ajusta la ruta a tu archivo

const {Title, Text} = Typography;

const CashPage = () => {
    const [loading, setLoading] = useState(false);
    const [cashRegister, setCashRegister] = useState(null);
    const [movements, setMovements] = useState([]);
    const [isMovementModalVisible, setIsMovementModalVisible] = useState(false);
    const [movementForm] = Form.useForm();
    const [isCloseModalVisible, setIsCloseModalVisible] = useState(false);
    const [countData, setCountData] = useState(null); // Guardará el arqueo teórico
    const [closeForm] = Form.useForm();

    // TODO: In the future, get this ID from logged user in AuthContext/Localstorage
    const SUCURSAL_ID = 1;

    // to control floating form
    const [isOpenModalVisible, setIsOpenModalVisible] = useState(false);
    const [form] = Form.useForm();

    // when the opening is confirmed
    const handleOpenCashRegister = async (values) => {
        setLoading(true);
        try {
            await cashRegisterService.open(SUCURSAL_ID, {
                openingAmount: values.openingAmount,
                notes: values.notes
            });
            message.success('¡Caja abierta exitosamente!');
            setIsOpenModalVisible(false); // close modal
            form.resetFields(); // clear the form
            fetchCurrentStatus(); // backend executes again, to change the screen
        } catch (error) {
            message.error(error.response?.data?.message || 'Error al abrir la caja.');
        } finally {
            setLoading(false);
        }
    };


    useEffect(() => {
        fetchCurrentStatus();
    }, []);

    // 1st: is there a register open?
    const fetchCurrentStatus = async () => {
        setLoading(true);
        try {
            const data = await cashRegisterService.getCurrent(SUCURSAL_ID);
            setCashRegister(data); // save the open register
            fetchMovements(data.id); // if it is, we search it's history
        } catch (error) {
            // if the backend throws an error, clear the state
            setCashRegister(null);
            setMovements([]);
        } finally {
            setLoading(false);
        }
    };

    // 2nd: search the ingresos/egresos history
    const fetchMovements = async (cashRegisterId) => {
        try {
            const data = await cashRegisterService.getMovements(cashRegisterId);
            setMovements(data);
        } catch (error) {
            message.error("Error al cargar el historial de movimientos");
        }
    };

    // 3rd: column table configuration by Ant Design
    const columns = [
        {
            title: 'Fecha y Hora',
            dataIndex: 'occurredAt',
            key: 'occurredAt',
            render: (text) => new Date(text).toLocaleString()
        },
        {
            title: 'Tipo',
            dataIndex: 'type',
            key: 'type',
            render: (type) => (
                <Tag color={type === 'CASH_IN' ? 'green' : 'red'}>
                    {type === 'CASH_IN' ? 'INGRESO' : 'EGRESO'}
                </Tag>
            )
        },
        {title: 'Motivo', dataIndex: 'reason', key: 'reason'},
        {
            title: 'Monto',
            dataIndex: 'amount',
            key: 'amount',
            render: (amount) => <b>${amount}</b>
        },
        {title: 'Descripción', dataIndex: 'description', key: 'description'},
    ];

    const handleRegisterMovement = async (values) => {
        setLoading(true);
        try {
            await cashRegisterService.registerMovement(cashRegister.id, {
                type: values.type,
                reason: values.reason,
                amount: values.amount,
                description: values.description
            });
            message.success('Movimiento registrado exitosamente');
            setIsMovementModalVisible(false);
            movementForm.resetFields(); // clear form
            fetchMovements(cashRegister.id); // reload the table to see new movement
        } catch (error) {
            message.error(error.response?.data?.message || 'Error al registrar el movimiento.');
        } finally {
            setLoading(false);
        }
    };

    //closing procedure
    const handleOpenCloseModal = async () => {
        setLoading(true);
        try {
            const data = await cashRegisterService.count(cashRegister.id);
            setCountData(data);
            setIsCloseModalVisible(true);
        } catch (error) {
            message.error('Error al obtener el arqueo de caja');
        } finally {
            setLoading(false);
        }
    };

    const handleCloseCashRegister = async (values) => {
        setLoading(true);
        try {
            await cashRegisterService.close(cashRegister.id, {
                closingAmount: values.closingAmount,
                notes: values.notes
            });
            message.success('Caja cerrada exitosamente');
            setIsCloseModalVisible(false);
            closeForm.resetFields();
            fetchCurrentStatus(); // reload, so we return to the "Caja Cerrada" screen
        } catch (error) {
            message.error(error.response?.data?.message || 'Error al cerrar la caja');
        } finally {
            setLoading(false);
        }
    };




    return (
        <div style={{padding: '24px', maxWidth: '1200px', margin: '0 auto'}}>
            <Title level={2}>Gestión de Caja</Title>

            {/* if there is no Register, show the aperture screen*/}
            {!cashRegister ? (
                <Card style={{textAlign: 'center', marginTop: '40px', padding: '40px 0'}}>
                    <Title level={4}>La caja está actualmente cerrada</Title>
                    <Text type="secondary">Debes abrir la caja con un monto inicial para comenzar a operar.</Text>
                    <br/><br/>
                    <Button
                        type="primary"
                        size="large"
                        loading={loading}
                        onClick={() => setIsOpenModalVisible(true)}
                    >
                        Abrir Caja
                    </Button>
                </Card>
            ) : (
                /* if there is a Register, show the table and control panel */
                <Space direction="vertical" size="large" style={{display: 'flex'}}>

                    {/* upper panel: information and buttons */}
                    <Card>
                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            flexWrap: 'wrap',
                            gap: '16px'
                        }}>
                            <div>
                                <Title level={4} style={{margin: 0, color: '#52c41a'}}>Caja Operativa</Title>
                                <Text type="secondary">Monto de apertura: <b>${cashRegister.openingAmount}</b></Text>
                            </div>
                            <Space wrap>
                                <Button
                                    type="primary"
                                    onClick={() => setIsMovementModalVisible(true)}
                                >
                                    Registrar Movimiento
                                </Button>
                                <Button danger onClick={handleOpenCloseModal}>
                                    Arqueo y Cierre
                                </Button>
                            </Space>
                        </div>
                    </Card>

                    {/* bottom table: history */}
                    <Card title="Historial de Movimientos de Hoy">
                        <Table
                            dataSource={movements}
                            columns={columns}
                            rowKey="id"
                            loading={loading}
                            pagination={{pageSize: 5}}
                            scroll={{x: 'max-content'}} // activates the horizontal scroll
                        />
                    </Card>

                </Space>
            )}
            <Modal
                title="Abrir Caja Diaria"
                open={isOpenModalVisible}
                onCancel={() => setIsOpenModalVisible(false)}
                footer={null} // hide buttons by default, we will use the Form;s
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleOpenCashRegister}
                    initialValues={{openingAmount: 0}}
                >
                    <Form.Item
                        label="Monto Inicial en Caja ($)"
                        name="openingAmount"
                        rules={[{required: true, message: 'Por favor ingresa el monto inicial'}]}
                    >
                        <InputNumber
                            style={{width: '100%'}}
                            min={0}
                            step={100}
                            prefix="$"
                        />
                    </Form.Item>

                    <Form.Item
                        label="Notas de Apertura (Opcional)"
                        name="notes"
                    >
                        <Input.TextArea rows={3} placeholder="Ej: Cambio chico en billetes de 1000..."/>
                    </Form.Item>

                    <Form.Item style={{textAlign: 'right', marginBottom: 0}}>
                        <Space>
                            <Button onClick={() => setIsOpenModalVisible(false)}>
                                Cancelar
                            </Button>
                            <Button type="primary" htmlType="submit" loading={loading}>
                                Confirmar Apertura
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal
                title="Registrar Ingreso o Egreso"
                open={isMovementModalVisible}
                onCancel={() => setIsMovementModalVisible(false)}
                footer={null}
            >
                <Form
                    form={movementForm}
                    layout="vertical"
                    onFinish={handleRegisterMovement}
                    initialValues={{ type: 'CASH_IN', reason: 'SALE' }}
                >
                    <Form.Item
                        label="Tipo de Movimiento"
                        name="type"
                        rules={[{ required: true, message: 'Selecciona el tipo' }]}
                    >
                        <Select>
                            <Select.Option value="CASH_IN">Ingreso a caja (CASH_IN)</Select.Option>
                            <Select.Option value="CASH_OUT">Retiro de caja (CASH_OUT)</Select.Option>
                        </Select>
                    </Form.Item>

                    <Form.Item
                        label="Motivo"
                        name="reason"
                        rules={[{ required: true, message: 'Selecciona un motivo' }]}
                    >
                        <Select>
                            <Select.Option value="SALE">Venta</Select.Option>
                            <Select.Option value="REFUND">Reembolso</Select.Option>
                            <Select.Option value="DEPOSIT">Depósito</Select.Option>
                            <Select.Option value="WITHDRAWAL">Retiro</Select.Option>
                            <Select.Option value="ADJUSTMENT">Ajuste</Select.Option>
                        </Select>
                    </Form.Item>

                    <Form.Item
                        label="Monto ($)"
                        name="amount"
                        rules={[{ required: true, message: 'Por favor ingresa el monto' }]}
                    >
                        <InputNumber
                            style={{ width: '100%' }}
                            min={0.01}
                            step={100}
                            prefix="$"
                        />
                    </Form.Item>

                    <Form.Item
                        label="Descripción (Opcional)"
                        name="description"
                    >
                        <Input.TextArea rows={2} placeholder="Ej: Pago a proveedor de agua..." />
                    </Form.Item>

                    <Form.Item style={{ textAlign: 'right', marginBottom: 0 }}>
                        <Space>
                            <Button onClick={() => setIsMovementModalVisible(false)}>
                                Cancelar
                            </Button>
                            <Button type="primary" htmlType="submit" loading={loading}>
                                Guardar Movimiento
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal
                title="Arqueo y Cierre de Caja"
                open={isCloseModalVisible}
                onCancel={() => setIsCloseModalVisible(false)}
                footer={null}
            >
                {/* rundown of the calculations from the backend */}
                {countData && (
                    <div style={{ marginBottom: 24, padding: 16, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
                        <p style={{ margin: '4px 0' }}>Monto Inicial: <b>${countData.openingAmount}</b></p>
                        <p style={{ margin: '4px 0' }}>Total Ingresos: <b style={{color: '#52c41a'}}>${countData.cashIn}</b></p>
                        <p style={{ margin: '4px 0' }}>Total Egresos: <b style={{color: '#ff4d4f'}}>${countData.cashOut}</b></p>
                        <Title level={5} style={{ marginTop: 12, marginBottom: 0 }}>
                            Total Esperado en Sistema: ${countData.expectedAmount}
                        </Title>
                    </div>
                )}

                <Form
                    form={closeForm}
                    layout="vertical"
                    onFinish={handleCloseCashRegister}
                >
                    <Form.Item
                        label="Dinero Físico Real en Caja ($)"
                        name="closingAmount"
                        rules={[{ required: true, message: 'Ingresa el dinero físico con el que cierras' }]}
                        extra="Si el monto real no coincide con el sistema, se registrará una diferencia."
                    >
                        <InputNumber style={{ width: '100%' }} min={0} step={100} prefix="$" />
                    </Form.Item>

                    <Form.Item label="Notas de Cierre (Opcional)" name="notes">
                        <Input.TextArea rows={2} placeholder="Ej: Faltan $50 por error de vuelto..." />
                    </Form.Item>

                    <Form.Item style={{ textAlign: 'right', marginBottom: 0 }}>
                        <Space>
                            <Button onClick={() => setIsCloseModalVisible(false)}>Cancelar</Button>
                            <Button danger type="primary" htmlType="submit" loading={loading}>
                                Cerrar Caja
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default CashPage;