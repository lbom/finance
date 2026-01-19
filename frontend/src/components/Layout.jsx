import React from 'react';
import { Box, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography, AppBar } from '@mui/material';
import { ShowChart, Dashboard as DashboardIcon } from '@mui/icons-material';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import { Logout } from '@mui/icons-material';
// import { useAuth } from '../context/AuthContext';

const drawerWidth = 240;

export const Layout = () => {
    const navigate = useNavigate();
    const location = useLocation();
    // const { logout } = useAuth();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const menuItems = [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
        { text: 'Trades', icon: <ShowChart />, path: '/trades' },
    ];

    return (
        <Box sx={{ display: 'flex' }}>
            <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, bgcolor: 'primary.dark' }} elevation={0}>
                <Toolbar>
                    <Typography variant="h6" noWrap component="div">GreenFinance</Typography>
                </Toolbar>
            </AppBar>
            <Drawer
                variant="permanent"
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box', borderRight: 'none', bgcolor: 'background.default' },
                }}
            >
                <Toolbar />
                <Box sx={{ overflow: 'auto', mt: 2 }}>
                    <List>
                        {menuItems.map((item) => (
                            <ListItem key={item.text} disablePadding>
                                <ListItemButton
                                    selected={location.pathname === item.path}
                                    onClick={() => navigate(item.path)}
                                    sx={{
                                        borderRadius: '0 24px 24px 0', mr: 2,
                                        '&.Mui-selected': { bgcolor: 'secondary.light', color: 'primary.dark' }
                                    }}
                                >
                                    <ListItemIcon sx={{ color: location.pathname === item.path ? 'primary.dark' : 'inherit' }}>{item.icon}</ListItemIcon>
                                    <ListItemText primary={item.text} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>
                </Box>
                <Box sx={{ mt: 'auto', mb: 2 }}>
                    <ListItem disablePadding>
                        <ListItemButton onClick={handleLogout} sx={{ borderRadius: '0 24px 24px 0', mr: 2 }}>
                            <ListItemIcon><Logout /></ListItemIcon>
                            <ListItemText primary="Logout" />
                        </ListItemButton>
                    </ListItem>
                </Box>
            </Drawer>
            <Box component="main" sx={{ flexGrow: 1, p: 3, bgcolor: 'background.default', minHeight: '100vh' }}>
                <Toolbar />
                <Outlet />
            </Box>
        </Box>
    );
};