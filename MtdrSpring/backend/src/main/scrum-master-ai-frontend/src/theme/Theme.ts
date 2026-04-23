import { alpha, createTheme } from "@mui/material/styles";

export const AppTheme = createTheme({
  palette: {
    mode: "dark",
    primary: {
      main: "#02102f",
      light: "#0b2857",
      dark: "#01021a",
      contrastText: "#FFFFFF",
    },
    secondary: {
      main: "#1f73d4",
      light: "#4272aa",
      dark: "#02234a",
      contrastText: "#0e75eb",
    },
    info: {
      main: "#77ffc0",
      light: "#a7ffc9",
      dark: "#3be5e8",
      contrastText: "#081218",
    },
    success: {
      main: "#4ef770",
      light: "#86ffb0",
      dark: "#1dbb4a",
      contrastText: "#071408",
    },
    warning: {
      main: "#FFD166",
      light: "#FFE29B",
      dark: "#D6A63A",
      contrastText: "#1A1204",
    },
    error: {
      main: "#FF5C8A",
      light: "#FF8CAD",
      dark: "#D93A67",
      contrastText: "#19060D",
    },
    background: {
      default: "#000214",
      paper: "#04041f",
    },
    text: {
      primary: "#F5F7FF",
      secondary: "#b4bdc7",
    },
    divider: alpha("#77fffa", 0.16),
  },
  shape: {
    borderRadius: 12,
  },
  typography: {
    fontFamily: `'Inter', sans-serif`,
    h1: {
      fontFamily: `'Space Grotesk', sans-serif`,
      fontWeight: 700,
    },
    h2: {
      fontFamily: `'Space Grotesk', sans-serif`,
      fontWeight: 700,
    },
    h3: {
      fontFamily: `'Space Grotesk', sans-serif`,
      fontWeight: 700,
    },
    h4: {
      fontFamily: `'Space Grotesk', sans-serif`,
      fontWeight: 700,
    },
    h5: {
      fontFamily: `'Space Grotesk', sans-serif`,
      fontWeight: 700,
    },
    h6: {
      fontFamily: `'Space Grotesk', sans-serif`,
      fontWeight: 700,
    },
    subtitle1: {
      fontFamily: `'Inter', sans-serif`,
      fontWeight: 500,
    },
    subtitle2: {
      fontFamily: `'Inter', sans-serif`,
      fontWeight: 600,
    },
    body1: {
      fontFamily: `'Inter', sans-serif`,
    },
    body2: {
      fontFamily: `'Inter', sans-serif`,
    },
    button: {
      fontFamily: `'Inter', sans-serif`,
      fontWeight: 600,
      textTransform: "none",
    },
    caption: {
      fontFamily: `'Inter', sans-serif`,
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          border: `1px solid ${alpha("#77fffa", 0.12)}`,
          boxShadow: `0 10px 40px ${alpha("#000000", 0.32)}`,
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: 14,
          backgroundColor: alpha("#FFFFFF", 0.02),
          transition: "all 0.2s ease",
          "&:hover .MuiOutlinedInput-notchedOutline": {
            borderColor: alpha("#77fffa", 0.5),
          },
          "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
            borderColor: "#77fffa",
            boxShadow: `0 0 0 2px ${alpha("#77fffa", 0.12)}`,
          },
        },
        notchedOutline: {
          borderColor: alpha("#77fffa", 0.2),
        },
        input: {
          color: "#F5F7FF",
        },
      },
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          color: "#b4b9c7",
          "&.Mui-focused": {
            color: "#77fffa",
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 14,
          paddingTop: 12,
          paddingBottom: 12,
        },
      },
      variants: [
        {
          props: { variant: "contained", color: "primary" },
          style: {
            background: "linear-gradient(135deg, #0300a6 0%, #7794ff 100%)",
            color: "#F5F7FF",
            boxShadow: `0 8px 24px ${alpha("#0300a6", 0.35)}`,
          },
        },
        {
          props: { variant: "outlined" },
          style: ({ theme }) => ({
            borderColor: theme.palette.info.main,
            color: theme.palette.info.main,
            backgroundColor: alpha(theme.palette.info.main, 0.08),
            "&:hover": {
              backgroundColor: alpha(theme.palette.info.main, 0.16),
              borderColor: theme.palette.info.main,
            },
          }),
        },
      ],
    },
    MuiLink: {
      styleOverrides: {
        root: {
          color: "#77fffa",
          textDecorationColor: alpha("#77fffa", 0.5),
          "&:hover": {
            color: "#a7fffe",
          },
        },
      },
    },
  },
});
