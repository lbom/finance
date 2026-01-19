import React, { useState } from 'react';
import { Box, Paper, Typography, TextField, Button, InputAdornment, Alert } from '@mui/material';
import { AccountCircle, Lock } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        const success = await login(username, password);

        if (success) {
            navigate('/');
        } else {
            setError('Invalid credentials or server error');
            setLoading(false);
        }
    };

    return (
        <Box
            sx={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'background.default'
            }}
        >
            <Paper elevation={3} sx={{ p: 5, width: '100%', maxWidth: 400, borderRadius: 4, textAlign: 'center' }}>
                <Typography variant="h4" color="primary.main" gutterBottom sx={{ fontWeight: 'bold' }}>
                    Finance
                </Typography>
                <Typography variant="body2" color="text.secondary" mb={4}>
                    Sign in to manage your portfolio
                </Typography>

                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                <form onSubmit={handleSubmit}>
                    <TextField
                        fullWidth
                        label="Username"
                        variant="outlined"
                        margin="normal"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        InputProps={{
                            startAdornment: <InputAdornment position="start"><AccountCircle color="action" /></InputAdornment>,
                        }}
                    />
                    <TextField
                        fullWidth
                        label="Password"
                        type="password"
                        variant="outlined"
                        margin="normal"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        InputProps={{
                            startAdornment: <InputAdornment position="start"><Lock color="action" /></InputAdornment>,
                        }}
                    />

                    <Button
                        fullWidth
                        type="submit"
                        variant="contained"
                        size="large"
                        disabled={loading}
                        sx={{ mt: 3, mb: 2, py: 1.5 }}
                    >
                        {loading ? 'Signing In...' : 'Sign In'}
                    </Button>
                </form>
            </Paper>
        </Box>
    );
};