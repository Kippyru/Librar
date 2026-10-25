import React from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircleFilled } from '@ant-design/icons';
import { Typography } from 'antd';
import { Space, Button } from 'antd';

const { Title, Text } = Typography;

const HomePage = () => {
    const navigate = useNavigate();
    return (
        <div style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '80vh',
            textAlign: 'center'
        }}>

            <CheckCircleFilled style={{ fontSize: '72px', color: '#52c41a', marginBottom: '16px' }} />


            <Title level={2} style={{ margin: 0, color: '#262626' }}>
                ¡Te damos la bienvenida al Panel de Control!
            </Title>
            <Text type="secondary" style={{ display: 'block', marginTop: '8px', marginBottom: '32px' }}>
                Has ingresado correctamente.
            </Text>


            <Space size={16} wrap>
                <Button size="large" onClick={() => navigate('/products')}>
                    Gestión de Productos
                </Button>

                <Button size="large" onClick={() => navigate('/stock')}>
                    Ver Stock por Sucursales
                </Button>

                <Button type="primary" size="large" onClick={() => navigate('/register')}>
                    Registrar Nuevo Empleado
                </Button>
                <Button size="large" onClick={() => navigate('/caja')}>
                    Gestión de Caja
                </Button>

                <Button size="large" onClick={() => navigate('/pos')}>
                    POS
                </Button>
            </Space>

        </div>
    );
};

export default HomePage;