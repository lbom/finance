import client from './axios';

export const api = {
    auth: {
        // Matches @RequestBody LoginRequest in Java
        login: (username, password) => client.post('/auth/login', { username, password }),
        logout: () => client.post('/logout'),
    },
    trades: {
        list: (personId) => client.get('/finance/personal/trades', { params: { personId } }).then(r => r.data),
        create: (personId, data) => client.post('/finance/personal/trades', data, { params: { personId } }),
        update: (personId, tradeId, data) => client.put(`/finance/personal/trades/${tradeId}`, data, { params: { personId } }),
        profit: (personId) => client.get('/finance/personal/trades/profit', { params: { personId } }).then(r => r.data),
        delete: (personId, tradeId) => client.delete(`/finance/personal/trades/${tradeId}`, { params: { personId } }),
    },
    invest: {
        list: (personId) => client.get('/finance/personal/invest', { params: { personId } }).then(r => r.data),
        create: (personId, data) => client.post('/finance/personal/invest', data, { params: { personId } }),
        update: (personId, investId, data) => client.put(`/finance/personal/invest/${investId}`, data, { params: { personId } }),
        profit: (personId) => client.get('/finance/personal/invest/profit', { params: { personId } }).then(r => r.data),
        delete: (personId, investId) => client.delete(`/finance/personal/invest/${investId}`, { params: { personId } }),
    },
    personal: {
        list: (personId, type) => client.get('/finance/personal/transaction', { params: { personId, type } }).then(r => r.data),
        create: (personId, data) => client.post('/finance/personal/transaction', data, { params: { personId } }),
        update: (personId, transactionId, data) => (
            client.put(`/finance/personal/transaction/${transactionId}`, data, { params: { personId } })
        ),
        delete: (personId, transactionId) => client.delete(`/finance/personal/transaction/${transactionId}`, { params: { personId } }),
    },
    recurrent: {
        list: (personId) => client.get('/finance/personal/transaction/recurrent', { params: { personId } }).then(r => r.data),
        create: (personId, data) => client.post('/finance/personal/transaction/recurrent', data, { params: { personId } }),
        update: (personId, data) => client.put('/finance/personal/transaction/recurrent', data, { params: { personId } }),
    },
    person: {
        list: () => client.get('/finance/personal/person').then(r => r.data),
        create: (data) => client.post('/finance/personal/person', data),
    },
    balance: {
        list: (personId) => client.get('/finance/personal/balance', { params: { personId } }).then(r => r.data),
        create: (personId, data) => client.post('/finance/personal/balance', data, { params: { personId } }),
        sumAll: (personId, baseCurrencyId, balanceType) => (
            client.get('/finance/personal/balance/sumAll', { params: { personId, baseCurrencyId, balanceType } })
                .then(r => r.data)
        ),
    },
    dictionary: {
        currency: {
            list: () => client.get('/finance/dictionary/currency').then(r => r.data),
            create: (data) => client.post('/finance/dictionary/currency', data),
        },
        institution: {
            list: () => client.get('/finance/dictionary/institution').then(r => r.data),
            create: (data) => client.post('/finance/dictionary/institution', data),
        },
        symbol: {
            list: () => client.get('/finance/dictionary/symbol').then(r => r.data),
            create: (data) => client.post('/finance/dictionary/symbol', data),
        },
        pair: {
            list: () => client.get('/finance/dictionary/pair').then(r => r.data),
            create: (data) => client.post('/finance/dictionary/pair', data),
        },
    },
};