import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Grid, Paper, Typography, Box } from '@mui/material';
import { api } from '../api/client';

const StatCard = ({ title, value, isLoading }) => (
    <Paper sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <Typography variant="subtitle1" color="text.secondary">{title}</Typography>
        <Typography variant="h3" color="primary.main">{isLoading ? '...' : `$${value?.toLocaleString()}`}</Typography>
    </Paper>
);

export const Dashboard = () => {
    const { data: tradeProfit, isLoading: l1 } = useQuery({ queryKey: ['tradeProfit'], queryFn: api.trades.profit });
    const { data: investProfit, isLoading: l2 } = useQuery({ queryKey: ['investProfit'], queryFn: api.invest.profit });

    return (
        <Box>
            <Typography variant="h4" gutterBottom>Financial Overview</Typography>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}><StatCard title="Total Trade Profit" value={tradeProfit} isLoading={l1} /></Grid>
                <Grid item xs={12} md={6}><StatCard title="Investment Returns" value={investProfit} isLoading={l2} /></Grid>
            </Grid>
        </Box>
    );
};