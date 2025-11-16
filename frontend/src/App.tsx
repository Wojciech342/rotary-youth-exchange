import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './hooks/auth';
import ProtectedRoute from './components/protected-route';
import styles from './styles/App.module.css';

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path='/login' element={} />

        <Route element={<ProtectedRoute />}>
          <Route path='/' element={}>
            <Route index element={} />
            <Route path="current" element={} />
            <Route path="archive" element={} />
            <Route path="coordinators" element={} />
          </Route>
        </Route>
      </Routes>
    </AuthProvider>
  );
}

export default App;
