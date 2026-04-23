import { Box, Button, Stack, Typography } from "@mui/material";
import { AddRounded } from "@mui/icons-material";

interface TaskBacklogHeaderProps {
  onCreateTask: () => void;
}

const TaskBacklogHeader = ({ onCreateTask }: TaskBacklogHeaderProps) => {
  return (
    <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ justifyContent: 'space-between', alignItems: { xs: 'stretch', md: 'flex-start' } }}>
      <Box>
        <Typography variant="h4" gutterBottom>
            Product Backlog
        </Typography>
        <Typography color="text.secondary">
            Administra el product backlog del proyecto, crea tareas, asigna responsables y prioriza el trabajo pendiente para el equipo de desarrollo.
        </Typography>
      </Box>
      <Box>
        <Button variant="contained" startIcon={<AddRounded />} onClick={onCreateTask} sx={{ alignSelf: 'self-start'}}>
          Agregar Tarea
        </Button>
      </Box>
    </Stack>
  );
}

export default TaskBacklogHeader