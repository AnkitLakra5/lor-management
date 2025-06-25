import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Alert,
  LinearProgress,
  List,
  ListItem,
  ListItemText,
  Divider,
  Paper,
  Chip,
  IconButton
} from '@mui/material';
import {
  CloudUpload as UploadIcon,
  Download as DownloadIcon,
  Close as CloseIcon,
  CheckCircle as SuccessIcon,
  Error as ErrorIcon
} from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { apiService } from '../services/api';

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

const DropZone = styled(Paper)(({ theme }) => ({
  border: `2px dashed ${theme.palette.primary.main}`,
  borderRadius: theme.shape.borderRadius,
  padding: theme.spacing(4),
  textAlign: 'center',
  cursor: 'pointer',
  transition: 'all 0.3s ease',
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
    borderColor: theme.palette.primary.dark,
  },
  '&.dragover': {
    backgroundColor: theme.palette.primary.light,
    borderColor: theme.palette.primary.dark,
  }
}));

interface BulkImportProfessorsModalProps {
  open: boolean;
  onClose: () => void;
  onImportComplete: () => void;
}

interface ImportResult {
  success: boolean;
  totalRows: number;
  successCount: number;
  errorCount: number;
  errors: string[];
  importedProfessors: any[];
  message: string;
}

const BulkImportProfessorsModal: React.FC<BulkImportProfessorsModalProps> = ({
  open,
  onClose,
  onImportComplete
}) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [importing, setImporting] = useState(false);
  const [importResult, setImportResult] = useState<ImportResult | null>(null);
  const [dragOver, setDragOver] = useState(false);

  const handleClose = () => {
    setSelectedFile(null);
    setImportResult(null);
    onClose();
  };

  const handleDownloadTemplate = async () => {
    try {
      const blob = await apiService.downloadProfessorTemplate();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'professor_template.csv';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Failed to download template:', error);
    }
  };

  const handleFileSelect = (file: File) => {
    if (file.type === 'text/csv' || file.name.endsWith('.csv')) {
      setSelectedFile(file);
      setImportResult(null);
    } else {
      alert('Please select a CSV file');
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleDragOver = (event: React.DragEvent) => {
    event.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = () => {
    setDragOver(false);
  };

  const handleDrop = (event: React.DragEvent) => {
    event.preventDefault();
    setDragOver(false);
    const file = event.dataTransfer.files[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleImport = async () => {
    if (!selectedFile) return;

    setImporting(true);
    try {
      const result = await apiService.bulkImportProfessors(selectedFile);
      setImportResult(result);
      if (result.success) {
        onImportComplete();
      }
    } catch (error: any) {
      setImportResult({
        success: false,
        totalRows: 0,
        successCount: 0,
        errorCount: 1,
        errors: [error.response?.data?.message || 'Failed to import professors'],
        importedProfessors: [],
        message: 'Import failed'
      });
    } finally {
      setImporting(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Bulk Import Professors</Typography>
          <IconButton onClick={handleClose}>
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <DialogContent>
        {!importResult ? (
          <>
            {/* Instructions */}
            <Alert severity="info" sx={{ mb: 3 }}>
              <Typography variant="body2">
                <strong>Instructions:</strong>
                <br />
                1. Download the CSV template below
                <br />
                2. Fill in your professor data (remove example rows and instruction lines)
                <br />
                3. Upload the completed CSV file
                <br />
                4. Review the import results
              </Typography>
            </Alert>

            {/* Download Template */}
            <Box sx={{ mb: 3 }}>
              <Button
                variant="outlined"
                startIcon={<DownloadIcon />}
                onClick={handleDownloadTemplate}
                fullWidth
              >
                Download CSV Template
              </Button>
            </Box>

            {/* File Upload */}
            <DropZone
              className={dragOver ? 'dragover' : ''}
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
              onClick={() => document.getElementById('professor-file-input')?.click()}
            >
              <UploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
              <Typography variant="h6" gutterBottom>
                {selectedFile ? selectedFile.name : 'Drop CSV file here or click to browse'}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Supported format: CSV files only
              </Typography>
              <VisuallyHiddenInput
                id="professor-file-input"
                type="file"
                accept=".csv"
                onChange={handleFileChange}
              />
            </DropZone>

            {selectedFile && (
              <Box sx={{ mt: 2 }}>
                <Chip
                  label={`Selected: ${selectedFile.name} (${(selectedFile.size / 1024).toFixed(1)} KB)`}
                  color="primary"
                  variant="outlined"
                />
              </Box>
            )}

            {importing && (
              <Box sx={{ mt: 3 }}>
                <Typography variant="body2" gutterBottom>
                  Importing professors...
                </Typography>
                <LinearProgress />
              </Box>
            )}
          </>
        ) : (
          /* Import Results */
          <Box>
            <Alert 
              severity={importResult.success ? "success" : "warning"} 
              icon={importResult.success ? <SuccessIcon /> : <ErrorIcon />}
              sx={{ mb: 3 }}
            >
              <Typography variant="body1" fontWeight="bold">
                {importResult.message}
              </Typography>
            </Alert>

            {/* Summary */}
            <Box sx={{ mb: 3 }}>
              <Typography variant="h6" gutterBottom>Import Summary</Typography>
              <Box display="flex" gap={2} flexWrap="wrap">
                <Chip label={`Total Rows: ${importResult.totalRows}`} />
                <Chip label={`Successful: ${importResult.successCount}`} color="success" />
                {importResult.errorCount > 0 && (
                  <Chip label={`Errors: ${importResult.errorCount}`} color="error" />
                )}
              </Box>
            </Box>

            {/* Errors */}
            {importResult.errors.length > 0 && (
              <Box>
                <Typography variant="h6" gutterBottom color="error">
                  Errors ({importResult.errors.length})
                </Typography>
                <Paper variant="outlined" sx={{ maxHeight: 200, overflow: 'auto' }}>
                  <List dense>
                    {importResult.errors.map((error, index) => (
                      <React.Fragment key={index}>
                        <ListItem>
                          <ListItemText 
                            primary={error}
                            primaryTypographyProps={{ variant: 'body2', color: 'error' }}
                          />
                        </ListItem>
                        {index < importResult.errors.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </List>
                </Paper>
              </Box>
            )}
          </Box>
        )}
      </DialogContent>

      <DialogActions>
        {!importResult ? (
          <>
            <Button onClick={handleClose}>Cancel</Button>
            <Button
              variant="contained"
              onClick={handleImport}
              disabled={!selectedFile || importing}
              startIcon={<UploadIcon />}
            >
              {importing ? 'Importing...' : 'Import Professors'}
            </Button>
          </>
        ) : (
          <Button variant="contained" onClick={handleClose}>
            Close
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default BulkImportProfessorsModal;
