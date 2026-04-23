import { useEffect, useMemo, useState } from "react";
import {
  CloseRounded,
  EditRounded,
  RestoreRounded,
  DeleteRounded,
} from "@mui/icons-material";
import type {
  CreateTaskPayload,
  SprintOption,
  TaskAssignee,
  TaskDialogMode,
  TaskItem,
  TaskPriority,
  TaskStatus,
  UpdateTaskPayload,
} from "../types/tasks.types";
import TaskPriorityChip from "./TaskPriorityChip";
import TaskStatusChip from "./TaskStatusChip";
import Dialog from "@mui/material/Dialog";
import {
  Alert,
  Box,
  Button,
  Chip,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  MenuItem,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";

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
  estimatedHours: number | null;
}

const createInitialState = (task?: TaskItem | null): TaskFormState => ({
  title: task?.title ?? "",
  description: task?.description ?? "",
  status: task?.status ?? "TODO",
  priority: task?.priority ?? "LOW",
  assigneeIds: task?.assignees.map((a) => a.userId) ?? [],
  sprintId: task?.sprintId ?? null,
  estimatedHours:
    task?.estimatedHours === null || task?.estimatedHours === undefined
      ? null
      : task.estimatedHours,
});

const getSprintLabel = (sprint?: SprintOption) =>
  sprint ? `Sprint ${sprint.sprintNumber}` : "Sin sprint";

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

  const selectedSprint = sprints.find((s) => s.sprintId === form.sprintId);
  const selectedDeveloperNames = developers
    .filter((dev) => form.assigneeIds.includes(dev.userId))
    .map((dev) => dev.username);

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

    if (!form.description.trim()) {
      setErrorMsg("La descripción es obligatoria.");
      return false;
    }

    if (form.estimatedHours !== null) {
      const value = form.estimatedHours;

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
      sprintId: form.sprintId ?? undefined,
      estimatedHours: form.estimatedHours ?? undefined,
    };

    if (internalMode === "create") {
      await onSubmitCreate(payload as CreateTaskPayload);
      return;
    }

    if (internalMode === "edit" && task) {
      await onSubmitUpdate(task.taskId, payload as UpdateTaskPayload);
      return;
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
              <Typography variant="h6">
                {internalMode === "create" ? (
                  "Crear Tarea"
                ) : internalMode === "edit" ? (
                  "Editar Tarea"
                ) : (
                  <Typography variant="h5">
                    {form.title || "Sin título"}
                  </Typography>
                )}
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
                      minRows={14}
                      value={form.description}
                      onChange={(e) =>
                        handleFieldChange("description", e.target.value)
                      }
                    />
                  )}
                </Stack>
              </Grid>

              <Grid size={{ xs: 12, lg: 5 }}>
                <Stack spacing={2}>
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
                          Asignados
                        </Typography>
                        <Stack
                          direction="row"
                          spacing={1}
                          useFlexGap
                          sx={{ flexWrap: "wrap" }}
                        >
                          {selectedDeveloperNames.length > 0 ? (
                            selectedDeveloperNames.map((name) => (
                              <Chip
                                key={name}
                                label={name}
                                size="small"
                                variant="outlined"
                              />
                            ))
                          ) : (
                            <Typography variant="body2" color="text.secondary">
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
                          label={getSprintLabel(selectedSprint)}
                          color={selectedSprint ? "info" : "default"}
                          size="small"
                          variant="outlined"
                        />
                      </Stack>

                      <Stack spacing={0.75}>
                        <Typography variant="caption" color="text.secondary">
                          Horas Estimadas
                        </Typography>
                        <Typography>
                          {form.estimatedHours !== null
                            ? `${form.estimatedHours}h`
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
                        <MenuItem value="TODO">To Do</MenuItem>
                        <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
                        <MenuItem value="IN_REVIEW">In Review</MenuItem>
                        <MenuItem value="DONE">Done</MenuItem>
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

                      <TextField
                        select
                        fullWidth
                        label="Assignee"
                        value={form.assigneeIds}
                        slotProps={{ select: { multiple: true } }}
                        onChange={(e) =>
                          handleFieldChange(
                            "assigneeIds",
                            e.target.value as unknown as string[],
                          )
                        }
                      >
                        {developers.map((developer) => (
                          <MenuItem
                            key={developer.userId}
                            value={developer.userId}
                          >
                            {developer.username}
                          </MenuItem>
                        ))}
                      </TextField>

                      <TextField
                        select
                        fullWidth
                        label="Sprint"
                        value={form.sprintId ?? ""}
                        onChange={(e) =>
                          handleFieldChange("sprintId", e.target.value || null)
                        }
                      >
                        <MenuItem value="">Backlog</MenuItem>
                        {sprints.map((sprint) => (
                          <MenuItem
                            key={sprint.sprintId}
                            value={sprint.sprintId}
                          >
                            {getSprintLabel(sprint)}
                          </MenuItem>
                        ))}
                      </TextField>

                      <TextField
                        fullWidth
                        label="Horas estimadas"
                        type="number"
                        slotProps={{ htmlInput: { min: 0, max: 4, step: 0.5 } }}
                        value={form.estimatedHours ?? ""}
                        onChange={(e) =>
                          handleFieldChange(
                            "estimatedHours",
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
                    disabled={!hasUnsavedChanges || loading}
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
                  disabled={loading}
                >
                  {internalMode === "create"
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
