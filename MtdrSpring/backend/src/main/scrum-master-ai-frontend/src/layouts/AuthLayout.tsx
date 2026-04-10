import type { ReactNode } from 'react';
import { Box, Container, Stack, Avatar, Typography } from '@mui/material';
import HubOutlinedIcon from '@mui/icons-material/HubOutlined';
import { alpha } from '@mui/material/styles';

interface AuthLayoutProps {
  children: ReactNode;
}

const AuthLayout = ({ children }: AuthLayoutProps) => {
  return (
    <Box sx={{ minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      px: 2,
      py: 6,
      bgcolor: 'background.default',
      background: `
        radial-gradient(circle at top left, rgba(0, 33, 166, 0.16), transparent 30%),
        radial-gradient(circle at bottom right, rgba(119, 255, 232, 0.14), transparent 25%),
        linear-gradient(180deg, #000114 0%, #050012 100%)
      `,
      }}
    >
      <Stack spacing={1.5} sx={{ alignItems: "center", textAlign: "center", mb: 4 }}>
        <Avatar
          sx={{
            width: 80,
            height: 80,
            bgcolor: alpha('#0300a6', 0.18),
            border: '1px solid',
            borderColor: alpha('#7794ff', 0.28),
            boxShadow: `0 0 30px ${alpha('#0300a6', 0.24)}`,
          }}
        >
          <HubOutlinedIcon sx={{ fontSize: 40, color: 'info.main' }} />
        </Avatar>
        <Typography variant="h2" sx={{ letterSpacing: '-0.03em' }}>
          AgiFlow
        </Typography>
        <Typography
          variant="body1"
          color="text.secondary"
          sx={{ maxWidth: 420 }}
        >
          Portal de gestión ágil para proyectos de software.
        </Typography>
      </Stack>
      <Container maxWidth="sm">
        <Box sx={{
            width: '100%',
            border: '1px solid',
            borderColor: 'divider',
            borderRadius: 4,
            px: { xs: 1.5, sm: 3 },
            py: { xs: 2, sm: 4 },
            backgroundColor: 'background.paper',
            backdropFilter: 'blur(12px)',
          }}
        >
          {children}
        </Box>
      </Container>
    </Box>
  )
}

export default AuthLayout