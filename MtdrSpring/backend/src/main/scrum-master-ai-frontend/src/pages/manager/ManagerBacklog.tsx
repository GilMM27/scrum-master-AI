import { useMemo, useState } from 'react';
import { Alert, Snackbar, Stack } from '@mui/material';
import DashboardLayout from '../../layouts/DashboardLayout';
import TaskBacklogHeader from '../../features/tasks/components/TaskBacklogHeader';
import TaskFiltersBar from '../../features/tasks/components/TaskFiltersBar';
import TaskStatsChips from '../../features/tasks/components/TaskStatsChips';
import TasksTable from '../../features/tasks/components/TasksTable';
import TaskDeleteConfirmDialog from '../../features/tasks/components/TaskDeleteConfirmDialog';
import { applyTaskFilters } from '../../features/tasks/utils/taskFilters';
import { getTaskStats } from '../../features/tasks/utils/taskStats';
import type { CreateTaskPayload, SprintOption, TaskAssignee, TaskDialogMode, TaskFiltersState, TaskItem, UpdateTaskPayload } from '../../features/tasks/types/tasks.types';
import TaskFormDialog from '../../features/tasks/components/TaskFormDialog';

const initialFilters: TaskFiltersState = {
  search: '',
  status: '',
  priority: '',
  inReview: false,
  blocked: false,
};

const mockDevelopers: TaskAssignee[] = [
  { userId: 'u1', username: 'diego' },
  { userId: 'u2', username: 'ana' },
  { userId: 'u3', username: 'luis' },
];

const mockSprints: SprintOption[] = [
  { sprintId: 's1', sprintNumber: 1, status: 'ACTIVE', name: 'Current Sprint' },
  { sprintId: 's2', sprintNumber: 2, status: 'PLANNED', name: 'Planned Sprint' },
];

const initialTasks: TaskItem[] = [
  {
    taskId: 't1',
    title: 'Implement login module',
    description: 'Build login UI and integrate authentication flow.',
    status: 'DONE',
    priority: 'HIGH',
    assignees: [mockDevelopers[0]],
    sprintId: 's1',
    sprintNumber: 1,
    estimatedHours: 4,
    actualHours: 3.5,
    blocked: false,
    inReview: false,
  },
  {
    taskId: 't2',
    title: 'Create users management table',
    description: 'Render admin users table with role and authorization actions.',
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    assignees: [mockDevelopers[1], mockDevelopers[2]],
    sprintId: 's1',
    sprintNumber: 2,
    estimatedHours: 4,
    actualHours: 2,
    blocked: false,
    inReview: false,
  },
  {
    taskId: 't3',
    title: 'Design sprint kanban board',
    description: 'Prepare board layout for drag-and-drop interactions.',
    status: 'TODO',
    priority: 'MEDIUM',
    assignees: [],
    sprintId: null,
    sprintNumber: null,
    estimatedHours: 2,
    actualHours: null,
    blocked: true,
    inReview: false,
  },
];


const ManagerBacklog = () => {
  const [tasks, setTasks] = useState<TaskItem[]>(initialTasks);
  const [filters, setFilters] = useState<TaskFiltersState>(initialFilters);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<TaskDialogMode>('create');
  const [selectedTask, setSelectedTask] = useState<TaskItem | null>(null);

  const [deleteOpen, setDeleteOpen] = useState(false);
  const [taskPendingDelete, setTaskPendingDelete] = useState<TaskItem | null>(null);

  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const filteredTasks = useMemo(() => applyTaskFilters(tasks, filters), [tasks, filters]);

  const activeSprintId = useMemo(() => mockSprints.find(s => s.status === 'ACTIVE')?.sprintId ?? null, []);

  const stats = useMemo(() => getTaskStats(tasks, activeSprintId), [tasks, activeSprintId]);

  const openCreateDialog = () => {
    setDialogMode('create');
    setSelectedTask(null);
    setDialogOpen(true);
  };

  const openViewDialog = (task: TaskItem) => {
    setDialogMode('view');
    setSelectedTask(task);
    setDialogOpen(true);
  };

  const handleCreateTask = async (payload: CreateTaskPayload) => {
    try {
      const selectedAssignees = mockDevelopers.filter(dev => payload.assigneeIds?.includes(dev.userId)) || [];

      const sprint = mockSprints.find(s => s.sprintId === payload.sprintId) || null;

      const newTask: TaskItem = {
        taskId: crypto.randomUUID(),
        title: payload.title,
        description: payload.description || null,
        status: payload.status,
        priority: payload.priority,
        assignees: selectedAssignees,
        sprintId: payload.sprintId || null,
        sprintNumber: sprint ? sprint.sprintNumber : null,
        estimatedHours: payload.estimatedHours || null,
        actualHours: null,
        blocked: false,
        inReview: payload.status === 'IN_REVIEW',
      };

      setTasks(prev => [...prev, newTask]);
      setSuccessMsg('Tarea creada exitosamente.');
      setDialogOpen(false);
    } catch (error) {
      setErrorMsg('No fue posible crear la tarea.');
    }
  };

  const handleUpdateTask = async (taskId: string, payload: UpdateTaskPayload) => {
    try {
      const selectedAsignees = mockDevelopers.filter(dev => payload.assigneeIds?.includes(dev.userId)) || [];

      const sprint = mockSprints.find(s => s.sprintId === payload.sprintId) || null;

      setTasks(prev => 
        prev.map(task => 
          task.taskId === taskId
            ? {
                ...task,
                title: payload.title,
                description: payload.description || null,
                status: payload.status,
                priority: payload.priority,
                assignees: selectedAsignees,
                sprintId: payload.sprintId || null,
                sprintNumber: sprint ? sprint.sprintNumber : null,
                estimatedHours: payload.estimatedHours || null,
                inReview: payload.status === 'IN_REVIEW',
            }
          : task,
        ),
      );

      setSelectedTask(null);
      setSuccessMsg('Tarea actualizada exitosamente.');
      setDialogOpen(false);
    } catch {
      setErrorMsg('No fue posible actualizar la tarea.');
    }
  };

  const handleDeleteRequest = (task: TaskItem) => {
    setTaskPendingDelete(task);
    setDeleteOpen(true);
  };

  const handleDeleteTask = async () => {
    if (!taskPendingDelete) return;

    try {
      setTasks((prev) => prev.filter((task) => task.taskId !== taskPendingDelete.taskId));
      setSelectedTask(null);
      setTaskPendingDelete(null);
      setSuccessMsg('La tarea se eliminó correctamente.');
      setDeleteOpen(false);
      setDialogOpen(false);
    } catch {
      setErrorMsg('No fue posible eliminar la tarea.');
    }
  };
  
  return (
    <DashboardLayout>
      <Stack spacing={3}>
        <TaskBacklogHeader onCreateTask={openCreateDialog} />
        <TaskFiltersBar filters={filters} onChange={setFilters} />
        <TaskStatsChips stats={stats} />
        <TasksTable tasks={filteredTasks} onViewTask={openViewDialog} showAssignee showSprint />
      </Stack>
      
      <TaskFormDialog
        open={dialogOpen}
        mode={dialogMode}
        task={selectedTask}
        developers={mockDevelopers}
        sprints={mockSprints}
        onClose={() => { setDialogOpen(false); setSelectedTask(null); }}
        onDeleteRequest={handleDeleteRequest}
        onSubmitCreate={handleCreateTask}
        onSubmitUpdate={handleUpdateTask}
      />

      <TaskDeleteConfirmDialog
        open={deleteOpen}
        taskTitle={taskPendingDelete?.title}
        onClose={() => {  setDeleteOpen(false); setTaskPendingDelete(null); }}
        onConfirm={handleDeleteTask}
      />

      <Snackbar open={Boolean(successMsg)} autoHideDuration={3500} onClose={() => setSuccessMsg('')}>
        <Alert onClose={() => setSuccessMsg('')} severity="success">
          {successMsg}
        </Alert>
      </Snackbar>

      <Snackbar open={Boolean(errorMsg)} autoHideDuration={4000} onClose={() => setErrorMsg('')}>
        <Alert onClose={() => setErrorMsg('')} severity="error">
          {errorMsg}
        </Alert>
      </Snackbar>
    </DashboardLayout>
  );
};

export default ManagerBacklog;