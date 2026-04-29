import { useEffect, useMemo, useState } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { CloseRounded, RestoreRounded } from "@mui/icons-material";
import type {
  SprintDialogMode,
  SprintItem,
  CreateSprintPayload,
  UpdateSprintPayload,
} from "../types/sprint.types";

interface SprintFormDialogProps {
  open: boolean;
  mode: SprintDialogMode;
  sprint?: SprintItem | null;
  onClose: () => void;
  onSubmitCreate: (payload: CreateSprintPayload) => Promise<void>;
  onSubmitUpdate: (
    sprintId: string,
    payload: UpdateSprintPayload,
  ) => Promise<void>;
}

interface SprintFormState {
  name: string;
  startDate: string;
  endDate: string;
  status: string;
}

const toFormState = (sprint?: SprintItem | null): SprintFormState => ({
  name: sprint?.name ?? "",
  startDate: sprint?.startDate ?? "",
  endDate: sprint?.endDate ?? "",
  status: sprint?.status ?? "PLANNED",
});

const STATUS_TRANSITIONS: Record<string, string[]> = {
  PLANNED: ["PLANNED", "ACTIVE"],
  ACTIVE: ["ACTIVE"],
  CLOSED: ["CLOSED"],
};

const STATUS_LABELS: Record<string, string> = {
  PLANNED: "Planificado",
  ACTIVE: "Activo",
  CLOSED: "Cerrado",
};

const SPRINT_NAME_REGEX = /\d/;
const MIN_DAYS = 7;
const MAX_DAYS = 28;

const daysBetween = (start: string, end: string): number => {
  const s = new Date(start);
  const e = new Date(end);
  return Math.round((e.getTime() - s.getTime()) / (1000 * 60 * 60 * 24));
};

const SprintFormDialog = ({
  open,
  mode,
  sprint,
  onClose,
  onSubmitCreate,
  onSubmitUpdate,
}: SprintFormDialogProps) => {
  const [form, setForm] = useState<SprintFormState>(toFormState(sprint));
  const [original, setOriginal] = useState<SprintFormState>(
    toFormState(sprint),
  );
  const [errorMsg, setErrorMsg] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [confirmDiscardOpen, setConfirmDiscardOpen] = useState(false);

  useEffect(() => {
    const s = toFormState(sprint);
    setForm(s);
    setOriginal(s);
    setErrorMsg("");
    setConfirmDiscardOpen(false);
  }, [open, sprint, mode]);

  const hasChanges = useMemo(
    () => JSON.stringify(form) !== JSON.stringify(original),
    [form, original],
  );

  const allowedStatuses =
    mode === "edit"
      ? (STATUS_TRANSITIONS[sprint?.status ?? "PLANNED"] ?? ["PLANNED"])
      : ["PLANNED"];

  const isStatusLocked = mode === "edit" && sprint?.status === "ACTIVE";

  const setField = <K extends keyof SprintFormState>(
    key: K,
    value: SprintFormState[K],
  ) => setForm((p) => ({ ...p, [key]: value }));

  const validate = (): boolean => {
    if (!form.name.trim()) {
      setErrorMsg("El nombre del sprint es obligatorio.");
      return false;
    }
    if (!SPRINT_NAME_REGEX.test(form.name)) {
      setErrorMsg(
        "El nombre debe incluir un indicador numérico (ej. 'Sprint 1').",
      );
      return false;
    }
    if (form.name.length > 255) {
      setErrorMsg("El nombre no puede superar 255 caracteres.");
      return false;
    }
    if (!form.startDate) {
      setErrorMsg("La fecha de inicio es obligatoria.");
      return false;
    }
    if (!form.endDate) {
      setErrorMsg("La fecha de cierre es obligatoria.");
      return false;
    }
    const diff = daysBetween(form.startDate, form.endDate);
    if (diff < MIN_DAYS) {
      setErrorMsg("La duración mínima del sprint es de 1 semana (7 días).");
      return false;
    }
    if (diff > MAX_DAYS) {
      setErrorMsg("La duración máxima del sprint es de 4 semanas (28 días).");
      return false;
    }
    setErrorMsg("");
    return true;
  };

  const handleRequestClose = () => {
    if (submitting) return;
    if (hasChanges) {
      setConfirmDiscardOpen(true);
      return;
    }
    onClose();
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    setSubmitting(true);
    try {
      if (mode === "create") {
        await onSubmitCreate({
          projectId: "",
          name: form.name.trim(),
          startDate: form.startDate,
          endDate: form.endDate,
        });
      } else if (sprint) {
        const payload: UpdateSprintPayload = {
          name: form.name.trim(),
          startDate: form.startDate || null,
          endDate: form.endDate || null,
        };
        if (form.status !== original.status) {
          payload.status = form.status as UpdateSprintPayload["status"];
        }
        await onSubmitUpdate(sprint.sprintId, payload);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <Dialog
        open={open}
        onClose={submitting ? undefined : handleRequestClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle sx={{ p: 2, bgcolor: "primary.dark" }}>
          <Stack
            direction="row"
            sx={{ alignItems: "center", justifyContent: "space-between" }}
          >
            <Typography variant="h5">
              {mode === "create" ? "Crear Sprint" : "Editar Sprint"}
            </Typography>
            <Stack direction="row" spacing={1}>
              {hasChanges && mode === "edit" && (
                <Tooltip title="Restaurar cambios">
                  <IconButton
                    onClick={() => {
                      setForm(original);
                      setErrorMsg("");
                    }}
                    disabled={submitting}
                    color="info"
                  >
                    <RestoreRounded />
                  </IconButton>
                </Tooltip>
              )}
              <Tooltip title="Cerrar">
                <IconButton onClick={handleRequestClose} disabled={submitting}>
                  <CloseRounded />
                </IconButton>
              </Tooltip>
            </Stack>
          </Stack>
        </DialogTitle>

        <DialogContent dividers>
          <Stack spacing={3} sx={{ pt: 1 }}>
            <TextField
              fullWidth
              label="Nombre del sprint"
              placeholder="Ej. Sprint 1"
              value={form.name}
              onChange={(e) => setField("name", e.target.value)}
              helperText="Debe incluir un número indicador (ej. 'Sprint 1', 'Q2 Sprint 3')."
              disabled={submitting}
            />

            <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
              <TextField
                fullWidth
                label="Fecha de inicio"
                type="date"
                value={form.startDate}
                onChange={(e) => setField("startDate", e.target.value)}
                slotProps={{ inputLabel: { shrink: true } }}
                disabled={submitting}
              />
              <TextField
                fullWidth
                label="Fecha de cierre"
                type="date"
                value={form.endDate}
                onChange={(e) => setField("endDate", e.target.value)}
                slotProps={{ inputLabel: { shrink: true } }}
                helperText="Duración: 1-4 semanas."
                disabled={submitting}
              />
            </Stack>

            {mode === "edit" && (
              <TextField
                fullWidth
                select
                label="Estado"
                value={form.status}
                onChange={(e) => setField("status", e.target.value)}
                disabled={submitting || isStatusLocked}
                helperText={
                  isStatusLocked
                    ? "Para cerrar el sprint usa el botón 'Cerrar Sprint' en la página principal."
                    : undefined
                }
              >
                {allowedStatuses.map((s) => (
                  <MenuItem key={s} value={s}>
                    {STATUS_LABELS[s]}
                  </MenuItem>
                ))}
              </TextField>
            )}

            {mode === "create" && (
              <Box
                sx={{
                  p: 1.5,
                  borderRadius: 2,
                  bgcolor: "primary.dark",
                  border: "1px solid",
                  borderColor: "divider",
                }}
              >
                <Typography variant="caption" color="text.secondary">
                  El estado inicial de todo sprint es{" "}
                  <strong>Planificado</strong>. Puedes activarlo después desde
                  la vista de edición.
                </Typography>
              </Box>
            )}

            {errorMsg && <Alert severity="error">{errorMsg}</Alert>}
          </Stack>
        </DialogContent>

        <DialogActions sx={{ p: 2 }}>
          <Button onClick={handleRequestClose} disabled={submitting} variant="outlined" color="error">
            Cancelar
          </Button>
          <Button
            variant="contained"
            onClick={handleSubmit}
            disabled={submitting || !hasChanges}
            startIcon={submitting ? <CircularProgress size={16} /> : undefined}
          >
            {mode === "create" ? "Crear Sprint" : "Guardar Cambios"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Discard confirmation */}
      <Dialog open={confirmDiscardOpen} maxWidth="xs" fullWidth>
        <DialogTitle>¿Descartar cambios?</DialogTitle>
        <DialogContent>
          <Typography>
            El sprint no ha sido guardado. Si sales ahora, la información ingresada se perderá. ¿Deseas salir sin guardar?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button 
            onClick={() => setConfirmDiscardOpen(false)}  variant="outlined"
            color="info"
          >
            Continuar editando
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => {
              setConfirmDiscardOpen(false);
              onClose();
            }}
          >
            Salir sin guardar
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default SprintFormDialog;
