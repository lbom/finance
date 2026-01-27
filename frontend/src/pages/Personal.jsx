import React, { useEffect, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Paper, Typography, Button, Grid, Dialog, DialogTitle,
    DialogContent, DialogActions, TextField, MenuItem, Chip, Avatar,
    Switch, FormControlLabel
} from '@mui/material';
import {
    Add,
    Wallet,
    ReceiptLong,
    TrendingUp,
    TrendingDown,
    Repeat,
    AccountBalanceWallet
} from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { useForm, Controller, useWatch } from 'react-hook-form';
import { api } from '../api/endpoints';

const ProfitTypes = ["SALARY", "BUSINESS", "INVESTMENT", "TRADE", "GIFT", "OTHER"];
const SpendingTypes = ["FOOD_STORE", "FOOD_RESTAURANT", "RENT", "UTILITIES", "ENTERTAINMENT", "BUSINESS", "INVESTMENT", "TRADE", "OTHER"];
const BalanceTypes = ["REGULAR", "CRYPTO"];

// Safe Currency Formatter
const formatMoney = (val) => {
    if (val == null) return '-';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
};

// --- Reusable Section Component ---
const FinanceSection = ({ title, subtitle, data, isLoading, onAdd, type, disableAdd, onDelete }) => {

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
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 0.5, color: accentColor, height: '100%' }}>
                    {isIncome ? <TrendingUp fontSize="small" /> : <TrendingDown fontSize="small" />}
                    <Typography fontWeight="bold" fontSize="0.9rem">
                        {formatMoney(params.value)}
                    </Typography>
                </Box>
            )
        },
    ];
    if (onDelete) {
        columns.push({
            field: 'actions',
            headerName: '',
            width: 120,
            sortable: false,
            filterable: false,
            renderCell: (params) => (
                <Button size="small" color="error" onClick={() => onDelete(params.row)}>
                    Delete
                </Button>
            )
        });
    }

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
                    disabled={disableAdd}
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
                        '& .MuiDataGrid-cell': { alignItems: 'center' },
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
    const [recurrentOpen, setRecurrentOpen] = useState(false);
    const [recurrentEditing, setRecurrentEditing] = useState(null);
    const [balanceOpen, setBalanceOpen] = useState(false);
    const [balanceEditing, setBalanceEditing] = useState(null);
    const [personOpen, setPersonOpen] = useState(false);

    const { data: persons, isLoading: personsLoading } = useQuery({ queryKey: ['persons'], queryFn: api.person.list });
    const activePersonId = persons?.[0]?.id;

    const { data: balances, isLoading: balancesLoading } = useQuery({
        queryKey: ['balances', activePersonId],
        queryFn: () => api.balance.list(activePersonId),
        enabled: !!activePersonId,
    });
    const { data: currencies, isLoading: currenciesLoading } = useQuery({
        queryKey: ['currencies'],
        queryFn: api.dictionary.currency.list
    });
    const activeBalanceId = balances?.[0]?.id;
    const canTransact = !!activePersonId && !!activeBalanceId;

    // Unified Querying
    const { data: profits, isLoading: l1 } = useQuery({
        queryKey: ['personal', activePersonId, 'PROFIT'],
        queryFn: () => api.personal.list(activePersonId, 'PROFIT'),
        enabled: !!activePersonId,
    });
    const { data: spendings, isLoading: l2 } = useQuery({
        queryKey: ['personal', activePersonId, 'SPENDING'],
        queryFn: () => api.personal.list(activePersonId, 'SPENDING'),
        enabled: !!activePersonId,
    });
    const { data: recurrent, isLoading: l3 } = useQuery({
        queryKey: ['recurrent', activePersonId],
        queryFn: () => api.recurrent.list(activePersonId),
        enabled: !!activePersonId,
    });

    const addTransaction = useMutation({
        mutationFn: (data) => api.personal.create(activePersonId, data),
        onSuccess: (_, variables) => {
            // Invalidate specifically the type we just added
            queryClient.invalidateQueries(['personal', activePersonId, variables.type]);
            handleClose();
        }
    });
    const deleteTransaction = useMutation({
        mutationFn: (transactionId) => api.personal.delete(activePersonId, transactionId),
        onSuccess: () => {
            queryClient.invalidateQueries(['personal', activePersonId, 'PROFIT']);
            queryClient.invalidateQueries(['personal', activePersonId, 'SPENDING']);
            queryClient.invalidateQueries(['balances', activePersonId]);
        }
    });
    const saveRecurrent = useMutation({
        mutationFn: (payload) => (
            payload.id
                ? api.recurrent.update(activePersonId, payload)
                : api.recurrent.create(activePersonId, payload)
        ),
        onSuccess: () => {
            queryClient.invalidateQueries(['recurrent', activePersonId]);
            handleRecurrentClose();
        }
    });
    const saveBalance = useMutation({
        mutationFn: (payload) => api.balance.create(activePersonId, payload),
        onSuccess: () => {
            queryClient.invalidateQueries(['balances', activePersonId]);
            queryClient.invalidateQueries(['personal', activePersonId, 'PROFIT']);
            queryClient.invalidateQueries(['personal', activePersonId, 'SPENDING']);
            handleBalanceClose();
        }
    });
    const createPerson = useMutation({
        mutationFn: api.person.create,
        onSuccess: () => {
            queryClient.invalidateQueries(['persons']);
            setPersonOpen(false);
            resetPerson();
        }
    });

    const today = new Date().toISOString().slice(0, 10);
    const { control, handleSubmit, reset } = useForm({
        defaultValues: { category: '', amount: '', localDate: today, details: '' }
    });
    const {
        control: recurrentControl,
        handleSubmit: handleRecurrentSubmit,
        reset: resetRecurrent,
        setValue: setRecurrentValue
    } = useForm({
        defaultValues: {
            name: '',
            periodDays: 30,
            type: 'PROFIT',
            category: ProfitTypes[0],
            amount: '',
            isActive: true
        }
    });
    const {
        control: balanceControl,
        handleSubmit: handleBalanceSubmit,
        reset: resetBalance
    } = useForm({
        defaultValues: {
            currencyId: '',
            amount: '',
            type: 'REGULAR'
        }
    });
    const {
        control: personControl,
        handleSubmit: handlePersonSubmit,
        reset: resetPerson
    } = useForm({
        defaultValues: {
            name: ''
        }
    });
    const recurrentType = useWatch({ control: recurrentControl, name: 'type' });
    const recurrentCategory = useWatch({ control: recurrentControl, name: 'category' });

    useEffect(() => {
        if (!recurrentType) return;
        const options = recurrentType === 'SPENDING' ? SpendingTypes : ProfitTypes;
        if (!options.includes(recurrentCategory)) {
            setRecurrentValue('category', options[0]);
        }
    }, [recurrentType, recurrentCategory, setRecurrentValue]);

    const handleOpen = (section) => {
        setActiveSection(section);
        reset({
            category: section === 'PROFIT' ? ProfitTypes[0] : SpendingTypes[0],
            amount: '',
            localDate: today,
            details: ''
        });
        setDialogOpen(true);
    };

    const handleClose = () => setDialogOpen(false);
    const handleRecurrentClose = () => {
        setRecurrentOpen(false);
        setRecurrentEditing(null);
    };
    const handleBalanceClose = () => {
        setBalanceOpen(false);
        setBalanceEditing(null);
    };
    const handlePersonClose = () => {
        setPersonOpen(false);
    };

    const onSubmit = (data) => {
        if (!canTransact) return;
        // Construct payload to match PersonalTransactionDto
        const payload = {
            type: activeSection,
            amount: Number(data.amount),
            personId: activePersonId,
            balanceId: activeBalanceId,
            localDate: data.localDate,
            details: data.details?.trim() || '',
            // Map the selection to the correct DTO field
            profitType: activeSection === 'PROFIT' ? data.category : null,
            spendingType: activeSection === 'SPENDING' ? data.category : null
        };
        addTransaction.mutate(payload);
    };

    const handleRecurrentOpenAdd = () => {
        setRecurrentEditing(null);
        resetRecurrent({
            name: '',
            periodDays: 30,
            type: 'PROFIT',
            category: ProfitTypes[0],
            amount: '',
            isActive: true
        });
        setRecurrentOpen(true);
    };

    const handleRecurrentOpenEdit = (row) => {
        setRecurrentEditing(row);
        const category = row.profitType || row.spendingType || (row.type === 'PROFIT' ? ProfitTypes[0] : SpendingTypes[0]);
        resetRecurrent({
            name: row.name || '',
            periodDays: row.periodDays || 30,
            type: row.type || 'PROFIT',
            category,
            amount: row.amount ?? '',
            isActive: !!row.isActive
        });
        setRecurrentOpen(true);
    };

    const onSubmitRecurrent = (data) => {
        if (!canTransact) return;
        const payload = {
            id: recurrentEditing?.id,
            personId: activePersonId,
            balanceId: activeBalanceId,
            name: data.name.trim(),
            periodDays: Number(data.periodDays),
            amount: Number(data.amount),
            isActive: !!data.isActive,
            type: data.type,
            profitType: data.type === 'PROFIT' ? data.category : null,
            spendingType: data.type === 'SPENDING' ? data.category : null
        };
        saveRecurrent.mutate(payload);
    };

    const handleBalanceOpenAdd = () => {
        setBalanceEditing(null);
        resetBalance({ currencyId: currencies?.[0]?.id || '', amount: '', type: 'REGULAR' });
        setBalanceOpen(true);
    };

    const handleBalanceOpenEdit = (row) => {
        setBalanceEditing(row);
        resetBalance({
            currencyId: row.currencyId ?? '',
            amount: row.amount ?? '',
            type: row.type || 'REGULAR'
        });
        setBalanceOpen(true);
    };

    const onSubmitBalance = (data) => {
        if (!activePersonId) return;
        const payload = {
            id: balanceEditing?.id,
            personId: activePersonId,
            currencyId: Number(data.currencyId),
            amount: Number(data.amount),
            type: data.type || 'REGULAR'
        };
        saveBalance.mutate(payload);
    };

    const recurrentRows = (recurrent || []).map(item => ({
        ...item,
        displayCategory: item.profitType || item.spendingType || 'OTHER'
    }));

    const recurrentColumns = [
        {
            field: 'name',
            headerName: 'Name',
            flex: 1,
            minWidth: 160
        },
        {
            field: 'type',
            headerName: 'Type',
            width: 120,
            renderCell: (params) => (
                <Chip
                    label={params.value?.replace(/_/g, ' ')}
                    size="small"
                    sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }}
                />
            )
        },
        {
            field: 'displayCategory',
            headerName: 'Category',
            flex: 1,
            minWidth: 140,
            renderCell: (params) => (
                <Chip
                    label={params.value?.replace(/_/g, ' ')}
                    size="small"
                    sx={{ bgcolor: 'rgba(0,0,0,0.05)', color: 'text.secondary', fontWeight: 600 }}
                />
            )
        },
        {
            field: 'periodDays',
            headerName: 'Every (days)',
            width: 130
        },
        {
            field: 'amount',
            headerName: 'Amount',
            width: 130,
            align: 'right',
            headerAlign: 'right',
            renderCell: (params) => (
                <Typography fontWeight="bold" fontSize="0.9rem" sx={{ display: 'flex', alignItems: 'center', height: '100%' }}>
                    {formatMoney(params.value)}
                </Typography>
            )
        },
        {
            field: 'isActive',
            headerName: 'Active',
            width: 100,
            renderCell: (params) => (
                <Chip
                    label={params.value ? 'ON' : 'OFF'}
                    size="small"
                    sx={{
                        bgcolor: params.value ? 'secondary.main' : 'rgba(0,0,0,0.08)',
                        color: params.value ? 'primary.dark' : 'text.secondary',
                        fontWeight: 600
                    }}
                />
            )
        },
        {
            field: 'actions',
            headerName: '',
            width: 120,
            sortable: false,
            filterable: false,
            renderCell: (params) => (
                <Button size="small" onClick={() => handleRecurrentOpenEdit(params.row)}>
                    Edit
                </Button>
            )
        }
    ];

    const currencyMap = new Map((currencies || []).map((c) => [c.id, c]));
    const balanceRows = (balances || []).map(item => ({
        ...item,
        type: item.type || 'REGULAR',
        currency: currencyMap.get(item.currencyId)
    }));
    const balanceColumns = [
        {
            field: 'currency',
            headerName: 'Currency',
            flex: 1,
            minWidth: 160,
            renderCell: (params) => (
                <Chip
                    label={params.value?.symbol || '—'}
                    size="small"
                    sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }}
                />
            )
        },
        {
            field: 'type',
            headerName: 'Type',
            width: 120,
            renderCell: (params) => (
                <Chip
                    label={params.value}
                    size="small"
                    sx={{ bgcolor: 'rgba(0,0,0,0.05)', color: 'text.secondary', fontWeight: 600 }}
                />
            )
        },
        {
            field: 'amount',
            headerName: 'Balance',
            width: 140,
            align: 'right',
            headerAlign: 'right',
            renderCell: (params) => (
                <Typography fontWeight="bold" fontSize="0.9rem" sx={{ display: 'flex', alignItems: 'center', height: '100%' }}>
                    {formatMoney(params.value)}
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
                <Button size="small" onClick={() => handleBalanceOpenEdit(params.row)}>
                    Edit
                </Button>
            )
        }
    ];

    return (
        <Box sx={{ pb: 5 }}>
            <Box mb={4}>
                <Box display="flex" alignItems="center" justifyContent="space-between" gap={2} mb={1}>
                    <Typography variant="h4">Cash Flow</Typography>
                    <Button
                        variant="outlined"
                        startIcon={<Add />}
                        onClick={() => setPersonOpen(true)}
                        sx={{ borderRadius: 2 }}
                    >
                        Add Person
                    </Button>
                </Box>
                <Typography variant="body1" color="text.secondary">
                    Track your personal income streams and daily expenditures.
                </Typography>
                {!personsLoading && !activePersonId && (
                    <Typography variant="body2" color="error.main" sx={{ mt: 1 }}>
                        No person profile found. Create one before adding transactions.
                    </Typography>
                )}
                {!balancesLoading && activePersonId && !activeBalanceId && (
                    <Typography variant="body2" color="error.main" sx={{ mt: 1 }}>
                        No balance found for this person. Add a balance to log transactions.
                    </Typography>
                )}
            </Box>

            <Box mb={4}>
                <Paper sx={{ p: 0, borderRadius: 3, overflow: 'hidden' }}>
                    <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid', borderColor: 'divider' }}>
                        <Box display="flex" alignItems="center" gap={2}>
                            <Avatar sx={{ bgcolor: 'secondary.main', color: 'primary.main' }} variant="rounded">
                                <AccountBalanceWallet fontSize="small" />
                            </Avatar>
                            <Box>
                                <Typography variant="h6" fontSize="1rem" fontWeight="bold">Balances</Typography>
                                <Typography variant="caption" color="text.secondary">
                                    Track cash by currency.
                                </Typography>
                            </Box>
                        </Box>
                        <Button
                            size="small"
                            variant="outlined"
                            startIcon={<Add />}
                            onClick={handleBalanceOpenAdd}
                            disabled={!activePersonId}
                            sx={{ borderRadius: 2, borderColor: 'divider', color: 'text.secondary' }}
                        >
                            Add Balance
                        </Button>
                    </Box>
                    <Box sx={{ height: 320 }}>
                        <DataGrid
                            rows={balanceRows}
                            columns={balanceColumns}
                            loading={balancesLoading || personsLoading || currenciesLoading}
                            disableRowSelectionOnClick
                            rowHeight={55}
                            initialState={{ pagination: { paginationModel: { pageSize: 5 } } }}
                            sx={{
                                border: 'none',
                                '& .MuiDataGrid-cell': { alignItems: 'center' },
                                '& .MuiDataGrid-columnSeparator': { display: 'none' }
                            }}
                        />
                    </Box>
                </Paper>
            </Box>

            <Box sx={{ display: 'flex', gap: 3, alignItems: 'stretch', flexWrap: 'nowrap' }}>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                    <FinanceSection
                        title="Income Streams"
                        subtitle="Salary, Business, etc."
                        type="income" // UI styling prop
                        data={profits}
                        isLoading={l1 || personsLoading || balancesLoading}
                        onAdd={() => handleOpen('PROFIT')}
                        onDelete={(row) => deleteTransaction.mutate(row.id)}
                        disableAdd={!canTransact}
                    />
                </Box>

                <Box sx={{ flex: 1, minWidth: 0 }}>
                    <FinanceSection
                        title="Expenses"
                        subtitle="Rent, Food, Lifestyle"
                        type="expense" // UI styling prop
                        data={spendings}
                        isLoading={l2 || personsLoading || balancesLoading}
                        onAdd={() => handleOpen('SPENDING')}
                        onDelete={(row) => deleteTransaction.mutate(row.id)}
                        disableAdd={!canTransact}
                    />
                </Box>

                <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Paper sx={{ p: 0, height: '100%', borderRadius: 3, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
                        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid', borderColor: 'divider' }}>
                            <Box display="flex" alignItems="center" gap={2}>
                                <Avatar sx={{ bgcolor: 'secondary.main', color: 'primary.main' }} variant="rounded">
                                    <Repeat fontSize="small" />
                                </Avatar>
                                <Box>
                                    <Typography variant="h6" fontSize="1rem" fontWeight="bold">Recurrent Transactions</Typography>
                                    <Typography variant="caption" color="text.secondary">
                                        Automate recurring income and expenses.
                                    </Typography>
                                </Box>
                            </Box>
                            <Button
                                size="small"
                                variant="outlined"
                                startIcon={<Add />}
                                onClick={handleRecurrentOpenAdd}
                                disabled={!canTransact}
                                sx={{ borderRadius: 2, borderColor: 'divider', color: 'text.secondary' }}
                            >
                                Add
                            </Button>
                        </Box>
                        <Box sx={{ flexGrow: 1, minHeight: 400 }}>
                            <DataGrid
                                rows={recurrentRows}
                                columns={recurrentColumns}
                                loading={l3 || personsLoading || balancesLoading}
                                disableRowSelectionOnClick
                                rowHeight={55}
                                initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
                                sx={{
                                    border: 'none',
                                    '& .MuiDataGrid-cell': { alignItems: 'center' },
                                    '& .MuiDataGrid-columnSeparator': { display: 'none' }
                                }}
                            />
                        </Box>
                    </Paper>
                </Box>
            </Box>

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
                            <Controller
                                name="localDate"
                                control={control}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Date"
                                        type="date"
                                        fullWidth
                                        InputLabelProps={{ shrink: true }}
                                        sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
                                    />
                                )}
                            />
                            <Controller
                                name="details"
                                control={control}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Details"
                                        fullWidth
                                        multiline
                                        minRows={2}
                                        sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
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

            <Dialog open={balanceOpen} onClose={handleBalanceClose} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleBalanceSubmit(onSubmitBalance)}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">
                            {balanceEditing ? 'Update Balance' : 'New Balance'}
                        </Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="currencyId"
                                control={balanceControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        select
                                        label="Currency"
                                        fullWidth
                                        SelectProps={{ sx: { borderRadius: 2 } }}
                                    >
                                        {(currencies || []).map((currency) => (
                                            <MenuItem key={currency.id} value={currency.id}>
                                                {currency.symbol} — {currency.name}
                                            </MenuItem>
                                        ))}
                                    </TextField>
                                )}
                            />
                            <Controller
                                name="amount"
                                control={balanceControl}
                                rules={{ required: true, min: 0 }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Amount"
                                        type="number"
                                        fullWidth
                                        InputProps={{
                                            startAdornment: '$',
                                            sx: { borderRadius: 2, fontSize: '1.1rem', fontWeight: 600 }
                                        }}
                                    />
                                )}
                            />
                            <Controller
                                name="type"
                                control={balanceControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        select
                                        label="Type"
                                        fullWidth
                                        SelectProps={{ sx: { borderRadius: 2 } }}
                                    >
                                        {BalanceTypes.map((type) => (
                                            <MenuItem key={type} value={type}>{type}</MenuItem>
                                        ))}
                                    </TextField>
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={handleBalanceClose} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={saveBalance.isLoading}
                            sx={{ borderRadius: 2, px: 3 }}
                        >
                            {saveBalance.isLoading ? 'Saving...' : (balanceEditing ? 'Update' : 'Create')}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

            <Dialog open={personOpen} onClose={handlePersonClose} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handlePersonSubmit((data) => createPerson.mutate(data))}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">New Person</Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="name"
                                control={personControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Name" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={handlePersonClose} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={createPerson.isLoading}
                            sx={{ borderRadius: 2, px: 3 }}
                        >
                            {createPerson.isLoading ? 'Saving...' : 'Create'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

            <Dialog open={recurrentOpen} onClose={handleRecurrentClose} fullWidth maxWidth="sm" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleRecurrentSubmit(onSubmitRecurrent)}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">
                            {recurrentEditing ? 'Update Recurring Transaction' : 'New Recurring Transaction'}
                        </Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            <Grid item xs={12}>
                                <Controller
                                    name="name"
                                    control={recurrentControl}
                                    rules={{ required: true }}
                                    render={({ field }) => (
                                        <TextField {...field} label="Name" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                    )}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <Controller
                                    name="type"
                                    control={recurrentControl}
                                    render={({ field }) => (
                                        <TextField {...field} select label="Type" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
                                            <MenuItem value="PROFIT">PROFIT</MenuItem>
                                            <MenuItem value="SPENDING">SPENDING</MenuItem>
                                        </TextField>
                                    )}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <Controller
                                    name="periodDays"
                                    control={recurrentControl}
                                    rules={{ required: true, min: 1 }}
                                    render={({ field }) => (
                                        <TextField {...field} label="Every (days)" type="number" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                    )}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Controller
                                    name="category"
                                    control={recurrentControl}
                                    render={({ field }) => (
                                        <TextField
                                            {...field}
                                            select
                                            label="Category"
                                            fullWidth
                                            SelectProps={{ sx: { borderRadius: 2 } }}
                                        >
                                            {(recurrentType === 'SPENDING' ? SpendingTypes : ProfitTypes).map(opt => (
                                                <MenuItem key={opt} value={opt}>{opt.replace(/_/g, ' ')}</MenuItem>
                                            ))}
                                        </TextField>
                                    )}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Controller
                                    name="amount"
                                    control={recurrentControl}
                                    rules={{ required: true, min: 0 }}
                                    render={({ field }) => (
                                        <TextField
                                            {...field}
                                            label="Amount"
                                            type="number"
                                            fullWidth
                                            InputProps={{
                                                startAdornment: '$',
                                                sx: { borderRadius: 2, fontSize: '1.1rem', fontWeight: 600 }
                                            }}
                                        />
                                    )}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Controller
                                    name="isActive"
                                    control={recurrentControl}
                                    render={({ field }) => (
                                        <FormControlLabel
                                            control={<Switch checked={!!field.value} onChange={(e) => field.onChange(e.target.checked)} />}
                                            label="Active"
                                        />
                                    )}
                                />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={handleRecurrentClose} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={saveRecurrent.isLoading}
                            sx={{ borderRadius: 2, px: 3 }}
                        >
                            {saveRecurrent.isLoading ? 'Saving...' : (recurrentEditing ? 'Update' : 'Create')}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
        </Box>
    );
};