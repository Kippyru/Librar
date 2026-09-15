import { createBrowserRouter, Navigate } from 'react-router-dom';

// Páginas rápidas de ejemplo
const Home = () => <h2>Página de Bienvenida</h2>;
const NotFound = () => <h2>404 - Página no encontrada</h2>;

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Home />,
  },
  {
    path: '/404',
    element: <NotFound />,
  },
  {
    path: '*',
    element: <Navigate to="/404" replace />,
  },
]);
