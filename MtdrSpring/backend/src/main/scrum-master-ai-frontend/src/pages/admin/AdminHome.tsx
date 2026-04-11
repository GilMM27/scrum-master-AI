import { Box, Button, Typography } from '@mui/material';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import { useNavigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

const AdminHome = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    console.log('[Auth] Session cleared — redirecting to /login');
    navigate('/login', { replace: true });
  };

  return (
    <Box>
      <Typography variant='h4'>Admin Home</Typography>
      <Typography variant='body1'>Bienvenido al portal de administradores.</Typography>
      <Button
        variant='outlined'
        color='error'
        startIcon={<LogoutRoundedIcon />}
        onClick={handleLogout}
        sx={{ mt: 2 }}
      >
        Cerrar sesión
      </Button>
    </Box>
  );
};

export default AdminHome;