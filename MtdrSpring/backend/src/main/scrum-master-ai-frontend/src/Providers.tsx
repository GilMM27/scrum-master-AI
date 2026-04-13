import type { ReactNode } from "react";
import { ThemeProvider, CssBaseline } from "@mui/material";
import { BrowserRouter } from "react-router-dom";
import AuthProvider from "./context/AuthContext";
import { AppTheme } from "./theme/Theme";

interface AppProvidersProps {
    children: ReactNode;
}

const AppProviders = ({ children }: AppProvidersProps) => {
    return (
        <ThemeProvider theme={AppTheme}>
            <CssBaseline />
            <BrowserRouter>
                <AuthProvider>
                    {children}
                </AuthProvider>
            </BrowserRouter>
        </ThemeProvider>
    )
}

export default AppProviders;