import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { theme } from './theme';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Layout } from './components/Layout';
import { Dashboard } from './pages/Dashboard';
import { Trades } from './pages/Trades';
import { Login } from './pages/Login';
import { Investments } from "./pages/Investments";

const queryClient = new QueryClient();

// FIX: Use <Outlet /> instead of children for Route wrapping
const ProtectedRoute = () => {
    const { isAuthenticated } = useAuth();
    // If authenticated, render the child routes (The Layout), otherwise redirect
    return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
};

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <AuthProvider>
                    <BrowserRouter>
                        <Routes>
                            {/* Public Route */}
                            <Route path="/login" element={<Login />} />

                            {/* Guarded Routes */}
                            <Route element={<ProtectedRoute />}>
                                {/* The Layout is rendered only if Auth passes */}
                                <Route path="/" element={<Layout />}>
                                    <Route index element={<Dashboard />} />
                                    <Route path="trades" element={<Trades />} />
                                    <Route path="investments" element={<Investments />} />
                                </Route>
                            </Route>

                            {/* Catch-all Redirect */}
                            <Route path="*" element={<Navigate to="/" replace />} />
                        </Routes>
                    </BrowserRouter>
                </AuthProvider>
            </ThemeProvider>
        </QueryClientProvider>
    );
}

export default App;