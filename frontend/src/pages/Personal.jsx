import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Paper, Typography, Button, Grid, Dialog, DialogTitle,
    DialogContent, DialogActions, TextField, MenuItem, Chip, Avatar
} from '@mui/material';
import { Add, Wallet, ReceiptLong, TrendingUp, TrendingDown } from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { useForm, Controller } from 'react-hook-form';
import { api } from '../api/endpoints';

const RevenueTypes = ["SALARY", "DIVIDENDS", "FREELANCE", "GIFT", "OTHER"];
const ExpenseTypes = ["FOOD", "RENT", "TRANSPORT", "ENTERTAINMENT", "HEALTH", "TAXES"];

// Safe Currency Formatter
const formatMoney = (val) => {
    if (val == null) return '-';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
};

// --- Reusable Section Component ---
const FinanceSection = ({ title, subtitle, data, isLoading, onAdd, type }) => {

    // Determine Styles based on Type (Income vs Expense)
    const isIncome = type === 'income';
    const accentColor = isIncome ? 'primary.main' : 'error.main';
    const chipBg = isIncome ? 'secondary.main' : '#FFEBEE'; // Mint vs Soft Red
    const chipText = isIncome ? 'primary.dark' : 'error.dark';
    const Icon = isIncome ? Wallet : ReceiptLong;

    const rows = (data || []).map(item => ({
        ...item,
        type: item.expenseType || item.revenueType || item.type || 'UNKNOWN'
    }));

    const columns = [
        {
            field: 'type',
            headerName: 'Category',
            flex: 1,
            renderCell: (params) => (
                <Chip
                    label={params.value?.replace(/_/g, ' ')}
                    size="small"
                    sx={{
                        bgcolor: chipBg,
                        color: chipText,
                        fontWeight: 600,
                        border: 'none',
                        fontSize: '0.75rem',
                        textTransform: 'capitalize'
                    }}
                />
            )
        },
        {
            field: 'amount',
            headerName: 'Amount',
            width: 120,
            align: 'right',
            headerAlign: 'right',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 0.5, color: accentColor }}>
                    {isIncome ? <TrendingUp fontSize="small" /> : <TrendingDown fontSize="small" />}
                    <Typography fontWeight="bold" fontSize="0.9rem">
                        {formatMoney(params.value)}
                    </Typography>
                </Box>
            )
        },
    ];

    return (
        <Paper sx={{ p: 0, height: '100%', borderRadius: 3, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>

            {/* Card Header */}
            <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid', borderColor: 'divider' }}>
                <Box display="flex" alignItems="center" gap={2}>
                    <Avatar sx={{ bgcolor: chipBg, color: chipText }} variant="rounded">
                        <Icon fontSize="small" />
                    </Avatar>
                    <Box>
                        <Typography variant="h6" fontSize="1rem" fontWeight="bold">{title}</Typography>
                        <Typography variant="caption" color="text.secondary">{subtitle}</Typography>
                    </Box>
                </Box>
                <Button
                    size="small"
                    variant="outlined"
                    startIcon={<Add />}
                    onClick={onAdd}
                    sx={{ borderRadius: 2, borderColor: 'divider', color: 'text.secondary', '&:hover': { borderColor: accentColor, color: accentColor } }}
                >
                    Add
                </Button>
            </Box>

            {/* Grid Content */}
            <Box sx={{ flexGrow: 1, minHeight: 400 }}>
                <DataGrid
                    rows={rows}
                    columns={columns}
                    loading={isLoading}
                    disableRowSelectionOnClick
                    rowHeight={55}
                    initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
                    sx={{
                        border: 'none',
                        '& .MuiDataGrid-columnSeparator': { display: 'none' },
                        '& .MuiDataGrid-columnHeaders': { bgcolor: 'transparent' }
                    }}
                />
            </Box>
        </Paper>
    );
};

export const Personal = () => {
    const queryClient = useQueryClient();
    const [dialogOpen, setDialogOpen] = useState(false);
    const [activeSection, setActiveSection] = useState(null); // 'REVENUE' or 'EXPENSE'

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
            {/* Page Header */}
            <Box mb={4}>
                <Typography variant="h4" gutterBottom>Cash Flow</Typography>
                <Typography variant="body1" color="text.secondary">
                    Track your personal income streams and daily expenditures.
                </Typography>
            </Box>

            <Grid container spacing={3}>
                {/* Income Column */}
                <Grid item xs={12} md={6}>
                    <FinanceSection
                        title="Income Streams"
                        subtitle="Salary, Dividends, etc."
                        type="income"
                        data={revenues}
                        isLoading={l1}
                        onAdd={() => handleOpen('REVENUE')}
                    />
                </Grid>

                {/* Expense Column */}
                <Grid item xs={12} md={6}>
                    <FinanceSection
                        title="Expenses"
                        subtitle="Rent, Food, Lifestyle"
                        type="expense"
                        data={expenses}
                        isLoading={l2}
                        onAdd={() => handleOpen('EXPENSE')}
                    />
                </Grid>
            </Grid>

            {/* Shared Dialog */}
            <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">
                            {activeSection === 'REVENUE' ? 'Add Income' : 'Log Expense'}
                        </Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="type"
                                control={control}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} select label="Category" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
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
                                        InputProps={{
                                            startAdornment: '$',
                                            sx: { borderRadius: 2, fontSize: '1.2rem', fontWeight: 600 }
                                        }}
                                    />
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={handleClose} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button
                            type="submit"
                            variant="contained"
                            color={activeSection === 'REVENUE' ? "success" : "error"}
                            sx={{ borderRadius: 2, px: 3 }}
                        >
                            Save
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};