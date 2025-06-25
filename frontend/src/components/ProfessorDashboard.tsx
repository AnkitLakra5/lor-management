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
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Alert,
  CircularProgress,
  IconButton,
  Tabs,
  Tab,
} from '@mui/material';
import {
  CheckCircle as ApproveIcon,
  Cancel as RejectIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
// import { useAuth } from '../contexts/AuthContext';
import { apiService } from '../services/api';
import { LorRequest } from '../types';
import PdfEditModal from './PdfEditModal';

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
      id={`professor-tabpanel-${index}`}
      aria-labelledby={`professor-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const ProfessorDashboard: React.FC = () => {
  // const { user } = useAuth(); // Currently not used
  const [tabValue, setTabValue] = useState(0);
  const [allRequests, setAllRequests] = useState<LorRequest[]>([]);
  const [pendingRequests, setPendingRequests] = useState<LorRequest[]>([]);
  const [selectedRequest, setSelectedRequest] = useState<LorRequest | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [actionType, setActionType] = useState<'approve' | 'reject'>('approve');
  const [comments, setComments] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  const [pdfEditModalOpen, setPdfEditModalOpen] = useState(false);
  const [selectedRequestForPdf, setSelectedRequestForPdf] = useState<number | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [allData, pendingData] = await Promise.all([
        apiService.getProfessorRequests(),
        apiService.getPendingRequestsForProfessor(),
      ]);
      setAllRequests(allData.requests);
      setPendingRequests(pendingData.requests);
    } catch (err: any) {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleAction = (request: LorRequest, action: 'approve' | 'reject') => {
    setSelectedRequest(request);
    setActionType(action);
    setComments('');
    setOpenDialog(true);
  };

  const handleSubmitAction = async () => {
    if (!selectedRequest) {
      setError('No request selected');
      return;
    }

    // Only require comments for rejection
    if (actionType === 'reject' && !comments.trim()) {
      setError('Please provide reason for rejection');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      
      if (actionType === 'approve') {
        await apiService.approveLorRequest(selectedRequest.id, '');
        setSuccess('Request approved successfully!');
      } else {
        await apiService.rejectLorRequest(selectedRequest.id, comments);
        setSuccess('Request rejected successfully!');
      }
      
      setOpenDialog(false);
      setSelectedRequest(null);
      setComments('');
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to process request');
    } finally {
      setSubmitting(false);
    }
  };

  const handleOpenPdfEdit = (requestId: number) => {
    setSelectedRequestForPdf(requestId);
    setPdfEditModalOpen(true);
  };

  const handleClosePdfEdit = () => {
    setPdfEditModalOpen(false);
    setSelectedRequestForPdf(null);
  };

  const handlePdfGenerated = (referenceNumber: string) => {
    setSuccess(`PDF generated successfully! Reference: ${referenceNumber}`);
    loadData(); // Refresh the data to show PDF is ready
  };

  const handleDeleteRequest = async (requestId: number, status: string) => {
    let confirmMessage = 'Are you sure you want to delete this LOR request? This action cannot be undone.';

    // Add extra warning for approved requests
    if (status === 'APPROVED') {
      confirmMessage = 'WARNING: You are about to delete an APPROVED LOR request. This will also delete any generated PDF documents and remove the approved recommendation. This action cannot be undone. Are you sure you want to proceed?';
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
      <Typography variant="h4" component="h1" gutterBottom>
        Professor Dashboard
      </Typography>

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
              {allRequests.length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Pending
            </Typography>
            <Typography variant="h4" color="warning.main">
              {allRequests.filter(r => r.status === 'PENDING').length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Approved
            </Typography>
            <Typography variant="h4" color="success.main">
              {allRequests.filter(r => r.status === 'APPROVED').length}
            </Typography>
          </CardContent>
        </Card>
        <Card sx={{ minWidth: 200, flex: 1 }}>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Rejected
            </Typography>
            <Typography variant="h4" color="error.main">
              {allRequests.filter(r => r.status === 'REJECTED').length}
            </Typography>
          </CardContent>
        </Card>
      </Box>

      <Card>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={handleTabChange}>
            <Tab label={`Pending Requests (${pendingRequests.length})`} />
            <Tab label={`All Requests (${allRequests.length})`} />
          </Tabs>
        </Box>

        <TabPanel value={tabValue} index={0}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Student</TableCell>
                  <TableCell>Course</TableCell>
                  <TableCell>Institute/Company</TableCell>
                  <TableCell>Semester</TableCell>
                  <TableCell>Session</TableCell>
                  <TableCell>Requested Date</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {pendingRequests.map((request) => (
                  <TableRow key={request.id}>
                    <TableCell>
                      <Box>
                        <Typography variant="body2" fontWeight="bold">
                          {request.studentName}
                        </Typography>
                        <Typography variant="caption" color="textSecondary">
                          Reg: {request.registrationNumber}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>{request.course}</TableCell>
                    <TableCell>{request.instituteCompany}</TableCell>
                    <TableCell>{request.semester}</TableCell>
                    <TableCell>{request.session}</TableCell>
                    <TableCell>
                      {new Date(request.requestedAt).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <Box display="flex" gap={1}>
                        <IconButton
                          onClick={() => handleAction(request, 'approve')}
                          color="success"
                          title="Approve"
                        >
                          <ApproveIcon />
                        </IconButton>
                        <IconButton
                          onClick={() => handleAction(request, 'reject')}
                          color="error"
                          title="Reject"
                        >
                          <RejectIcon />
                        </IconButton>
                        <IconButton
                          onClick={() => handleDeleteRequest(request.id, request.status)}
                          color="error"
                          title="Delete Request"
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
                {pendingRequests.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      <Typography variant="body2" color="textSecondary">
                        No pending requests
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Student</TableCell>
                  <TableCell>Course</TableCell>
                  <TableCell>Institute/Company</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Requested Date</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {allRequests.map((request) => (
                  <TableRow key={request.id}>
                    <TableCell>
                      <Box>
                        <Typography variant="body2" fontWeight="bold">
                          {request.studentName}
                        </Typography>
                        <Typography variant="caption" color="textSecondary">
                          Reg: {request.registrationNumber}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>{request.course}</TableCell>
                    <TableCell>{request.instituteCompany}</TableCell>
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
                      <Box display="flex" gap={1} alignItems="center">
                        {request.status === 'APPROVED' && !request.hasPdf && (
                          <IconButton
                            onClick={() => handleOpenPdfEdit(request.id)}
                            color="primary"
                            title="Preview & Edit LOR"
                          >
                            <EditIcon />
                          </IconButton>
                        )}
                        {request.status === 'APPROVED' && request.hasPdf && (
                          <Chip label="PDF Ready" color="success" size="small" />
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
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>
      </Card>

      {/* Action Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {actionType === 'approve' ? 'Approve' : 'Reject'} LOR Request
        </DialogTitle>
        <DialogContent>
          {selectedRequest && (
            <Box mb={2}>
              <Typography variant="body2">
                <strong>Student:</strong> {selectedRequest.studentName}
              </Typography>
              <Typography variant="body2">
                <strong>Course:</strong> {selectedRequest.course}
              </Typography>
              <Typography variant="body2">
                <strong>Institute/Company:</strong> {selectedRequest.instituteCompany}
              </Typography>
            </Box>
          )}
          {actionType === 'reject' && (
            <TextField
              margin="normal"
              fullWidth
              multiline
              rows={4}
              label="Comments"
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="Provide reason for rejection..."
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button
            onClick={handleSubmitAction}
            variant="contained"
            color={actionType === 'approve' ? 'success' : 'error'}
            disabled={submitting || (actionType === 'reject' && !comments.trim())}
          >
            {submitting ? <CircularProgress size={24} /> : actionType === 'approve' ? 'Approve' : 'Reject'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* PDF Edit Modal */}
      <PdfEditModal
        open={pdfEditModalOpen}
        onClose={handleClosePdfEdit}
        requestId={selectedRequestForPdf || 0}
        onPdfGenerated={handlePdfGenerated}
      />
    </Container>
  );
};

export default ProfessorDashboard;
