import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Grid, Paper, Typography, Box, Chip } from '@mui/material';
import { api } from '../api/endpoints.js';

const StatCard = ({ title, value, isLoading, color = 'primary.main' }) => {
    const displayValue = value == null ? '—' : `${Number(value).toLocaleString()}`;
    return (
        <Paper sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <Typography variant="subtitle1" color="text.secondary">{title}</Typography>
            <Typography variant="h3" color={color}>{isLoading ? '...' : displayValue}</Typography>
        </Paper>
    );
};

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
    const usdCurrencyId = (currencies || []).find((c) => c.symbol?.toUpperCase() === 'USD')?.id;
    const usdtCurrencyId = (currencies || []).find((c) => c.symbol?.toUpperCase() === 'USDT')?.id;
    const regularBalances = (balances || []).filter((b) => (b.type || 'REGULAR') === 'REGULAR');
    const cryptoBalances = (balances || []).filter((b) => b.type === 'CRYPTO');

    const { data: totalUsd, isLoading: totalUsdLoading } = useQuery({
        queryKey: ['balances', activePersonId, 'sumAll', 'USD', usdCurrencyId],
        queryFn: () => api.balance.sumAll(activePersonId, usdCurrencyId, 'REGULAR'),
        enabled: !!activePersonId && !!usdCurrencyId,
    });
    const { data: totalUsdt, isLoading: totalUsdtLoading } = useQuery({
        queryKey: ['balances', activePersonId, 'sumAll', 'USDT', usdtCurrencyId],
        queryFn: () => api.balance.sumAll(activePersonId, usdtCurrencyId, 'CRYPTO'),
        enabled: !!activePersonId && !!usdtCurrencyId,
    });
    const { data: subscriptionsTotal, isLoading: subscriptionsLoading } = useQuery({
        queryKey: ['recurrent', activePersonId, 'sumSubscriptions'],
        queryFn: () => api.recurrent.sumSubscriptions(activePersonId),
        enabled: !!activePersonId,
    });

    return (
        <Box>
            <Typography variant="h4" gutterBottom>Financial Overview</Typography>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}><StatCard title="Trade Profits" value={tradeProfit} isLoading={l1 || personsLoading} /></Grid>
                <Grid item xs={12} md={6}><StatCard title="Investment Returns" value={investProfit} isLoading={l2 || personsLoading} /></Grid>
                <Grid item xs={12} md={6}><StatCard title="Total Balances (USD)" value={totalUsd} isLoading={totalUsdLoading || personsLoading} /></Grid>
                <Grid item xs={12} md={6}><StatCard title="Total Balances (USDT)" value={totalUsdt} isLoading={totalUsdtLoading || personsLoading} /></Grid>
                <Grid item xs={12} md={6}><StatCard title="Monthly Subscriptions" value={subscriptionsTotal} isLoading={subscriptionsLoading || personsLoading} color="error.main" /></Grid>
                <Grid item xs={12}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="subtitle1" color="text.secondary">Balances</Typography>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 2, mb: 2 }}>
                            {(balancesLoading || personsLoading) && (
                                <Typography variant="body2" color="text.secondary">Loading balances…</Typography>
                            )}
                            {!balancesLoading && regularBalances.length === 0 && (
                                <Typography variant="body2" color="text.secondary">No balances yet.</Typography>
                            )}
                            {regularBalances.map((balance) => {
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
                        <Typography variant="subtitle1" color="text.secondary">Crypto Balances</Typography>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 2 }}>
                            {(balancesLoading || personsLoading) && (
                                <Typography variant="body2" color="text.secondary">Loading crypto balances…</Typography>
                            )}
                            {!balancesLoading && cryptoBalances.length === 0 && (
                                <Typography variant="body2" color="text.secondary">No crypto balances yet.</Typography>
                            )}
                            {cryptoBalances.map((balance) => {
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