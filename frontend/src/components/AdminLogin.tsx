import React, { useState } from 'react';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
  Link,
  Avatar,
  Chip,
  IconButton,
} from '@mui/material';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { ArrowBack, AdminPanelSettings as AdminIcon } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { LoginRequest } from '../types';
import PasswordField from './PasswordField';

const AdminLogin: React.FC = () => {
  const [formData, setFormData] = useState<LoginRequest>({
    username: '',
    password: '',
  });
  const [error, setError] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  const { login, logout } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await login(formData);

      // Check if the logged-in user is actually an admin
      const tokenPayload = JSON.parse(atob(localStorage.getItem('token')?.split('.')[1] || ''));
      const userRole = tokenPayload.role;

      if (userRole !== 'ADMIN') {
        // Clear the authentication and show error
        logout();
        setError(`Access denied. This is the Admin Portal. You are logged in as ${userRole}. Please use the correct portal for your role.`);
        return;
      }

      navigate('/dashboard', { replace: true });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #f57c00 0%, #ef6c00 100%)',
        py: 4,
      }}
    >
      <Container component="main" maxWidth="sm">
        <Box
          sx={{
            marginTop: 4,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Paper elevation={6} sx={{ padding: 4, width: '100%', borderRadius: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
              <IconButton 
                onClick={() => navigate('/')}
                sx={{ mr: 1 }}
              >
                <ArrowBack />
              </IconButton>
              <Typography component="h1" variant="h6" sx={{ flexGrow: 1 }}>
                Back to Home
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3 }}>
              <Avatar
                sx={{
                  bgcolor: '#f57c00',
                  width: 80,
                  height: 80,
                }}
              >
                <AdminIcon sx={{ fontSize: 40 }} />
              </Avatar>
            </Box>

            <Typography component="h1" variant="h4" align="center" gutterBottom>
              Admin Portal
            </Typography>
            
            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3 }}>
              <Chip 
                label="Administrator Sign In"
                sx={{ 
                  bgcolor: '#f57c00',
                  color: 'white',
                  fontWeight: 'bold',
                  px: 2
                }}
              />
            </Box>

            <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 3 }}>
              Manage users, oversee all requests, and maintain system settings.
            </Typography>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                label="Admin Email Address"
                name="username"
                autoComplete="email"
                autoFocus
                value={formData.username}
                onChange={handleChange}
                disabled={isLoading}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    '&.Mui-focused fieldset': {
                      borderColor: '#f57c00',
                    },
                  },
                }}
              />
              <PasswordField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                id="password"
                autoComplete="current-password"
                value={formData.password}
                onChange={handleChange}
                disabled={isLoading}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    '&.Mui-focused fieldset': {
                      borderColor: '#f57c00',
                    },
                  },
                }}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ 
                  mt: 3, 
                  mb: 2,
                  bgcolor: '#f57c00',
                  py: 1.5,
                  fontSize: '1.1rem',
                  fontWeight: 'bold',
                  '&:hover': {
                    bgcolor: '#ef6c00',
                  },
                }}
                disabled={isLoading}
              >
                {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Sign In as Administrator'}
              </Button>

              <Box sx={{ textAlign: 'center', mt: 2 }}>
                <Typography variant="body2">
                  Need access to admin portal?{' '}
                  <Link component={RouterLink} to="/register" sx={{ color: '#f57c00' }}>
                    Contact System Administrator
                  </Link>
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Box>
      </Container>
    </Box>
  );
};

export default AdminLogin;
