import React from "react";
import { type Coordinator } from "../types/coordinator";
import Modal from "./Modal";
import DetailRow from "./DetailRow";
import styles from "../assets/styles/CoordinatorDetailsModal.module.css";

interface CoordinatorDetailsModalProps {
    coordinator: Coordinator;
    onClose: () => void;
}

const CoordinatorDetailsModal = ({ coordinator, onClose }: CoordinatorDetailsModalProps) => {
    const name = coordinator.name.split(" ");
    const placeholderImg = `https://placehold.co/400x400/aaabab/ffffff?text=${name[0].substring(0, 1) + name[1].substring(0, 1)}`;

    return (
        <Modal title="Coordinator Details" onClose={onClose}>
            <div className={styles.container}>
                <div className={styles.imageColumn}>
                    <img
                        src={placeholderImg}
                        alt={coordinator.name}
                        className={styles.image}
                    />
                </div>

                <div className={styles.detailsColumn}>
                    <h2 className={styles.name}>{coordinator.name}</h2>
                    <p className={styles.district}>
                        {coordinator.district}
                    </p>
                    <h3 className={styles.contact}>Contact</h3>
                    <DetailRow label="Email" value={coordinator.email} />
                    <DetailRow label="Phone" value={coordinator.phone} />

                    {coordinator.description && (
                        <>
                            <h3 className={styles.about}>About</h3>
                            <p className={styles.description}>{coordinator.description}</p>
                        </>
                    )}

                    <h3 className={styles.camps}>Managed Camps</h3>
                    <ul className={styles.campList}>
                        {coordinator.camps && coordinator.camps.length > 0 ? (
                            coordinator.camps.map(camp => (
                                <li key={camp.id} className={styles.campItem}>
                                    {camp.name} ({camp.year})
                                </li>
                            ))
                        ): (
                            <li className={styles.campItem}>No camps managed.</li>
                        )}
                    </ul>
                </div>
            </div>
        </Modal>
    );
};

export default CoordinatorDetailsModal;
