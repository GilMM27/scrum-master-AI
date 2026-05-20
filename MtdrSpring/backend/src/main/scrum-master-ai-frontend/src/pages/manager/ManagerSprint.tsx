import { useEffect, useMemo, useState } from "react";
import { Box, Button, CircularProgress, Stack, Tooltip, Typography } from "@mui/material";
import { EditRounded, LockRounded, AddRounded } from "@mui/icons-material";
import DashboardLayout from "../../layouts/DashboardLayout";
import TaskFormDialog from "../../features/tasks/components/TaskFormDialog";
import { applyTaskFilters } from "../../features/tasks/utils/taskFilters";
import { getTaskDetails } from "../../features/tasks/services/tasks.service";
import type { TaskDialogMode, TaskFiltersState, TaskItem, TaskStatus } from "../../features/tasks/types/tasks.types";
import { TASK_FILTERS_INITIAL_STATE } from "../../features/tasks/utils/taskFilters";
import useTaskDialogData from "../../features/tasks/hooks/useTaskDialogData";
import SprintHeader from "../../features/sprints/components/SprintHeader";
import SprintLabel from "../../features/sprints/components/SprintLabel";
import SprintSummaryChips from "../../features/sprints/components/SprintSummaryChips";
import SprintFormDialog from "../../features/sprints/components/SprintFormDialog";
import SprintSummaryDialog from "../../features/sprints/components/SprintSummaryDialog";
import SprintBoard from "../../features/sprints/components/SprintBoard";
import SprintTable from "../../features/sprints/components/SprintTable";
import TaskFiltersBar from "../../features/tasks/components/TaskFiltersBar";
import TabManager, { type SprintTab } from "../../features/sprints/components/TabManager";
import type { CreateSprintPayload, SprintDialogMode, SprintItem, SprintSummaryData, UpdateSprintPayload } from "../../features/sprints/types/sprint.types";
import { createSprint, getSprintSummary, updateSprint, updateTaskStatus } from "../../features/sprints/services/sprint.service";
import useProject from "../../hooks/useProject";
import useNotification from "../../hooks/useNotification";
import { useSprintPageData } from "../../features/sprints/hooks/useSprintPageData";

const addDays = (dateStr: string, days: number): Date => {
  const d = new Date(dateStr);
  d.setDate(d.getDate() + days);
  return d;
};

const canCloseSprint = (sprint: SprintItem | null | undefined): boolean => {
  if (!sprint || sprint.status !== "ACTIVE") return false;
  if (!sprint.endDate) return false;
  const closeThreshold = addDays(sprint.endDate, 2);
  return new Date() >= closeThreshold;
};

const ManagerSprintContent = () => {
  const { selectedProjectId } = useProject();
  const { showSuccess, showError } = useNotification();

  const {
    sprints,
    sprintsLoading,
    selectedSprintId,
    setSelectedSprintId,
    tasks,
    setTasks,
    tasksLoading,
    selectedSprint,
    fetchSprints,
    fetchSprintTasks,
  } = useSprintPageData({ projectId: selectedProjectId, onError: showError });

  const [sprintFormOpen, setSprintFormOpen] = useState(false);
  const [sprintFormMode, setSprintFormMode] =
    useState<SprintDialogMode>("create");
  const [editingSprint, setEditingSprint] = useState<SprintItem | null>(null);

  const [summaryDialogOpen, setSummaryDialogOpen] = useState(false);
  const [summaryData, setSummaryData] = useState<SprintSummaryData | null>(
    null,
  );
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [confirming, setConfirming] = useState(false);

  const [taskDialogOpen, setTaskDialogOpen] = useState(false);
  const [taskDialogMode, setTaskDialogMode] = useState<TaskDialogMode>("view");

  const [filters, setFilters] = useState<TaskFiltersState>(TASK_FILTERS_INITIAL_STATE);
  const [activeTab, setActiveTab] = useState<SprintTab>("board");
  const {
    clearSelectedTask,
    detailLoading,
    developers,
    loadReferenceData,
    loadTaskDetails,
    selectedTask,
    sprintOptions,
  } = useTaskDialogData({ projectId: selectedProjectId });

  // Reload task reference data when the project changes.
  useEffect(() => {
    if (selectedProjectId) loadReferenceData();
  }, [selectedProjectId, loadReferenceData]);

  const filteredTasks = useMemo(
    () => applyTaskFilters(tasks, filters),
    [tasks, filters],
  );
  const closeable = canCloseSprint(selectedSprint);

  const handleOpenCreateSprint = () => {
    setEditingSprint(null);
    setSprintFormMode("create");
    setSprintFormOpen(true);
  };

  const handleOpenEditSprint = () => {
    if (!selectedSprint) return;
    setEditingSprint(selectedSprint);
    setSprintFormMode("edit");
    setSprintFormOpen(true);
  };

  const handleCreateSprint = async (payload: CreateSprintPayload) => {
    if (!selectedProjectId) return;
    await createSprint({ ...payload, projectId: selectedProjectId });
    showSuccess("Sprint creado exitosamente.");
    setSprintFormOpen(false);
    await fetchSprints();
  };

  const handleUpdateSprint = async (
    sprintId: string,
    payload: UpdateSprintPayload,
  ) => {
    await updateSprint(sprintId, payload);
    showSuccess("Sprint actualizado exitosamente.");
    setSprintFormOpen(false);
    await fetchSprints();
  };

  const handleOpenCloseSprint = async () => {
    if (!selectedSprintId) return;
    setSummaryData(null);
    setSummaryLoading(true);
    setSummaryDialogOpen(true);
    try {
      const data = await getSprintSummary(selectedSprintId);
      setSummaryData(data);
    } catch {
      showError("Error al cargar el resumen del sprint.");
      setSummaryDialogOpen(false);
    } finally {
      setSummaryLoading(false);
    }
  };

  const handleConfirmCloseSprint = async () => {
    if (!selectedSprintId) return;
    setConfirming(true);
    try {
      await updateSprint(selectedSprintId, { status: "CLOSED" });
      showSuccess("Sprint cerrado correctamente.");
      setSummaryDialogOpen(false);
      await fetchSprints();
      fetchSprintTasks(selectedSprintId);
    } catch {
      showError("Error al cerrar el sprint.");
    } finally {
      setConfirming(false);
    }
  };

  const handleViewTask = async (task: TaskItem) => {
    setTaskDialogMode("view");
    clearSelectedTask();
    setTaskDialogOpen(true);

    const taskDetail = await loadTaskDetails(task.taskId, {
      onError: () => showError("Error al cargar los detalles de la tarea."),
    });

    if (!taskDetail) {
      setTaskDialogOpen(false);
    }
  };

  const handleTaskStatusChange = async (
    taskId: string,
    newStatus: TaskStatus,
  ) => {
    const updatedTask = await updateTaskStatus(taskId, newStatus);

    // If the new status is BLOCKED or REVIEW, do a GET to confirm the
    // server-computed blocked/inReview flags are reflected in the task list.
    if (newStatus === "BLOCKED" || newStatus === "REVIEW") {
      const freshDetail = await getTaskDetails(taskId);
      setTasks((prev) =>
        prev.map((t) =>
          t.taskId === taskId
            ? {
                ...t,
                status: freshDetail.status,
                blocked: freshDetail.blocked,
                inReview: freshDetail.inReview,
              }
            : t,
        ),
      );
    } else {
      setTasks((prev) =>
        prev.map((t) =>
          t.taskId === taskId
            ? {
                ...t,
                status: updatedTask.status,
                blocked: updatedTask.blocked,
                inReview: updatedTask.inReview,
              }
            : t,
        ),
      );
    }
  };

  return (
    <>
      <Stack spacing={3}>
        <Stack
          direction={{ xs: "column", md: "row" }}
          spacing={2}
          sx={{
            alignItems: { md: "flex-start", xs: "stretch" },
            justifyContent: "space-between",
          }}
        >
          <SprintHeader />

          <Stack spacing={1.5} direction={{ xs: "column", md: "row" }}>
            <Tooltip title="Crear nuevo sprint">
              <Button
                size="small"
                variant="contained"
                startIcon={<AddRounded />}
                onClick={handleOpenCreateSprint}
              >
                Nuevo Sprint
              </Button>
            </Tooltip>

            {closeable && (
              <Tooltip title="Cerrar sprint actual">
                <Button
                  size="small"
                  variant="contained"
                  color="error"
                  startIcon={<LockRounded />}
                  onClick={handleOpenCloseSprint}
                >
                  Cerrar Sprint
                </Button>
              </Tooltip>
            )}
          </Stack>
        </Stack>

        <Stack direction="row" spacing={2} sx={{ alignItems: "center" }}>
          <SprintLabel
            sprints={sprints}
            selectedSprintId={selectedSprintId}
            onSelectSprint={setSelectedSprintId}
            loading={sprintsLoading}
          />
          {selectedSprint && selectedSprint.status !== "CLOSED" && (
            <Tooltip title="Editar sprint actual">
              <Button
                size="small"
                variant="outlined"
                color="inherit"
                startIcon={<EditRounded />}
                onClick={handleOpenEditSprint}
              >
                Editar
              </Button>
            </Tooltip>
          )}
        </Stack>

        <TaskFiltersBar filters={filters} onChange={setFilters} />

        <SprintSummaryChips sprint={selectedSprint} tasks={tasks} />

        {!selectedProjectId ? (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography color="text.secondary">
              Selecciona un proyecto para ver sus sprints.
            </Typography>
          </Box>
        ) : sprintsLoading ? (
          <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
            <CircularProgress />
          </Box>
        ) : sprints.length === 0 ? (
          <Box sx={{ textAlign: "center", py: 8 }}>
            <Typography color="text.secondary">
              No hay sprints en este proyecto.
            </Typography>
          </Box>
        ) : (
          <Stack spacing={2}>
            <TabManager activeTab={activeTab} onChange={setActiveTab} />
            <Box>
              {tasksLoading ? (
                <Box sx={{ display: "flex", justifyContent: "center", py: 6 }}>
                  <CircularProgress />
                </Box>
              ) : activeTab === "board" ? (
                <SprintBoard
                  tasks={filteredTasks}
                  onViewTask={handleViewTask}
                  onTaskStatusChange={handleTaskStatusChange}
                  onDropError={showError}
                />
              ) : (
                <SprintTable
                  tasks={filteredTasks}
                  onViewTask={handleViewTask}
                />
              )}
            </Box>
          </Stack>
        )}

        <SprintFormDialog
          open={sprintFormOpen}
          mode={sprintFormMode}
          sprint={editingSprint}
          onClose={() => setSprintFormOpen(false)}
          onSubmitCreate={handleCreateSprint}
          onSubmitUpdate={handleUpdateSprint}
        />

        <SprintSummaryDialog
          open={summaryDialogOpen}
          loading={summaryLoading}
          summaryData={summaryData}
          onClose={() => setSummaryDialogOpen(false)}
          onConfirmClose={handleConfirmCloseSprint}
          confirming={confirming}
        />

        {taskDialogOpen && (
          <TaskFormDialog
            open={taskDialogOpen}
            mode={taskDialogMode}
            task={selectedTask}
            loading={detailLoading}
            developers={developers}
            sprints={sprintOptions}
            onClose={() => {
              setTaskDialogOpen(false);
              clearSelectedTask();
            }}
            onSubmitCreate={async () => {}}
            onSubmitUpdate={async () => {}}
            onDeleteRequest={() => {}}
          />
        )}
      </Stack>
    </>
  );
};

const ManagerSprint = () => (
  <DashboardLayout>
    <ManagerSprintContent />
  </DashboardLayout>
);

export default ManagerSprint;
