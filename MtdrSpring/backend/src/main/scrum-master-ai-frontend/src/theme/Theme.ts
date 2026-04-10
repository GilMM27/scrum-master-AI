import { createTheme } from "@mui/material/styles";

export const AppTheme = createTheme({
    palette: {
        primary: {
            main: '#1565C0',
        },
        secondary: {
            main: '#6A1B9A',
        },
        background: {
            default: '#F4F4F4',
            paper: '#FFFFFF',
        },
        success: {
            main: '#2E7D32',
        },
        warning: {
            main: '#ED6C02',
        },
        error: {
            main: '#D32F2F',
        },
    },
    typography: {
        fontFamily: `'Inter', 'Roboto', 'Helvetica', 'Arial', sans-serif`,
        h4: {
            fontWeight: 700,
        },
        h5: {
            fontWeight: 700,
        },
        h6: {
            fontWeight: 600,
        },
        button: {
            textTransform: 'none',
            fontWeight: 600,
        },
    },
});