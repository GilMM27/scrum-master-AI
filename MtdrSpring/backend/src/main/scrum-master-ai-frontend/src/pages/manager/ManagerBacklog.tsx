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
import { getProjectTasks, getTaskDetails, getProjectDevelopers, getAvailableSprints, createTask, updateTask, deleteTask } from "../../features/tasks/services/tasks.service";
import useProject from "../../hooks/useProject";

const initialFilters: TaskFiltersState = {
  search: "",
  status: "",
  priority: "",
  inReview: false,
  blocked: false,
};

const ManagerBacklogContent = () => {
  const { selectedProjectId } = useProject();

  const [tasks, setTasks] = useState<TaskItem[]>([]);
  const [tasksLoading, setTasksLoading] = useState(false);

  const [developers, setDevelopers] = useState<TaskAssignee[]>([]);
  const [sprints, setSprints] = useState<SprintOption[]>([]);

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

  const fetchProjectData = useCallback(async () => {
    if (!selectedProjectId) {
      setDevelopers([]);
      setSprints([]);
      return;
    }
    try {
      const [devs, availSprints] = await Promise.all([
        getProjectDevelopers(selectedProjectId),
        getAvailableSprints(selectedProjectId),
      ]);
      setDevelopers(devs);
      setSprints(availSprints);
    } catch {
      // non-critical: form will show empty lists
    }
  }, [selectedProjectId]);

  useEffect(() => {
    setTasks([]);
    fetchTasks();
    fetchProjectData();
  }, [fetchTasks, fetchProjectData]);

  const filteredTasks = useMemo(
    () => applyTaskFilters(tasks, filters),
    [tasks, filters],
  );

  const activeSprintId = useMemo(
    () => sprints.find((s) => s.status === "ACTIVE")?.sprintId ?? null,
    [sprints],
  );

  const stats = useMemo(() => getTaskStats(tasks, activeSprintId), [tasks, activeSprintId]);

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

  const handleCreateTask = async (payload: CreateTaskPayload) => {
    if (!selectedProjectId) return;
    try {
      await createTask({ ...payload, projectId: selectedProjectId });
      setSuccessMsg("Tarea creada exitosamente.");
      setDialogOpen(false);
      await fetchTasks();
    } catch {
      setErrorMsg("No fue posible crear la tarea.");
    }
  };

  const handleUpdateTask = async (
    taskId: string,
    payload: UpdateTaskPayload,
  ) => {
    try {
      await updateTask(taskId, payload);
      setSelectedTask(null);
      setSuccessMsg("Tarea actualizada exitosamente.");
      setDialogOpen(false);
      await fetchTasks();
    } catch {
      setErrorMsg("No fue posible actualizar la tarea.");
    }
  };

  const handleDeleteRequest = (task: TaskItem) => {
    setTaskPendingDelete(task);
    setDeleteOpen(true);
  };

  const handleDeleteTask = async () => {
    if (!taskPendingDelete) return;
    try {
      await deleteTask(taskPendingDelete.taskId);
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
        developers={developers}
        sprints={sprints}
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
