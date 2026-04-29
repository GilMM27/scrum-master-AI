import { Chip, Stack } from "@mui/material";
import type { TaskStats } from "../types/tasks.types";

interface TaskStatsChipsProps {
  stats: TaskStats;
}

const TaskStatsChips = ({ stats }: TaskStatsChipsProps) => {
  return (
    <Stack direction="row" spacing={1.25} sx={{ flexWrap: 'wrap' }} useFlexGap>
      <Chip label={`Total tareas: ${stats.totalProjectTasks}`} color="default" variant="outlined" />
      <Chip label={`Sprint actual: ${stats.totalCurrentSprintTasks}`} color="info" variant="outlined" />
      <Chip label={`Completadas: ${stats.totalCompletedTasks}`} color="success" variant="outlined" />
      <Chip label={`En revisión: ${stats.totalReviewTasks}`} color="warning" variant="outlined" />
      <Chip label={`Bloqueadas: ${stats.totalBlockedTasks}`} color="error" variant="outlined" />
    </Stack>
  )
}

export default TaskStatsChips