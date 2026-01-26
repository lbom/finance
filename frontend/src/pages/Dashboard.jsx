import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Grid, Paper, Typography, Box, Chip } from '@mui/material';
import { api } from '../api/endpoints.js';

const StatCard = ({ title, value, isLoading }) => (
    <Paper sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <Typography variant="subtitle1" color="text.secondary">{title}</Typography>
        <Typography variant="h3" color="primary.main">{isLoading ? '...' : `$${value?.toLocaleString()}`}</Typography>
    </Paper>
);

export const Dashboard = () => {
    const { data: persons, isLoading: personsLoading } = useQuery({ queryKey: ['persons'], queryFn: api.person.list });
    const activePersonId = persons?.[0]?.id;

    const { data: tradeProfit, isLoading: l1 } = useQuery({
        queryKey: ['tradeProfit', activePersonId],
        queryFn: () => api.trades.profit(activePersonId),
        enabled: !!activePersonId,
    });
    const { data: investProfit, isLoading: l2 } = useQuery({
        queryKey: ['investProfit', activePersonId],
        queryFn: () => api.invest.profit(activePersonId),
        enabled: !!activePersonId,
    });
    const { data: balances, isLoading: balancesLoading } = useQuery({
        queryKey: ['balances', activePersonId],
        queryFn: () => api.balance.list(activePersonId),
        enabled: !!activePersonId,
    });
    const { data: currencies } = useQuery({
        queryKey: ['currencies'],
        queryFn: api.dictionary.currency.list
    });

    const currencyMap = new Map((currencies || []).map((c) => [c.id, c]));

    return (
        <Box>
            <Typography variant="h4" gutterBottom>Financial Overview</Typography>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}><StatCard title="Trade Profits" value={tradeProfit} isLoading={l1 || personsLoading} /></Grid>
                <Grid item xs={12} md={6}><StatCard title="Investment Returns" value={investProfit} isLoading={l2 || personsLoading} /></Grid>
                <Grid item xs={12}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="subtitle1" color="text.secondary">Balances</Typography>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 2 }}>
                            {(balancesLoading || personsLoading) && (
                                <Typography variant="body2" color="text.secondary">Loading balances…</Typography>
                            )}
                            {!balancesLoading && (balances || []).length === 0 && (
                                <Typography variant="body2" color="text.secondary">No balances yet.</Typography>
                            )}
                            {(balances || []).map((balance) => {
                                const currency = currencyMap.get(balance.currencyId);
                                const label = currency ? currency.symbol : '—';
                                return (
                                    <Chip
                                        key={balance.id}
                                        label={`${label} ${Number(balance.amount).toLocaleString()}`}
                                        sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }}
                                    />
                                );
                            })}
                        </Box>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};