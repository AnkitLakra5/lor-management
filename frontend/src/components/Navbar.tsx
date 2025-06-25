import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Chip,
  IconButton,
  Menu,
  MenuItem,
  Button,
} from '@mui/material';
import {
  AccountCircle as AccountIcon,
  ExitToApp as LogoutIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { performLogout } from '../utils/logout';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    handleClose();
    logout();
    // Use the utility function to ensure complete logout and redirect
    performLogout();
  };

  const getRoleColor = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return 'error';
      case 'PROFESSOR':
        return 'secondary';
      case 'STUDENT':
        return 'success';
      default:
        return 'default';
    }
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          LOR Management System
        </Typography>
        
        {user ? (
          <Box display="flex" alignItems="center" gap={2}>
            <Chip
              label={user.role}
              color={getRoleColor(user.role) as any}
              size="small"
              variant="outlined"
              sx={{ color: 'white', borderColor: 'white' }}
            />

            <Box display="flex" alignItems="center">
              <Typography variant="body2" sx={{ mr: 1 }}>
                {user.name}
              </Typography>
              <IconButton
                size="large"
                aria-label="account of current user"
                aria-controls="menu-appbar"
                aria-haspopup="true"
                onClick={handleMenu}
                color="inherit"
              >
                <AccountIcon />
              </IconButton>
              <Menu
                id="menu-appbar"
                anchorEl={anchorEl}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorEl)}
                onClose={handleClose}
              >
                <MenuItem onClick={handleClose}>
                  <Box>
                    <Typography variant="body2" fontWeight="bold">
                      {user.name}
                    </Typography>
                    <Typography variant="caption" color="textSecondary">
                      {user.email}
                    </Typography>
                  </Box>
                </MenuItem>
                <MenuItem onClick={handleLogout}>
                  <LogoutIcon sx={{ mr: 1 }} />
                  Logout
                </MenuItem>
              </Menu>
            </Box>
          </Box>
        ) : (
          <Button
            color="inherit"
            onClick={() => navigate('/')}
            sx={{ fontWeight: 'bold' }}
          >
            Home
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
