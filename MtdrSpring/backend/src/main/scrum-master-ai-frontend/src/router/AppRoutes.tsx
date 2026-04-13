import { Route, Routes } from "react-router-dom";
import Login from "../pages/auth/Login";
import ForgotPassword from "../pages/auth/ForgotPassword";
import Unauthorized from "../pages/common/Unauthorized";
import NotFound from "../pages/common/NotFound";
import DeveloperHome from "../pages/developer/DeveloperHome";
import ManagerHome from "../pages/manager/ManagerHome";
import AdminHome from "../pages/admin/AdminHome";
import ProtectedRoute from "./ProtectedRoute";
import RoleGuard from "./RoleGuard";
import RedirectByRole from "./RedirectByRole";
import GuestRoute from "./GuestRoute";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<RedirectByRole />} />

      <Route element={<GuestRoute />}>
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
      </Route>
      <Route path="/unauthorized" element={<Unauthorized />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<RoleGuard allowedRoles={["DEVELOPER"]} />}>
          <Route path="/developer" element={<DeveloperHome />} />
        </Route>

        <Route element={<RoleGuard allowedRoles={["MANAGER"]} />}>
          <Route path="/manager" element={<ManagerHome />} />
        </Route>

        <Route element={<RoleGuard allowedRoles={["ADMIN"]} />}>
          <Route path="/admin" element={<AdminHome />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AppRoutes;
