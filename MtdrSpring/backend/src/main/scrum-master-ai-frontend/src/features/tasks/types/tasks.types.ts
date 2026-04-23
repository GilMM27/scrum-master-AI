export type TaskStatus = | 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'DONE' | 'BLOCKED';
export type TaskPriority = | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type SprintStatus = 'PLANNED' | 'ACTIVE' | 'CLOSED';


export interface TaskAssignee {
  userId: string;
  username: string;
  email?: string | null;
}

export interface SprintOption {
  sprintId: string;
  sprintNumber: number;
  name?: string | null;
  status: SprintStatus;
}

export interface TaskItem {    
  taskId: string;
  title: string;
  description?: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  assignees: TaskAssignee[];
  sprintId: string | null;
  sprintNumber: number | null;
  estimatedHours: number | null;
  actualHours: number | null;
  blocked: boolean;
  inReview: boolean;
}

export interface TaskFiltersState {
  search: string;
  status: TaskStatus | '';
  priority: TaskPriority | '';
  inReview: boolean;
  blocked: boolean;
}

export interface TaskStats {
  totalProjectTasks: number;
  totalCurrentSprintTasks: number;
  totalCompletedTasks: number;
  totalInReviewTasks: number;
  totalBlockedTasks: number;
}

export interface CreateTaskPayload {
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeIds?: string[];
  sprintId?: string;
  estimatedHours?: number;
}

export interface UpdateTaskPayload extends CreateTaskPayload {}

export type TaskDialogMode = 'create' | 'view' | 'edit';