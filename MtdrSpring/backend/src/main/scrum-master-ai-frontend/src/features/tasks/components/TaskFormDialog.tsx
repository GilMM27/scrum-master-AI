import { useEffect, useMemo, useState } from "react";
import {
  CloseRounded,
  EditRounded,
  RestoreRounded,
  DeleteRounded,
} from "@mui/icons-material";
import CircularProgress from "@mui/material/CircularProgress";
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
  storyPoints: number | null;
}

const createInitialState = (task?: TaskItem | null): TaskFormState => ({
  title: task?.title ?? "",
  description: task?.description ?? "",
  status: task?.status ?? "TODO",
  priority: task?.priority ?? "LOW",
  assigneeIds: task?.assignees.map((a) => a.userId) ?? [],
  sprintId: task?.sprintId ?? null,
  storyPoints:
    task?.storyPoints === null || task?.storyPoints === undefined
      ? null
      : task.storyPoints,
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

  // View mode: derive display values directly from the task prop (comes from API with full data)
  const viewAssignees = task?.assignees ?? [];
  const viewSprintLabel = task?.sprintName ?? "Backlog";

  // Edit / create mode: derive display values from form state + lookup lists
  const selectedSprint = sprints.find((s) => s.sprintId === form.sprintId);
  const selectedDevelopers = developers.filter((d) =>
    form.assigneeIds.includes(d.userId),
  );

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
      storyPoints: form.storyPoints ?? undefined,
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
                              : "No definidos"}
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
                          <MenuItem value="REVIEW">In Review</MenuItem>
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
                          label="Desarrolladores"
                          value={form.assigneeIds}
                          slotProps={{ select: { multiple: true } }}
                          onChange={(e) =>
                            handleFieldChange(
                              "assigneeIds",
                              e.target.value as unknown as string[],
                            )
                          }
                          helperText={
                            selectedDevelopers.length > 0
                              ? selectedDevelopers
                                  .map((d) => d.username)
                                  .join(", ")
                              : "Sin asignados"
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
                            handleFieldChange(
                              "sprintId",
                              e.target.value || null,
                            )
                          }
                          helperText={
                            selectedSprint
                              ? getSprintLabel(selectedSprint)
                              : "Backlog"
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
                          label="Story Points"
                          type="number"
                          slotProps={{ htmlInput: { min: 0, step: 1 } }}
                          value={form.storyPoints ?? ""}
                          onChange={(e) =>
                            handleFieldChange(
                              "storyPoints",
                              e.target.value === ""
                                ? null
                                : Number(e.target.value),
                            )
                          }
                          helperText="Número de story points para esta tarea."
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
