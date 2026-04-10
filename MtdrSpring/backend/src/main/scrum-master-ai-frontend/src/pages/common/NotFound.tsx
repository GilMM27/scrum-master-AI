import { Box, Typography } from '@mui/material';

const NotFound = () => {
    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h4" gutterBottom>
                404
            </Typography>
            <Typography variant="body1" gutterBottom>
                Pagina no encontrada.
            </Typography>
        </Box>
    )
}

export default NotFound;