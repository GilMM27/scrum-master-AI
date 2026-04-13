import { Navigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import type { UserRole } from "../types/Role.types";

const ROLE_ROUTES: Record<UserRole, string> = {
  ADMIN: "/admin/home",
  MANAGER: "/manager/home",
  DEVELOPER: "/developer/home",
};

const RedirectByRole = () => {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated || !user) {
    console.log("[Router] No valid session — redirecting to /login");
    return <Navigate to="/login" replace />;
  }

  const destination = ROLE_ROUTES[user.role];
  console.log(
    `[Router] Valid session found (${user.role}) — redirecting to ${destination}`,
  );
  return <Navigate to={destination} replace />;
};

export default RedirectByRole;
