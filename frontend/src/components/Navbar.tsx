import React from "react";
import { NavLink } from "react-router-dom";
import styles from "../assets/styles/Navbar.module.css";

const Navbar = () => {
    const getNavLinkClass = ({ isActive }: { isActive: boolean }) => {
        return isActive ? `${styles.navLink} ${styles.active}` : styles.navLink;
    };

    return (
        <nav className={styles.nav}>
            <div className={styles.container}>
                <NavLink to="/camps" className={getNavLinkClass}>
                    Camps
                </NavLink>
                <NavLink to="/archive" className={getNavLinkClass}>
                    Archive
                </NavLink>
                <NavLink to="/coordinators" className={getNavLinkClass}>
                    Coordinators
                </NavLink>
            </div>
        </nav>
    );
};

export default Navbar;
