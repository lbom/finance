import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Box, Button, Typography, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem, Grid, Chip } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Add } from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints.js';

const TradeTypes = ["POLITICAL_INSIGHT", "ONCHAIN_DATA", "ECONOMIC_DATA", "STRAIGHT_FINANCIAL"];

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
        { field: 'id', headerName: 'ID', width: 70 },
        { field: 'type', headerName: 'Strategy', width: 200, renderCell: (p) => <Chip label={p.value.replace('_', ' ')} size="small" color="primary" variant="outlined" /> },
        { field: 'amount', headerName: 'Amount', width: 130, valueFormatter: (p) => `$${p.value?.toLocaleString()}` },
        { field: 'profit', headerName: 'Profit', width: 130, renderCell: (p) => <Typography color={p.value >= 0 ? 'success.main' : 'error.main'} fontWeight="bold">{p.value}</Typography> },
        { field: 'reason', headerName: 'Reason', flex: 1 },
    ];

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" mb={3}>
                <Typography variant="h4">Trades</Typography>
                <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>New Trade</Button>
            </Box>
            <Box sx={{ height: 600, width: '100%', bgcolor: 'white', borderRadius: 3, p: 2 }}>
                <DataGrid rows={trades || []} columns={columns} loading={isLoading} disableRowSelectionOnClick />
            </Box>
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