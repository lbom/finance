import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Paper, Typography, Button, Grid, Dialog, DialogTitle,
    DialogContent, DialogActions, TextField, MenuItem, Chip, IconButton
} from '@mui/material';
import { Add, ArrowUpward, ArrowDownward } from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints';

const RevenueTypes = ["SALARY", "DIVIDENDS", "FREELANCE", "GIFT", "OTHER"];
const ExpenseTypes = ["FOOD", "RENT", "TRANSPORT", "ENTERTAINMENT", "HEALTH", "TAXES"];

// Helper for safe currency formatting
const formatMoney = (val) => val ? `$${Number(val).toLocaleString()}` : '$0.00';

const FinanceSection = ({ title, data, isLoading, onAdd, color }) => {

    // 1. Data Mapping: Ensure we always have a 'type' field
    const rows = (data || []).map(item => ({
        ...item,
        // Check all possible field names from your Java DTOs
        type: item.expenseType || item.revenueType || item.type || 'UNKNOWN'
    }));

    const columns = [
        { field: 'id', headerName: 'ID', width: 50 },
        {
            field: 'type',
            headerName: 'Category',
            flex: 1,
            minWidth: 120, // FIX: Prevents "Ca..." cut-off
            renderCell: (params) => (
                <Chip
                    label={params.value?.replace(/_/g, ' ')}
                    size="small"
                    variant="outlined"
                    color={color}
                    sx={{ fontWeight: 500, minWidth: 80, justifyContent: 'center' }}
                />
            )
        },
        {
            field: 'amount',
            headerName: 'Amount',
            width: 120,
            type: 'number',
            align: 'right',
            headerAlign: 'right',
            // FIX: DataGrid v6 passes the value directly, not params.value
            valueFormatter: (value) => formatMoney(value)
        },
    ];

    return (
        <Paper
            elevation={2}
            sx={{
                p: 2,
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                borderRadius: 2
            }}
        >
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Box display="flex" alignItems="center" gap={1}>
                    {color === 'success' ? <ArrowUpward color="success" /> : <ArrowDownward color="error" />}
                    <Typography variant="h6" fontWeight="bold">{title}</Typography>
                </Box>
                <Button size="small" startIcon={<Add />} variant="outlined" color={color} onClick={onAdd}>
                    Add
                </Button>
            </Box>

            {/* FIX: autoHeight makes the table shrink/grow with data, preventing empty whitespace */}
            <DataGrid
                rows={rows}
                columns={columns}
                loading={isLoading}
                disableRowSelectionOnClick
                density="comfortable"
                autoHeight={rows.length > 0}
                sx={{
                    border: 'none',
                    '& .MuiDataGrid-cell': { fontSize: '0.9rem' },
                    '& .MuiDataGrid-columnHeaders': { bgcolor: 'action.hover' },
                    minHeight: 200 // Minimum height if empty
                }}
                initialState={{
                    pagination: { paginationModel: { pageSize: 10 } }
                }}
            />
        </Paper>
    );
};

export const Personal = () => {
    const queryClient = useQueryClient();
    const [dialogOpen, setDialogOpen] = useState(false);
    const [activeSection, setActiveSection] = useState(null);

    const { data: revenues, isLoading: l1 } = useQuery({ queryKey: ['revenue'], queryFn: api.personal.revenue });
    const { data: expenses, isLoading: l2 } = useQuery({ queryKey: ['expenses'], queryFn: api.personal.expenses });

    const addRevenue = useMutation({
        mutationFn: api.personal.createRevenue,
        onSuccess: () => { queryClient.invalidateQueries(['revenue']); handleClose(); }
    });

    const addExpense = useMutation({
        mutationFn: api.personal.createExpense,
        onSuccess: () => { queryClient.invalidateQueries(['expenses']); handleClose(); }
    });

    const { control, handleSubmit, reset } = useForm({
        defaultValues: { type: '', amount: '' }
    });

    const handleOpen = (section) => {
        setActiveSection(section);
        // Default to first option to prevent empty select
        reset({ type: section === 'REVENUE' ? RevenueTypes[0] : ExpenseTypes[0], amount: '' });
        setDialogOpen(true);
    };

    const handleClose = () => setDialogOpen(false);

    const onSubmit = (data) => {
        const payload = { amount: Number(data.amount) };
        if (activeSection === 'REVENUE') {
            payload.revenueType = data.type;
            addRevenue.mutate(payload);
        } else {
            payload.expenseType = data.type;
            addExpense.mutate(payload);
        }
    };

    return (
        <Box sx={{ pb: 5 }}>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <FinanceSection
                        title="Income"
                        data={revenues}
                        isLoading={l1}
                        color="success"
                        onAdd={() => handleOpen('REVENUE')}
                    />
                </Grid>

                <Grid item xs={12} md={6}>
                    <FinanceSection
                        title="Expenses"
                        data={expenses}
                        isLoading={l2}
                        color="error"
                        onAdd={() => handleOpen('EXPENSE')}
                    />
                </Grid>
            </Grid>

            <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth="xs">
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle>
                        {activeSection === 'REVENUE' ? 'Add Income' : 'Log Expense'}
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="type"
                                control={control}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} select label="Category" fullWidth>
                                        {(activeSection === 'REVENUE' ? RevenueTypes : ExpenseTypes).map(opt => (
                                            <MenuItem key={opt} value={opt}>{opt.replace(/_/g, ' ')}</MenuItem>
                                        ))}
                                    </TextField>
                                )}
                            />
                            <Controller
                                name="amount"
                                control={control}
                                rules={{ required: true, min: 0 }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Amount"
                                        type="number"
                                        fullWidth
                                        InputProps={{ startAdornment: '$' }}
                                    />
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose}>Cancel</Button>
                        <Button
                            type="submit"
                            variant="contained"
                            color={activeSection === 'REVENUE' ? "success" : "error"}
                        >
                            Save
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};