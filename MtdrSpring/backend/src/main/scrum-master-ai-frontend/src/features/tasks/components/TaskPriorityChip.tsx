import { Chip } from "@mui/material"
import type { TaskPriority } from "../types/tasks.types"

interface TaskPriorityChipProps {
  priority: TaskPriority;
};

const PRIORITY_LABELS: Record<TaskPriority, string> = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'High',
  CRITICAL: 'Critical'
};

const PRIORITY_COLORS: Record<TaskPriority, 'secondary' | 'info' | 'warning' | 'success' | 'error'> = {
  LOW: 'secondary',
  MEDIUM: 'info',
  HIGH: 'warning',
  CRITICAL: 'error'
};

const TaskPriorityChip = ({ priority }: TaskPriorityChipProps) => {
  return (
    <Chip 
      size="small"
      label={PRIORITY_LABELS[priority]} 
      color={PRIORITY_COLORS[priority]} 
      variant={priority === 'CRITICAL' ? 'filled' : 'outlined'}
    />
  )
}

export default TaskPriorityChip