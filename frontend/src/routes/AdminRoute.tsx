import { Navigate, Outlet } from 'react-router-dom';
import { getUserRole } from '../utils/auth';

export const AdminRoute = () => {
    const role = getUserRole();

    if (role !== 'ROLE_ADMIN') {
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
};