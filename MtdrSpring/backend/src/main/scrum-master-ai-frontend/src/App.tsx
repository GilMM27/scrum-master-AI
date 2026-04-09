import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/auth/Login';
import Dashboard from './pages/dashboard/Dashboard';
import Backlog from './pages/backlog/Backlog';
import Sprint from './pages/sprint/Sprint';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/backlog" element={<Backlog />} />
        <Route path="/sprint" element={<Sprint />} />
      </Routes>
    </Router>
  )
}

export default App
