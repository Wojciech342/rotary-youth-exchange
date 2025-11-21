import React, { useState, useEffect, useMemo } from 'react';
import { type Camp } from "../types/types";
import { useAuth } from '../hooks/auth';
import styles from "../assets/styles/CampsPage.module.css";

import CampList from '../components/CampList';
import CampDetailsModal from '../components/CampDetailsModal';
import AddCampModal from '../components/AddCampModal';

import { mockCoordinator, mockMyCamps, mockOtherCamps } from "../types/mock-data";

const CampsPage = () => {
    const { token, user } = useAuth();

    const [myCamps, setMyCamps] = useState<Camp[]>([]);
    const [otherCamps, setOtherCamps] = useState<Camp[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [selectedCamp, setSelectedCamp] = useState<Camp | null>(null);
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [searchQueryMyCamps, setSearchQueryMyCamps] = useState('');
    const [searchQueryOtherCamps, setSearchQueryOtherCamps] = useState('');

    useEffect(() => {
        const fetchCamps = async () => {
            if (!token) return;

            setIsLoading(true);
            setError(null);

            try {
                //TODO: Make a call to an endpoint on backend where we download camps created by current user
                //TODO: Make a call to an endpoint on backend where we download other camps active this year

                await new Promise(resolve => setTimeout(resolve, 1000));
                const myCampsData = mockMyCamps;
                const otherCampsData = mockOtherCamps;

                setMyCamps(myCampsData);
                setOtherCamps(otherCampsData);
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

    const filteredMyCamps = useMemo(() => {
        return myCamps.filter(camp =>
            camp.name.toLowerCase().includes(searchQueryMyCamps.toLowerCase()) ||
            camp.country.toLowerCase().includes(searchQueryMyCamps.toLowerCase()) ||
            camp.description.toLowerCase().includes(searchQueryMyCamps.toLowerCase())
        );
    }, [myCamps, searchQueryMyCamps]);

    const filteredOtherCamps = useMemo(() => {
        return otherCamps.filter(camp =>
            camp.name.toLowerCase().includes(searchQueryOtherCamps.toLowerCase()) ||
            camp.country.toLowerCase().includes(searchQueryOtherCamps.toLowerCase()) ||
            camp.description.toLowerCase().includes(searchQueryOtherCamps.toLowerCase())
        );
    }, [otherCamps, searchQueryOtherCamps]);

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
                        <input
                            type="text"
                            placeholder="Search by name, country, description"
                            className={styles.searchInput}
                            value={searchQueryMyCamps}
                            onChange={e => setSearchQueryMyCamps(e.target.value)}
                        />
                        <button
                            className={styles.addButton}
                            onClick={() => setIsAddModalOpen(true)}
                        >
                            + Add Camp
                        </button>
                    </div>
                    <CampList
                        camps={filteredMyCamps}
                        onCampClick={camp => setSelectedCamp(camp)}
                    />
                </section>

                <section className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2 className={styles.sectionTitle}>Other Camps</h2>
                        <input
                            type="text"
                            placeholder="Search by name, country, description"
                            className={styles.searchInput}
                            value={searchQueryOtherCamps}
                            onChange={e => setSearchQueryOtherCamps(e.target.value)}
                        />
                    </div>
                    <CampList
                        camps={filteredOtherCamps}
                        onCampClick={camp => setSelectedCamp(camp)}
                    />
                </section>
            </>
        )
    }

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>
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
