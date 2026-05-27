import { useCallback, useEffect, useMemo, useState } from "react";
import { CircularProgress, Stack, Typography } from "@mui/material";
import DashboardLayout from "../../layouts/DashboardLayout";
import TaskBacklogHeader from "../../features/tasks/components/TaskBacklogHeader";
import TaskFiltersBar from "../../features/tasks/components/TaskFiltersBar";
import TaskStatsChips from "../../features/tasks/components/TaskStatsChips";
import TasksTable from "../../features/tasks/components/TasksTable";
import TaskDeleteConfirmDialog from "../../features/tasks/components/TaskDeleteConfirmDialog";
import { applyTaskFilters, TASK_FILTERS_INITIAL_STATE } from "../../features/tasks/utils/taskFilters";
import { getTaskStats } from "../../features/tasks/utils/taskStats";
import type { CreateTaskPayload, TaskDialogMode, TaskFiltersState, TaskItem, UpdateTaskPayload } from "../../features/tasks/types/tasks.types";
import TaskFormDialog from "../../features/tasks/components/TaskFormDialog";
import { createTask, deleteTask, getProjectTasks, updateTask } from "../../features/tasks/services/tasks.service";
import useTaskDialogData from "../../features/tasks/hooks/useTaskDialogData";
import useProject from "../../hooks/useProject";
import useNotification from "../../hooks/useNotification";

const ManagerBacklogContent = () => {
  const { selectedProjectId } = useProject();

  const [tasks, setTasks] = useState<TaskItem[]>([]);
  const [tasksLoading, setTasksLoading] = useState(false);

  const [filters, setFilters] = useState<TaskFiltersState>(
    TASK_FILTERS_INITIAL_STATE,
  );

  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<TaskDialogMode>("create");

  const [deleteOpen, setDeleteOpen] = useState(false);
  const [taskPendingDelete, setTaskPendingDelete] = useState<TaskItem | null>(
    null,
  );

  const { showSuccess, showError } = useNotification();
  const {
    clearSelectedTask,
    detailLoading,
    developers,
    loadReferenceData,
    loadTaskDetails,
    selectedTask,
    sprintOptions,
  } = useTaskDialogData({ projectId: selectedProjectId });

  const fetchTasks = useCallback(async () => {
    if (!selectedProjectId) return;
    setTasksLoading(true);
    try {
      const data = await getProjectTasks(selectedProjectId);
      setTasks(data);
    } catch {
      showError("No fue posible cargar las tareas del proyecto.");
    } finally {
      setTasksLoading(false);
    }
  }, [selectedProjectId, showError]);

  useEffect(() => {
    setTasks([]);
    fetchTasks();
    loadReferenceData();
  }, [fetchTasks, loadReferenceData]);

  const filteredTasks = useMemo(
    () => applyTaskFilters(tasks, filters),
    [tasks, filters],
  );

  const activeSprintId = useMemo(
    () => sprintOptions.find((sprint) => sprint.status === "ACTIVE")?.sprintId ?? null,
    [sprintOptions],
  );

  const stats = useMemo(
    () => getTaskStats(tasks, activeSprintId),
    [tasks, activeSprintId],
  );

  const openCreateDialog = () => {
    setDialogMode("create");
    clearSelectedTask();
    setDialogOpen(true);
  };

  const openViewDialog = async (task: TaskItem) => {
    setDialogMode("view");
    clearSelectedTask();
    setDialogOpen(true);
    const taskDetail = await loadTaskDetails(task.taskId, {
      onError: () => showError("No fue posible cargar los detalles de la tarea."),
    });

    if (!taskDetail) {
      setDialogOpen(false);
    }
  };

  const handleCreateTask = async (payload: CreateTaskPayload) => {
    if (!selectedProjectId) return;
    try {
      await createTask({ ...payload, projectId: selectedProjectId });
      showSuccess("Tarea creada exitosamente.");
      setDialogOpen(false);
      await fetchTasks();
    } catch {
      showError("No fue posible crear la tarea.");
    }
  };

  const handleUpdateTask = async (
    taskId: string,
    payload: UpdateTaskPayload,
  ) => {
    try {
      await updateTask(taskId, payload);
      clearSelectedTask();
      showSuccess("Tarea actualizada exitosamente.");
      setDialogOpen(false);
      await fetchTasks();
    } catch {
      showError("No fue posible actualizar la tarea.");
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
      clearSelectedTask();
      setTaskPendingDelete(null);
      showSuccess("La tarea se eliminó correctamente.");
      setDeleteOpen(false);
      setDialogOpen(false);
    } catch {
      showError("No fue posible eliminar la tarea.");
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
        sprints={sprintOptions}
        loading={detailLoading}
        onClose={() => {
          setDialogOpen(false);
          clearSelectedTask();
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
    </>
  );
};

const ManagerBacklog = () => (
  <DashboardLayout>
    <ManagerBacklogContent />
  </DashboardLayout>
);

export default ManagerBacklog;
