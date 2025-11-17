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
                    <DetailRow label="Status" value={camp.status} />
                    <DetailRow label="Date" value={formattedDate} />
                    <DetailRow label="Ages" value={`${camp.age_min} - ${camp.age_max} years`} />
                    <DetailRow label="Cost" value={`${camp.price} EUR`} />
                    <DetailRow label="Country" value={camp.country} />
                    <h3 className={styles.subHeader}>Organizer</h3>
                    <DetailRow label="Name" value={camp.coordinator.name} />
                    <DetailRow label="Email" value={camp.coordinator.email} />
                    <DetailRow label="Phone" value={camp.coordinator.phone} />
                    <h3 className={styles.subHeader}>Description</h3>
                    <p className={styles.description}>{camp.description}</p>
                </div>
            </div>
        </Modal>
    );
};

export default CampDetailsModal;
