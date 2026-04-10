import type { ReactNode } from 'react';
import { Box, Container, Typography, Paper } from '@mui/material';

interface AuthLayoutProps {
  title?: string;
  subtitle?: string;
  children: ReactNode;
}

const AuthLayout = ({ title = "AgiFlow", 
  subtitle = "Portal de gestion agil de proyectos de software", 
  children,
 }: AuthLayoutProps) => {
  return (
    <div>AuthLayout</div>
  )
}

export default AuthLayout