import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import type { UserRole } from '../types/Role.types';

interface RoleGuardProps {
  allowedRoles: UserRole[];
}

const RoleGuard = ({ allowedRoles }: RoleGuardProps) => {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" replace />;
  }
  
  return <Outlet />;
}

export default RoleGuard