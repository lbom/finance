import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box,
    Paper,
    Typography,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    MenuItem,
    Chip,
    Avatar,
    Grid
} from '@mui/material';
import {
    Add,
    CurrencyExchange,
    CorporateFare,
    ConfirmationNumber
} from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { Controller, useForm } from 'react-hook-form';
import { api } from '../api/endpoints';

const SymbolGroups = ['SECURITY', 'FUTURES', 'CRYPTO', 'DEPOSIT'];

const DictionaryCard = ({ title, subtitle, icon, onAdd, rows, columns, loading }) => (
    <Paper sx={{ p: 0, borderRadius: 3, overflow: 'hidden' }}>
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid', borderColor: 'divider' }}>
            <Box display="flex" alignItems="center" gap={2}>
                <Avatar sx={{ bgcolor: 'secondary.main', color: 'primary.main' }} variant="rounded">
                    {icon}
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
                sx={{ borderRadius: 2, borderColor: 'divider', color: 'text.secondary' }}
            >
                Add
            </Button>
        </Box>
        <Box sx={{ height: 320 }}>
            <DataGrid
                rows={rows}
                columns={columns}
                loading={loading}
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
);

export const Dictionaries = () => {
    const queryClient = useQueryClient();
    const [currencyOpen, setCurrencyOpen] = useState(false);
    const [institutionOpen, setInstitutionOpen] = useState(false);
    const [symbolOpen, setSymbolOpen] = useState(false);

    const { data: currencies, isLoading: currenciesLoading } = useQuery({
        queryKey: ['currencies'],
        queryFn: api.dictionary.currency.list
    });
    const { data: institutions, isLoading: institutionsLoading } = useQuery({
        queryKey: ['institutions'],
        queryFn: api.dictionary.institution.list
    });
    const { data: symbols, isLoading: symbolsLoading } = useQuery({
        queryKey: ['symbols'],
        queryFn: api.dictionary.symbol.list
    });
    const createCurrency = useMutation({
        mutationFn: api.dictionary.currency.create,
        onSuccess: () => {
            queryClient.invalidateQueries(['currencies']);
            setCurrencyOpen(false);
            resetCurrency();
        }
    });
    const createInstitution = useMutation({
        mutationFn: api.dictionary.institution.create,
        onSuccess: () => {
            queryClient.invalidateQueries(['institutions']);
            setInstitutionOpen(false);
            resetInstitution();
        }
    });
    const createSymbol = useMutation({
        mutationFn: api.dictionary.symbol.create,
        onSuccess: () => {
            queryClient.invalidateQueries(['symbols']);
            setSymbolOpen(false);
            resetSymbol();
        }
    });
    const {
        control: currencyControl,
        handleSubmit: handleCurrencySubmit,
        reset: resetCurrency
    } = useForm({
        defaultValues: { symbol: '', name: '' }
    });

    const {
        control: institutionControl,
        handleSubmit: handleInstitutionSubmit,
        reset: resetInstitution
    } = useForm({
        defaultValues: { symbol: '', name: '', country: '' }
    });

    const {
        control: symbolControl,
        handleSubmit: handleSymbolSubmit,
        reset: resetSymbol
    } = useForm({
        defaultValues: { symbol: '', group: SymbolGroups[0], institutionId: '' }
    });
    const institutionMap = new Map((institutions || []).map((inst) => [inst.id, inst]));
    const currencyMap = new Map((currencies || []).map((currency) => [currency.id, currency]));

    const currencyColumns = [
        {
            field: 'symbol',
            headerName: 'Symbol',
            width: 120,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
                    <Chip label={params.value} size="small" sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }} />
                </Box>
            )
        },
        {
            field: 'name',
            headerName: 'Name',
            flex: 1,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', width: '100%' }}>
                    <Typography variant="body2" color="text.secondary" noWrap>
                        {params.value}
                    </Typography>
                </Box>
            )
        }
    ];

    const institutionColumns = [
        {
            field: 'symbol',
            headerName: 'Symbol',
            width: 120,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
                    <Chip label={params.value} size="small" sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }} />
                </Box>
            )
        },
        {
            field: 'name',
            headerName: 'Name',
            flex: 1,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', width: '100%' }}>
                    <Typography variant="body2" color="text.secondary" noWrap>
                        {params.value}
                    </Typography>
                </Box>
            )
        },
        {
            field: 'country',
            headerName: 'Country',
            width: 140,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', width: '100%' }}>
                    <Typography variant="body2" color="text.secondary" noWrap>
                        {params.value}
                    </Typography>
                </Box>
            )
        }
    ];

    const symbolRows = (symbols || []).map((item) => ({
        ...item,
        institution: institutionMap.get(item.institutionId)
    }));
    const symbolColumns = [
        {
            field: 'symbol',
            headerName: 'Symbol',
            width: 120,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
                    <Chip label={params.value} size="small" sx={{ bgcolor: 'secondary.main', color: 'primary.dark', fontWeight: 600 }} />
                </Box>
            )
        },
        {
            field: 'group',
            headerName: 'Group',
            width: 140,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
                    <Chip label={params.value} size="small" sx={{ bgcolor: 'rgba(0,0,0,0.05)', color: 'text.secondary', fontWeight: 600 }} />
                </Box>
            )
        },
        {
            field: 'institution',
            headerName: 'Institution',
            flex: 1,
            align: 'center',
            headerAlign: 'center',
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', width: '100%' }}>
                    <Typography variant="body2" color="text.secondary" noWrap>
                        {params.value?.name || '—'}
                    </Typography>
                </Box>
            )
        }
    ];

    return (
        <Box sx={{ pb: 5 }}>
            <Box mb={4}>
                <Typography variant="h4" gutterBottom>Dictionaries</Typography>
                <Typography variant="body1" color="text.secondary">
                    Manage currencies, institutions, and symbols.
                </Typography>
            </Box>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <DictionaryCard
                        title="Currencies"
                        subtitle="ISO codes and labels"
                        icon={<CurrencyExchange fontSize="small" />}
                        onAdd={() => setCurrencyOpen(true)}
                        rows={currencies || []}
                        columns={currencyColumns}
                        loading={currenciesLoading}
                    />
                </Grid>
                <Grid item xs={12} md={6}>
                    <DictionaryCard
                        title="Institutions"
                        subtitle="Brokers, exchanges, banks"
                        icon={<CorporateFare fontSize="small" />}
                        onAdd={() => setInstitutionOpen(true)}
                        rows={institutions || []}
                        columns={institutionColumns}
                        loading={institutionsLoading}
                    />
                </Grid>
                <Grid item xs={12} md={6}>
                    <DictionaryCard
                        title="Symbols"
                        subtitle="Instruments and tickers"
                        icon={<ConfirmationNumber fontSize="small" />}
                        onAdd={() => setSymbolOpen(true)}
                        rows={symbolRows}
                        columns={symbolColumns}
                        loading={symbolsLoading || institutionsLoading}
                    />
                </Grid>
            </Grid>

            <Dialog open={currencyOpen} onClose={() => setCurrencyOpen(false)} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleCurrencySubmit((data) => createCurrency.mutate(data))}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">New Currency</Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="symbol"
                                control={currencyControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Symbol" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                            <Controller
                                name="name"
                                control={currencyControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Name" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={() => setCurrencyOpen(false)} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button type="submit" variant="contained" disabled={createCurrency.isLoading} sx={{ borderRadius: 2, px: 3 }}>
                            {createCurrency.isLoading ? 'Saving...' : 'Create'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

            <Dialog open={institutionOpen} onClose={() => setInstitutionOpen(false)} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleInstitutionSubmit((data) => createInstitution.mutate(data))}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">New Institution</Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="symbol"
                                control={institutionControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Symbol" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                            <Controller
                                name="name"
                                control={institutionControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Name" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                            <Controller
                                name="country"
                                control={institutionControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Country" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={() => setInstitutionOpen(false)} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button type="submit" variant="contained" disabled={createInstitution.isLoading} sx={{ borderRadius: 2, px: 3 }}>
                            {createInstitution.isLoading ? 'Saving...' : 'Create'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

            <Dialog open={symbolOpen} onClose={() => setSymbolOpen(false)} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
                <form onSubmit={handleSymbolSubmit((data) => createSymbol.mutate({
                    ...data,
                    institutionId: data.institutionId ? Number(data.institutionId) : null
                }))}>
                    <DialogTitle>
                        <Typography variant="h6" fontWeight="bold">New Symbol</Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box display="flex" flexDirection="column" gap={2} mt={1}>
                            <Controller
                                name="symbol"
                                control={symbolControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} label="Symbol" fullWidth InputProps={{ sx: { borderRadius: 2 } }} />
                                )}
                            />
                            <Controller
                                name="group"
                                control={symbolControl}
                                rules={{ required: true }}
                                render={({ field }) => (
                                    <TextField {...field} select label="Group" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
                                        {SymbolGroups.map((group) => (
                                            <MenuItem key={group} value={group}>{group}</MenuItem>
                                        ))}
                                    </TextField>
                                )}
                            />
                            <Controller
                                name="institutionId"
                                control={symbolControl}
                                render={({ field }) => (
                                    <TextField {...field} select label="Institution (optional)" fullWidth SelectProps={{ sx: { borderRadius: 2 } }}>
                                        <MenuItem value="">None</MenuItem>
                                        {(institutions || []).map((inst) => (
                                            <MenuItem key={inst.id} value={inst.id}>{inst.symbol} — {inst.name}</MenuItem>
                                        ))}
                                    </TextField>
                                )}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 3 }}>
                        <Button onClick={() => setSymbolOpen(false)} sx={{ color: 'text.secondary' }}>Cancel</Button>
                        <Button type="submit" variant="contained" disabled={createSymbol.isLoading} sx={{ borderRadius: 2, px: 3 }}>
                            {createSymbol.isLoading ? 'Saving...' : 'Create'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

        </Box>
    );
};
