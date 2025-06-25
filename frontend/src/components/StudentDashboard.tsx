import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Button,

  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Alert,
  CircularProgress,
  IconButton,
} from '@mui/material';
import {
  Add as AddIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
// import { useAuth } from '../contexts/AuthContext';
import { apiService } from '../services/api';
import { LorRequest, CreateLorRequest, User } from '../types';

const StudentDashboard: React.FC = () => {
  // const { user } = useAuth(); // Currently not used
  const [requests, setRequests] = useState<LorRequest[]>([]);
  const [professors, setProfessors] = useState<User[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  const [newRequest, setNewRequest] = useState<CreateLorRequest>({
    professorId: 0,
    semester: '',
    session: '',
    classRollNumber: '',
    instituteCompany: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [requestsData, professorsData] = await Promise.all([
        apiService.getStudentRequests(),
        apiService.getActiveProfessors(),
      ]);
      setRequests(requestsData.requests || []);
      setProfessors(professorsData.professors || []);
    } catch (err: any) {
      setError('Failed to load data');
      // Ensure arrays are set even on error
      setRequests([]);
      setProfessors([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRequest = async () => {
    if (!newRequest.professorId || !newRequest.semester || !newRequest.session || !newRequest.classRollNumber || !newRequest.instituteCompany) {
      setError('Please fill in all fields');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      await apiService.createLorRequest(newRequest);
      setSuccess('LOR request created successfully!');
      setOpenDialog(false);
      setNewRequest({
        professorId: 0,
        semester: '',
        session: '',
        classRollNumber: '',
        instituteCompany: '',
      });
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create request');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDownloadPdf = async (referenceNumber: string, fileName: string) => {
    try {
      const blob = await apiService.downloadPdf(referenceNumber);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err: any) {
      setError('Failed to download PDF');
    }
  };

  const handleDeleteRequest = async (requestId: number, status: string) => {
    let confirmMessage = 'Are you sure you want to delete this LOR request? This action cannot be undone.';

    // Add extra warning for approved requests
    if (status === 'APPROVED') {
      confirmMessage = 'WARNING: You are about to delete an APPROVED LOR request. This will also delete any generated PDF documents. This action cannot be undone. Are you sure you want to proceed?';
    }

    if (!window.confirm(confirmMessage)) {
      return;
    }

    try {
      await apiService.deleteLorRequest(requestId);
      setSuccess('LOR request deleted successfully!');
      loadData(); // Reload the data to reflect the deletion
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete request');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'APPROVED':
        return 'success';
      case 'REJECTED':
        return 'error';
      default:
        return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Student Dashboard
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenDialog(true)}
        >
          New LOR Request
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
          {success}
        </Alert>
      )}

      <Box display="flex" gap={2} mb={3} flexWrap="wrap">
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Total Requests
            </Typography>
            <Typography variant="h4">
              {requests.length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Pending
            </Typography>
            <Typography variant="h4" color="warning.main">
              {requests.filter(r => r.status === 'PENDING').length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Approved
            </Typography>
            <Typography variant="h4" color="success.main">
              {requests.filter(r => r.status === 'APPROVED').length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Rejected
            </Typography>
            <Typography variant="h4" color="error.main">
              {requests.filter(r => r.status === 'REJECTED').length}
            </Typography>
          </CardContent>
        </Card>
      </Box>

      {/* Requests Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            My LOR Requests
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Professor</TableCell>
                  <TableCell>Institute/Company</TableCell>
                  <TableCell>Semester</TableCell>
                  <TableCell>Session</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Requested Date</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {requests.map((request) => (
                  <TableRow key={request.id}>
                    <TableCell>
                      <Box>
                        <Typography variant="body2" fontWeight="bold">
                          {request.professorName}
                        </Typography>
                        <Typography variant="caption" color="textSecondary">
                          {request.professorDepartment}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>{request.instituteCompany}</TableCell>
                    <TableCell>{request.semester}</TableCell>
                    <TableCell>{request.session}</TableCell>
                    <TableCell>
                      <Chip
                        label={request.status}
                        color={getStatusColor(request.status) as any}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {new Date(request.requestedAt).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <Box display="flex" gap={1}>
                        {request.status === 'APPROVED' && request.hasPdf && (
                          <IconButton
                            onClick={() => handleDownloadPdf(request.pdfReferenceNumber!, request.pdfFileName!)}
                            color="primary"
                            title="Download PDF"
                          >
                            <DownloadIcon />
                          </IconButton>
                        )}
                        <IconButton
                          onClick={() => handleDeleteRequest(request.id, request.status)}
                          color="error"
                          title={`Delete ${request.status === 'APPROVED' ? 'Approved ' : ''}Request`}
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
                {requests.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      <Typography variant="body2" color="textSecondary">
                        No LOR requests found. Create your first request!
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Create Request Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create New LOR Request</DialogTitle>
        <DialogContent>
          {loading ? (
            <Box display="flex" justifyContent="center" alignItems="center" py={4}>
              <CircularProgress />
            </Box>
          ) : (
            <>

          <FormControl fullWidth margin="normal" variant="outlined">
            <InputLabel id="professor-select-label">Professor</InputLabel>
            <Select
              labelId="professor-select-label"
              id="professor-select"
              value={newRequest.professorId}
              label="Professor"
              onChange={(e) => setNewRequest({ ...newRequest, professorId: Number(e.target.value) })}
            >
              {professors && professors.length > 0 ? (
                professors.map((prof) => (
                  <MenuItem key={prof.id} value={prof.id}>
                    {prof.name} - {prof.department}
                  </MenuItem>
                ))
              ) : (
                <MenuItem disabled>
                  No professors available
                </MenuItem>
              )}
            </Select>
          </FormControl>
          <TextField
            margin="normal"
            fullWidth
            label="Semester"
            value={newRequest.semester}
            onChange={(e) => setNewRequest({ ...newRequest, semester: e.target.value })}
          />
          <TextField
            margin="normal"
            fullWidth
            label="Session"
            value={newRequest.session}
            onChange={(e) => setNewRequest({ ...newRequest, session: e.target.value })}
          />
          <TextField
            margin="normal"
            fullWidth
            label="Class Roll Number"
            value={newRequest.classRollNumber}
            onChange={(e) => setNewRequest({ ...newRequest, classRollNumber: e.target.value })}
            placeholder="Enter your class roll number"
          />
          <TextField
            margin="normal"
            fullWidth
            label="Institute/Company"
            value={newRequest.instituteCompany}
            onChange={(e) => setNewRequest({ ...newRequest, instituteCompany: e.target.value })}
          />
          </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateRequest} variant="contained" disabled={submitting}>
            {submitting ? <CircularProgress size={24} /> : 'Create Request'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default StudentDashboard;
