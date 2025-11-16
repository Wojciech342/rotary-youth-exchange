import React, { createContext, useContext, useState, type ReactNode } from "react";
import { type AuthContextType, type User } from "../types/types";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider =  ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    const login = async (email: string, password: string) => {
        //TODO: implement funtction that will call endpoint on the backend and save all necessary data
    }

    const logout = () => {
        //TODO: implement function for logout by deleting token from the storage
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
