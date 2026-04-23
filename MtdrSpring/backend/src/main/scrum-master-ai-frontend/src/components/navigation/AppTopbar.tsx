import { Link as RouterLink, useLocation, useNavigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import type { User } from "../../types/User.types";
import { AppBar, Avatar, Box, Breadcrumbs, Button, Chip, IconButton, Link, Stack, Toolbar, Tooltip, Typography } from "@mui/material";
import { alpha } from "@mui/material/styles";
import { ChevronRightRounded, LogoutRounded, MenuRounded, NotificationsNoneRounded, PersonOutlineRounded, SettingsRounded } from "@mui/icons-material";
import { useMemo, useState } from "react";
import { getBreadcrumbsFromPath } from "./navigation.utils";
import StyledUserMenu from "../common/StyledUserMenu";
import ProjectSelectorChip from "../common/ProjectSelectorChip";

interface AppTopbarProps {
  drawerWidth: number;
  onOpenMobileSidebar: () => void;
  user: User;
}

const getRoleLabel = (role: User["role"]) => {
  switch (role) {
    case "DEVELOPER":
      return "Desarrollador";
    case "MANAGER":
      return "Manager";
    case "ADMIN":
      return "Administrador";
    default:
      return role;
  }
};

const AppTopbar = ({
  drawerWidth,
  onOpenMobileSidebar,
  user,
}: AppTopbarProps) => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [userAnchorEl, setUserAnchorEl] = useState<null | HTMLElement>(null);

  const breadcrumbs = useMemo(
    () => getBreadcrumbsFromPath(location.pathname, user.role),
    [location.pathname, user.role],
  );

  const openUserMenu = (event: React.MouseEvent<HTMLElement>) =>
    setUserAnchorEl(event.currentTarget);

  const closeUserMenu = () => setUserAnchorEl(null);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <AppBar
      position="fixed"
      elevation={0}
      sx={{
        width: { lg: `calc(100% - ${drawerWidth}px)` },
        ml: { lg: `${drawerWidth}px` },
        bgcolor: alpha("#04041f", 0.82),
        backdropFilter: "blur(12px)",
        borderBottom: "1px solid",
        borderColor: "divider",
      }}
    >
      <Toolbar sx={{ minHeight: "72px !important", gap: 2 }}>
        <IconButton
          color="inherit"
          edge="start"
          onClick={onOpenMobileSidebar}
          sx={{ mr: 1.5, display: { lg: "none" } }}
        >
          <MenuRounded />
        </IconButton>
        <Box sx={{ flexGrow: 1, minWidth: 0 }}>
          <Breadcrumbs
            separator={
              <ChevronRightRounded
                sx={{ fontSize: 18, color: "text.secondary" }}
              />
            }
            aria-label="breadcrumb"
            sx={{
              "& .MuiBreadcrumbs-ol": {
                flexWrap: "nowrap",
              },
            }}
          >
            {breadcrumbs.map((crumb) =>
              crumb.isLast ? (
                <Typography
                  key={crumb.path}
                  variant="body2"
                  sx={{
                    color: "text.primary",
                    fontWeight: 600,
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                  }}
                >
                  {crumb.label}
                </Typography>
              ) : (
                <Link
                  key={crumb.path}
                  component={RouterLink}
                  to={crumb.path}
                  underline="hover"
                  sx={{
                    color: "text.secondary",
                    fontSize: "0.875rem",
                    whiteSpace: "nowrap",
                  }}
                >
                  {crumb.label}
                </Link>
              ),
            )}
          </Breadcrumbs>
        </Box>

        <Stack direction="row" spacing={1.25} sx={{ alignItems: "center" }}>
          
          <ProjectSelectorChip userRole={user.role} />

          <Chip
            label={getRoleLabel(user.role)}
            sx={{
              display: { xs: "none", sm: "inline-flex" },
              bgcolor: alpha("#77ffc0", 0.1),
              color: "info.main",
              border: "1px solid",
              borderColor: alpha("#77ffc0", 0.18),
            }}
          />

          <Tooltip title="Notificaciones">
            <IconButton
              sx={{
                border: "1px solid",
                borderColor: "divider",
                bgcolor: alpha("#77ffc0", 0.04),
              }}
            >
              <NotificationsNoneRounded />
            </IconButton>
          </Tooltip>

          <Tooltip title="Configuración">
            <IconButton
              sx={{
                border: "1px solid",
                borderColor: "divider",
                bgcolor: alpha("#77ffc0", 0.04),
              }}
            >
              <SettingsRounded />
            </IconButton>
          </Tooltip>

          <Tooltip title="Cuenta">
            <IconButton
              onClick={openUserMenu}
              sx={{
                border: "1px solid",
                borderColor: "divider",
                bgcolor: alpha("#0016a6", 0.34),
              }}
            >
              <Avatar
                alt={user.username}
                sx={{
                  width: 30,
                  height: 30,
                  bgcolor: "transparent",
                  color: "info.main",
                }}
              >
                {user.username.charAt(0).toUpperCase()}
                {user.username.charAt(1).toLowerCase()}
              </Avatar>
            </IconButton>
          </Tooltip>
        </Stack>

        <StyledUserMenu
          anchorEl={userAnchorEl}
          open={Boolean(userAnchorEl)}
          onClose={closeUserMenu}
        >
          <Box sx={{ px: 1.5, py: 1 }}>
            <Typography sx={{ fontSize: 12 }}>{user.username}</Typography>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ fontSize: 10 }}
            >
              {user.email}
            </Typography>
          </Box>

          <Stack spacing={1.5} sx={{ p: 1 }}>
            <Button
              fullWidth
              variant="outlined"
              size="small"
              color="info"
              onClick={closeUserMenu}
              startIcon={<PersonOutlineRounded />}
              sx={{
                borderRadius: 1.5,
                borderColor: "info.main",
                color: "info.main",
                bgcolor: (theme) => alpha(theme.palette.info.main, 0.08),
                "&:hover": {
                  bgcolor: (theme) => alpha(theme.palette.info.main, 0.16),
                  borderColor: "info.main",
                },
                "& .MuiButton-startIcon": {
                  color: "info.main",
                },
              }}
            >
              Perfil
            </Button>
            <Button
              fullWidth
              variant="outlined"
              size="small"
              color="error"
              onClick={handleLogout}
              startIcon={<LogoutRounded />}
              sx={{
                borderRadius: 1.5,
                borderColor: "error.main",
                color: "error.main",
                bgcolor: (theme) => alpha(theme.palette.error.main, 0.08),
                "&:hover": {
                  bgcolor: (theme) => alpha(theme.palette.error.main, 0.16),
                  borderColor: "error.main",
                },
                "& .MuiButton-startIcon": {
                  color: "error.main",
                },
              }}
            >
              Cerrar Sesión
            </Button> 
          </Stack>
        </StyledUserMenu>
      </Toolbar>
    </AppBar>
  );
};

export default AppTopbar;
