import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './hooks/auth';
import ProtectedRoute from './components/protected-route';
import styles from './assets/styles/App.module.css';

import LoginPage from './pages/LoginPage';

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path='/login' element={<LoginPage />} />

        {/* <Route element={<ProtectedRoute />}>
          <Route path='/' element={}>
            <Route index element={} />
            <Route path="current" element={} />
            <Route path="archive" element={} />
            <Route path="coordinators" element={} />
          </Route>
        </Route> */}
      </Routes>
    </AuthProvider>
  );
}

export default App;
