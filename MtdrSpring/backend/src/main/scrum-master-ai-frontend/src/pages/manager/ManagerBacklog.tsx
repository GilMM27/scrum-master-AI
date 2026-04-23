import { useCallback, useEffect, useMemo, useState } from "react";
import { Alert, CircularProgress, Snackbar, Stack, Typography } from "@mui/material";
import DashboardLayout from "../../layouts/DashboardLayout";
import TaskBacklogHeader from "../../features/tasks/components/TaskBacklogHeader";
import TaskFiltersBar from "../../features/tasks/components/TaskFiltersBar";
import TaskStatsChips from "../../features/tasks/components/TaskStatsChips";
import TasksTable from "../../features/tasks/components/TasksTable";
import TaskDeleteConfirmDialog from "../../features/tasks/components/TaskDeleteConfirmDialog";
import { applyTaskFilters } from "../../features/tasks/utils/taskFilters";
import { getTaskStats } from "../../features/tasks/utils/taskStats";
import type { CreateTaskPayload, SprintOption, TaskAssignee, TaskDetailItem, TaskDialogMode, TaskFiltersState, TaskItem, UpdateTaskPayload } from "../../features/tasks/types/tasks.types";
import TaskFormDialog from "../../features/tasks/components/TaskFormDialog";
import { getProjectTasks, getTaskDetails } from "../../features/tasks/services/tasks.service";
import useProject from "../../hooks/useProject";

const initialFilters: TaskFiltersState = {
  search: "",
  status: "",
  priority: "",
  inReview: false,
  blocked: false,
};

const mockDevelopers: TaskAssignee[] = [];
const mockSprints: SprintOption[] = [];

const ManagerBacklogContent = () => {
  const { selectedProjectId } = useProject();

  const [tasks, setTasks] = useState<TaskItem[]>([]);
  const [tasksLoading, setTasksLoading] = useState(false);

  const [filters, setFilters] = useState<TaskFiltersState>(initialFilters);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<TaskDialogMode>("create");
  const [selectedTask, setSelectedTask] = useState<TaskDetailItem | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const [deleteOpen, setDeleteOpen] = useState(false);
  const [taskPendingDelete, setTaskPendingDelete] = useState<TaskItem | null>(
    null,
  );

  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const fetchTasks = useCallback(async () => {
    if (!selectedProjectId) return;
    setTasksLoading(true);
    try {
      const data = await getProjectTasks(selectedProjectId);
      setTasks(data);
    } catch {
      setErrorMsg("No fue posible cargar las tareas del proyecto.");
    } finally {
      setTasksLoading(false);
    }
  }, [selectedProjectId]);

  useEffect(() => {
    setTasks([]);
    fetchTasks();
  }, [fetchTasks]);

  const filteredTasks = useMemo(
    () => applyTaskFilters(tasks, filters),
    [tasks, filters],
  );

  const stats = useMemo(() => getTaskStats(tasks, null), [tasks]);

  const openCreateDialog = () => {
    setDialogMode("create");
    setSelectedTask(null);
    setDialogOpen(true);
  };

  const openViewDialog = async (task: TaskItem) => {
    setDialogMode("view");
    setSelectedTask(null);
    setDialogOpen(true);
    setDetailLoading(true);
    try {
      const detail = await getTaskDetails(task.taskId);
      setSelectedTask(detail);
    } catch {
      setErrorMsg("No fue posible cargar los detalles de la tarea.");
      setDialogOpen(false);
    } finally {
      setDetailLoading(false);
    }
  };

  const handleCreateTask = async (_payload: CreateTaskPayload) => {
    // TODO: connect to create task API
    setSuccessMsg("Tarea creada exitosamente.");
    setDialogOpen(false);
    await fetchTasks();
  };

  const handleUpdateTask = async (
    _taskId: string,
    _payload: UpdateTaskPayload,
  ) => {
    // TODO: connect to update task API
    setSelectedTask(null);
    setSuccessMsg("Tarea actualizada exitosamente.");
    setDialogOpen(false);
    await fetchTasks();
  };

  const handleDeleteRequest = (task: TaskItem) => {
    setTaskPendingDelete(task);
    setDeleteOpen(true);
  };

  const handleDeleteTask = async () => {
    if (!taskPendingDelete) return;
    try {
      // TODO: connect to delete task API
      setTasks((prev) =>
        prev.filter((t) => t.taskId !== taskPendingDelete.taskId),
      );
      setSelectedTask(null);
      setTaskPendingDelete(null);
      setSuccessMsg("La tarea se eliminó correctamente.");
      setDeleteOpen(false);
      setDialogOpen(false);
    } catch {
      setErrorMsg("No fue posible eliminar la tarea.");
    }
  };

  return (
    <>
      <Stack spacing={3}>
        <TaskBacklogHeader onCreateTask={openCreateDialog} />
        <TaskFiltersBar filters={filters} onChange={setFilters} />
        <TaskStatsChips stats={stats} />

        {tasksLoading ? (
          <Stack sx={{ alignItems: "center", py: 6 }}>
            <CircularProgress />
            <Typography color="text.secondary" sx={{ mt: 2 }}>
              Cargando tareas…
            </Typography>
          </Stack>
        ) : (
          <TasksTable
            tasks={filteredTasks}
            onViewTask={openViewDialog}
            showAssignee
            showSprint
          />
        )}
      </Stack>

      <TaskFormDialog
        open={dialogOpen}
        mode={dialogMode}
        task={selectedTask}
        developers={mockDevelopers}
        sprints={mockSprints}
        loading={detailLoading}
        onClose={() => {
          setDialogOpen(false);
          setSelectedTask(null);
        }}
        onDeleteRequest={handleDeleteRequest}
        onSubmitCreate={handleCreateTask}
        onSubmitUpdate={handleUpdateTask}
      />

      <TaskDeleteConfirmDialog
        open={deleteOpen}
        taskTitle={taskPendingDelete?.title}
        onClose={() => {
          setDeleteOpen(false);
          setTaskPendingDelete(null);
        }}
        onConfirm={handleDeleteTask}
      />

      <Snackbar
        open={Boolean(successMsg)}
        autoHideDuration={3500}
        onClose={() => setSuccessMsg("")}
      >
        <Alert onClose={() => setSuccessMsg("")} severity="success">
          {successMsg}
        </Alert>
      </Snackbar>

      <Snackbar
        open={Boolean(errorMsg)}
        autoHideDuration={4000}
        onClose={() => setErrorMsg("")}
      >
        <Alert onClose={() => setErrorMsg("")} severity="error">
          {errorMsg}
        </Alert>
      </Snackbar>
    </>
  );
};

const ManagerBacklog = () => (
  <DashboardLayout>
    <ManagerBacklogContent />
  </DashboardLayout>
);

export default ManagerBacklog;
