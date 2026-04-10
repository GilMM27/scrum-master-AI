import { Box, Button, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const Unauthorized = () => {
    const navigate = useNavigate();

    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h4" gutterBottom>
                Acceso no autorizado
            </Typography>
            <Typography variant="body1" gutterBottom>
                No tienes permiso para acceder a esta página.
            </Typography>
            <Button variant="contained" color="primary" onClick={() => navigate('/login')}>
                Ir a Login
            </Button>
        </Box>
    )
}

export default Unauthorized;