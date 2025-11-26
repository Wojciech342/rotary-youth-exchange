import React, { type ReactNode } from "react";
import styles from '../assets/styles/Modal.module.css';

interface ModalProps {
    children: ReactNode;
    onClose: () => void;
    title: string;
}

const Modal = ({ children, onClose, title }: ModalProps) => {
    const handleContentClick = (e: React.MouseEvent) => {
        e.stopPropagation();
    };

    return (
        <div className={styles.overlay} onClick={onClose}>
            <div className={styles.modal} onClick={handleContentClick}>
                <div className={styles.header}>
                    <h2 className={styles.title}>{title}</h2>
                    <button className={styles.closeButton} onClick={onClose}>
                        &times;
                    </button>
                </div>
                <div className={styles.content}>
                    {children}
                </div>
            </div>
        </div>
    );
};

export default Modal;
