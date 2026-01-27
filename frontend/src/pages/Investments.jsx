import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Button, Typography, Dialog, DialogTitle, DialogContent,
    DialogActions, TextField, MenuItem, Grid, Chip, Tooltip, Paper, Avatar
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add, TrendingUp, TrendingDown, AccessTime, Savings, PieChart, AccountBalance, Delete } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints';

const InvestTypes = ["REAL_ESTATE", "BONDS", "STOCKS_LONG", "VENTURE", "CRYPTO_HOLD"];

// FIX: Safe formatter that won't crash on null/undefined
const formatMoney = (val) => {
    if (val == null) return '-';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
};

const dateFormatter = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-GB', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
};

export const Investments = () => {
    const [open, setOpen] = useState(false);
    const queryClient = useQueryClient();

    const { data: persons, isLoading: personsLoading } = useQuery({ queryKey: ['persons'], queryFn: api.person.list });
    const activePersonId = persons?.[0]?.id;

    const { data: investments, isLoading } = useQuery({
        queryKey: ['investments', activePersonId],
        queryFn: () => api.invest.list(activePersonId),
        enabled: !!activePersonId
    });

    const mutation = useMutation({
        mutationFn: (data) => api.invest.create(activePersonId, data),
        onSuccess: () => {
            queryClient.invalidateQueries(['investments', activePersonId]);
            setOpen(false);
            reset();
        },
    });
    const deleteMutation = useMutation({
        mutationFn: (investId) => api.invest.delete(activePersonId, investId),
        onSuccess: () => {
            queryClient.invalidateQueries(['investments', activePersonId]);
        },
    });

    const { control, handleSubmit, reset } = useForm({
        defaultValues: {
            type: 'STOCKS_LONG', institutionId: 1, symbolId: 1, reason: '', amount: 0,
            startDate: new Date().toISOString().slice(0, 16),
            endDate: ''
        }
    });

    const onSubmit = (data) => {
        if (!activePersonId) return;
        const payload = {
            ...data,
            personId: activePersonId,
            startDate: new Date(data.startDate).toISOString(),
            endDate: data.endDate ? new Date(data.endDate).toISOString() : null
        };
        mutation.mutate(payload);
    };

    const columns = [
        {
            field: 'startDate',
            headerName: 'Inv. Date',
            width: 130,
            valueFormatter: (value) => dateFormatter(value)
        },
        {
            field: 'endDate',
            headerName: 'Maturity',
            width: 130,
            renderCell: (params) => (
                <Typography
                    variant="body2"
                    color={params.value ? 'text.secondary' : 'primary.main'}
                    fontWeight={params.value ? 400 : 600}
                    fontSize="0.85rem"
                    sx={{ display: 'flex', alignItems: 'center', height: '100%' }}
                >
                    {params.value ? dateFormatter(params.value) : 'Perpetual'}
                </Typography>
            )
        },
        {
            field: 'type',
            headerName: 'Asset Class',
            width: 160,
            renderCell: (params) => (
                <Chip
                    icon={<AccountBalance sx={{ fontSize: 16 }} />}
                    label={params.value?.replace('_', ' ')}
                    size="small"
                    // STYLE UPDATE: Soft Mint look
                    sx={{
                        bgcolor: 'secondary.main',
                        color: 'primary.dark',
                        fontWeight: 600,
                        border: 'none'
                    }}
                />
            )
        },
        {
            field: 'amount',
            headerName: 'Principal',
            width: 140,
            type: 'number',
            align: 'right',
            headerAlign: 'right',
            valueFormatter: (value) => formatMoney(value) // FIX: Uses safe formatter
        },
        {
            field: 'profit',
            headerName: 'Returns',
            width: 150,
            align: 'right',
            headerAlign: 'right',
            renderCell: (params) => {
                const value = params.value;
                if (value === null) {
                    return (
                        <Tooltip title="Position Open">
                            <Chip
                                icon={<AccessTime sx={{ fontSize: 14 }} />}
                                label="ACTIVE"
                                size="small"
                                sx={{ bgcolor: 'rgba(0,0,0,0.05)', color: 'text.secondary', fontWeight: 'bold' }}
                            />
                        </Tooltip>
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
            headerName: 'Thesis',
            flex: 1,
            minWidth: 220,
            renderCell: (params) => (
                <Typography variant="body2" color="text.secondary" noWrap sx={{ py: 1.5 }}>
                    {params.value}
                </Typography>
            )
        },
        {
            field: 'actions',
            headerName: '',
            width: 120,
            sortable: false,
            filterable: false,
            renderCell: (params) => (
                <Button
                    size="small"
                    color="error"
                    startIcon={<Delete fontSize="small" />}
                    onClick={() => deleteMutation.mutate(params.row.id)}
                    disabled={!activePersonId || deleteMutation.isLoading}
                >
                    Delete
                </Button>
            )
        }
    ];

    return (
        <Box sx={{ pb: 4 }}>
            {/* 2. Main Card */}
            <Paper sx={{ p: 0, overflow: 'hidden' }}> {/* p=0 allows header to sit flush */}

                {/* Card Toolbar */}
                <Box sx={{ p: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid', borderColor: 'divider' }}>
                    <Box display="flex" alignItems="center" gap={2}>
                        <Avatar sx={{ bgcolor: 'secondary.main', color: 'primary.main' }} variant="rounded">
                            <PieChart />
                        </Avatar>
                        <Box>
                            <Typography variant="h6">Active Holdings</Typography>
                            <Typography variant="caption" color="text.secondary">
                                {personsLoading ? 'Loading person…' : `${investments?.length || 0} Positions managed`}
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
                        New Position
                    </Button>
                </Box>

                {/* DataGrid */}
                <Box sx={{ height: 600, width: '100%' }}>
                    <DataGrid
                        rows={investments || []}
                        columns={columns}
                        loading={isLoading || personsLoading}
                        disableRowSelectionOnClick
                        rowHeight={60} // Taller rows for modern feel
                        initialState={{
                            pagination: { paginationModel: { pageSize: 10 } },
                            sorting: { sortModel: [{ field: 'startDate', sort: 'desc' }] },
                        }}
                        pageSizeOptions={[10, 25]}
                        sx={{
                            border: 'none',
                            '& .MuiDataGrid-cell': { alignItems: 'center' },
                            // Remove column separators
                            '& .MuiDataGrid-columnSeparator': { display: 'none' },
                        }}
                    />
                </Box>
            </Paper>

            {/* Dialog Form */}
            <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm" PaperProps={{ sx: { borderRadius: 4 } }}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle sx={{ pb: 1 }}>
                        <Typography variant="h6" fontWeight="bold">New Investment</Typography>
                        <Typography variant="body2" color="text.secondary">Commit capital to a new long-term position.</Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            <Grid item xs={12}>
                                <Controller name="type" control={control} render={({ field }) => (
                                    <TextField {...field} select label="Asset Class" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
                                        {InvestTypes.map((o) => <MenuItem key={o} value={o}>{o.replace(/_/g, ' ')}</MenuItem>)}
                                    </TextField>
                                )} />
                            </Grid>

                            {/* IDs in a secondary row */}
                            <Grid item xs={6}><Controller name="institutionId" control={control} render={({ field }) => <TextField {...field} label="Institution ID" type="number" fullWidth InputProps={{ sx: { borderRadius: 2 } }} /> } /></Grid>
                            <Grid item xs={6}><Controller name="symbolId" control={control} render={({ field }) => <TextField {...field} label="Symbol ID" type="number" fullWidth InputProps={{ sx: { borderRadius: 2 } }} /> } /></Grid>

                            <Grid item xs={12}>
                                <Controller name="amount" control={control} render={({ field }) => (
                                    <TextField {...field} label="Principal Amount" type="number" fullWidth InputProps={{ startAdornment: '$', sx: { borderRadius: 2, fontSize: '1.2rem', fontWeight: 600 } }} />
                                )} />
                            </Grid>

                            <Grid item xs={6}>
                                <Controller name="startDate" control={control} render={({ field }) => (
                                    <TextField {...field} label="Start Date" type="datetime-local" fullWidth InputLabelProps={{ shrink: true }} InputProps={{ sx: { borderRadius: 2 } }} />
                                )} />
                            </Grid>
                            <Grid item xs={6}>
                                <Controller name="endDate" control={control} render={({ field }) => (
                                    <TextField {...field} label="Maturity Date" type="datetime-local" fullWidth InputLabelProps={{ shrink: true }} helperText="Optional (for bonds/fixed term)" InputProps={{ sx: { borderRadius: 2 } }} />
                                )} />
                            </Grid>

                            <Grid item xs={12}>
                                <Controller name="reason" control={control} render={({ field }) => (
                                    <TextField {...field} label="Investment Thesis" multiline rows={3} fullWidth placeholder="Rationale for this allocation..." InputProps={{ sx: { borderRadius: 2 } }} />
                                )} />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={() => setOpen(false)} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button type="submit" variant="contained" sx={{ px: 4, borderRadius: 3 }}>Commit</Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};