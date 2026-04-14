import { Navigate, Outlet } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import type { UserRole } from "../types/Role.types";

const ROLE_ROUTES: Record<UserRole, string> = {
  ADMIN: "/admin/home",
  MANAGER: "/manager/home",
  DEVELOPER: "/developer/home",
};

const GuestRoute = () => {
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated && user) {
    const destination = ROLE_ROUTES[user.role];
    console.log(
      `[Router] Already authenticated (${user.role}) — redirecting away from login to ${destination}`,
    );
    return <Navigate to={destination} replace />;
  }

  return <Outlet />;
};

export default GuestRoute;
