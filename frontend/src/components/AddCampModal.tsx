import React, { useState } from "react";
import { type Camp, type CampCreateDto } from "../types/types";
import { useAuth } from "../hooks/auth";
import Modal from "./Modal";
import styles from "../assets/styles/AddCampModal.module.css";

interface AddCampModalProps {
    onClose: () => void;
    onCampAdded: (newCamp: Camp) => void;
}

const initialState: CampCreateDto = {
    name: '',
    description: '',
    country: '',
    date_start: '',
    date_end: '',
    age_min: 0,
    age_max: 0,
    price: 0,
    status: 'OPEN',
    entire_limit: 0,
    male_limit: 0,
    female_limit: 0,
    limit_per_country: 0,
};

const AddCampModal = ({ onClose, onCampAdded }: AddCampModalProps) => {
    const [formData, setFormData] = useState<CampCreateDto>(initialState);
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    // const { token, user } = useAuth();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;

        setFormData(prev => ({
            ...prev,
            [name]: type === 'number' ? parseInt(value) || 0 : value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            //TODO: call here an endpoint responsible for adding new camp
        } catch (err: any) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Modal title="Create New Camp" onClose={onClose}>
            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.formGroup}>
                    <label htmlFor="name" className={styles.label}>Camp Name</label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        className={styles.input}
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className={styles.formGroup}>
                    <label htmlFor="description" className={styles.label}>Description</label>
                    <textarea
                        name="description"
                        id="description"
                        className={styles.textarea}
                        rows={4}
                        value={formData.description}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className={styles.grid}>
                    <div className={styles.formGroup}>
                        <label htmlFor="date_start" className={styles.label}>Start Date</label>
                        <input
                            type="date"
                            id="date_start"
                            name="date_start"
                            className={styles.input}
                            value={formData.date_start}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="date_end" className={styles.label}>End Date</label>
                        <input
                            type="date"
                            id="date_end"
                            name="date_end"
                            className={styles.input}
                            value={formData.date_end}
                            onChange={handleChange}
                            required
                        />
                    </div>
                </div>
                <div className={styles.grid}>
                    <div className={styles.formGroup}>
                        <label htmlFor="price" className={styles.label}>Price (USD)</label>
                        <input
                            type="number"
                            id="price"
                            name="price"
                            className={styles.input}
                            value={formData.price}
                            onChange={handleChange} />
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="status" className={styles.label}>Status</label>
                        <select
                            id="status"
                            name="status"
                            className={styles.input}
                            value={formData.status}
                            onChange={handleChange}
                        >
                            <option value="PENDING">Pending</option>
                            <option value="OPEN">Open</option>
                        </select>
                    </div>
                </div>
                <div className={styles.gridSmall}>
                    <div className={styles.formGroup}>
                        <label htmlFor="age_min" className={styles.label}>Min Age</label>
                        <input
                            type="number"
                            id="age_min"
                            name="age_min"
                            className={styles.input}
                            value={formData.age_min}
                            onChange={handleChange}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="age_max" className={styles.label}>Max Age</label>
                        <input
                            type="number"
                            id="age_max"
                            name="age_max"
                            className={styles.input}
                            value={formData.age_max}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                <div className={styles.gridSmall}>
                    <div className={styles.formGroup}>
                        <label htmlFor="entire_limit" className={styles.label}>Total Limit</label>
                        <input
                            type="number"
                            id="entire_limit"
                            name="entire_limit"
                            className={styles.input}
                            value={formData.entire_limit}
                            onChange={handleChange}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="male_limit" className={styles.label}>Male Limit</label>
                        <input
                            type="number"
                            id="male_limit"
                            name="male_limit"
                            className={styles.input}
                            value={formData.male_limit}
                            onChange={handleChange}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="female_limit" className={styles.label}>Female Limit</label>
                        <input
                            type="number"
                            id="female_limit"
                            name="female_limit"
                            className={styles.input}
                            value={formData.female_limit}
                            onChange={handleChange}
                        />
                    </div>
                </div>

                {error && <p className={styles.error}>{error}</p>}

                <div className={styles.buttonGroup}>
                    <button type="button" className={styles.buttonSecondary} onClick={onClose} disabled={isLoading}>
                        Cancel
                    </button>
                    <button type="submit" className={styles.buttonPrimary} disabled={isLoading}>
                        {isLoading ? 'Creating...' : 'Create Camp'}
                    </button>
                </div>
            </form>
        </Modal>
    );
};

export default AddCampModal;
