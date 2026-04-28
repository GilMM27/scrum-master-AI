export type SprintStatus = "PLANNED" | "ACTIVE" | "CLOSED";
export type SprintDialogMode = "create" | "edit";

export interface SprintItem {
  sprintId: string;
  name: string | null;
  startDate: string | null;
  endDate: string | null;
  status: SprintStatus;
}

export interface SprintSummaryData {
  sprintName: string;
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  reviewTasks: number;
  blockedTasks: number;
  toDoTasks: number;
  totalEstimatedHours: number;
  totalActualHours: number | null;
}

export interface CreateSprintPayload {
  projectId: string;
  name: string;
  startDate: string;
  endDate: string;
}

export interface UpdateSprintPayload {
  name?: string;
  startDate?: string | null;
  endDate?: string | null;
  status?: SprintStatus;
}
