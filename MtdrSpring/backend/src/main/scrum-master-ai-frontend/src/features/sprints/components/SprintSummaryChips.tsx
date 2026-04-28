import { Chip, Stack } from "@mui/material";
import type { SprintItem } from "../types/sprint.types";
import type { TaskItem } from "../../tasks/types/tasks.types";

interface SprintSummaryChipsProps {
  sprint: SprintItem | null;
  tasks: TaskItem[];
}

const STATUS_LABELS: Record<string, string> = {
  PLANNED: "Planificado",
  ACTIVE: "Activo",
  CLOSED: "Cerrado",
};

const STATUS_COLORS: Record<
  string,
  "default" | "success" | "error" | "warning"
> = {
  PLANNED: "default",
  ACTIVE: "success",
  CLOSED: "error",
};

const SprintSummaryChips = ({ sprint, tasks }: SprintSummaryChipsProps) => {
  if (!sprint) return null;

  const totalTasks = tasks.length;
  const doneTasks = tasks.filter((t) => t.status === "DONE").length;
  const blockedTasks = tasks.filter((t) => t.blocked).length;
  const remainingTasks = totalTasks - doneTasks;

  return (
    <Stack direction="row" spacing={1.25} sx={{ flexWrap: "wrap" }} useFlexGap>
      <Chip
        label={`Estado: ${STATUS_LABELS[sprint.status]}`}
        color={STATUS_COLORS[sprint.status]}
        variant="outlined"
      />
      <Chip
        label={`Total tareas: ${totalTasks}`}
        color="default"
        variant="outlined"
      />
      <Chip
        label={`Completadas: ${doneTasks}`}
        color="success"
        variant="outlined"
      />
      <Chip
        label={`Pendientes: ${remainingTasks}`}
        color="warning"
        variant="outlined"
      />
      {blockedTasks > 0 && (
        <Chip
          label={`Bloqueadas: ${blockedTasks}`}
          color="error"
          variant="outlined"
        />
      )}
    </Stack>
  );
};

export default SprintSummaryChips;
