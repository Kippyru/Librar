import React from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircleFilled } from '@ant-design/icons';
import { Typography } from 'antd';
import { Space, Button } from 'antd';
import { getUserRole } from '../../utils/auth';

const { Title, Text } = Typography;

const HomePage = () => {
    const isAdmin = getUserRole() === 'ROLE_ADMIN';
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

                <Button size="large" onClick={() => navigate('/suppliers')}>
                    Gestión de Proveedores
                </Button>

                <Button size="large" onClick={() => navigate('/categories')}>
                    Gestión de Categorías
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
                {isAdmin && (
                    <Button
                        type="primary"
                        size="large"
                        onClick={() => navigate('/reports')}
                    >
                        Reportes
                    </Button>
                )}
            </Space>
        </div>
    );
};

export default HomePage;