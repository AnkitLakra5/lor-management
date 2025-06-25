import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Alert,
  CircularProgress,
  MenuItem,
} from '@mui/material';
import { AdminProfessor } from '../types';

interface AddProfessorModalProps {
  open: boolean;
  onClose: () => void;
  onProfessorAdded: (professor: AdminProfessor) => void;
  onAddProfessor: (professor: Omit<AdminProfessor, 'id' | 'createdAt' | 'updatedAt'>) => Promise<AdminProfessor>;
}

const departments = [
  'Computer Science',
  'Electronics and Communication',
  'Mathematics',
  'Physics',
  'Chemistry',
  'English',
  'Commerce',
  'Management',
];

const AddProfessorModal: React.FC<AddProfessorModalProps> = ({
  open,
  onClose,
  onProfessorAdded,
  onAddProfessor,
}) => {
  const [formData, setFormData] = useState({
    name: '',
    userId: '',
    department: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
    setError(''); // Clear error when user starts typing
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (!formData.name.trim()) {
      setError('Name is required');
      return;
    }
    if (!formData.userId.trim()) {
      setError('User ID is required');
      return;
    }
    if (!formData.department.trim()) {
      setError('Department is required');
      return;
    }

    try {
      setLoading(true);
      setError('');
      
      const newProfessor = await onAddProfessor(formData);
      onProfessorAdded(newProfessor);
      
      // Reset form
      setFormData({
        name: '',
        userId: '',
        department: '',
      });
      
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add professor');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    if (!loading) {
      setFormData({
        name: '',
        userId: '',
        department: '',
      });
      setError('');
      onClose();
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Add New Professor</DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Professor Name"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="Full name of the professor"
            />
            
            <TextField
              label="User ID"
              value={formData.userId}
              onChange={(e) => handleInputChange('userId', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="e.g., PROF001, PROF026"
            />
            
            <TextField
              select
              label="Department"
              value={formData.department}
              onChange={(e) => handleInputChange('department', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="Select the department"
            >
              {departments.map((dept) => (
                <MenuItem key={dept} value={dept}>
                  {dept}
                </MenuItem>
              ))}
            </TextField>
          </Box>
        </DialogContent>
        
        <DialogActions>
          <Button onClick={handleClose} disabled={loading}>
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="contained" 
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : null}
          >
            {loading ? 'Adding...' : 'Add Professor'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default AddProfessorModal;
