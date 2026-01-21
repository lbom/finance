import client from './axios';

export const api = {
    auth: {
        // Matches @RequestBody LoginRequest in Java
        login: (username, password) => client.post('/auth/login', { username, password }),
        logout: () => client.post('/auth/logout'),
    },
    trades: {
        list: () => client.get('/finance/trades').then(r => r.data),
        create: (data) => client.post('/finance/trades', data),
        profit: () => client.get('/finance/trades/profit').then(r => r.data),
    },
    invest: {
        list: () => client.get('/finance/invest').then(r => r.data),
        create: (data) => client.post('/finance/invest', data),
        profit: () => client.get('/finance/invest/profit').then(r => r.data),
    },
    personal: {
        list: (type) => client.get(`/finance/personal/transaction?type=${type}`).then(r => r.data),
        create: (data) => client.post('/finance/personal/transaction', data),
    },
};