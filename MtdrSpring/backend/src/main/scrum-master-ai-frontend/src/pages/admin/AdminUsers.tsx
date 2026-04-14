import { useEffect, useMemo, useState } from 'react'
import type { CreateManagedUserRequest, ManagedUserRole, ManagedUserStatus, UserDetail, UsersFilterState, UserSummary } from '../../features/users/types/users.types'
import { createManagedUser, getAllUsers, getUserById, updateUserAuthorization, updateUserRole } from '../../features/users/services/users.services';
import DashboardLayout from '../../layouts/DashboardLayout';
import { Alert, alpha, Box, Button, CircularProgress, Snackbar, Stack, Typography } from '@mui/material';
import UsersFilters from '../../features/users/components/UsersFilters';
import UsersTable from '../../features/users/components/UsersTable';
import UserDetailDialog from '../../features/users/components/UserDetailDialog';
import { PersonAddAlt1Rounded } from '@mui/icons-material';
import CreateUserDialog from '../../features/users/components/CreateUserDialog';

const AdminUsers = () => {
  const [users, setUsers] = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyUserId, setBusyUserId] = useState<string | null>(null);
  const [createLoading, setCreateLoading] = useState(false);

  const [filters, setFilters] = useState<UsersFilterState>({
    search: '',
    userRole: '',
    accountStatus: '',
  });

  const [selectedUserDetail, setSelectedUserDetail] = useState<UserDetail | null>(null);
  const [detailOpen, setDetailOpen] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);

  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await getAllUsers();
      setUsers(response);
    } catch (error) {
      setErrorMsg(error instanceof Error ? error.message : 'No fue posible cargar los usuarios.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchUsers();
  }, []);

  const filteredUsers = useMemo(() => {
    const searchValue = filters.search.toLowerCase();

    return users.filter((user) => {
      const matchesSearch =
        !searchValue ||
        user.username.toLowerCase().includes(searchValue) ||
        user.email.toLowerCase().includes(searchValue);

      const matchesRole = !filters.userRole || user.userRole === filters.userRole;
      const matchesStatus = !filters.accountStatus || user.accountStatus === filters.accountStatus;

      return matchesSearch && matchesRole && matchesStatus;
    });
  }, [users, filters]);

  const handleViewDetails = async (user: UserSummary) => {
    try {
      setBusyUserId(user.userId);
      const detail = await getUserById(user.userId);
      setSelectedUserDetail(detail);
      setDetailOpen(true);
    } catch (error) {
      setErrorMsg(error instanceof Error ? error.message : 'No fue posible cargar los detalles del usuario.');
    } finally {
      setBusyUserId(null);
    }
  };

  const handleChangeRole = async (user: UserSummary, nextRole: ManagedUserRole) => {
    if (user.userRole === nextRole) return;

    try {
      setBusyUserId(user.userId);
      const updated = await updateUserRole(user.userId, { userRole: nextRole });

      setUsers((prev) => 
        prev.map((item) =>
          item.userId === updated.userId
            ? {
                ...item,
                userRole: updated.userRole,
                accountStatus: updated.accountStatus,
              }
            : item
        )
      );

      setSuccessMsg('Rol actualizado correctamente.');
    } catch (error) {
      setErrorMsg(error instanceof Error ? error.message : 'No fue posible actualizar el rol.');
    } finally {
      setBusyUserId(null);
    }
  };

  const handleChangeStatus = async (user: UserSummary, nexStatus: ManagedUserStatus) => {
    if (user.accountStatus === nexStatus) return;

    try {
      setBusyUserId(user.userId);
      const updated = await updateUserAuthorization(user.userId, { accountStatus: nexStatus });

      setUsers((prev) => 
        prev.map((item) =>
          item.userId === updated.userId
            ? {
                ...item,
                userRole: updated.userRole,
                accountStatus: updated.accountStatus,
              }
            : item
        )
      );

      setSuccessMsg('Estado de autorización actualizado correctamente.');
    } catch (error) {
      setErrorMsg(error instanceof Error ? error.message : 'No fue posible actualizar el estado de autorización.');
    } finally {
      setBusyUserId(null);
    }
  };

  const handleCreateUser = async (payload: CreateManagedUserRequest) => {
    try {
      setCreateLoading(true);
      const created = await createManagedUser(payload);
      setUsers((prev) => [...prev, {
        userId: created.userId,
        username: created.username,
        email: created.email,
        userRole: created.userRole,
        accountStatus: created.accountStatus,
      }]);
      setCreateOpen(false);
      setSuccessMsg('Usuario creado exitosamente.');
      await fetchUsers();
    } catch (error) {
      setErrorMsg(error instanceof Error ? error.message : 'No fue posible crear el usuario.');
    } finally {
      setCreateLoading(false);
    }
  };

  return (
    <DashboardLayout>
      <Stack spacing={3}>
        <Box>
          <Typography variant="h4" gutterBottom>
            Gestión de Usuarios
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Supervisa los usuarios registrados y gestiona sus roles y autorizaciones desde este panel.
          </Typography>
        </Box>

        <Stack direction={{ xs: 'column', lg: 'row' }} spacing={2} sx={{ justifyContent: 'space-between', alignItems: { xs: 'stretch', lg: 'center' } }}>
          <Box sx={{ flex: 1 }}>
            <UsersFilters filters={filters} onChange={setFilters} />
          </Box>

          <Button variant='contained' size='large' startIcon={<PersonAddAlt1Rounded />} onClick={() => setCreateOpen(true)} sx={{ '&:hover': { color: alpha('#b4bdc7', 0.8) }  }}>
            Crear Usuario
          </Button>
        </Stack>

        {loading ? (
          <Box sx={{ py: 8, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        ) : (
          <UsersTable users={filteredUsers} 
            onViewDetails={handleViewDetails} 
            onChangeRole={handleChangeRole} 
            onChangeStatus={handleChangeStatus} 
            busyUserId={busyUserId} 
          />
        )}
      </Stack>

      <CreateUserDialog open={createOpen} 
        onClose={() => setCreateOpen(false)}
        loading={createLoading}
        onSubmit={handleCreateUser}
      />

      <UserDetailDialog open={detailOpen}
        userDetail={selectedUserDetail}
        onClose={() => { setDetailOpen(false); setSelectedUserDetail(null); }}
      />

      <Snackbar
        open={Boolean(successMsg)}
        autoHideDuration={3500}
        onClose={() => setSuccessMsg('')}
      >
        <Alert onClose={() => setSuccessMsg('')} severity="success">
          {successMsg}
        </Alert>
      </Snackbar>

      <Snackbar
        open={Boolean(errorMsg)}
        autoHideDuration={4000}
        onClose={() => setErrorMsg('')}
      >
        <Alert onClose={() => setErrorMsg('')} severity="error">
          {errorMsg}
        </Alert>
      </Snackbar>
    </DashboardLayout>
  );
};

export default AdminUsers;