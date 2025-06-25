import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface AuthRedirectProps {
  children: React.ReactNode;
}

/**
 * Component that redirects authenticated users to dashboard
 * and allows unauthenticated users to access the wrapped component
 * Also enforces role-based access to specific login portals
 */
const AuthRedirect: React.FC<AuthRedirectProps> = ({ children }) => {
  const { user } = useAuth();
  const location = useLocation();

  // If user is authenticated, check if they're trying to access wrong portal
  if (user) {
    const currentPath = location.pathname;

    // Check if user is trying to access wrong login portal
    if (currentPath === '/student-login' && user.role !== 'STUDENT') {
      return <Navigate to="/dashboard" replace />;
    }
    if (currentPath === '/professor-login' && user.role !== 'PROFESSOR') {
      return <Navigate to="/dashboard" replace />;
    }
    if (currentPath === '/admin-login' && user.role !== 'ADMIN') {
      return <Navigate to="/dashboard" replace />;
    }

    // For any other page, redirect to dashboard
    return <Navigate to="/dashboard" replace />;
  }

  // If user is not authenticated, show the wrapped component
  return <>{children}</>;
};

export default AuthRedirect;
