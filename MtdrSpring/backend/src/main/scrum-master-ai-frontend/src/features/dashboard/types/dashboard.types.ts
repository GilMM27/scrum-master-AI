import type { SvgIconComponent } from "@mui/icons-material";

export type DashboardScopeType = "project" | "sprint";

export interface DashboardScope {
  type: DashboardScopeType;
  sprintId?: string;
  sprintName?: string;
}

export type AlertLevel = "green" | "yellow" | "red" | "neutral";

export interface KpiCardData {
  label: string;
  value: string;
  sublabel?: string;
  alert: AlertLevel;
  Icon: SvgIconComponent;
}

export interface VelocityDataPoint {
  sprintId: string;
  sprintName: string | null;
  completedStoryPoints: number;
  completedTasks: number;
  totalTasks: number;
}

export interface LeadTimeTrendPoint {
  weekLabel: string;
  avgLeadTimeDays: number | null;
  taskCount: number;
}

export interface BurndownDataPoint {
  date: string;
  remaining: number | null;
  ideal: number;
}

export interface WipByDeveloperItem {
  userId: string;
  username: string;
  inProgressTasks: number;
  reviewTasks: number;
  totalActiveTasks: number;
}

export interface HistogramBucket {
  days: number;
  count: number;
}

export interface TasksDoneBySprintRow {
  sprintId: string;
  sprintName: string | null;
  tasksDoneByUser: Record<string, number>;
}

export interface ProjectAnalyticsData {
  avgLeadTimeDays: number | null;
  avgCycleTimeDays: number | null;
  completionRate: number | null;
  blockedTasksCount: number;
  delayedTasksCount: number;
  velocityBySprint: VelocityDataPoint[];
  leadTimeHistogram: HistogramBucket[];
  leadTimeMean: number | null;
  cycleTimeHistogram: HistogramBucket[];
  cycleTimeMean: number | null;
  tasksDoneBySprint: TasksDoneBySprintRow[];
  burndownData: BurndownDataPoint[];
}

export interface SprintAnalyticsData {
  sprintId: string;
  sprintName: string | null;
  startDate: string | null;
  endDate: string | null;
  sprintAccomplishment: number | null;
  avgCycleTimeDays: number | null;
  delayedTasksCount: number;
  blockedTasksCount: number;
  wipByDeveloper: WipByDeveloperItem[];
  burndownData: BurndownDataPoint[];
  leadTimeHistogram: HistogramBucket[];
  leadTimeMean: number | null;
  cycleTimeHistogram: HistogramBucket[];
  cycleTimeMean: number | null;
  tasksDoneBySprint: TasksDoneBySprintRow[];
}
