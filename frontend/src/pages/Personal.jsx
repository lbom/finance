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

const ProfitTypes = ["SALARY", "BUSINESS", "INVESTMENT", "TRADE", "GIFT", "OTHER"];
const SpendingTypes = ["FOOD_STORE", "FOOD_RESTAURANT", "RENT", "UTILITIES", "ENTERTAINMENT", "BUSINESS", "INVESTMENT", "TRADE", "OTHER"];

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
        displayCategory: item.profitType || item.spendingType || 'OTHER'
    }));

    const columns = [
        {
            field: 'displayCategory',
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
    const [activeSection, setActiveSection] = useState(null); // 'PROFIT' or 'SPENDING'

    // Unified Querying
    const { data: profits, isLoading: l1 } = useQuery({
        queryKey: ['personal', 'PROFIT'],
        queryFn: () => api.personal.list('PROFIT')
    });
    const { data: spendings, isLoading: l2 } = useQuery({
        queryKey: ['personal', 'SPENDING'],
        queryFn: () => api.personal.list('SPENDING')
    });

    const addTransaction = useMutation({
        mutationFn: api.personal.create,
        onSuccess: (_, variables) => {
            // Invalidate specifically the type we just added
            queryClient.invalidateQueries(['personal', variables.type]);
            handleClose();
        }
    });

    const { control, handleSubmit, reset } = useForm({
        defaultValues: { category: '', amount: '' }
    });

    const handleOpen = (section) => {
        setActiveSection(section);
        reset({ category: section === 'PROFIT' ? ProfitTypes[0] : SpendingTypes[0], amount: '' });
        setDialogOpen(true);
    };

    const handleClose = () => setDialogOpen(false);

    const onSubmit = (data) => {
        // Construct payload to match PersonalTransactionDto
        const payload = {
            type: activeSection,
            amount: Number(data.amount),
            // Map the selection to the correct DTO field
            profitType: activeSection === 'PROFIT' ? data.category : null,
            spendingType: activeSection === 'SPENDING' ? data.category : null
        };
        addTransaction.mutate(payload);
    };

    return (
        <Box sx={{ pb: 5 }}>
            <Box mb={4}>
                <Typography variant="h4" gutterBottom>Cash Flow</Typography>
                <Typography variant="body1" color="text.secondary">
                    Track your personal income streams and daily expenditures.
                </Typography>
            </Box>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <FinanceSection
                        title="Income Streams"
                        subtitle="Salary, Business, etc."
                        type="income" // UI styling prop
                        data={profits}
                        isLoading={l1}
                        onAdd={() => handleOpen('PROFIT')}
                    />
                </Grid>

                <Grid item xs={12} md={6}>
                    <FinanceSection
                        title="Expenses"
                        subtitle="Rent, Food, Lifestyle"
                        type="expense" // UI styling prop
                        data={spendings}
                        isLoading={l2}
                        onAdd={() => handleOpen('SPENDING')}
                    />
                </Grid>
            </Grid>

            <Dialog open={dialogOpen} onClose={handleClose} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">
                            {activeSection === 'PROFIT' ? 'Add Income' : 'Log Expense'}
                        </Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="category"
                                control={control}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} select label="Category" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
                                        {(activeSection === 'PROFIT' ? ProfitTypes : SpendingTypes).map(opt => (
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
                            disabled={addTransaction.isLoading}
                            color={activeSection === 'PROFIT' ? "success" : "error"}
                            sx={{ borderRadius: 2, px: 3 }}
                        >
                            {addTransaction.isLoading ? 'Saving...' : 'Save'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};