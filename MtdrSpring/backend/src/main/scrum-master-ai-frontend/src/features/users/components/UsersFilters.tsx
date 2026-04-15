import { Grid, MenuItem, TextField } from '@mui/material';
import type { ManagedUserStatus, ManagedUserRole, UsersFilterState } from '../types/users.types';

interface UserFilterProps {
    filters: UsersFilterState;
    onChange: (updatedFilters: UsersFilterState) => void;
};

const UsersFilters = ({ filters, onChange }: UserFilterProps) => {
  return (
    <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 6 }}>
            <TextField fullWidth label="Buscar usuario" placeholder="Buscar por username o email"
                value={filters.search}
                onChange={(e) => onChange({ ...filters, search: e.target.value })}
            />
        </Grid>

        <Grid size={{ xs: 12, md: 3 }}>
            <TextField select fullWidth label="Rol"
                value={filters.userRole}
                onChange={(e) => onChange({
                    ...filters,
                    userRole: e.target.value as ManagedUserRole | '',
                })}
            >
                <MenuItem value="">Todos</MenuItem>
                <MenuItem value="ADMIN">Administrador</MenuItem>
                <MenuItem value="MANAGER">Manager</MenuItem>
                <MenuItem value="DEVELOPER">Desarrollador</MenuItem>
            </TextField>
        </Grid>

        <Grid size={{ xs: 12, md: 3 }}>
            <TextField select fullWidth label="Estado"
                value={filters.accountStatus}
                onChange={(e) => onChange({
                    ...filters,
                    accountStatus: e.target.value as ManagedUserStatus | '',
                })}
            >
                <MenuItem value="">Todos</MenuItem>
                <MenuItem value="ACTIVE">Activo</MenuItem>
                <MenuItem value="INACTIVE">Inactivo</MenuItem>
            </TextField>
        </Grid>
    </Grid>
  );
};

export default UsersFilters;