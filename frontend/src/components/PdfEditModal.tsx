import React, { useState, useEffect, useCallback } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Typography,
  Box,
  CircularProgress,
  Alert,
  Divider
} from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledDialog = styled(Dialog)(({ theme }) => ({
  '& .MuiDialog-paper': {
    maxWidth: '900px',
    width: '90%',
    maxHeight: '90vh',
  },
}));

const ContentTextField = styled(TextField)(({ theme }) => ({
  '& .MuiInputBase-root': {
    minHeight: '200px',
    alignItems: 'flex-start',
  },
  '& .MuiInputBase-input': {
    minHeight: '180px !important',
  },
}));

interface LorPreviewData {
  studentName: string;
  classRollNumber: string;
  registrationNumber: string;
  examinationNumber: string;
  course: string;
  semester: string;
  session: string;
  instituteCompany: string;
  recipientTitle: string;
  recipientDepartment: string;
  recipientCompany: string;
  recipientLocation: string;
  subject: string;
  salutation: string;
  mainContent: string;
  paperCode: string;
  professorName: string;
  professorDepartment: string;
  professorDesignation: string;
  referenceNumber: string;
  currentDate: string;
}

interface PdfEditModalProps {
  open: boolean;
  onClose: () => void;
  requestId: number;
  onPdfGenerated: (referenceNumber: string) => void;
}

const PdfEditModal: React.FC<PdfEditModalProps> = ({
  open,
  onClose,
  requestId,
  onPdfGenerated
}) => {
  const [previewData, setPreviewData] = useState<LorPreviewData | null>(null);
  const [loading, setLoading] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPreviewData = useCallback(async () => {
    setLoading(true);
    setError(null);
    
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/lor-requests/${requestId}/preview`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch preview data');
      }

      const data = await response.json();
      setPreviewData(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load preview data');
    } finally {
      setLoading(false);
    }
  }, [requestId]);

  // Fetch preview data when modal opens
  useEffect(() => {
    if (open && requestId) {
      fetchPreviewData();
    }
  }, [open, requestId, fetchPreviewData]);

  const handleInputChange = (field: keyof LorPreviewData, value: string) => {
    if (previewData) {
      setPreviewData({
        ...previewData,
        [field]: value
      });
    }
  };

  const handleGeneratePdf = async () => {
    if (!previewData) return;

    setGenerating(true);
    setError(null);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/lor-requests/${requestId}/generate-pdf`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(previewData),
      });

      if (!response.ok) {
        throw new Error('Failed to generate PDF');
      }

      const result = await response.json();
      onPdfGenerated(result.referenceNumber);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to generate PDF');
    } finally {
      setGenerating(false);
    }
  };

  const handleClose = () => {
    setPreviewData(null);
    setError(null);
    onClose();
  };

  return (
    <StyledDialog open={open} onClose={handleClose} maxWidth="lg">
      <DialogTitle>
        <Typography variant="h5" component="div">
          Preview & Edit LOR Content
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Review and modify the LOR content before generating the final PDF
        </Typography>
      </DialogTitle>

      <DialogContent dividers>
        {loading && (
          <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
            <CircularProgress />
          </Box>
        )}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {previewData && (
          <Box>
            {/* Student Information Section */}
            <Typography variant="h6" gutterBottom color="primary">
              Student Information
            </Typography>
            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Student Name"
                    value={previewData.studentName}
                    onChange={(e) => handleInputChange('studentName', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Class Roll Number"
                    value={previewData.classRollNumber}
                    onChange={(e) => handleInputChange('classRollNumber', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Registration Number"
                    value={previewData.registrationNumber}
                    onChange={(e) => handleInputChange('registrationNumber', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Examination Number"
                    value={previewData.examinationNumber}
                    onChange={(e) => handleInputChange('examinationNumber', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Course"
                    value={previewData.course}
                    onChange={(e) => handleInputChange('course', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Semester"
                    value={previewData.semester}
                    onChange={(e) => handleInputChange('semester', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Session"
                    value={previewData.session}
                    onChange={(e) => handleInputChange('session', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Institute/Company"
                    value={previewData.instituteCompany}
                    onChange={(e) => handleInputChange('instituteCompany', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
            </Box>

            <Divider sx={{ my: 2 }} />

            {/* Recipient Information Section */}
            <Typography variant="h6" gutterBottom color="primary">
              Recipient Information
            </Typography>
            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Recipient Title"
                    value={previewData.recipientTitle}
                    onChange={(e) => handleInputChange('recipientTitle', e.target.value)}
                    variant="outlined"
                    size="small"
                    helperText="e.g., The General Manager, The HR Director"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Recipient Department"
                    value={previewData.recipientDepartment}
                    onChange={(e) => handleInputChange('recipientDepartment', e.target.value)}
                    variant="outlined"
                    size="small"
                    helperText="e.g., Human Resource Department"
                  />
                </Box>
              </Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Recipient Company"
                    value={previewData.recipientCompany}
                    onChange={(e) => handleInputChange('recipientCompany', e.target.value)}
                    variant="outlined"
                    size="small"
                    helperText="Company/Organization name"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Recipient Location"
                    value={previewData.recipientLocation}
                    onChange={(e) => handleInputChange('recipientLocation', e.target.value)}
                    variant="outlined"
                    size="small"
                    helperText="e.g., Ranchi, Mumbai, Delhi"
                  />
                </Box>
              </Box>
            </Box>

            <Divider sx={{ my: 2 }} />

            {/* Letter Content Section */}
            <Typography variant="h6" gutterBottom color="primary">
              Letter Content
            </Typography>
            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Subject"
                    value={previewData.subject}
                    onChange={(e) => handleInputChange('subject', e.target.value)}
                    variant="outlined"
                    size="small"
                    helperText="Letter subject line"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Salutation"
                    value={previewData.salutation}
                    onChange={(e) => handleInputChange('salutation', e.target.value)}
                    variant="outlined"
                    size="small"
                    helperText="e.g., Dear Sir / Madam, Dear Mr. Smith"
                  />
                </Box>
              </Box>
              <Box sx={{ mb: 2 }}>
                <ContentTextField
                  fullWidth
                  label="Main Content"
                  value={previewData.mainContent}
                  onChange={(e) => handleInputChange('mainContent', e.target.value)}
                  variant="outlined"
                  multiline
                  rows={8}
                  helperText="Edit the main content of the Letter of Recommendation"
                />
              </Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Paper Code"
                    value={previewData.paperCode}
                    onChange={(e) => handleInputChange('paperCode', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
            </Box>

            <Divider sx={{ my: 2 }} />

            {/* Professor Information Section */}
            <Typography variant="h6" gutterBottom color="primary">
              Professor Information
            </Typography>
            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Professor Name"
                    value={previewData.professorName}
                    onChange={(e) => handleInputChange('professorName', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Department"
                    value={previewData.professorDepartment}
                    onChange={(e) => handleInputChange('professorDepartment', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Designation"
                    value={previewData.professorDesignation}
                    onChange={(e) => handleInputChange('professorDesignation', e.target.value)}
                    variant="outlined"
                    size="small"
                  />
                </Box>
              </Box>
            </Box>

            <Divider sx={{ my: 2 }} />

            {/* Reference Information Section */}
            <Typography variant="h6" gutterBottom color="primary">
              Reference Information
            </Typography>
            <Box>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Reference Number"
                    value={previewData.referenceNumber}
                    variant="outlined"
                    size="small"
                    disabled
                    helperText="Auto-generated reference number"
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px', minWidth: '250px' }}>
                  <TextField
                    fullWidth
                    label="Current Date"
                    value={previewData.currentDate}
                    variant="outlined"
                    size="small"
                    disabled
                    helperText="Current date for the letter"
                  />
                </Box>
              </Box>
            </Box>
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button onClick={handleClose} disabled={generating}>
          Cancel
        </Button>
        <Button
          onClick={handleGeneratePdf}
          variant="contained"
          disabled={!previewData || generating}
          startIcon={generating ? <CircularProgress size={20} /> : null}
        >
          {generating ? 'Generating PDF...' : 'Generate PDF'}
        </Button>
      </DialogActions>
    </StyledDialog>
  );
};

export default PdfEditModal;
