import axios from 'axios';

const client = axios.create({
    baseURL: 'http://localhost:8080',
});

export const fetcher = async ({ queryKey }) => {
    const [url] = queryKey;
    const response = await client.get(url);
    return response.data;
};

export const api = {
    trades: {
        list: () => client.get('/finance/trades').then(r => r.data),
        create: (data) => client.post('/finance/trades', data),
        profit: () => client.get('/finance/trades/profit').then(r => r.data),
    },
    invest: {
        profit: () => client.get('/finance/invest/profit').then(r => r.data),
    },
    personal: {
        revenue: () => client.get('/finance/personal/revenue').then(r => r.data),
        expenses: () => client.get('/finance/personal/expenses').then(r => r.data),
    },
};
