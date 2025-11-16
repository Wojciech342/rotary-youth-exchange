import React, { useState } from 'react';
import { type PageType, type LoginCredentials } from '../types/types';
import styles from "../styles/AuthPage.module.css";

interface AuthPageProps {
    setPage: (page: PageType) => void;
    setIsAuthenticated: (auth: boolean) => void;
}
//TODO: put props inside the functional component

const AuthPage: React.FC<AuthPageProps> = () => {
    const [credentials, setCredentials] = useState<LoginCredentials>({ email: '', password: ''});
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
        setErrorMessage(null);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        //TODO: write a function that call an API to communicate with backend
        //TODO: store given token from response inside storage and update state of authentication
        //TODO: navigate to current page
        //TODO: dispplay an error message if something wrong happend
    }

    return (
        <div className={styles.authPage}>
            <div className={styles.authCard}>
                <h1 className={styles.authTitle}>
                    Login
                </h1>

                {errorMessage && (
                    <div className={styles.error}>
                        {errorMessage}
                    </div>
                )}

                <form onSubmit={handleSubmit} className={styles.authForm}>
                    <input
                        type="email"
                        name="email"
                        placeholder='Enter your email'
                        value={credentials.email}
                        onChange={handleChange}
                        className={styles.inputField}
                        required
                        disabled={isLoading}
                    />

                    <input
                        type="password"
                        name="password"
                        placeholder='Enter your password'
                        value={credentials.password}
                        onChange={handleChange}
                        className={styles.inputField}
                        required
                        disabled={isLoading}
                    />

                    <button
                        type='submit'
                        className={styles.authSubmit}
                        disabled={isLoading}
                    >
                        {isLoading ? "Loading..." : "Login"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AuthPage;
