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
        <div className={styles.item}>
            <div className={styles.info}>
                <div className={styles.title}>
                    {camp.name}
                </div>
                <div className={styles.secondaryInfo}>
                    <p className={styles.country}> Country: {camp.country}</p>
                    <p className={styles.age}>Age: {camp.age_min}-{camp.age_max}</p>
                </div>
                <div className={styles.majorInfo}>
                    <p className={styles.price}>
                        <span className={styles.type}>Price:</span> {camp.price} USD
                    </p>
                    <p className={styles.date}>
                        <span className={styles.type}>Date:</span> {formattedDate}
                    </p>
                    <p className={`${styles.status} ${styles[camp.status.toLowerCase()]}`}>
                        <span className={styles.type}>Status:</span> {camp.status}
                    </p>
                </div>
            </div>
            <button className={styles.button} onClick={onClick}>
                More
            </button>
        </div>
    );
};

export default CampListItem;
