import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import UserProfile from './components/UserProfile';
import UsersList from './components/UsersList';
import UserView from './components/UserView';
import UserForm from './components/UserForm';
import TransactionsList from './components/TransactionsList';
import TransactionForm from './components/TransactionForm';
import SavingsList from './components/SavingsList';
import SavingsForm from './components/SavingsForm';
import CurrentBalance from './components/CurrentBalance';
import MonthlyBalance from './components/MonthlyBalance';
import Forecast from './components/Forecast';
import ProtectedRoute from './components/ProtectedRoute';
import { authService } from './services/auth';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={
            authService.isAuthenticated() ? <Navigate to="/" replace /> : <Login />
          }
        />
        <Route
          path="/register"
          element={
            authService.isAuthenticated() ? <Navigate to="/" replace /> : <Register />
          }
        />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <UserProfile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users"
          element={
            <ProtectedRoute requireAdmin>
              <UsersList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users/new"
          element={
            <ProtectedRoute requireAdmin>
              <UserForm isNew />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users/:id"
          element={
            <ProtectedRoute requireAdmin>
              <UserView />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users/:id/edit"
          element={
            <ProtectedRoute>
              <UserForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="/transactions"
          element={
            <ProtectedRoute>
              <TransactionsList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/transactions/new"
          element={
            <ProtectedRoute>
              <TransactionForm isNew />
            </ProtectedRoute>
          }
        />
        <Route
          path="/transactions/:id/edit"
          element={
            <ProtectedRoute>
              <TransactionForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="/savings"
          element={
            <ProtectedRoute>
              <SavingsList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/savings/new"
          element={
            <ProtectedRoute>
              <SavingsForm isNew />
            </ProtectedRoute>
          }
        />
        <Route
          path="/savings/:id/edit"
          element={
            <ProtectedRoute>
              <SavingsForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="/balance"
          element={
            <ProtectedRoute>
              <CurrentBalance />
            </ProtectedRoute>
          }
        />
        <Route
          path="/balance/monthly"
          element={
            <ProtectedRoute>
              <MonthlyBalance />
            </ProtectedRoute>
          }
        />
        <Route
          path="/forecast"
          element={
            <ProtectedRoute>
              <Forecast />
            </ProtectedRoute>
          }
        />
        <Route
          path="*"
          element={
            <Navigate to={authService.isAuthenticated() ? "/" : "/login"} replace />
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
