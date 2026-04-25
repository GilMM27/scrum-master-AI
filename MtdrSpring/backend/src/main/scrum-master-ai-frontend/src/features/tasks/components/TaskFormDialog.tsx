import { useEffect, useMemo, useState } from "react";
import { CloseRounded, EditRounded, RestoreRounded, DeleteRounded } from "@mui/icons-material";
import CircularProgress from "@mui/material/CircularProgress";
import type { CreateTaskPayload, SprintOption, TaskAssignee, TaskDialogMode, TaskItem, TaskPriority, TaskStatus, UpdateTaskPayload } from "../types/tasks.types";
import TaskPriorityChip from "./TaskPriorityChip";
import TaskStatusChip from "./TaskStatusChip";
import Dialog from "@mui/material/Dialog";
import { Alert, Box, Button, Checkbox, Chip, DialogActions, DialogContent, DialogTitle, FormControlLabel, Grid, IconButton, MenuItem, Stack, TextField, Tooltip, Typography } from "@mui/material";

interface TaskFormDialogProps {
  open: boolean;
  mode: TaskDialogMode;
  task?: TaskItem | null;
  developers: TaskAssignee[];
  sprints: SprintOption[];
  loading?: boolean;
  onClose: () => void;
  onDeleteRequest: (task: TaskItem) => void;
  onSubmitCreate: (payload: CreateTaskPayload) => Promise<void>;
  onSubmitUpdate: (taskId: string, payload: UpdateTaskPayload) => Promise<void>;
}

interface TaskFormState {
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeIds: string[];
  sprintId: string | null;
  storyPoints: number | null;
}

const createInitialState = (task?: TaskItem | null): TaskFormState => ({
  title: task?.title ?? "",
  description: task?.description ?? "",
  status: task?.status ?? "TO_DO",
  priority: task?.priority ?? "LOW",
  assigneeIds: task?.assignees.map((a) => a.userId) ?? [],
  sprintId: task?.sprintId ?? null,
  storyPoints:
    task?.storyPoints === null || task?.storyPoints === undefined
      ? null
      : task.storyPoints,
});

const formatSprintDate = (iso: string | null): string => {
  if (!iso) return "";
  const [year, month, day] = iso.split("-");
  const months = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
  return `${parseInt(day)} ${months[parseInt(month) - 1]} ${year}`;
};

const getSprintLabel = (sprint: SprintOption): string =>
  sprint.name ?? `Sprint (${sprint.sprintId.slice(0, 6)}…)`;

const VALID_STATUS_TRANSITIONS: Record<TaskStatus, TaskStatus[]> = {
  TO_DO: ["IN_PROGRESS"],
  IN_PROGRESS: ["REVIEW", "BLOCKED"],
  REVIEW: ["IN_PROGRESS", "BLOCKED", "DONE"],
  BLOCKED: ["IN_PROGRESS", "REVIEW"],
  DONE: [],
};

const TaskFormDialog = ({
  open,
  mode,
  task,
  developers,
  sprints,
  loading = false,
  onClose,
  onDeleteRequest,
  onSubmitCreate,
  onSubmitUpdate,
}: TaskFormDialogProps) => {
  const [internalMode, setInternalMode] = useState<TaskDialogMode>(mode);
  const [form, setForm] = useState<TaskFormState>(createInitialState(task));
  const [originalForm, setOriginalForm] = useState<TaskFormState>(
    createInitialState(task),
  );
  const [errorMsg, setErrorMsg] = useState("");
  const [confirmDiscardOpen, setConfirmDiscardOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const initial = createInitialState(task);
    setInternalMode(mode);
    setForm(initial);
    setOriginalForm(initial);
    setErrorMsg("");
    setConfirmDiscardOpen(false);
  }, [mode, task, open]);

  const isReadOnly = internalMode === "view";

  const hasUnsavedChanges = useMemo(
    () => JSON.stringify(form) !== JSON.stringify(originalForm),
    [form, originalForm],
  );

  // View mode: derive display values directly from the task prop (comes from API with full data)
  const viewAssignees = task?.assignees ?? [];
  const viewSprintLabel = task?.sprintName ?? "Backlog";

  // In edit mode, only allow valid transitions from the original status.
  // In create mode, all statuses are available.
  const allowedStatuses: Set<TaskStatus> = useMemo(() => {
    if (internalMode === "edit" && originalForm.status) {
      const transitions = VALID_STATUS_TRANSITIONS[originalForm.status];
      return new Set([originalForm.status, ...transitions]);
    }
    return new Set<TaskStatus>([
      "TO_DO",
      "IN_PROGRESS",
      "REVIEW",
      "DONE",
      "BLOCKED",
    ]);
  }, [internalMode, originalForm.status]);

  const handleFieldChange = <K extends keyof TaskFormState>(
    key: K,
    value: TaskFormState[K],
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const validateForm = () => {
    if (!form.title.trim()) {
      setErrorMsg("El título es obligatorio.");
      return false;
    }

    if (form.storyPoints !== null) {
      const value = form.storyPoints;

      if (Number.isNaN(value) || value < 0 || value > 4) {
        setErrorMsg(
          "Las horas estimadas deben ser un número entre 0 y 4. Considera subdividir la tarea en subtareas si requiere más tiempo.",
        );
        return false;
      }
    }

    setErrorMsg("");
    return true;
  };

  const handleRequestClose = () => {
    if (submitting) return;
    if (
      (internalMode === "create" || internalMode === "edit") &&
      hasUnsavedChanges
    ) {
      setConfirmDiscardOpen(true);
      return;
    }

    onClose();
  };

  const handleSubmit = async () => {
    if (isReadOnly) return;
    if (!validateForm()) return;

    const payload: CreateTaskPayload | UpdateTaskPayload = {
      title: form.title.trim(),
      description: form.description.trim(),
      status: form.status,
      priority: form.priority,
      assigneeIds: form.assigneeIds,
      sprintId: form.sprintId,
      storyPoints: form.storyPoints,
    };

    setSubmitting(true);
    try {
      if (internalMode === "create") {
        await onSubmitCreate(payload as CreateTaskPayload);
        return;
      }

      if (internalMode === "edit" && task) {
        await onSubmitUpdate(task.taskId, payload as UpdateTaskPayload);
        return;
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleRestore = () => {
    setForm(originalForm);
    setErrorMsg("");
  };

  return (
    <>
      <Dialog
        open={open}
        onClose={loading ? undefined : handleRequestClose}
        maxWidth="lg"
        fullWidth
      >
        <DialogTitle sx={{ p: 2, bgcolor: "primary.dark" }}>
          <Stack
            direction="row"
            sx={{ alignItems: "center", justifyContent: "space-between" }}
            spacing={2}
          >
            <Stack direction="row" spacing={1} sx={{ alignItems: "center" }}>
              <Typography variant="h5">
                {internalMode === "create"
                  ? "Crear Tarea"
                  : internalMode === "edit"
                    ? "Editar Tarea"
                    : form.title || "Sin título"}
              </Typography>
            </Stack>

            <Stack direction="row" spacing={1}>
              {internalMode === "view" && task && (
                <>
                  <Tooltip title="Editar tarea">
                    <IconButton onClick={() => setInternalMode("edit")}>
                      <EditRounded />
                    </IconButton>
                  </Tooltip>

                  <Tooltip title="Eliminar tarea">
                    <IconButton
                      color="error"
                      onClick={() => onDeleteRequest(task)}
                    >
                      <DeleteRounded />
                    </IconButton>
                  </Tooltip>
                </>
              )}

              <Tooltip title="Cerrar">
                <IconButton onClick={handleRequestClose}>
                  <CloseRounded />
                </IconButton>
              </Tooltip>
            </Stack>
          </Stack>
        </DialogTitle>

        <DialogContent dividers>
          {loading && !task ? (
            <Stack
              sx={{
                alignItems: "center",
                justifyContent: "center",
                py: 8,
                gap: 2,
              }}
            >
              <CircularProgress />
              <Typography color="text.secondary">
                Cargando detalles de tarea…
              </Typography>
            </Stack>
          ) : (
            <Stack spacing={3}>
              <Stack spacing={1.5}>
                {!isReadOnly && (
                  <TextField
                    fullWidth
                    label="Título de la tarea"
                    value={form.title}
                    onChange={(e) => handleFieldChange("title", e.target.value)}
                  />
                )}
              </Stack>

              <Grid container spacing={3}>
                <Grid size={{ xs: 12, lg: 7 }}>
                  <Stack spacing={1.25}>
                    <Typography variant="subtitle2">
                      Información principal
                    </Typography>

                    {isReadOnly ? (
                      <Box
                        sx={{
                          minHeight: 320,
                          p: 2,
                          borderRadius: 3,
                          border: "1px solid",
                          borderColor: "divider",
                          backgroundColor: "background.default",
                        }}
                      >
                        <Typography
                          variant="body1"
                          sx={{ whiteSpace: "pre-line" }}
                        >
                          {form.description || "Sin descripción"}
                        </Typography>
                      </Box>
                    ) : (
                      <TextField
                        fullWidth
                        label="Descripción"
                        multiline
                        minRows={13.4}
                        value={form.description}
                        onChange={(e) =>
                          handleFieldChange("description", e.target.value)
                        }
                      />
                    )}
                  </Stack>
                </Grid>

                <Grid size={{ xs: 12, lg: 5 }}>
                  <Stack spacing={1.25}>
                    <Typography variant="subtitle2">
                      Detalles de la tarea
                    </Typography>

                    {isReadOnly ? (
                      <>
                        <Stack spacing={0.75} sx={{ alignItems: "flex-start" }}>
                          <Typography variant="caption" color="text.secondary">
                            Estado
                          </Typography>
                          <TaskStatusChip status={form.status} />
                        </Stack>

                        <Stack spacing={0.75} sx={{ alignItems: "flex-start" }}>
                          <Typography variant="caption" color="text.secondary">
                            Prioridad
                          </Typography>
                          <TaskPriorityChip priority={form.priority} />
                        </Stack>

                        <Stack spacing={0.75}>
                          <Typography variant="caption" color="text.secondary">
                            Desarrolladores Asignados
                          </Typography>
                          <Stack
                            direction="row"
                            spacing={1}
                            useFlexGap
                            sx={{ flexWrap: "wrap" }}
                          >
                            {viewAssignees.length > 0 ? (
                              viewAssignees.map((a) => (
                                <Chip
                                  key={a.userId}
                                  label={a.username}
                                  size="small"
                                  variant="outlined"
                                />
                              ))
                            ) : (
                              <Typography
                                variant="body2"
                                color="text.secondary"
                              >
                                Sin asignados
                              </Typography>
                            )}
                          </Stack>
                        </Stack>

                        <Stack spacing={0.75} sx={{ alignItems: "flex-start" }}>
                          <Typography variant="caption" color="text.secondary">
                            Sprint
                          </Typography>
                          <Chip
                            label={viewSprintLabel}
                            color={task?.sprintName ? "info" : "default"}
                            size="small"
                            variant="outlined"
                          />
                        </Stack>

                        <Stack spacing={0.75}>
                          <Typography variant="caption" color="text.secondary">
                            Horas Estimadas
                          </Typography>
                          <Typography>
                            {task?.storyPoints != null && task.storyPoints > 0
                              ? task.storyPoints
                              : "No definidas"}
                          </Typography>
                        </Stack>
                      </>
                    ) : (
                      <>
                        <TextField
                          select
                          fullWidth
                          label="Status"
                          value={form.status}
                          onChange={(e) =>
                            handleFieldChange(
                              "status",
                              e.target.value as TaskStatus,
                            )
                          }
                        >
                          <MenuItem
                            value="TO_DO"
                            disabled={!allowedStatuses.has("TO_DO")}
                          >
                            To Do
                          </MenuItem>
                          <MenuItem
                            value="IN_PROGRESS"
                            disabled={!allowedStatuses.has("IN_PROGRESS")}
                          >
                            In Progress
                          </MenuItem>
                          <MenuItem
                            value="REVIEW"
                            disabled={!allowedStatuses.has("REVIEW")}
                          >
                            In Review
                          </MenuItem>
                          <MenuItem
                            value="BLOCKED"
                            disabled={!allowedStatuses.has("BLOCKED")}
                          >
                            Blocked
                          </MenuItem>
                          <MenuItem
                            value="DONE"
                            disabled={!allowedStatuses.has("DONE")}
                          >
                            Done
                          </MenuItem>
                        </TextField>

                        <TextField
                          select
                          fullWidth
                          label="Priority"
                          value={form.priority}
                          onChange={(e) =>
                            handleFieldChange(
                              "priority",
                              e.target.value as TaskPriority,
                            )
                          }
                        >
                          <MenuItem value="LOW">Low</MenuItem>
                          <MenuItem value="MEDIUM">Medium</MenuItem>
                          <MenuItem value="HIGH">High</MenuItem>
                          <MenuItem value="CRITICAL">Critical</MenuItem>
                        </TextField>

                        {/* Developers — checkbox list (multi-select) */}
                        <Stack spacing={0.5}>
                          <Typography variant="caption" color="text.secondary">
                            Desarrolladores
                          </Typography>
                          {developers.length === 0 ? (
                            <Typography variant="body2" color="text.secondary">
                              Sin desarrolladores disponibles
                            </Typography>
                          ) : (
                            developers.map((dev) => (
                              <FormControlLabel
                                key={dev.userId}
                                label={dev.username}
                                control={
                                  <Checkbox
                                    size="small"
                                    checked={form.assigneeIds.includes(
                                      dev.userId,
                                    )}
                                    onChange={(e) => {
                                      const ids = e.target.checked
                                        ? [...form.assigneeIds, dev.userId]
                                        : form.assigneeIds.filter(
                                            (id) => id !== dev.userId,
                                          );
                                      handleFieldChange("assigneeIds", ids);
                                    }}
                                    sx={{
                                      color: "info.main",
                                      "&.Mui-checked": { color: "info.main" },
                                    }}
                                  />
                                }
                              />
                            ))
                          )}
                        </Stack>

                        {/* Sprint — single-select checkbox list (mutually exclusive) */}
                        <Stack spacing={0.5}>
                          <Typography variant="caption" color="text.secondary">
                            Sprint
                          </Typography>

                          <FormControlLabel
                            label="Backlog"
                            control={
                              <Checkbox
                                size="small"
                                checked={form.sprintId === null}
                                onChange={() =>
                                  handleFieldChange("sprintId", null)
                                }
                                sx={{
                                  color: "info.main",
                                  "&.Mui-checked": { color: "info.main" },
                                }}
                              />
                            }
                          />

                          {sprints.map((sprint) => (
                            <FormControlLabel
                              key={sprint.sprintId}
                              label={
                                <Stack spacing={0}>
                                  <Stack
                                    direction="row"
                                    spacing={0.75}
                                    sx={{ alignItems: "center" }}
                                  >
                                    <Typography variant="body2">
                                      {getSprintLabel(sprint)}
                                    </Typography>
                                    {sprint.status === "ACTIVE" && (
                                      <Chip
                                        label="Activo"
                                        size="small"
                                        color="success"
                                        variant="outlined"
                                        sx={{
                                          height: 18,
                                          fontSize: "0.65rem",
                                          "& .MuiChip-label": { px: 0.75 },
                                        }}
                                      />
                                    )}
                                  </Stack>
                                  {sprint.startDate && sprint.endDate && (
                                    <Typography
                                      variant="caption"
                                      color="text.secondary"
                                    >
                                      {formatSprintDate(sprint.startDate)}
                                      {" - "}
                                      {formatSprintDate(sprint.endDate)}
                                    </Typography>
                                  )}
                                </Stack>
                              }
                              control={
                                <Checkbox
                                  size="small"
                                  checked={form.sprintId === sprint.sprintId}
                                  onChange={() =>
                                    handleFieldChange(
                                      "sprintId",
                                      sprint.sprintId,
                                    )
                                  }
                                  sx={{
                                    color:
                                      sprint.status === "ACTIVE"
                                        ? "success.main"
                                        : "info.main",
                                    "&.Mui-checked": {
                                      color:
                                        sprint.status === "ACTIVE"
                                          ? "success.main"
                                          : "info.main",
                                    },
                                  }}
                                />
                              }
                            />
                          ))}

                          {sprints.length === 0 && (
                            <Typography variant="body2" color="text.secondary">
                              Sin sprints disponibles
                            </Typography>
                          )}
                        </Stack>

                        <TextField
                          fullWidth
                          label="Horas Estimadas"
                          type="number"
                          slotProps={{
                            htmlInput: { min: 0, max: 4, step: 0.5 },
                          }}
                          value={form.storyPoints ?? ""}
                          onChange={(e) =>
                            handleFieldChange(
                              "storyPoints",
                              e.target.value === ""
                                ? null
                                : Number(e.target.value),
                            )
                          }
                          helperText="Máximo 4 horas. Si excede este límite, subdivide la tarea."
                        />
                      </>
                    )}
                  </Stack>
                </Grid>
              </Grid>
            </Stack>
          )}
        </DialogContent>

        {!isReadOnly && (
          <DialogActions sx={{ px: 3, py: 2 }}>
            <Stack
              direction="row"
              spacing={1.5}
              sx={{
                alignItems: "center",
                justifyContent: "space-between",
                width: "100%",
              }}
            >
              <Box>
                {internalMode === "edit" && (
                  <Button
                    variant="outlined"
                    startIcon={<RestoreRounded />}
                    onClick={handleRestore}
                    disabled={!hasUnsavedChanges || loading || submitting}
                  >
                    Restaurar
                  </Button>
                )}
              </Box>

              {errorMsg && <Alert severity="error">{errorMsg}</Alert>}

              <Stack direction="row" spacing={1.5}>
                <Button
                  variant="contained"
                  onClick={handleSubmit}
                  disabled={loading || submitting}
                  startIcon={
                    submitting ? (
                      <CircularProgress size={16} color="inherit" />
                    ) : undefined
                  }
                >
                  {submitting
                    ? internalMode === "create"
                      ? "Subiendo tarea…"
                      : "Actualizando tarea…"
                    : internalMode === "create"
                      ? "Guardar tarea"
                      : "Guardar cambios"}
                </Button>
              </Stack>
            </Stack>
          </DialogActions>
        )}
      </Dialog>

      <Dialog
        open={confirmDiscardOpen}
        onClose={() => setConfirmDiscardOpen(false)}
        maxWidth="xs"
        fullWidth
      >
        <DialogTitle>Descartar cambios</DialogTitle>

        <DialogContent>
          <Typography>
            La tarea no ha sido guardada. Si sales ahora, la información
            capturada se perderá.
          </Typography>
        </DialogContent>

        <DialogActions>
          <Button
            variant="outlined"
            onClick={() => setConfirmDiscardOpen(false)}
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

export default TaskFormDialog;
