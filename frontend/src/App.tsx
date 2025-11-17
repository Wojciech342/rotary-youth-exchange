import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './hooks/auth';
import ProtectedRoute from './components/protected-route';
import MainLayout from './components/MainLayout';
import styles from './assets/styles/App.module.css';

import LoginPage from './pages/LoginPage';
import CampsPage from './pages/CampsPage';
import ArchivePage from './pages/ArchivePage';
import CoordinatorsPage from './pages/CoordinatorsPage';

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path='/login' element={<LoginPage />} />

        <Route element={<ProtectedRoute />}>
          <Route path='/' element={<MainLayout />}>
            <Route index element={<CampsPage />} />
            <Route path="camps" element={<CampsPage />} />
            <Route path="archive" element={<ArchivePage />} />
            <Route path="coordinators" element={<CoordinatorsPage />} />
          </Route>
        </Route>
      </Routes>
    </AuthProvider>
  );
}

export default App;
