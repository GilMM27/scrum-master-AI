import { Box, Typography } from "@mui/material";

const SprintHeader = () => {
  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Gestión de Sprints
      </Typography>
      <Typography color="text.secondary">
        Administra el sprint vigente del proyecto, monitorea el progreso del
        equipo, gestiona las tareas por estado y coordina el cierre del sprint.
      </Typography>
    </Box>
  );
};

export default SprintHeader;
