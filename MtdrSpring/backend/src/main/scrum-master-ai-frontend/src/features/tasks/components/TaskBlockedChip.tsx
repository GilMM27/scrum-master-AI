import { Chip } from "@mui/material"

const TaskBlockedChip = ({ blocked }: { blocked: boolean }) => {
  return (
    <Chip
      size="small"
      label={blocked ? 'Sí' : 'No'}
      color={blocked ? 'error' : 'success'}
      variant={blocked ? 'filled' : 'outlined'}
    />
  )
}

export default TaskBlockedChip