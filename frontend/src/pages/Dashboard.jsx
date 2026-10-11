import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Grid, Paper, Typography, Box, Chip } from '@mui/material';
import { api } from '../api/endpoints.js';

const StatCard = ({ title, value, isLoading, color = 'primary.main' }) => {
    const displayValue = value == null ? '—' : Number(value).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
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
    const cryptoCurrencySymbols = new Set(['BTC', 'ETH', 'SOL', 'BNB', 'USDT']);
    const isCryptoBalance = (b) => {
        if (b.type === 'CRYPTO') return true;
        const sym = currencyMap.get(b.currencyId)?.symbol?.toUpperCase();
        return cryptoCurrencySymbols.has(sym);
    };
    const regularBalances = (balances || []).filter((b) => (b.type || 'REGULAR') === 'REGULAR' && !isCryptoBalance(b));
    const cryptoBalances = (balances || []).filter(isCryptoBalance);
    const depositBalances = (balances || []).filter((b) => b.type === 'DEPOSIT');

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
    const { data: totalDepositsUsd, isLoading: totalDepositsUsdLoading } = useQuery({
        queryKey: ['balances', activePersonId, 'sumAll', 'DEPOSIT', usdCurrencyId],
        queryFn: () => api.balance.sumAll(activePersonId, usdCurrencyId, 'DEPOSIT'),
        enabled: !!activePersonId && !!usdCurrencyId,
    });
    const { data: depositProfit, isLoading: depositProfitLoading } = useQuery({
        queryKey: ['balances', activePersonId, 'depositProfit', usdCurrencyId],
        queryFn: () => api.balance.depositProfit(activePersonId, usdCurrencyId),
        enabled: !!activePersonId,
    });
    const { data: subscriptionsTotal, isLoading: subscriptionsLoading } = useQuery({
        queryKey: ['recurrent', activePersonId, 'sumSubscriptions'],
        queryFn: () => api.recurrent.sumSubscriptions(activePersonId),
        enabled: !!activePersonId,
    });
    const { data: foodStoreMonthly, isLoading: foodStoreLoading } = useQuery({
        queryKey: ['personal', activePersonId, 'monthlyExpenses', 'FOOD_STORE'],
        queryFn: () => api.personal.monthlyExpenses(activePersonId, 'FOOD_STORE'),
        enabled: !!activePersonId,
    });
    const { data: foodRestaurantMonthly, isLoading: foodRestaurantLoading } = useQuery({
        queryKey: ['personal', activePersonId, 'monthlyExpenses', 'FOOD_RESTAURANT'],
        queryFn: () => api.personal.monthlyExpenses(activePersonId, 'FOOD_RESTAURANT'),
        enabled: !!activePersonId,
    });

    return (
        <Box>
            <Typography variant="h4" gutterBottom>Financial Overview</Typography>
            <Grid container spacing={3}>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Trade Profits" value={tradeProfit} isLoading={l1 || personsLoading} /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Investment Returns" value={investProfit} isLoading={l2 || personsLoading} /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Deposit Profit (USD)" value={depositProfit} isLoading={depositProfitLoading || personsLoading} color="success.main" /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Total Deposits (USD)" value={totalDepositsUsd} isLoading={totalDepositsUsdLoading || personsLoading} color="info.main" /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Total Balances (USD)" value={totalUsd} isLoading={totalUsdLoading || personsLoading} /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Total Balances (USDT)" value={totalUsdt} isLoading={totalUsdtLoading || personsLoading} /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Monthly Subscriptions" value={subscriptionsTotal} isLoading={subscriptionsLoading || personsLoading} color="error.main" /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Food Store Monthly Expenses" value={foodStoreMonthly} isLoading={foodStoreLoading || personsLoading} color="error.main" /></Grid>
                <Grid item xs={12} sm={6} md={4}><StatCard title="Food Restaurant Monthly Expenses" value={foodRestaurantMonthly} isLoading={foodRestaurantLoading || personsLoading} color="error.main" /></Grid>
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
                                        label={`${label} ${Number(balance.amount).toLocaleString('en-US', { maximumFractionDigits: 2 })}`}
                                        sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }}
                                    />
                                );
                            })}
                        </Box>

                        <Typography variant="subtitle1" color="text.secondary">Deposit Balances</Typography>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 2, mb: 2 }}>
                            {(balancesLoading || personsLoading) && (
                                <Typography variant="body2" color="text.secondary">Loading deposit balances…</Typography>
                            )}
                            {!balancesLoading && depositBalances.length === 0 && (
                                <Typography variant="body2" color="text.secondary">No deposit balances yet.</Typography>
                            )}
                            {depositBalances.map((balance) => {
                                const currency = currencyMap.get(balance.currencyId);
                                const label = currency ? currency.symbol : '—';
                                const rateLabel = balance.interestRate ? ` (${balance.interestRate}% APY)` : '';
                                return (
                                    <Chip
                                        key={balance.id}
                                        label={`${label} ${Number(balance.amount).toLocaleString('en-US', { maximumFractionDigits: 2 })}${rateLabel}`}
                                        sx={{ bgcolor: 'info.main', color: '#fff', fontWeight: 600 }}
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
                                        label={`${label} ${Number(balance.amount).toLocaleString('en-US', { maximumFractionDigits: 8 })}`}
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