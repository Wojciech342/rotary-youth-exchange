import React, { createContext, useContext, useState, type ReactNode } from "react";
import { type AuthContextType, type User } from "../types/types";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider =  ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    const login = async (email: string, password: string) => {
        //TODO: implement funtction that will call endpoint on the backend and save all necessary data
        console.log('Attempting login with:', { email, password });
        if (email === 'coordinator@example.com' && password === 'password') {
            const mockToken = 'mock.jwt.token-string-from-backend';
            const mockUser: User = { id: 1, name: 'Jan Kowalski', email: email };

            localStorage.setItem('authToken', mockToken);
            setUser(mockUser);
            setToken(mockToken);
            setIsLoading(false);
        } else {
            throw new Error('Invalid email or password');
        }
    }

    const logout = () => {
        //TODO: implement function for logout by deleting token from the storage
        setUser(null);
        setToken(null);
        localStorage.removeItem('authToken');
    }

    return (
        <AuthContext.Provider value={{ user, token, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};
