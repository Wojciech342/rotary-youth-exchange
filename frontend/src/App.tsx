import React, { useState } from 'react';
import { type PageType } from './types/page-type';
import styles from './styles/App.module.css';

const App: React.FC = () => {

  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  // TODO: write the correct logic for definition of authenticated user from storage
  const [currentPage, setCurrentPage] = useState<PageType>('Current');

  const renderPage = (): React.ReactNode => {
    if (!isAuthenticated) {
      return;
    }
    switch(currentPage) {
      case 'Current':
        return;
      case 'Archive':
        return;
      case 'Coordinators':
        return;
      default:
        return;
    }
  }

  return(
    <div className={styles.appContainer}>
      <main className={styles.mainContent}>
        {renderPage()}
      </main>
    </div>
  );
};

export default App
