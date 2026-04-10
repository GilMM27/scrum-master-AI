import React from 'react';
import { Container, Paper } from '@mui/material';

const Login = () => {
  return (
    <Container component="main" maxWidth="xxl" sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
    }}>
      <Paper>
        Log In
      </Paper>
    </Container>
  )
}

export default Login