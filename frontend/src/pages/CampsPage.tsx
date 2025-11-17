import React, { useState, useEffect } from 'react';
import { type Camp } from "../types/types";
import { useAuth } from '../hooks/auth';
import styles from "../assets/styles/CampsPage.module.css";

import CampList from '../components/CampList';
import CampDetailsModal from '../components/CampDetailsModal';
import AddCampModal from '../components/AddCampModal';

const CampsPage = () => {
    const { token, user } = useAuth();

    const [myCamps, setMyCamps] = useState<Camp[]>([]);
    const [otherCamps, setOtherCamps] = useState<Camp[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [selectedCamp, setSelectedCamp] = useState<Camp | null>(null);
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);

    useEffect(() => {
        const fetchCamps = async () => {
            if (!token) return;

            setIsLoading(true);
            setError(null);

            try {
                //TODO: Make a call to an endpoint on backend where we download camps created by current user
                //TODO: Make a call to an endpoint on backend where we download other camps active this year
            } catch (err: any) {
                setError(err.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchCamps();
    }, [token]);

    const handleCampAdded = (newCamp: Camp) => {
        setMyCamps(prevCamps => [newCamp, ...prevCamps]);
    };

    const renderContent = () => {
        if (isLoading) {
            return <p>Loading camps...</p>;
        }

        if (error) {
            return <p className={styles.error}>Error: {error}</p>;
        }

        return (
            <>
                <section className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2 className={styles.sectionTitle}>My Camps</h2>
                        <button
                            className={styles.addButton}
                            onClick={() => setIsAddModalOpen(true)}
                        >
                            + Add Camp
                        </button>
                    </div>
                    <CampList
                        camps={myCamps}
                        onCampClick={camp => setSelectedCamp(camp)}
                    />
                </section>

                <section className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2 className={styles.sectionTitle}>Other Camps</h2>
                    </div>
                    <CampList
                        camps={otherCamps}
                        onCampClick={camp => setSelectedCamp(camp)}
                    />
                </section>
            </>
        )
    }

    return (
        <div>
            <h1>
                Current Camps
            </h1>
            {renderContent()}

            {selectedCamp && (
                <CampDetailsModal
                    camp={selectedCamp}
                    onClose={() => setSelectedCamp(null)}
                />
            )}

            {isAddModalOpen && (
                <AddCampModal
                    onClose={() => setIsAddModalOpen(false)}
                    onCampAdded={handleCampAdded}
                />
            )}
        </div>
    );
};

export default CampsPage;
