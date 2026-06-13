import { Navigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import type { UserRole } from "../types/Role.types";

const ROLE_ROUTES: Record<UserRole, string> = {
  ADMIN: "/admin/users",
  MANAGER: "/manager/kpis",
  DEVELOPER: "/developer/backlog",
};

const RedirectByRole = () => {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  const destination = ROLE_ROUTES[user.role];
  return <Navigate to={destination} replace />;
};

export default RedirectByRole;
