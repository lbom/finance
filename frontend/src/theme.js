import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
    palette: {
        primary: {
            main: '#135D36', // Deep Forest Green (Professional, Trust)
            light: '#4EA674',
            dark: '#0A3F22',
            contrastText: '#ffffff',
        },
        secondary: {
            main: '#E8F5E9', // Very light mint for backgrounds/hover
            dark: '#A5D6A7', // Accent for active states
            contrastText: '#135D36',
        },
        background: {
            default: '#F4F6F4', // A very subtle grey-green neutral
            paper: '#FFFFFF',
        },
        text: {
            primary: '#1A2C24', // Almost black, but slightly green-tinted
            secondary: '#5C7066',
        },
    },
    typography: {
        fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', // Try Google Font: Plus Jakarta Sans for a fintech look
        h4: { fontWeight: 700, letterSpacing: '-0.5px', color: '#135D36' },
        h6: { fontWeight: 600 },
        button: { fontWeight: 600, letterSpacing: '0.2px' },
    },
    shape: {
        borderRadius: 16, // Modern, softer corners
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                    textTransform: 'none',
                    padding: '10px 24px',
                    boxShadow: 'none',
                    '&:hover': { boxShadow: '0px 4px 12px rgba(19, 93, 54, 0.2)' },
                },
                contained: {
                    background: 'linear-gradient(135deg, #135D36 0%, #1E7E4A 100%)', // Subtle gradient
                },
            },
        },
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundImage: 'none',
                    boxShadow: '0px 2px 4px rgba(0,0,0,0.02), 0px 8px 24px rgba(0,0,0,0.04)', // Soft, high-end shadow
                    border: '1px solid rgba(0,0,0,0.03)',
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    border: 'none',
                    '& .MuiDataGrid-columnHeaders': {
                        backgroundColor: '#F9FAFB',
                        color: '#5C7066',
                        fontSize: '0.75rem',
                        textTransform: 'uppercase',
                        letterSpacing: '1px',
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: '#F4FBF6', // Very light mint hover
                    },
                    '& .MuiDataGrid-cell': {
                        borderBottom: '1px solid #F0F2F5',
                    },
                },
            },
        },
    },
});