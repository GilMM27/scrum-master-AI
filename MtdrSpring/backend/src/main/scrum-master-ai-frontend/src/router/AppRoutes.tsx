import { Navigate, Route, Routes } from "react-router-dom";

import Login from "../pages/auth/Login";
import ForgotPassword from "../pages/auth/ForgotPassword";
import Unauthorized from "../pages/common/Unauthorized";
import NotFound from "../pages/common/NotFound";

import DeveloperHome from "../pages/developer/DeveloperHome";
import DeveloperBacklog from "../pages/developer/DeveloperBacklog";
import DeveloperTasks from "../pages/developer/DeveloperTasks";

import ManagerHome from "../pages/manager/ManagerHome";
import ManagerBacklog from "../pages/manager/ManagerBacklog";
import ManagerSprint from "../pages/manager/ManagerSprint";
import ManagerDashboard from "../pages/manager/ManagerDashboard";

import AdminHome from "../pages/admin/AdminHome";
import AdminUsers from "../pages/admin/AdminUsers";

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
          <Route
            path="/developer"
            element={<Navigate to="/developer/home" replace />}
          />
          <Route path="/developer/home" element={<DeveloperHome />} />
          <Route path="/developer/backlog" element={<DeveloperBacklog />} />
          <Route path="/developer/tasks" element={<DeveloperTasks />} />
        </Route>

        <Route element={<RoleGuard allowedRoles={["MANAGER"]} />}>
          <Route
            path="/manager"
            element={<Navigate to="/manager/home" replace />}
          />
          <Route path="/manager/home" element={<ManagerHome />} />
          <Route path="/manager/backlog" element={<ManagerBacklog />} />
          <Route path="/manager/sprint" element={<ManagerSprint />} />
          <Route path="/manager/kpis" element={<ManagerDashboard />} />
        </Route>

        <Route element={<RoleGuard allowedRoles={["ADMIN"]} />}>
          <Route
            path="/admin"
            element={<Navigate to="/admin/home" replace />}
          />
          <Route path="/admin/home" element={<AdminHome />} />
          <Route path="/admin/users" element={<AdminUsers />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default AppRoutes;
