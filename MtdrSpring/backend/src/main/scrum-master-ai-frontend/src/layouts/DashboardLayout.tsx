import { useState, type ReactNode } from "react";
import useAuth from "../hooks/useAuth";
import { Navigate } from "react-router-dom";
import { Box, Toolbar } from "@mui/material";
import AppTopbar from "../components/navigation/AppTopbar";
import AppSiderbar from "../components/navigation/AppSidebar";
import ProjectProvider from "../context/ProjectContext";

interface DashboardLayoutProps {
  children: ReactNode;
}

const DRAWER_WIDTH = 288;

const DashboardLayout = ({ children }: DashboardLayoutProps) => {
  const { user, isAuthenticated } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <ProjectProvider>
      <Box
        sx={{
          display: "flex",
          minHeight: "100vh",
          bgcolor: "background.default",
        }}
      >
        <AppTopbar
          drawerWidth={DRAWER_WIDTH}
          onOpenMobileSidebar={() => setMobileOpen(true)}
          user={user!}
        />
        <AppSiderbar
          drawerWidth={DRAWER_WIDTH}
          mobileOpen={mobileOpen}
          onCloseMobile={() => setMobileOpen(false)}
          user={user!}
        />
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            ml: { lg: `${DRAWER_WIDTH}px` },
            width: { lg: `calc(100% - ${DRAWER_WIDTH}px)` },
            minHeight: "100vh",
            px: { xs: 2, sm: 3, md: 4 },
            py: 3,
          }}
        >
          <Toolbar />
          {children}
        </Box>
      </Box>
    </ProjectProvider>
  );
};

export default DashboardLayout;
