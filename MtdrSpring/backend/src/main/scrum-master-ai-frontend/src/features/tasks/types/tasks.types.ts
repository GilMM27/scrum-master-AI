export type TaskStatus = | 'TO_DO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE' | 'BLOCKED';
export type TaskPriority = | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type SprintStatus = 'PLANNED' | 'ACTIVE' | 'CLOSED';


export interface TaskAssignee {
  userId: string;
  username: string;
  email?: string | null;
}

export interface SprintOption {
  sprintId: string;
  name: string | null;
  startDate: string | null;
  endDate: string | null;
  status: SprintStatus;
}

export interface TaskItem {    
  taskId: string;
  projectId?: string;
  title: string;
  description?: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  assignees: TaskAssignee[];
  sprintId: string | null;
  sprintName: string | null;
  storyPoints: number;
  expectedHours: number;
  actualHours: number | null;
  blocked: boolean;
  inReview: boolean;
  createdAt: string | null;
  startedAt: string | null;
  deliveredAt: string | null;
}

export interface TaskDetailItem extends TaskItem {
  projectName?: string | null;
  aiFlagged?: boolean;
  blockedAt?: string | null;
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
  totalReviewTasks: number;
  totalBlockedTasks: number;
}

export interface CreateTaskPayload {
  projectId?: string;
  title: string;
  description?: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeIds?: string[] | null;
  sprintId?: string | null;
  storyPoints?: number | null;
  expectedHours?: number | null;
}

export interface UpdateTaskPayload extends CreateTaskPayload {}

export interface SprintTasksByStatus {
  todo: TaskItem[];
  inProgress: TaskItem[];
  review: TaskItem[];
  blocked: TaskItem[];
  done: TaskItem[];
}

export type TaskDialogMode = 'create' | 'view' | 'edit';