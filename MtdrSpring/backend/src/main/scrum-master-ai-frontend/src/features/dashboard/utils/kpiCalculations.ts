import BlockRounded from "@mui/icons-material/BlockRounded";
import CheckCircleRounded from "@mui/icons-material/CheckCircleRounded";
import TaskAltRounded from "@mui/icons-material/TaskAltRounded";
import TimerRounded from "@mui/icons-material/TimerRounded";
import TrendingUpRounded from "@mui/icons-material/TrendingUpRounded";
import WarningAmberRounded from "@mui/icons-material/WarningAmberRounded";
import type { AlertLevel, DashboardScope, KpiCardData, ProjectAnalyticsData, SprintAnalyticsData, TasksDoneBySprintRow } from "../types/dashboard.types";

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

  const completionProgress = Math.round((completionRate ?? 0) * 100);

  const leadProgress =
    leadTime !== null && leadTime !== undefined
      ? Math.min(100, Math.round((leadTime / 10) * 100))
      : 0;

  const cycleProgress =
    cycleTime !== null && cycleTime !== undefined
      ? Math.min(100, Math.round((cycleTime / 7) * 100))
      : 0;

  const delayedProgress =
    data.totalTasksCount && data.totalTasksCount > 0
      ? Math.min(
          100,
          Math.round((data.delayedTasksCount / data.totalTasksCount) * 100),
        )
      : 0;

  const blockedProgress =
    data.totalTasksCount && data.totalTasksCount > 0
      ? Math.min(
          100,
          Math.round((data.blockedTasksCount / data.totalTasksCount) * 100),
        )
      : 0;

  function completionTrend(rate: number | null): string | undefined {
    if (rate === null || rate === undefined) return undefined;
    const pct = Math.round(rate * 100);
    const TARGET = 80;
    const diff = Math.abs(pct - TARGET);
    return pct >= TARGET
      ? `${diff}% sobre el objetivo del ${TARGET}%`
      : `${diff}% bajo el objetivo del ${TARGET}%`;
  }

  function leadTimeTrend(days: number | null): string | undefined {
    if (days === null || days === undefined) return undefined;
    const TARGET = 5;
    const diff = Math.abs(days - TARGET).toFixed(1);
    if (days > 10) return `Supera el límite de 10 días`;
    if (days > TARGET) return `${diff} días sobre el umbral de ${TARGET} días`;
    return `${diff} días bajo el umbral de ${TARGET} días`;
  }

  function cycleTimeTrend(days: number | null): string | undefined {
    if (days === null || days === undefined) return undefined;
    const TARGET = 3;
    const diff = Math.abs(days - TARGET).toFixed(1);
    if (days > 7) return `Supera el límite de 7 días`;
    if (days > TARGET) return `${diff} días sobre el umbral de ${TARGET} días`;
    return `${diff} días bajo el umbral de ${TARGET} días`;
  }

  function sprintThroughputTrend(
    rows: TasksDoneBySprintRow[],
  ): string | undefined {
    if (rows.length < 2) return undefined;
    const sum = (row: TasksDoneBySprintRow) =>
      Object.values(row.tasksDoneByUser).reduce((s, n) => s + n, 0);
    const prev = sum(rows[rows.length - 2]);
    const curr = sum(rows[rows.length - 1]);
    const diff = curr - prev;
    if (diff === 0) return `Igual que el sprint anterior`;
    return `${Math.abs(diff)} ${diff > 0 ? "más" : "menos"} que el sprint anterior`;
  }

  function countRatioTrend(
    count: number,
    total: number | null,
  ): string | undefined {
    if (!total || total === 0) return undefined;
    const pct = Math.round((count / total) * 100);
    return `${count} de ${total} tareas (${pct}% del total)`;
  }

  const throughputAlert_ = throughputAlert(throughput);
  const tasksDoneRows = data.tasksDoneBySprint ?? [];

  return [
    {
      label: "Tasa de Completitud",
      value: fmtPct(completionRate),
      sublabel: isProject
        ? "Tareas completadas del total del proyecto"
        : "Tareas completadas vs. total del sprint",
      alert: completionAlert(completionRate),
      Icon: CheckCircleRounded,
      accentColor: "#22d3ee",
      progress: completionProgress,
      trend: completionTrend(completionRate),
    },
    {
      label: "Lead Time Promedio",
      value: fmt(leadTime, 1, " días"),
      sublabel: "Tiempo desde creación hasta entrega",
      alert: leadTimeAlert(leadTime),
      Icon: TrendingUpRounded,
      accentColor: "#6060fa",
      progress: leadProgress,
      trend: leadTimeTrend(leadTime),
    },
    {
      label: "Cycle Time Promedio",
      value: fmt(cycleTime, 1, " días"),
      sublabel: "Tiempo desde inicio hasta entrega",
      alert: cycleTimeAlert(cycleTime),
      Icon: TimerRounded,
      accentColor: "#a78bfa",
      progress: cycleProgress,
      trend: cycleTimeTrend(cycleTime),
    },
    {
      label: "Tareas Completadas",
      value: String(throughput),
      sublabel: isProject
        ? "Total de tareas entregadas en el proyecto"
        : "Tareas entregadas en este sprint",
      alert: throughputAlert_,
      Icon: TaskAltRounded,
      accentColor: "#34d399",
      progress: completionProgress,
      trend:
        sprintThroughputTrend(tasksDoneRows) ?? completionTrend(completionRate),
    },
    {
      label: "Tareas Retrasadas",
      value: String(data.delayedTasksCount),
      sublabel: isProject
        ? "No completadas en sprints cerrados"
        : "No completadas al cierre del sprint",
      alert: delayedAlert(data.delayedTasksCount),
      Icon: WarningAmberRounded,
      accentColor: "#fb923c",
      progress: delayedProgress,
      trend: countRatioTrend(
        data.delayedTasksCount,
        data.totalTasksCount ?? null,
      ),
    },
    {
      label: "Tareas Bloqueadas",
      value: String(data.blockedTasksCount),
      sublabel: "Requieren atención para continuar",
      alert: blockedAlert(data.blockedTasksCount),
      Icon: BlockRounded,
      accentColor: "#fb3c3c",
      progress: blockedProgress,
      trend: countRatioTrend(
        data.blockedTasksCount,
        data.totalTasksCount ?? null,
      ),
    },
  ];
}
