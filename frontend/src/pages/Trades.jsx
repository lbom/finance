import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Button, Typography, Dialog, DialogTitle, DialogContent,
    DialogActions, TextField, MenuItem, Grid, Chip, Paper, Avatar
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add, TrendingUp, TrendingDown, HourglassEmpty, ShowChart, CandlestickChart } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints.js';

const TradeTypes = ["POLITICAL_INSIGHT", "ONCHAIN_DATA", "ECONOMIC_DATA", "STRAIGHT_FINANCIAL"];

// FIX: Safe formatter
const formatMoney = (val) => {
    if (val == null) return '-';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
};

const dateFormatter = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('en-GB', {
        day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
    });
};

export const Trades = () => {
    const [open, setOpen] = useState(false);
    const queryClient = useQueryClient();

    const { data: persons, isLoading: personsLoading } = useQuery({ queryKey: ['persons'], queryFn: api.person.list });
    const activePersonId = persons?.[0]?.id;

    const { data: trades, isLoading } = useQuery({
        queryKey: ['trades', activePersonId],
        queryFn: () => api.trades.list(activePersonId),
        enabled: !!activePersonId,
    });

    const mutation = useMutation({
        mutationFn: (data) => api.trades.create(activePersonId, data),
        onSuccess: () => {
            queryClient.invalidateQueries(['trades', activePersonId]);
            setOpen(false);
            reset();
        },
    });

    const { control, handleSubmit, reset } = useForm({
        defaultValues: {
            type: 'STRAIGHT_FINANCIAL', institutionId: 1, symbolId: 1, reason: '', amount: 0,
            startDate: new Date().toISOString(), endDate: new Date().toISOString(),
        }
    });

    const onSubmit = (data) => {
        if (!activePersonId) return;
        mutation.mutate({ ...data, personId: activePersonId });
    };

    const columns = [
        {
            field: 'startDate',
            headerName: 'Date Opened',
            width: 160,
            valueFormatter: (value) => dateFormatter(value)
        },
        {
            field: 'type',
            headerName: 'Strategy',
            width: 180,
            renderCell: (params) => (
                <Chip
                    label={params.value?.replace(/_/g, ' ')} // clean formatting
                    size="small"
                    // STYLE: Mint background, dark green text
                    sx={{
                        bgcolor: 'secondary.main',
                        color: 'primary.dark',
                        fontWeight: 600,
                        border: 'none',
                        fontSize: '0.75rem'
                    }}
                />
            )
        },
        {
            field: 'amount',
            headerName: 'Position Size',
            width: 140,
            type: 'number',
            headerAlign: 'right',
            align: 'right',
            valueFormatter: (value) => formatMoney(value)
        },
        {
            field: 'profit',
            headerName: 'PnL',
            width: 140,
            headerAlign: 'right',
            align: 'right',
            renderCell: (params) => {
                const value = params.value;
                if (value === null) {
                    return (
                        <Chip
                            icon={<HourglassEmpty sx={{ fontSize: 14 }} />}
                            label="OPEN"
                            size="small"
                            sx={{ bgcolor: 'rgba(0,0,0,0.05)', color: 'text.secondary', fontWeight: 'bold' }}
                        />
                    );
                }
                const isProfit = value >= 0;
                return (
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 0.5, color: isProfit ? 'primary.main' : 'error.main', height: '100%' }}>
                        {isProfit ? <TrendingUp fontSize="small" /> : <TrendingDown fontSize="small" />}
                        <Typography fontWeight="bold" fontSize="0.9rem">
                            {formatMoney(value)}
                        </Typography>
                    </Box>
                );
            }
        },
        {
            field: 'reason',
            headerName: 'Thesis / Reason',
            flex: 1,
            minWidth: 250,
            renderCell: (params) => (
                <Typography variant="body2" color="text.secondary" noWrap sx={{ py: 1.5 }}>
                    {params.value}
                </Typography>
            )
        },
    ];

    return (
        <Box sx={{ pb: 4 }}>

            {/* 2. Main Card */}
            <Paper sx={{ p: 0, overflow: 'hidden' }}>

                {/* Card Toolbar */}
                <Box sx={{ p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid', borderColor: 'divider' }}>
                    <Box display="flex" alignItems="center" gap={2}>
                        <Avatar sx={{ bgcolor: 'secondary.main', color: 'primary.main' }} variant="rounded">
                            <CandlestickChart />
                        </Avatar>
                        <Box>
                            <Typography variant="h6">Recent Trades</Typography>
                            <Typography variant="caption" color="text.secondary">
                                {personsLoading ? 'Loading person…' : `${trades?.length || 0} Entries logged`}
                            </Typography>
                        </Box>
                    </Box>

                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => setOpen(true)}
                        disabled={!activePersonId}
                        sx={{ borderRadius: 3, px: 3 }}
                    >
                        Log Trade
                    </Button>
                </Box>

                {/* DataGrid */}
                <Box sx={{ height: 650, width: '100%' }}>
                    <DataGrid
                        rows={trades || []}
                        columns={columns}
                        loading={isLoading || personsLoading}
                        disableRowSelectionOnClick
                        rowHeight={60}
                        initialState={{
                            pagination: { paginationModel: { pageSize: 10 } },
                            sorting: { sortModel: [{ field: 'startDate', sort: 'desc' }] },
                        }}
                        pageSizeOptions={[10, 25, 50]}
                        sx={{
                            border: 'none',
                            '& .MuiDataGrid-cell': { alignItems: 'center' },
                            '& .MuiDataGrid-columnSeparator': { display: 'none' },
                        }}
                    />
                </Box>
            </Paper>

            {/* Dialog Form */}
            <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm" PaperProps={{ sx: { borderRadius: 4 } }}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle sx={{ pb: 1 }}>
                        <Typography variant="h6" fontWeight="bold">Log New Trade</Typography>
                        <Typography variant="body2" color="text.secondary">Enter details for a new market position.</Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            <Grid item xs={12}>
                                <Controller name="type" control={control} render={({ field }) => (
                                    <TextField {...field} select label="Strategy Type" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
                                        {TradeTypes.map((o) => <MenuItem key={o} value={o}>{o.replace(/_/g, ' ')}</MenuItem>)}
                                    </TextField>
                                )} />
                            </Grid>

                            <Grid item xs={6}><Controller name="institutionId" control={control} render={({ field }) => <TextField {...field} label="Inst ID" type="number" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />} /></Grid>
                            <Grid item xs={6}><Controller name="symbolId" control={control} render={({ field }) => <TextField {...field} label="Sym ID" type="number" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />} /></Grid>

                            <Grid item xs={12}>
                                <Controller name="amount" control={control} render={({ field }) => (
                                    <TextField {...field} label="Amount" type="number" fullWidth InputProps={{ startAdornment: '$', sx: { borderRadius: 2, fontSize: '1.2rem', fontWeight: 600 } }} />
                                )} />
                            </Grid>

                            <Grid item xs={12}>
                                <Controller name="reason" control={control} render={({ field }) => (
                                    <TextField {...field} label="Reason / Thesis" multiline rows={3} fullWidth placeholder="Why did you take this trade?" InputProps={{ sx: { borderRadius: 2 } }} />
                                )} />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={() => setOpen(false)} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button type="submit" variant="contained" sx={{ px: 4, borderRadius: 3 }}>Save</Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};