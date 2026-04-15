import React, { useEffect, useState } from "react";
import type { CreateManagedUserRequest, ManagedUserRole,} from "../types/users.types";
import { Alert, alpha, Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, InputAdornment, MenuItem, Stack, TextField, Typography } from "@mui/material";
import { CheckCircleRounded, CloseRounded, RadioButtonUncheckedRounded, VisibilityOffRounded, VisibilityRounded } from "@mui/icons-material";

interface CreateUserDialogProps {
  open: boolean;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (payload: CreateManagedUserRequest) => Promise<void>;
}

const USERNAME_MIN = 3;
const USERNAME_MAX = 20;
const PHONE_REGEX = /^\+?[\d\s\-().]{7,20}$/;

const passwordRules: { label: string; test: (p: string) => boolean }[] = [
  { label: "Al menos 8 caracteres", test: (p) => p.length >= 8 },
  { label: "Al menos una letra mayúscula", test: (p) => /[A-Z]/.test(p) },
  { label: "Al menos una letra minúscula", test: (p) => /[a-z]/.test(p) },
  { label: "Al menos un número", test: (p) => /\d/.test(p) },
  { label: "Al menos un carácter especial (!@#$…)", test: (p) => /[^A-Za-z0-9]/.test(p),
  },
];

const initialForm: CreateManagedUserRequest = {
  username: "",
  email: "",
  password: "",
  cellPhone: "",
  userRole: "DEVELOPER",
};

const CreateUserDialog = ({
  open,
  loading,
  onClose,
  onSubmit,
}: CreateUserDialogProps) => {
  const [form, setForm] = React.useState<CreateManagedUserRequest>(initialForm);
  const [errorMsg, setErrorMsg] = React.useState("");

  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    if (!open) {
      setForm(initialForm);
      setErrorMsg("");
    }
  }, [open]);

  const handleChange = <K extends keyof CreateManagedUserRequest>(
    key: K,
    value: CreateManagedUserRequest[K],
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const validateForm = () => {
    const username = form.username.trim();
    if (!username) {
      setErrorMsg("El username es requerido.");
      return false;
    }
    if (username.length < USERNAME_MIN) {
      setErrorMsg(
        `El username debe tener al menos ${USERNAME_MIN} caracteres.`,
      );
      return false;
    }
    if (username.length > USERNAME_MAX) {
      setErrorMsg(
        `El username no puede superar los ${USERNAME_MAX} caracteres.`,
      );
      return false;
    }

    if (!form.email.trim()) {
      setErrorMsg("El email es requerido.");
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(form.email)) {
      setErrorMsg("Ingresa un correo electrónico válido.");
      return false;
    }

    if (!form.password.trim()) {
      setErrorMsg("La contraseña es requerida.");
      return false;
    }

    const failedRule = passwordRules.find((r) => !r.test(form.password));
    if (failedRule) {
      setErrorMsg(`La contraseña no cumple: "${failedRule.label}".`);
      return false;
    }

    const phone = form.cellPhone.trim();
    if (!phone) {
      setErrorMsg("El número de celular es requerido.");
      return false;
    }
    if (!PHONE_REGEX.test(phone)) {
      setErrorMsg(
        "Ingresa un número de celular válido (ej. +52 999 123 4567).",
      );
      return false;
    }

    setErrorMsg("");
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    await onSubmit({
      ...form,
      username: form.username.trim(),
      email: form.email.trim(),
      cellPhone: form.cellPhone.trim(),
    });
  };

  return (
    <Dialog
      open={open}
      onClose={loading ? undefined : onClose}
      maxWidth="sm"
      fullWidth
    >
      <Stack
        direction="row"
        sx={{
          alignItems: "center",
          justifyContent: "space-between",
          bgcolor: "primary.dark",
          px: 2,
          py: 1,
        }}
      >
        <DialogTitle>Crear Usuario</DialogTitle>
        <IconButton
          onClick={onClose}
          size="small"
          sx={{ mr: 2, "&:hover": { color: alpha("#b4bdc7", 0.8) } }}
        >
          <CloseRounded />
        </IconButton>
      </Stack>

      <DialogContent dividers>
        <Stack spacing={2}>
          {errorMsg && (
            <Alert severity="error" onClose={() => setErrorMsg("")}>
              {errorMsg}
            </Alert>
          )}

          <TextField
            label="Nombre de Usuario"
            fullWidth
            value={form.username}
            onChange={(e) => handleChange("username", e.target.value)}
            disabled={loading}
            slotProps={{
              htmlInput: { minLength: USERNAME_MIN, maxLength: USERNAME_MAX },
            }}
            helperText={`${form.username.length}/${USERNAME_MAX} caracteres (mín. ${USERNAME_MIN})`}
          />
          <TextField
            label="Correo Electrónico"
            fullWidth
            value={form.email}
            onChange={(e) => handleChange("email", e.target.value)}
            disabled={loading}
          />
          <TextField
            label="Contraseña"
            type={showPassword ? "text" : "password"}
            fullWidth
            value={form.password}
            onChange={(e) => handleChange("password", e.target.value)}
            disabled={loading}
            slotProps={{
              input: {
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowPassword((prev) => !prev)}
                      edge="end"
                      aria-label={
                        showPassword
                          ? "Ocultar contraseña"
                          : "Mostrar contraseña"
                      }
                    >
                      {showPassword ? (
                        <VisibilityOffRounded sx={{ color: "info.main" }} />
                      ) : (
                        <VisibilityRounded
                          sx={{ color: `${alpha("#77ffc0", 0.12)}` }}
                        />
                      )}
                    </IconButton>
                  </InputAdornment>
                ),
              },
            }}
          />

          {form.password.length > 0 && (
            <Box
              sx={{
                border: "1px solid",
                borderColor: "divider",
                borderRadius: 2,
                px: 2,
                py: 1.5,
                bgcolor: (theme) => alpha(theme.palette.primary.dark, 0.4),
              }}
            >
              <Typography
                variant="caption"
                color="text.secondary"
                sx={{
                  letterSpacing: 0.5,
                  textTransform: "uppercase",
                  fontWeight: 600,
                }}
              >
                Requisitos de contraseña
              </Typography>
              <Stack spacing={0.75} sx={{ mt: 1 }}>
                {passwordRules.map((rule) => {
                  const met = rule.test(form.password);
                  return (
                    <Stack
                      key={rule.label}
                      direction="row"
                      sx={{ alignItems: "center", gap: 1 }}
                    >
                      {met ? (
                        <CheckCircleRounded
                          sx={{ fontSize: 15, color: "success.main" }}
                        />
                      ) : (
                        <RadioButtonUncheckedRounded
                          sx={{ fontSize: 15, color: "text.secondary" }}
                        />
                      )}
                      <Typography
                        variant="caption"
                        sx={{
                          color: met ? "success.main" : "text.secondary",
                          transition: "color 0.2s",
                        }}
                      >
                        {rule.label}
                      </Typography>
                    </Stack>
                  );
                })}
              </Stack>
            </Box>
          )}
          <TextField
            label="Número de Celular"
            fullWidth
            value={form.cellPhone}
            onChange={(e) => handleChange("cellPhone", e.target.value)}
            disabled={loading}
            placeholder="+52 999 123 4567"
            helperText="Formato internacional admitido: +52 999 123 4567"
          />
          <TextField
            select
            label="Rol de Usuario"
            fullWidth
            value={form.userRole}
            onChange={(e) =>
              handleChange("userRole", e.target.value as ManagedUserRole)
            }
            disabled={loading}
          >
            <MenuItem value="DEVELOPER">Desarrollador</MenuItem>
            <MenuItem value="MANAGER">Manager</MenuItem>
            <MenuItem value="ADMIN">Administrador</MenuItem>
          </TextField>
        </Stack>
      </DialogContent>

      <DialogActions>
        <Button
          onClick={onClose}
          variant="outlined"
          disabled={loading}
          sx={{
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
          Cancelar
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={loading}
          sx={{ "&:hover": { color: alpha("#b4bdc7", 0.8) } }}
        >
          {loading ? <CircularProgress size={22} color="inherit" /> : "Crear"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CreateUserDialog;
