import React from "react";
import styles from "../assets/styles/DetailRow.module.css";

const DetailRow = ({ label, value }: { label: string, value: string }) => {
    return (
        <div className={styles.detailRow}>
            <strong className={styles.detailLabel}>{label}:</strong>
            <span className={styles.detailValue}>{value}</span>
        </div>
    );
};

export default DetailRow;
