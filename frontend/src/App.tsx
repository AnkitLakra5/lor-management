import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Homepage from './components/Homepage';
import Login from './components/Login';
import StudentLogin from './components/StudentLogin';
import ProfessorLogin from './components/ProfessorLogin';
import AdminLogin from './components/AdminLogin';
import Register from './components/Register';
import StudentDashboard from './components/StudentDashboard';
import ProfessorDashboard from './components/ProfessorDashboard';
import AdminDashboard from './components/AdminDashboard';
import Navbar from './components/Navbar';
import AuthRedirect from './components/AuthRedirect';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

const ProtectedRoute: React.FC<{ children: React.ReactNode; allowedRoles?: string[] }> = ({
  children,
  allowedRoles
}) => {
  const { isAuthenticated, user, isLoading } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

const DashboardRouter: React.FC = () => {
  const { user, logout } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Additional security check - ensure user has a valid role
  if (!['STUDENT', 'PROFESSOR', 'ADMIN'].includes(user.role)) {
    logout();
    return <Navigate to="/login" replace />;
  }

  switch (user.role) {
    case 'STUDENT':
      return <StudentDashboard />;
    case 'PROFESSOR':
      return <ProfessorDashboard />;
    case 'ADMIN':
      return <AdminDashboard />;
    default:
      logout();
      return <Navigate to="/login" replace />;
  }
};

const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {isAuthenticated && <Navbar />}
      <Box component="main" sx={{ flexGrow: 1 }}>
        <Routes>
          <Route path="/" element={<AuthRedirect><Homepage /></AuthRedirect>} />
          <Route path="/login" element={<AuthRedirect><Login /></AuthRedirect>} />
          <Route path="/student-login" element={<AuthRedirect><StudentLogin /></AuthRedirect>} />
          <Route path="/professor-login" element={<AuthRedirect><ProfessorLogin /></AuthRedirect>} />
          <Route path="/admin-login" element={<AuthRedirect><AdminLogin /></AuthRedirect>} />
          <Route path="/register" element={<AuthRedirect><Register /></AuthRedirect>} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardRouter />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Box>
    </Box>
  );
};

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <AppContent />
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
