import React from "react";
import { useAuth } from "../hooks/auth";
import { useNavigate } from "react-router-dom";
import styles from "../assets/styles/Header.module.css";
import logo from "../assets/images/rotary-logo.svg";

const Header = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <header className={styles.header}>
            <div className={styles.container}>
                <div className={styles.logo}>
                    <img src={logo} alt="Rotary Club" className={styles.image} />
                    <p className={styles.text}>
                        Rotary <span>Youth Exchange</span>
                    </p>
                </div>
                <div className={styles.userContainer}>
                    <p className={styles.welcome}>
                        Welcome, {user?.name || 'User'}
                    </p>
                    <button
                        onClick={handleLogout}
                        className={styles.logout}
                    >
                        Logout
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Header;
