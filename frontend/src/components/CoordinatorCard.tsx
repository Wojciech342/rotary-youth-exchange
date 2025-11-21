import React from "react";
import { type Coordinator } from "../types/coordinator";
import styles from "../assets/styles/CoordinatorCard.module.css";

interface CoordinatorCardProps {
    coordinator: Coordinator;
    onClick: () => void;
}

const CoordinatorCard = ({ coordinator, onClick}: CoordinatorCardProps) => {
    const name = coordinator.name.split(" ");
    const placeholderImg = `https://placehold.co/100x100/aaabab/ffffff?text=${name[0].substring(0, 1) + name[1].substring(0, 1)}`;

    return (
        <div className={styles.card} onClick={onClick}>
            <div className={styles.imageWrapper}>
                <img
                    src={placeholderImg}
                    alt={coordinator.name}
                    className={styles.image}
                />
            </div>
            <div className={styles.wrapper}>
                <h3 className={styles.name}>{coordinator.name}</h3>
                <p className={styles.district}>
                    {coordinator.district}
                </p>
                <div className={styles.infoWrapper}>
                    <span className={styles.infoTitle}>Tel: </span>
                    <span className={styles.phone}>
                        {coordinator.phone}
                    </span>
                </div>
                <div className={styles.infoWrapper}>
                    <span className={styles.infoTitle}>Email: </span>
                    <span className={styles.email}>
                        {coordinator.email}
                    </span>
                </div>
            </div>
        </div>
    );
};

export default CoordinatorCard;
