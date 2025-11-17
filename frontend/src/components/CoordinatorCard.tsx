import React from "react";
import { type Coordinator } from "../types/coordinator";
import styles from "../assets/styles/CoordinatorCard.module.css";

interface CoordinatorCardProps {
    coordinator: Coordinator;
    onClick: () => void;
}

const CoordinatorCard = ({ coordinator, onClick}: CoordinatorCardProps) => {
    const placeholderImg = `https://placehold.co/100x100/003a70/fecb00?text=${coordinator.name.substring(0, 1)}`;

    return (
        <div className={styles.card} onClick={onClick}>
            <div className={styles.imageWrapper}>
                <img
                    src={placeholderImg}
                    alt={coordinator.name}
                    className={styles.image}
                />
            </div>
            <div className={styles.infoWrapper}>
                <h3 className={styles.name}>{coordinator.name}</h3>
                <p className={styles.district}>
                    {coordinator.district}
                </p>
            </div>
        </div>
    );
};

export default CoordinatorCard;
