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
} from '@mui/material';
import { AdminStudent } from '../types';

interface AddStudentModalProps {
  open: boolean;
  onClose: () => void;
  onStudentAdded: (student: AdminStudent) => void;
  onAddStudent: (student: Omit<AdminStudent, 'id' | 'createdAt' | 'updatedAt'>) => Promise<AdminStudent>;
}

const AddStudentModal: React.FC<AddStudentModalProps> = ({
  open,
  onClose,
  onStudentAdded,
  onAddStudent,
}) => {
  const [formData, setFormData] = useState({
    name: '',
    registrationNumber: '',
    examinationNumber: '',
    course: '',
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
    if (!formData.registrationNumber.trim()) {
      setError('Registration number is required');
      return;
    }
    if (!formData.examinationNumber.trim()) {
      setError('Examination number is required');
      return;
    }
    if (!formData.course.trim()) {
      setError('Course is required');
      return;
    }

    try {
      setLoading(true);
      setError('');
      
      const newStudent = await onAddStudent(formData);
      onStudentAdded(newStudent);
      
      // Reset form
      setFormData({
        name: '',
        registrationNumber: '',
        examinationNumber: '',
        course: '',
      });
      
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add student');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    if (!loading) {
      setFormData({
        name: '',
        registrationNumber: '',
        examinationNumber: '',
        course: '',
      });
      setError('');
      onClose();
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Add New Student</DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Student Name"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="Full name of the student"
            />
            
            <TextField
              label="Registration Number"
              value={formData.registrationNumber}
              onChange={(e) => handleInputChange('registrationNumber', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="e.g., 22SXC051718"
            />
            
            <TextField
              label="Examination Number"
              value={formData.examinationNumber}
              onChange={(e) => handleInputChange('examinationNumber', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="e.g., 22VBCA051718"
            />
            
            <TextField
              label="Course"
              value={formData.course}
              onChange={(e) => handleInputChange('course', e.target.value)}
              fullWidth
              required
              disabled={loading}
              helperText="e.g., Computer Science, BCA"
            />
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
            {loading ? 'Adding...' : 'Add Student'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default AddStudentModal;
