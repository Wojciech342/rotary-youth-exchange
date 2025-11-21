import React, { useState, useEffect, useMemo } from "react";
import { type Coordinator } from "../types/coordinator";
import { useAuth } from "../hooks/auth";
import styles from "../assets/styles/CoordintorPage.module.css";

import CoordinatorCard from "../components/CoordinatorCard";
import CoordinatorDetailsModal from "../components/CoordinatorDetailsModal";
import { mockCoordinators } from "../types/mock-coordinators-data";

const CoordinatorsPage = () => {
    const { token } = useAuth();

    const [coordinators, setCoordinators] = useState<Coordinator[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchQuery, setSearchQuery] = useState('');

    const [selectedCoordinator, setSelectedCoordinator] = useState<Coordinator | null>(null);

    useEffect(() => {
        const fetchCoordinators = async () => {
            if (!token) return;

            setIsLoading(true);
            setError(null);

            try {
                //TODO: make an api call to fetch all coordinators

                await new Promise(resolve => setTimeout(resolve, 1000));
                const coordinatorData = mockCoordinators;

                setCoordinators(coordinatorData);
            } catch (err: any) {
                setError(err.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchCoordinators();
    }, [token]);

    const filteredCoordinators = useMemo(() => {
        return coordinators.filter(coordinator =>
            coordinator.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            coordinator.district.toLowerCase().includes(searchQuery.toLowerCase())
        );
    }, [coordinators, searchQuery]);

    const renderContent = () => {
        if (isLoading) {
            return <p>Loading coordinators...</p>;
        }
        if (error) {
            return <p className={styles.error}>Error: {error}</p>;
        }
        if (filteredCoordinators.length === 0) {
            return <p>No coordinators found.</p>;
        }

        return (
            <div className={styles.grid}>
                {filteredCoordinators.map(coordinator => (
                    <CoordinatorCard
                        key={coordinator.id}
                        coordinator={coordinator}
                        onClick={() => setSelectedCoordinator(coordinator)}
                    />
                ))}
            </div>
        );
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Coordinators</h1>
            <div className={styles.searchWrapper}>
                <input
                    type="text"
                    placeholder="Search by name, district"
                    className={styles.searchInput}
                    value={searchQuery}
                    onChange={e => setSearchQuery(e.target.value)}
                />
            </div>

            <section className={styles.section}>
                {renderContent()}
            </section>

            {selectedCoordinator && (
                <CoordinatorDetailsModal
                    coordinator={selectedCoordinator}
                    onClose={() => setSelectedCoordinator(null)}
                />
            )}
        </div>
    );
};

export default CoordinatorsPage;
