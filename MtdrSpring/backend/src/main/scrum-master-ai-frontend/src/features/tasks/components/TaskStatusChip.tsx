import { Chip } from "@mui/material"
import type { TaskStatus } from "../types/tasks.types"

interface TaskStatusChipProps {
  status: TaskStatus;
};

const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  REVIEW: 'In Review',
  DONE: 'Done',
  BLOCKED: 'Blocked'
};

const STATUS_COLORS: Record<TaskStatus, 'secondary' | 'info' | 'warning' | 'success' | 'error'> = {
  TODO: 'secondary',
  IN_PROGRESS: 'info',
  REVIEW: 'warning',
  DONE: 'success',
  BLOCKED: 'error'
};

const TaskStatusChip = ({ status }: TaskStatusChipProps) => {
  return (
    <Chip 
      size="small"
      label={STATUS_LABELS[status]} 
      color={STATUS_COLORS[status]} 
      variant={status === 'BLOCKED' ? 'filled' : 'outlined'}
    />
  )
}

export default TaskStatusChip;