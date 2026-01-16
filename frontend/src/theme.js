import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
    palette: {
        primary: {
            main: '#2E7D32',
            light: '#4CAF50',
            dark: '#1B5E20',
            contrastText: '#ffffff',
        },
        secondary: {
            main: '#81C784',
            contrastText: '#000000',
        },
        background: {
            default: '#F1F8E9',
            paper: '#ffffff',
        },
    },
    typography: {
        fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
        h4: { fontWeight: 600, color: '#1B5E20' },
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: { borderRadius: 8, textTransform: 'none', fontWeight: 600 },
            },
        },
        MuiPaper: {
            styleOverrides: {
                root: { borderRadius: 12, boxShadow: '0px 4px 20px rgba(0,0,0,0.05)' },
            },
        },
    },
});