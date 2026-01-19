import axios from 'axios';
import Cookies from 'js-cookie';

// 1. Create instance with 'withCredentials' to send Cookies
const client = axios.create({
    baseURL: 'http://localhost:8080/', // adjusted to match standard Spring /api prefix
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true, // CRITICAL: Allows sending/receiving HttpOnly cookies
});

// 2. CSRF Interceptor: Reads the readable cookie -> Sets the required header
client.interceptors.request.use((config) => {
    // Only add token for state-changing methods
    if (['post', 'put', 'delete', 'patch'].includes(config.method)) {
        const csrfToken = Cookies.get('XSRF-TOKEN');
        if (csrfToken) {
            config.headers['X-XSRF-TOKEN'] = csrfToken;
        }
    }
    return config;
});

// 3. Response Interceptor: Handles 401 (Session Expired) globally
client.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Clear UI hints and redirect
            localStorage.removeItem('is_authenticated');
            // Optional: Dispatch a custom event if you want to clear Context without forcing reload
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default client;