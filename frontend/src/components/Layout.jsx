import React from 'react';
import { Box, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Typography, Avatar } from '@mui/material';
import { Wallet, PieChart, ShowChart, Dashboard as DashboardIcon, Logout, AccountBalanceWallet } from '@mui/icons-material';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // Ensure this path is correct

const drawerWidth = 260; // Slightly wider for better breathing room

export const Layout = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { logout } = useAuth(); // Assuming you have this context

    const handleLogout = () => {
        logout(); // Call the context logout
        navigate('/login');
    };

    const menuItems = [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
        { text: 'Personal', icon: <Wallet />, path: '/personal' },
        { text: 'Trades', icon: <ShowChart />, path: '/trades' },
        { text: 'Investments', icon: <PieChart />, path: '/investments' },
    ];

    return (
        <Box sx={{ display: 'flex', bgcolor: 'background.default', minHeight: '100vh' }}>

            {/* --- SIDEBAR --- */}
            <Drawer
                variant="permanent"
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    [`& .MuiDrawer-paper`]: {
                        width: drawerWidth,
                        boxSizing: 'border-box',
                        borderRight: 'none', // Remove harsh line
                        bgcolor: '#FFFFFF', // Clean white sidebar
                        boxShadow: '4px 0 24px rgba(0,0,0,0.02)', // Soft separation shadow
                        p: 2
                    },
                }}
            >
                {/* LOGO AREA */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, px: 2, py: 3, mb: 2 }}>
                    <Avatar sx={{ bgcolor: 'primary.main', width: 40, height: 40, variant: 'rounded' }}>
                        <AccountBalanceWallet fontSize="small" />
                    </Avatar>
                    <Box>
                        <Typography variant="h6" fontWeight="800" color="primary.main" lineHeight={1.2}>
                            Green
                        </Typography>
                        <Typography variant="caption" color="text.secondary" fontWeight="500">
                            Money Tracker
                        </Typography>
                    </Box>
                </Box>

                {/* MENU ITEMS */}
                <List sx={{ px: 1 }}>
                    {menuItems.map((item) => {
                        const active = location.pathname === item.path;
                        return (
                            <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
                                <ListItemButton
                                    selected={active}
                                    onClick={() => navigate(item.path)}
                                    sx={{
                                        borderRadius: 3, // Pill shape
                                        py: 1.5,
                                        transition: 'all 0.2s',
                                        bgcolor: active ? 'secondary.main' : 'transparent',
                                        color: active ? 'primary.dark' : 'text.secondary',
                                        '&:hover': {
                                            bgcolor: active ? 'secondary.main' : 'rgba(0,0,0,0.03)',
                                            transform: 'translateX(4px)'
                                        },
                                        '&.Mui-selected': {
                                            bgcolor: 'secondary.main',
                                            '&:hover': { bgcolor: 'secondary.main' }
                                        }
                                    }}
                                >
                                    <ListItemIcon sx={{
                                        minWidth: 40,
                                        color: active ? 'primary.main' : 'text.secondary'
                                    }}>
                                        {item.icon}
                                    </ListItemIcon>
                                    <ListItemText
                                        primary={item.text}
                                        primaryTypographyProps={{
                                            fontWeight: active ? 700 : 500,
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </ListItemButton>
                            </ListItem>
                        );
                    })}
                </List>

                {/* LOGOUT AT BOTTOM */}
                <Box sx={{ mt: 'auto', borderTop: '1px solid #F0F2F5', pt: 2 }}>
                    <ListItemButton onClick={handleLogout} sx={{ borderRadius: 3, color: 'text.secondary', '&:hover': { color: 'error.main', bgcolor: 'error.lighter' } }}>
                        <ListItemIcon><Logout sx={{ color: 'inherit' }} /></ListItemIcon>
                        <ListItemText primary="Sign Out" primaryTypographyProps={{ fontWeight: 600 }} />
                    </ListItemButton>
                </Box>
            </Drawer>

            {/* --- MAIN CONTENT AREA --- */}
            <Box component="main" sx={{ flexGrow: 1, p: 4, width: `calc(100% - ${drawerWidth}px)` }}>
                {/* No Toolbar spacer needed anymore since we removed the top AppBar */}
                <Outlet />
            </Box>
        </Box>
    );
};