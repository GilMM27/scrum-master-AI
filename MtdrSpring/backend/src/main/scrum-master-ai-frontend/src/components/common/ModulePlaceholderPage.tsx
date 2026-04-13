import { Paper, Stack, Typography, Chip, Box } from '@mui/material';
import ConstructionRoundedIcon from '@mui/icons-material/ConstructionRounded';
import DashboardLayout from '../../layouts/DashboardLayout';

interface ModulePlaceholderPageProps {
  title: string;
  description: string;
  role: 'ADMIN' | 'MANAGER' | 'DEVELOPER';
  moduleName: string;
}

const getRoleLabel = (role: ModulePlaceholderPageProps['role']) => {
  switch (role) {
    case 'ADMIN':
      return 'Administrador';
    case 'MANAGER':
      return 'Manager';
    case 'DEVELOPER':
      return 'Desarrollador';
    default:
      return role;
  }
};

const ModulePlaceholderPage = ({ title, description, role, moduleName,
}: ModulePlaceholderPageProps) => {
  return (
    <DashboardLayout>
      <Stack spacing={3}>
        <Box>
          <Typography variant="h4" gutterBottom>
            {title}
          </Typography>
          <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap', gap: 1 }}>
            <Chip label={getRoleLabel(role)} color="info" variant="outlined" />
            <Chip label={moduleName} variant="outlined" />
          </Stack>
        </Box>

        <Paper sx={{ p: 4, borderRadius: 4 }}>
          <Stack spacing={2} sx={{ alignItems: 'flex-start' }}>
            <ConstructionRoundedIcon color="info" sx={{ fontSize: 40 }} />
            <Typography variant="h6">
              Vista placeholder en construcción
            </Typography>
            <Typography color="text.secondary">
              {description}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Esta pantalla existe para dejar funcional la navegación, el sidebar,
              el breadcrumb y la estructura del dashboard mientras implementamos el módulo real.
            </Typography>
          </Stack>
        </Paper>
      </Stack>
    </DashboardLayout>
  );
};

export default ModulePlaceholderPage;