import { alpha, FormControl, IconButton, MenuItem, Paper, Select, Stack, Table, TableBody, TableCell, TableHead, TableRow, Tooltip, Typography } from "@mui/material";
import { VisibilityRounded } from "@mui/icons-material";
import type { ManagedUserRole, ManagedUserStatus, UserSummary } from "../types/users.types";

interface UsersTableProps {
  users: UserSummary[];
  onViewDetails: (user: UserSummary) => void;
  onChangeRole: (user: UserSummary, nextRole: ManagedUserRole) => void;
  onChangeStatus: (user: UserSummary, nextStatus: ManagedUserStatus) => void;
  busyUserId?: string | null;
}

const getRoleLabel = (role: ManagedUserRole) => {
  switch (role) {
    case "ADMIN":
      return "Administrador";
    case "MANAGER":
      return "Manager";
    case "DEVELOPER":
      return "Desarrollador";
    default:
      return role;
  }
};

const getStatusLabel = (status: ManagedUserStatus) => {
  switch (status) {
    case "ACTIVE":
      return "Activo";
    case "INACTIVE":
      return "Inactivo";
    default:
      return status;
  }
};

const UsersTable = ({
  users,
  onViewDetails,
  onChangeRole,
  onChangeStatus,
  busyUserId,
}: UsersTableProps) => {
  return (
    <Paper
      sx={{ borderRadius: 2, overflow: "hidden", borderColor: "divider" }}
      variant="outlined"
    >
      <Table>
        <TableHead>
          <TableRow sx={{ bgcolor: "primary.dark" }}>
            <TableCell align="center">Acciones</TableCell>
            <TableCell align="center">Username</TableCell>
            <TableCell align="center">Email</TableCell>
            <TableCell align="center">Rol</TableCell>
            <TableCell align="center">Estado</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {users.length === 0 ? (
            <TableRow>
              <TableCell colSpan={5} align="center">
                <Typography variant="body1" color="textSecondary">
                  No se encontraron usuarios.
                </Typography>
              </TableCell>
            </TableRow>
          ) : (
            users.map((user) => {
              const isBusy = busyUserId === user.userId;
              const isAdmin = user.userRole === "ADMIN";

              return (
                <TableRow key={user.userId} hover>
                  <TableCell align="center">
                    <Tooltip title="Ver detalles">
                      <IconButton
                        size="small"
                        onClick={() => onViewDetails(user)}
                        disabled={isBusy}
                      >
                        <VisibilityRounded sx={{ '&:hover': { color: alpha('#b4bdc7', 0.8) } }} />
                      </IconButton>
                    </Tooltip>
                  </TableCell>

                  <TableCell align="center">{user.username}</TableCell>
                  <TableCell align="center">{user.email}</TableCell>

                  <TableCell align="center" sx={{ minWidth: 180 }}>
                    <Stack spacing={0.75} sx={{ alignItems: 'flex-start'}}>
                        <FormControl
                            fullWidth
                            size="small"
                            disabled={isBusy || isAdmin}
                        >
                        <Select
                            value={user.userRole}
                            onChange={(e) =>
                            onChangeRole(user, e.target.value as ManagedUserRole)
                            }
                        >
                            <MenuItem value="ADMIN">
                            {getRoleLabel("ADMIN")}
                            </MenuItem>
                            <MenuItem value="MANAGER">
                            {getRoleLabel("MANAGER")}
                            </MenuItem>
                            <MenuItem value="DEVELOPER">
                            {getRoleLabel("DEVELOPER")}
                            </MenuItem>
                        </Select>
                        </FormControl>
                        {isAdmin && (
                        <Typography variant="caption" color="textSecondary">
                            Rol Protegido
                        </Typography>
                        )}
                    </Stack>
                  </TableCell>

                  <TableCell align="center" sx={{ minWidth: 160 }}>
                    <Stack spacing={0.75} sx={{ alignItems: 'flex-start'}}>
                        <FormControl
                            fullWidth
                            size="small"
                            disabled={isBusy || isAdmin}
                        >
                        <Select
                            value={user.accountStatus}
                            onChange={(e) =>
                            onChangeStatus(
                                user,
                                e.target.value as ManagedUserStatus,
                            )
                            }
                        >
                            <MenuItem value="ACTIVE">
                            {getStatusLabel("ACTIVE")}
                            </MenuItem>
                            <MenuItem value="INACTIVE">
                            {getStatusLabel("INACTIVE")}
                            </MenuItem>
                        </Select>
                        </FormControl>
                        {isAdmin && (
                        <Typography variant="caption" color="textSecondary">
                            Estado Protegido
                        </Typography>
                        )}
                    </Stack>
                  </TableCell>
                </TableRow>
              );
            })
          )}
        </TableBody>
      </Table>
    </Paper>
  );
};

export default UsersTable;
