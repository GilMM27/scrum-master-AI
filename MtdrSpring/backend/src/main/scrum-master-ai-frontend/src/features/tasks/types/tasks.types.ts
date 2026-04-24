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
  actualHours: number | null;
  blocked: boolean;
  inReview: boolean;
}

export interface TaskDetailItem extends TaskItem {
  projectName?: string | null;
  aiFlagged?: boolean;
  blockedAt?: string | null;
  createdAt?: string | null;
  startedAt?: string | null;
  deliveredAt?: string | null;
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
  projectId?: string;
  title: string;
  description?: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeIds?: string[] | null;
  sprintId?: string | null;
  storyPoints?: number | null;
}

export interface UpdateTaskPayload extends CreateTaskPayload {}

export type TaskDialogMode = 'create' | 'view' | 'edit';