import { FormControlLabel, Grid, Checkbox, MenuItem, TextField } from "@mui/material";
import type { TaskFiltersState, TaskPriority, TaskStatus } from "../types/tasks.types";

interface TaskFiltersBarProps {
  filters: TaskFiltersState;
  onChange: (filters: TaskFiltersState) => void;
}

const TaskFiltersBar = ({ filters, onChange }: TaskFiltersBarProps) => {
  return (
    <Grid container spacing={2} sx={{ alignItems: "center" }}>
      <Grid size={{ xs: 12, md: 4 }}>
        <TextField
          fullWidth
          label="Buscar tarea"
          placeholder="Nombre de la tarea"
          value={filters.search}
          onChange={(e) => onChange({ ...filters, search: e.target.value })}
        />
      </Grid>

      <Grid size={{ xs: 12, md: 3 }}>
        <TextField
          fullWidth
          select
          label="Estado"
          value={filters.status}
          onChange={(e) =>
            onChange({ ...filters, status: e.target.value as TaskStatus | '' })
          }
        >
          <MenuItem value="">Todos</MenuItem>
          <MenuItem value="TODO">To Do</MenuItem>
          <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
          <MenuItem value="REVIEW">In Review</MenuItem>
          <MenuItem value="DONE">Done</MenuItem>
        </TextField>
      </Grid>

      <Grid size={{ xs: 12, md: 3 }}>
        <TextField
          fullWidth
          select
          label="Prioridad"
          value={filters.priority}
          onChange={(e) =>
            onChange({ ...filters, priority: e.target.value as TaskPriority | '' })
          }
        >
          <MenuItem value="">Todas</MenuItem>
          <MenuItem value="LOW">Low</MenuItem>
          <MenuItem value="MEDIUM">Medium</MenuItem>
          <MenuItem value="HIGH">High</MenuItem>
          <MenuItem value="CRITICAL">Critical</MenuItem>
        </TextField>
      </Grid>

      <Grid size={{ xs: 12, md: 2 }}>
        <FormControlLabel
          control={
            <Checkbox
              checked={filters.inReview}
              onChange={(e) =>
                onChange({
                  ...filters,
                  inReview: e.target.checked,
                  blocked: e.target.checked ? false : filters.blocked,
                })
              }
              sx={{
                color: "info.main",
                "&.Mui-checked": {
                  color: "info.main",
                },
              }}
            />
          }
          label="In Review"
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={filters.blocked}
              onChange={(e) =>
                onChange({
                  ...filters,
                  blocked: e.target.checked,
                  inReview: e.target.checked ? false : filters.inReview,
                })
              }
              sx={{
                color: "info.main",
                "&.Mui-checked": {
                  color: "info.main",
                },
              }}
            />
          }
          label="Blocked"
        />
      </Grid>
    </Grid>
  );
};

export default TaskFiltersBar;
