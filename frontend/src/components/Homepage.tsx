import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  CardActions,
  Button,


  Avatar,
} from '@mui/material';
import {
  School as StudentIcon,
  Person as ProfessorIcon,
  AdminPanelSettings as AdminIcon,
} from '@mui/icons-material';

const Homepage: React.FC = () => {
  const navigate = useNavigate();

  const handleSignIn = (userType: string) => {
    switch (userType) {
      case 'student':
        navigate('/student-login');
        break;
      case 'professor':
        navigate('/professor-login');
        break;
      case 'admin':
        navigate('/admin-login');
        break;
      default:
        navigate('/login');
    }
  };

  const userTypes = [
    {
      type: 'student',
      title: 'Student Portal',
      description: 'Access your LOR requests, track status, and download approved letters.',
      icon: <StudentIcon sx={{ fontSize: 60 }} />,
      color: '#1976d2'
    },
    {
      type: 'professor',
      title: 'Professor Portal',
      description: 'Review student requests, approve/reject applications, and upload letters.',
      icon: <ProfessorIcon sx={{ fontSize: 60 }} />,
      color: '#388e3c'
    },
    {
      type: 'admin',
      title: 'Admin Portal',
      description: 'Manage users, oversee all requests, and maintain system settings.',
      icon: <AdminIcon sx={{ fontSize: 60 }} />,
      color: '#f57c00'
    }
  ];

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        py: 4,
      }}
    >
      <Container maxWidth="lg">
        {/* Header Section */}
        <Box textAlign="center" mb={6}>
          <Typography
            variant="h2"
            component="h1"
            sx={{
              color: 'white',
              fontWeight: 'bold',
              mb: 2,
              textShadow: '2px 2px 4px rgba(0,0,0,0.3)',
            }}
          >
            Letter of Recommendation
          </Typography>
          <Typography
            variant="h4"
            component="h2"
            sx={{
              color: 'white',
              fontWeight: 300,
              mb: 4,
              textShadow: '1px 1px 2px rgba(0,0,0,0.3)',
            }}
          >
            Management System
          </Typography>

        </Box>

        {/* Sign In Cards */}
        <Box
          display="flex"
          gap={4}
          justifyContent="center"
          flexWrap="wrap"
          sx={{ mb: 4 }}
        >
          {userTypes.map((userType) => (
            <Box key={userType.type} sx={{ flex: '1 1 300px', maxWidth: 400 }}>
              <Card
                sx={{
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  transition: 'all 0.3s ease-in-out',
                  '&:hover': {
                    transform: 'translateY(-8px)',
                    boxShadow: '0 12px 24px rgba(0,0,0,0.15)',
                  },
                }}
              >
                <CardContent sx={{ flexGrow: 1, textAlign: 'center', p: 3 }}>
                  <Avatar
                    sx={{
                      bgcolor: userType.color,
                      width: 80,
                      height: 80,
                      mx: 'auto',
                      mb: 2,
                    }}
                  >
                    {userType.icon}
                  </Avatar>
                  
                  <Typography
                    variant="h5"
                    component="h3"
                    gutterBottom
                    sx={{ fontWeight: 'bold', color: userType.color }}
                  >
                    {userType.title}
                  </Typography>
                  
                  <Typography
                    variant="body1"
                    color="text.secondary"
                    sx={{ mb: 3, lineHeight: 1.6 }}
                  >
                    {userType.description}
                  </Typography>
                </CardContent>
                
                <CardActions sx={{ p: 3, pt: 0 }}>
                  <Button
                    variant="contained"
                    fullWidth
                    size="large"
                    onClick={() => handleSignIn(userType.type)}
                    sx={{
                      bgcolor: userType.color,
                      py: 1.5,
                      fontSize: '1.1rem',
                      fontWeight: 'bold',
                      '&:hover': {
                        bgcolor: userType.color,
                        filter: 'brightness(0.9)',
                      },
                    }}
                  >
                    Sign In as {userType.title.split(' ')[0]}
                  </Button>
                </CardActions>
              </Card>
            </Box>
          ))}
        </Box>

        {/* Footer Section */}
        <Box textAlign="center" mt={6}>
          <Typography
            variant="body2"
            sx={{
              color: 'rgba(255,255,255,0.8)',
              mb: 2,
            }}
          >
            Don't have an account?
          </Typography>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate('/register')}
            sx={{
              color: 'white',
              borderColor: 'white',
              px: 4,
              py: 1,
              '&:hover': {
                borderColor: 'white',
                bgcolor: 'rgba(255,255,255,0.1)',
              },
            }}
          >
            Register Here
          </Button>
        </Box>
      </Container>
    </Box>
  );
};

export default Homepage;
