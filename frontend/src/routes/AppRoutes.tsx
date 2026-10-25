import { createBrowserRouter, Navigate } from 'react-router-dom';
import { LoginPage } from '../pages/Login/LoginPage';
import { ProtectedRoute } from './ProtectedRoute';
import HomePage from '../pages/Home/HomePage';
import ProductsPage from '../pages/Products/ProductsPage';
import StockPage from '../pages/Stock/StockPage';
import { RegisterPage } from '../pages/Auth/RegisterPage';
import CashPage from '../pages/Cash/CashPage';
import POSPage from '../pages/POS/POSPage';

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
  // protected routes
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