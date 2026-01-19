import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Button, Typography, Dialog, DialogTitle, DialogContent,
    DialogActions, TextField, MenuItem, Grid, Chip
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add, TrendingUp, TrendingDown, HourglassEmpty } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints.js';
import { format } from 'date-fns'; // Optional: or use native Intl.DateTimeFormat

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

    // --- REFACTORED COLUMNS ---
    const columns = [
        // 1. TIME: When did this happen? (Most critical context)
        {
            field: 'startDate',
            headerName: 'Date Opened',
            width: 160,
            valueFormatter: (params) => dateFormatter(params)
        },

        // 2. STRATEGY: What kind of trade is this?
        {
            field: 'type',
            headerName: 'Strategy',
            width: 180,
            renderCell: (params) => (
                <Chip
                    label={params.value.replace('_', ' ')}
                    size="small"
                    // Color coding strategies helps visual scanning
                    color={params.value.includes('POLITICAL') ? 'secondary' : 'primary'}
                    variant="outlined"
                    sx={{ fontWeight: 500, fontSize: '0.75rem' }}
                />
            )
        },

        // 3. EXPOSURE: How much money is on the line?
        {
            field: 'amount',
            headerName: 'Position Size',
            width: 140,
            type: 'number',
            headerAlign: 'right',
            align: 'right',
            valueFormatter: (params) => currencyFormatter.format(params)
        },

        // 4. PERFORMANCE: The Result (Visual priority)
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

        // 5. NARRATIVE: Why did we do this? (Flexible width)
        {
            field: 'reason',
            headerName: 'Thesis / Reason',
            flex: 1,
            minWidth: 200
        },

        // 6. TECHNICALS: IDs (Low priority, kept at end or hidden)
        { field: 'institutionId', headerName: 'Inst.', width: 70, type: 'number', headerAlign: 'center', align: 'center' },
        { field: 'symbolId', headerName: 'Sym.', width: 70, type: 'number', headerAlign: 'center', align: 'center' },
    ];

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" mb={3} alignItems="center">
                <Box>
                    <Typography variant="h4" fontWeight="bold">Trade Blotter</Typography>
                    <Typography variant="body2" color="text.secondary">
                        Active positions and historical performance
                    </Typography>
                </Box>
                <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>
                    Log Trade
                </Button>
            </Box>

            <Box sx={{ height: 650, width: '100%', bgcolor: 'background.paper', borderRadius: 2, boxShadow: 1, overflow: 'hidden' }}>
                <DataGrid
                    rows={trades || []}
                    columns={columns}
                    loading={isLoading}
                    disableRowSelectionOnClick
                    initialState={{
                        pagination: { paginationModel: { pageSize: 10 } },
                        sorting: { sortModel: [{ field: 'startDate', sort: 'desc' }] }, // Sort by newest first
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
            </Box>

            {/* Dialog Form remains the same, omitted for brevity but included in your file */}
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