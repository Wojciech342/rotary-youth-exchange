import React from "react";
import { type Camp } from "../types/types";
import Modal from "./Modal";
import DetailRow from "./DetailRow";
import styles from "../assets/styles/CampDetailsModal.module.css";

interface CampDetailsModalProps {
    camp: Camp;
    onClose: () => void;
}

const CampDetailsModal = ({ camp, onClose }: CampDetailsModalProps) => {
    const formattedDate = `${new Date(camp.date_start).toLocaleDateString()} - ${new Date(camp.date_end).toLocaleDateString()}`;

    return (
        <Modal title={camp.name} onClose={onClose}>
            <div className={styles.container}>
                <div className={styles.detailsColumn}>
                    <div className={styles.details}>
                        <div className={styles.countryDetails}>
                            <p className={styles.strong}>Country:</p>
                            <p className={`${styles.country} ${styles.detail}`}>{camp.country}</p>
                        </div>
                        <div className={styles.priceDetails}>
                            <p className={styles.strong}>Price:</p>
                            <p className={`${styles.price} ${styles.detail}`}>{camp.price} USD</p>
                        </div>
                        <div className={styles.ageDetails}>
                            <p className={styles.strong}>Age:</p>
                            <p className={`${styles.age} ${styles.detail}`}>{camp.age_min}-{camp.age_max}</p>
                        </div>
                        <div className={styles.dateDetails}>
                            <p className={styles.strong}>Date:</p>
                            <p className={`${styles.date} ${styles.detail}`}>{formattedDate}</p>
                        </div>
                    </div>

                    <p className={styles.statusDetails}>
                        <p className={styles.strong}>Status:</p>
                        <p className={`${styles.status} ${styles.detail}`}>{camp.status}</p>
                    </p>

                    <div className={styles.descriptionDetails}>
                        <p className={styles.strong}>Description:</p>
                        <p className={styles.description}>{camp.description}</p>
                    </div>

                    <div className={styles.organizerDetails}>
                        <p className={styles.strong}>Organizer:</p>
                        <p className={`${styles.organizer} ${styles.detail}`}>{camp.coordinator.name}</p>
                    </div>
                </div>
            </div>
        </Modal>
    );
};

export default CampDetailsModal;
