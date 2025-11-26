import React from "react";
import { type Camp } from "../types/types";
import CampListItem from "./CampListItem";
import styles from "../assets/styles/CampList.module.css";

interface CampListProps {
    camps: Camp[];
    onCampClick: (camp: Camp) => void;
}

const CampList = ({ camps, onCampClick }: CampListProps) => {
    if (camps.length === 0) {
        return <p className={styles.empty}>No camps to display.</p>;
    }

    return (
        <div className={styles.container}>
            <div className={styles.list}>
                {camps.map(camp => (
                    <CampListItem
                        key={camp.id}
                        camp={camp}
                        onClick={() => onCampClick(camp)}
                    />
                ))}
            </div>
        </div>
    );
};

export default CampList;
