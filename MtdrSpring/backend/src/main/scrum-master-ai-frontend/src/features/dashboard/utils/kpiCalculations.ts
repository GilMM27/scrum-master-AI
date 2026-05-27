import BlockRounded from "@mui/icons-material/BlockRounded";
import CheckCircleRounded from "@mui/icons-material/CheckCircleRounded";
import TaskAltRounded from "@mui/icons-material/TaskAltRounded";
import TimerRounded from "@mui/icons-material/TimerRounded";
import TrendingUpRounded from "@mui/icons-material/TrendingUpRounded";
import WarningAmberRounded from "@mui/icons-material/WarningAmberRounded";
import type {
  AlertLevel,
  DashboardScope,
  KpiCardData,
  ProjectAnalyticsData,
  SprintAnalyticsData,
  TasksDoneBySprintRow,
} from "../types/dashboard.types";

function fmt(value: number | null, decimals = 1, unit = ""): string {
  if (value === null || value === undefined) return "No disponible";
  return `${value.toFixed(decimals)}${unit}`;
}

function fmtPct(value: number | null): string {
  if (value === null || value === undefined) return "No disponible";
  return `${(value * 100).toFixed(0)}%`;
}

function leadTimeAlert(days: number | null): AlertLevel {
  if (days === null) return "neutral";
  if (days <= 5) return "green";
  if (days <= 10) return "yellow";
  return "red";
}

function cycleTimeAlert(days: number | null): AlertLevel {
  if (days === null) return "neutral";
  if (days <= 3) return "green";
  if (days <= 7) return "yellow";
  return "red";
}

function completionAlert(rate: number | null): AlertLevel {
  if (rate === null) return "neutral";
  if (rate >= 0.8) return "green";
  if (rate >= 0.6) return "yellow";
  return "red";
}

function throughputAlert(count: number): AlertLevel {
  if (count >= 5) return "green";
  if (count >= 1) return "yellow";
  return "red";
}

function delayedAlert(count: number): AlertLevel {
  if (count === 0) return "green";
  if (count <= 2) return "yellow";
  return "red";
}

function blockedAlert(count: number): AlertLevel {
  if (count === 0) return "green";
  if (count <= 2) return "yellow";
  return "red";
}

function computeThroughput(rows: TasksDoneBySprintRow[]): number {
  return rows.reduce(
    (total, row) =>
      total + Object.values(row.tasksDoneByUser).reduce((s, n) => s + n, 0),
    0,
  );
}

export function computeKpiCards(
  analytics: ProjectAnalyticsData | SprintAnalyticsData,
  scope: DashboardScope,
): KpiCardData[] {
  const isProject = scope.type === "project";
  const data = analytics as ProjectAnalyticsData & SprintAnalyticsData;

  const completionRate = isProject
    ? data.completionRate
    : data.sprintAccomplishment;
  const leadTime = isProject ? data.avgLeadTimeDays : data.leadTimeMean;
  const cycleTime = isProject ? data.avgCycleTimeDays : data.cycleTimeMean;
  const throughput = computeThroughput(data.tasksDoneBySprint ?? []);

  return [
    {
      label: "Tasa de Completitud",
      value: fmtPct(completionRate),
      sublabel: isProject
        ? "Tareas completadas del total del proyecto"
        : "Tareas completadas vs. total del sprint",
      alert: completionAlert(completionRate),
      Icon: CheckCircleRounded,
    },
    {
      label: "Lead Time Promedio",
      value: fmt(leadTime, 1, " días"),
      sublabel: "Tiempo desde creación hasta entrega",
      alert: leadTimeAlert(leadTime),
      Icon: TrendingUpRounded,
    },
    {
      label: "Cycle Time Promedio",
      value: fmt(cycleTime, 1, " días"),
      sublabel: "Tiempo desde inicio hasta entrega",
      alert: cycleTimeAlert(cycleTime),
      Icon: TimerRounded,
    },
    {
      label: "Tareas Completadas",
      value: String(throughput),
      sublabel: isProject
        ? "Total de tareas entregadas en el proyecto"
        : "Tareas entregadas en este sprint",
      alert: throughputAlert(throughput),
      Icon: TaskAltRounded,
    },
    {
      label: "Tareas Retrasadas",
      value: String(data.delayedTasksCount),
      sublabel: isProject
        ? "No completadas en sprints cerrados"
        : "No completadas al cierre del sprint",
      alert: delayedAlert(data.delayedTasksCount),
      Icon: WarningAmberRounded,
    },
    {
      label: "Tareas Bloqueadas",
      value: String(data.blockedTasksCount),
      sublabel: "Requieren atención para continuar",
      alert: blockedAlert(data.blockedTasksCount),
      Icon: BlockRounded,
    },
  ];
}
