import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Button, Typography, Dialog, DialogTitle, DialogContent,
    DialogActions, TextField, MenuItem, Grid, Chip, Tooltip, Paper
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add, TrendingUp, TrendingDown, AccessTime, Savings, PieChart } from '@mui/icons-material'; // Added PieChart
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints';

// Example types based on your DTO
const InvestTypes = ["REAL_ESTATE", "BONDS", "STOCKS_LONG", "VENTURE", "CRYPTO_HOLD"];

const currencyFormatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
});

const dateFormatter = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-GB', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
};

export const Investments = () => {
    const [open, setOpen] = useState(false);
    const queryClient = useQueryClient();

    const { data: investments, isLoading } = useQuery({
        queryKey: ['investments'],
        queryFn: api.invest.list
    });

    const mutation = useMutation({
        mutationFn: api.invest.create,
        onSuccess: () => {
            queryClient.invalidateQueries(['investments']);
            setOpen(false);
            reset();
        },
    });

    const { control, handleSubmit, reset } = useForm({
        defaultValues: {
            type: 'STOCKS_LONG',
            institutionId: 1,
            symbolId: 1,
            reason: '',
            amount: 0,
            startDate: new Date().toISOString().slice(0, 16), // Format for datetime-local input
            endDate: '' // Optional for investments
        }
    });

    const onSubmit = (data) => {
        // Ensure standard ISO format for backend
        const payload = {
            ...data,
            startDate: new Date(data.startDate).toISOString(),
            endDate: data.endDate ? new Date(data.endDate).toISOString() : null
        };
        mutation.mutate(payload);
    };

    const columns = [
        // 1. TIMELINE: Start vs End is crucial for investments
        {
            field: 'startDate',
            headerName: 'Inv. Date',
            width: 120,
            valueFormatter: (value) => dateFormatter(value) // Safe v6 formatter
        },
        {
            field: 'endDate',
            headerName: 'Maturity',
            width: 120,
            renderCell: (params) => (
                <Typography variant="body2" color={params.value ? 'text.primary' : 'text.disabled'}>
                    {params.value ? dateFormatter(params.value) : 'Perpetual'}
                </Typography>
            )
        },

        // 2. CATEGORY
        {
            field: 'type',
            headerName: 'Asset Class',
            width: 150,
            renderCell: (params) => (
                <Chip
                    icon={<Savings sx={{ fontSize: 16 }} />}
                    label={params.value?.replace('_', ' ')}
                    size="small"
                    color="info"
                    variant="outlined"
                    sx={{ fontWeight: 500, fontSize: '0.75rem' }}
                />
            )
        },

        // 3. PRINCIPAL
        {
            field: 'amount',
            headerName: 'Principal',
            width: 140,
            type: 'number',
            headerAlign: 'right',
            align: 'right',
            valueFormatter: (value) => currencyFormatter.format(value)
        },

        // 4. RETURNS (Highlighted)
        {
            field: 'profit',
            headerName: 'Returns',
            width: 140,
            headerAlign: 'right',
            align: 'right',
            renderCell: (params) => {
                const value = params.value;
                if (value === null) {
                    return (
                        <Tooltip title="Investment Active">
                            <Chip
                                icon={<AccessTime sx={{ fontSize: 14 }} />}
                                label="ACTIVE"
                                size="small"
                                sx={{ bgcolor: 'action.selected', color: 'text.secondary', fontWeight: 'bold' }}
                            />
                        </Tooltip>
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

        // 5. THESIS
        {
            field: 'reason',
            headerName: 'Investment Thesis',
            flex: 1,
            minWidth: 200
        },
    ];

    return (
        <Box>

            <Paper
                elevation={2}
                sx={{
                    p: 2,
                    height: 650,
                    width: '100%',
                    borderRadius: 3,
                    display: 'flex',
                    flexDirection: 'column'
                }}
            >
                {/* 1. HEADER ROW */}
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Box display="flex" alignItems="center" gap={1}>
                        <PieChart color="primary" />
                        <Typography variant="h6" fontWeight="bold">
                            Active Holdings
                        </Typography>
                    </Box>

                    <Button
                        variant="outlined"
                        startIcon={<Add />}
                        onClick={() => setOpen(true)}
                        sx={{
                            textTransform: 'none',
                            fontWeight: 'bold',
                            borderRadius: 2
                        }}
                    >
                        New Position
                    </Button>
                </Box>

                {/* 2. THE GRID */}
                <DataGrid
                    rows={investments || []}
                    columns={columns}
                    loading={isLoading}
                    disableRowSelectionOnClick
                    initialState={{
                        pagination: { paginationModel: { pageSize: 10 } },
                        sorting: { sortModel: [{ field: 'startDate', sort: 'desc' }] },
                    }}
                    pageSizeOptions={[10, 25]}
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

            <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle>Add Investment</DialogTitle>
                    <DialogContent>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            <Grid item xs={12}>
                                <Controller name="type" control={control} render={({ field }) => (
                                    <TextField {...field} select label="Asset Class" fullWidth>
                                        {InvestTypes.map((o) => <MenuItem key={o} value={o}>{o.replace('_', ' ')}</MenuItem>)}
                                    </TextField>
                                )} />
                            </Grid>
                            <Grid item xs={6}><Controller name="institutionId" control={control} render={({ field }) => <TextField {...field} label="Institution ID" type="number" fullWidth />} /></Grid>
                            <Grid item xs={6}><Controller name="symbolId" control={control} render={({ field }) => <TextField {...field} label="Symbol ID" type="number" fullWidth />} /></Grid>

                            <Grid item xs={12}>
                                <Controller name="amount" control={control} render={({ field }) => (
                                    <TextField {...field} label="Principal Amount" type="number" fullWidth InputProps={{ startAdornment: '$' }} />
                                )} />
                            </Grid>

                            <Grid item xs={6}>
                                <Controller name="startDate" control={control} render={({ field }) => (
                                    <TextField {...field} label="Start Date" type="datetime-local" fullWidth InputLabelProps={{ shrink: true }} />
                                )} />
                            </Grid>
                            <Grid item xs={6}>
                                <Controller name="endDate" control={control} render={({ field }) => (
                                    <TextField {...field} label="Target End Date" type="datetime-local" fullWidth InputLabelProps={{ shrink: true }} helperText="Leave empty if perpetual" />
                                )} />
                            </Grid>

                            <Grid item xs={12}>
                                <Controller name="reason" control={control} render={({ field }) => (
                                    <TextField {...field} label="Investment Thesis" multiline rows={3} fullWidth placeholder="Why are we holding this?" />
                                )} />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>Cancel</Button>
                        <Button type="submit" variant="contained">Commit Capital</Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};