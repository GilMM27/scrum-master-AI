import { useState } from "react";
import { Alert, Avatar, Box, Button, CircularProgress, IconButton, InputAdornment, Link, Stack, TextField, Typography } from "@mui/material";
import AutoAwesomeRoundedIcon from "@mui/icons-material/AutoAwesomeRounded";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import LoginRoundedIcon from "@mui/icons-material/LoginRounded";
import VisibilityOffRoundedIcon from "@mui/icons-material/VisibilityOffRounded";
import VisibilityRoundedIcon from "@mui/icons-material/VisibilityRounded";
import { alpha } from "@mui/material/styles";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../../layouts/AuthLayout";
import useAuth from "../../hooks/useAuth";
import { InactiveAccountError } from "../../features/auth/services/auth.service";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [inactiveMessage, setInactiveMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const getDestinationLabel = (role: string): string => {
    switch (role) {
      case "ADMIN":
        return "Portal de Administradores";
      case "MANAGER":
        return "Portal de Managers";
      case "DEVELOPER":
        return "Portal de Desarrolladores";
      default:
        return "tu página de inicio";
    }
  };

  const validateForm = () => {
    if (!username.trim() || !password.trim()) {
      setErrorMessage("Por favor, completa usuario y contraseña.");
      return false;
    }

    setErrorMessage("");
    return true;
  };

  const redirectByRole = (role: string) => {
    switch (role) {
      case "ADMIN":
        navigate("/admin/home", { replace: true });
        break;
      case "MANAGER":
        navigate("/manager/home", { replace: true });
        break;
      case "DEVELOPER":
        navigate("/developer/home", { replace: true });
        break;
      default:
        navigate("/unauthorized", { replace: true });
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!validateForm()) return;

    try {
      setIsSubmitting(true);
      setInactiveMessage("");

      const user = await login({ username: username.trim(), password });

      setSuccessMessage(
        `Inicio de sesión exitoso. Redirigiendo al ${getDestinationLabel(user.role)}...`,
      );
      await new Promise((resolve) => setTimeout(resolve, 2500));
      redirectByRole(user.role);
    } catch (error) {
      if (error instanceof InactiveAccountError) {
        setInactiveMessage(error.message);
        setErrorMessage("");
      } else {
        setInactiveMessage("");
        setErrorMessage(
          error instanceof Error ? error.message : "Error al iniciar sesión",
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AuthLayout>
      <Stack component="form" spacing={2.5} onSubmit={handleSubmit}>
        <Stack spacing={1.5} sx={{ alignItems: "center", textAlign: "center" }}>
          <Avatar
            sx={{
              width: 52,
              height: 52,
              bgcolor: alpha("#0300a6", 0.1),
              color: "info.main",
              border: "1px solid",
              borderColor: alpha("#7794ff", 0.22),
            }}
          >
            <LockOutlinedIcon />
          </Avatar>
          <Typography variant="h4">Iniciar sesión</Typography>
          <Typography variant="body2" color="text.primary">
            Accede con tus credenciales para entrar al portal AgiFlow.
          </Typography>
        </Stack>
        <TextField
          label="Username"
          name="username"
          fullWidth
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Ingresa tu usuario"
          autoComplete="username"
        />
        <TextField
          label="Password"
          name="password"
          type={showPassword ? "text" : "password"}
          fullWidth
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Ingresa tu contraseña"
          autoComplete="current-password"
          slotProps={{
            input: {
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    onClick={() => setShowPassword((prev) => !prev)}
                    edge="end"
                    aria-label={
                      showPassword ? "Ocultar contraseña" : "Mostrar contraseña"
                    }
                  >
                    {showPassword ? (
                      <VisibilityOffRoundedIcon sx={{ color: "info.main" }} />
                    ) : (
                      <VisibilityRoundedIcon
                        sx={{ color: `${alpha("#77ffc0", 0.12)}` }}
                      />
                    )}
                  </IconButton>
                </InputAdornment>
              ),
            },
          }}
        />
        <Button
          type="submit"
          variant="contained"
          size="large"
          disabled={isSubmitting}
          startIcon={isSubmitting ? undefined : <LoginRoundedIcon />}
        >
          {isSubmitting ? (
            <CircularProgress size={22} color="inherit" />
          ) : (
            "Iniciar sesión"
          )}
        </Button>
        {errorMessage && <Alert severity="error">{errorMessage}</Alert>}
        {inactiveMessage && (
          <Alert severity="warning">
            <strong>Cuenta inactiva.</strong> {inactiveMessage}
          </Alert>
        )}
        {successMessage && <Alert severity="success">{successMessage}</Alert>}
        <Box sx={{ textAlign: "center", pt: 0.5 }}>
          <Link
            component="button"
            type="button"
            underline="hover"
            onClick={() => navigate("/forgot-password")}
            sx={{
              display: "inline-flex",
              alignItems: "center",
              gap: 0.75,
              fontSize: "0.95rem",
            }}
          >
            <AutoAwesomeRoundedIcon sx={{ fontSize: 18 }} />
            ¿Olvidaste tu contraseña?
          </Link>
        </Box>
      </Stack>
    </AuthLayout>
  );
}
