import React from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Navbar from "./Navbar";
import styles from "../assets/styles/MainLayout.module.css";

const MainLayout = () => {
    return (
        <div className={styles.layout}>
            <Header />
            <Navbar />
            <main className={styles.content}>
                <Outlet />
            </main>
        </div>
    );
};

export default MainLayout;
