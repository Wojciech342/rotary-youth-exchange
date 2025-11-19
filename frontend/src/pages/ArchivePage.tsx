import React, { useState, useEffect } from "react";
import { type Camp } from "../types/types";
import { useAuth } from "../hooks/auth";
import styles from "../assets/styles/ArchivePage.module.css";

import CampList from "../components/CampList";
import CampDetailsModal from "../components/CampDetailsModal";

import { mockPastCamps } from "../types/mock-past-camps";

const ArchivePage = () => {
    const { token } = useAuth();

    const [pastCamps, setPastCamps] = useState<Camp[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [selectedCamp, setSelectedCamp] = useState<Camp | null>(null);

    useEffect(() => {
        const fetchPastCamps = async () => {
            if (!token) return;

            setIsLoading(true);
            setError(null);

            try {
                //TODO: make an actually api call to fetch all previous camps

                await new Promise(resolve => setTimeout(resolve, 1000));
                const pastCampsData = mockPastCamps;

                setPastCamps(pastCampsData);
            } catch (err: any) {
                setError(err.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchPastCamps();
    }, [token]);

    const renderContent = () => {
        if (isLoading) {
            return <p>Loading past camps...</p>;
        }

        if (error) {
            return <p className={styles.error}>Error: {error}</p>
        }

        return (
            <CampList
                camps={pastCamps}
                onCampClick={camp => setSelectedCamp(camp)}
            />
        );
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>
                Archive Camps
            </h1>

            <section className={styles.section}>
                {renderContent()}
            </section>

            {selectedCamp && (
                <CampDetailsModal
                    camp={selectedCamp}
                    onClose={() => setSelectedCamp(null)}
                />
            )}
        </div>
    );
};

export default ArchivePage;
