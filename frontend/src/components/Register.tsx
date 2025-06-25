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
  Tabs,
  Tab,
} from '@mui/material';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { RegisterStudentRequest, RegisterProfessorRequest } from '../types';
import { apiService } from '../services/api';
import PasswordField from './PasswordField';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`registration-tabpanel-${index}`}
      aria-labelledby={`registration-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const Register: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [studentData, setStudentData] = useState<RegisterStudentRequest>({
    name: '',
    email: '',
    password: '',
    registrationNumber: '',
    examinationNumber: '',
    course: '',
  });
  const [professorData, setProfessorData] = useState<RegisterProfessorRequest>({
    name: '',
    email: '',
    password: '',
    userId: '',
    department: '',
  });
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
    setError('');
    setSuccess('');
  };

  const handleStudentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setStudentData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleProfessorChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProfessorData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleStudentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsLoading(true);

    try {
      await apiService.registerStudent(studentData);
      setSuccess('Student registration successful! Logging you in...');
      
      // Auto-login after successful registration
      setTimeout(async () => {
        await login({ username: studentData.email, password: studentData.password });
        navigate('/dashboard');
      }, 1500);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleProfessorSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsLoading(true);

    try {
      await apiService.registerProfessor(professorData);
      setSuccess('Professor registration successful! Logging you in...');
      
      // Auto-login after successful registration
      setTimeout(async () => {
        await login({ username: professorData.email, password: professorData.password });
        navigate('/dashboard');
      }, 1500);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          marginTop: 4,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
          <Typography component="h1" variant="h4" align="center" gutterBottom>
            Register for LOR System
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {success}
            </Alert>
          )}

          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="registration tabs">
              <Tab label="Student Registration" />
              <Tab label="Professor Registration" />
            </Tabs>
          </Box>

          <TabPanel value={tabValue} index={0}>
            <Box component="form" onSubmit={handleStudentSubmit}>
              <TextField
                margin="normal"
                required
                fullWidth
                name="name"
                label="Full Name"
                value={studentData.name}
                onChange={handleStudentChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="email"
                label="Email Address"
                type="email"
                value={studentData.email}
                onChange={handleStudentChange}
                disabled={isLoading}
              />
              <PasswordField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                value={studentData.password}
                onChange={handleStudentChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="registrationNumber"
                label="Registration Number"
                value={studentData.registrationNumber}
                onChange={handleStudentChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="examinationNumber"
                label="Examination Number"
                value={studentData.examinationNumber}
                onChange={handleStudentChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="course"
                label="Course"
                value={studentData.course}
                onChange={handleStudentChange}
                disabled={isLoading}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                disabled={isLoading}
              >
                {isLoading ? <CircularProgress size={24} /> : 'Register as Student'}
              </Button>
            </Box>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
            <Box component="form" onSubmit={handleProfessorSubmit}>
              <TextField
                margin="normal"
                required
                fullWidth
                name="name"
                label="Full Name"
                value={professorData.name}
                onChange={handleProfessorChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="email"
                label="Email Address"
                type="email"
                value={professorData.email}
                onChange={handleProfessorChange}
                disabled={isLoading}
              />
              <PasswordField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                value={professorData.password}
                onChange={handleProfessorChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="userId"
                label="User ID"
                value={professorData.userId}
                onChange={handleProfessorChange}
                disabled={isLoading}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="department"
                label="Department"
                value={professorData.department}
                onChange={handleProfessorChange}
                disabled={isLoading}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                disabled={isLoading}
              >
                {isLoading ? <CircularProgress size={24} /> : 'Register as Professor'}
              </Button>
            </Box>
          </TabPanel>

          <Box sx={{ textAlign: 'center', mt: 2 }}>
            <Typography variant="body2">
              Already have an account?{' '}
              <Link component={RouterLink} to="/login">
                Sign in here
              </Link>
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Register;
