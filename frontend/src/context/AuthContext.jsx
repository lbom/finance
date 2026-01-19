import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/endpoints';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    // Initialize based on UI hint (localStorage)
    // The real security check happens on the first API call (401 response)
    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        return !!localStorage.getItem('is_authenticated');
    });

    const login = async (username, password) => {
        try {
            await api.auth.login(username, password);
            // On success: Set UI flag and State
            localStorage.setItem('is_authenticated', 'true');
            setIsAuthenticated(true);
            return true;
        } catch (error) {
            console.error("Login failed", error);
            return false;
        }
    };

    const logout = async () => {
        try {
            await api.auth.logout();
        } finally {
            // Always clear state, even if backend fails
            localStorage.removeItem('is_authenticated');
            setIsAuthenticated(false);
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);