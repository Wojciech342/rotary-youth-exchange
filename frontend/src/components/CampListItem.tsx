import React from "react";
import { type Camp } from "../types/types";
import styles from "../assets/styles/CampListItem.module.css";

interface CampListItemProps {
    camp: Camp;
    onClick: () => void;
}

const CampListItem = ({ camp, onClick }: CampListItemProps) => {
    const formattedDate = `${new Date(camp.date_start).toLocaleDateString()} - ${new Date(camp.date_end).toLocaleDateString()}`;

    return (
        <div className={styles.itemRow} onClick={onClick}>
            <div className={styles.itemCell}>
                {camp.name}
            </div>
            <div className={styles.itemCell}>
                {camp.country}
            </div>
            <div className={styles.itemCell}>
                {camp.age_min}-{camp.age_max}
            </div>
            <div className={styles.itemCell}>
                {formattedDate}
            </div>
            <div className={styles.itemCell}>
                <span className={`${styles.status} ${styles[camp.status.toLowerCase()]}`}>
                    {camp.status}
                </span>
            </div>
        </div>
    );
};

export default CampListItem;
