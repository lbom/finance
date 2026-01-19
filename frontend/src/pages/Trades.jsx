import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Button, Typography, Dialog, DialogTitle, DialogContent,
    DialogActions, TextField, MenuItem, Grid, Chip, Paper
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add, TrendingUp, TrendingDown, HourglassEmpty, ShowChart } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints.js';

const TradeTypes = ["POLITICAL_INSIGHT", "ONCHAIN_DATA", "ECONOMIC_DATA", "STRAIGHT_FINANCIAL"];

// Helper for currency formatting
const currencyFormatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
});

const dateFormatter = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('en-GB', {
        day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
    });
};

export const Trades = () => {
    const [open, setOpen] = useState(false);
    const queryClient = useQueryClient();

    const { data: trades, isLoading } = useQuery({ queryKey: ['trades'], queryFn: api.trades.list });

    const mutation = useMutation({
        mutationFn: api.trades.create,
        onSuccess: () => {
            queryClient.invalidateQueries(['trades']);
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

    const onSubmit = (data) => mutation.mutate(data);

    const columns = [
        {
            field: 'startDate',
            headerName: 'Date Opened',
            width: 160,
            valueFormatter: (params) => dateFormatter(params) // Fixed for DataGrid v6+ if needed, but params usually works for formatter
        },
        {
            field: 'type',
            headerName: 'Strategy',
            width: 180,
            renderCell: (params) => (
                <Chip
                    label={params.value?.replace('_', ' ')}
                    size="small"
                    color={params.value?.includes('POLITICAL') ? 'secondary' : 'primary'}
                    variant="outlined"
                    sx={{ fontWeight: 500, fontSize: '0.75rem' }}
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
            valueFormatter: (value) => currencyFormatter.format(value) // Safe v6 formatter
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
                            sx={{ bgcolor: 'action.hover', color: 'text.secondary', fontWeight: 'bold' }}
                        />
                    );
                }
                const isProfit = value >= 0;
                return (
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 0.5, color: isProfit ? 'success.main' : 'error.main' }}>
                        {isProfit ? <TrendingUp fontSize="small" /> : <TrendingDown fontSize="small" />}
                        <Typography fontWeight="bold">
                            {currencyFormatter.format(value)}
                        </Typography>
                    </Box>
                );
            }
        },
        {
            field: 'reason',
            headerName: 'Thesis / Reason',
            flex: 1,
            minWidth: 200
        },
    ];

    return (
        <Box>
            {/* THE CARD CONTAINER */}
            <Paper
                elevation={2}
                sx={{
                    p: 2,
                    height: 650,
                    width: '100%',
                    borderRadius: 3, // Rounded corners like the image
                    display: 'flex',
                    flexDirection: 'column'
                }}
            >
                {/* 1. HEADER ROW: Title Left, Button Right */}
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Box display="flex" alignItems="center" gap={1}>
                        <ShowChart color="primary" />
                        <Typography variant="h6" fontWeight="bold">
                            Recent Trades
                        </Typography>
                    </Box>

                    {/* Button moved here */}
                    <Button
                        variant="outlined"
                        startIcon={<Add />}
                        onClick={() => setOpen(true)}
                        size="medium"
                        sx={{
                            textTransform: 'none', // Keeps it looking like "Add" not "ADD"
                            fontWeight: 'bold',
                            borderRadius: 2
                        }}
                    >
                        Log Trade
                    </Button>
                </Box>

                {/* 2. THE GRID */}
                <DataGrid
                    rows={trades || []}
                    columns={columns}
                    loading={isLoading}
                    disableRowSelectionOnClick
                    initialState={{
                        pagination: { paginationModel: { pageSize: 10 } },
                        sorting: { sortModel: [{ field: 'startDate', sort: 'desc' }] },
                    }}
                    pageSizeOptions={[10, 25, 50]}
                    sx={{
                        border: 'none',
                        '& .MuiDataGrid-columnHeaders': {
                            bgcolor: 'background.default',
                            textTransform: 'uppercase',
                            fontSize: '0.75rem',
                            fontWeight: 'bold',
                            color: 'text.secondary'
                        },
                        '& .MuiDataGrid-row:hover': {
                            bgcolor: 'action.hover'
                        }
                    }}
                />
            </Paper>

            {/* Dialog Form */}
            <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle>Log New Trade</DialogTitle>
                    <DialogContent>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            <Grid item xs={12}>
                                <Controller name="type" control={control} render={({ field }) => (
                                    <TextField {...field} select label="Strategy Type" fullWidth>{TradeTypes.map((o) => <MenuItem key={o} value={o}>{o}</MenuItem>)}</TextField>
                                )} />
                            </Grid>
                            <Grid item xs={6}><Controller name="institutionId" control={control} render={({ field }) => <TextField {...field} label="Inst ID" type="number" fullWidth />} /></Grid>
                            <Grid item xs={6}><Controller name="symbolId" control={control} render={({ field }) => <TextField {...field} label="Sym ID" type="number" fullWidth />} /></Grid>
                            <Grid item xs={6}><Controller name="amount" control={control} render={({ field }) => <TextField {...field} label="Amount" type="number" fullWidth />} /></Grid>
                            <Grid item xs={12}><Controller name="reason" control={control} render={({ field }) => <TextField {...field} label="Reason" multiline rows={3} fullWidth />} /></Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>Cancel</Button>
                        <Button type="submit" variant="contained">Save</Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};