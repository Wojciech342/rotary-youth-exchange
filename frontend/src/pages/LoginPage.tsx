import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/auth';
import logo from "../assets/images/rotary-logo.svg";
import styles from "../assets/styles/LoginPage.module.css";

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const auth = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            await auth.login(email, password);
            navigate('/camps');
        } catch (err: any) {
            setError(err.message || 'Login failed. Please check your credentials.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.wrapper}>
                <div className={styles.description}>
                    <div className={styles.logo}>
                        <img src={logo} alt="Rotary Club" className={styles.image}/>
                        <p className={styles.text}>
                            Rotary <span>Youth Exchange</span>
                        </p>
                    </div>
                    <h2 className={styles.title}>
                        Camp Management System
                    </h2>
                    <p className={styles.subtitle}>
                        Please sign in to continue
                    </p>
                </div>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.inputGroup}>
                        {error && (
                            <div className={styles.error}>
                                {error}
                            </div>
                        )}
                        <div className={styles.inputWrapper}>
                            <label htmlFor="email-address" className={styles.label}>
                                Email
                            </label>
                            <input
                                id="email-address"
                                name="email"
                                type="email"
                                autoComplete="email"
                                required
                                className={styles.input}
                                placeholder="example@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                        <div className={styles.inputWrapper}>
                            <label htmlFor="password" className={styles.label}>
                                Password
                            </label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                autoComplete="current-password"
                                required
                                className={styles.input}
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className={styles.button}
                        >
                            {isLoading ? "Singing in..." : "Sign in"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
