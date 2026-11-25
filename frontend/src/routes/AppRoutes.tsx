import { createBrowserRouter, Navigate } from 'react-router-dom';
import { LoginPage } from '../pages/Login/LoginPage';
import { ProtectedRoute } from './ProtectedRoute';
import HomePage from '../pages/Home/HomePage';
import ProductsPage from '../pages/Products/ProductsPage';
import StockPage from '../pages/Stock/StockPage';
import { RegisterPage } from '../pages/Auth/RegisterPage';
import CashPage from '../pages/Cash/CashPage';
import POSPage from '../pages/POS/POSPage';
import SupplierPage from '../pages/Supplier/SupplierPage'; // <--- Importamos Proveedores
import CategoryPage from '../pages/Category/CategoryPage'; // <--- Importamos Categorías
import { AdminRoute } from './AdminRoute';
import ReportsPage from '../pages/Reports/ReportsPage';

const NotFound = () => <h2>404 - Página no encontrada</h2>;

export const router = createBrowserRouter([
    // public routes
    {
        path: '/login',
        element: <LoginPage />,
    },
    {
        path: '/register',
        element: <RegisterPage />,
    },

    // Protected routes
    {
        element: <ProtectedRoute />,
        children: [
            {
                path: '/',
                element: <HomePage />,
            },
            {
                path: '/products',
                element: <ProductsPage />,
            },
            {
                path: '/stock',
                element: <StockPage />,
            },
            {
                path: '/caja',
                element: <CashPage />,
            },
            {
                path: '/pos',
                element: <POSPage />,
            },
            {
                path: '/suppliers',
                element: <SupplierPage />,
            },
            {
                path: '/categories',
                element: <CategoryPage />,
            },

            // Admin-only routes
            {
                element: <AdminRoute />,
                children: [
                    {
                        path: '/reports',
                        element: <ReportsPage />,
                    },
                ],
            },
        ],
    },

    // error handling routes
    {
        path: '/404',
        element: <NotFound />,
    },
    {
        path: '*',
        element: <Navigate to="/404" replace />,
    },
]);