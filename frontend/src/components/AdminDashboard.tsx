import React, { useState, useEffect, useCallback } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Alert,
  CircularProgress,
  Chip,
  Switch,
  FormControlLabel,
  Tabs,
  Tab,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  People as PeopleIcon,
  Assignment as AssignmentIcon,
  PictureAsPdf as PdfIcon,
  Add as AddIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
  CloudUpload as UploadIcon,
} from '@mui/icons-material';
// import { useAuth } from '../contexts/AuthContext';
import { apiService } from '../services/api';
import { DashboardStats, User, AdminStudent, AdminProfessor } from '../types';
import AddStudentModal from './AddStudentModal';
import AddProfessorModal from './AddProfessorModal';
import BulkImportStudentsModal from './BulkImportStudentsModal';
import BulkImportProfessorsModal from './BulkImportProfessorsModal';
import SearchBar from './SearchBar';

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
      id={`admin-tabpanel-${index}`}
      aria-labelledby={`admin-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const AdminDashboard: React.FC = () => {
  // const { user } = useAuth(); // Currently not used
  const [tabValue, setTabValue] = useState(0);
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [users, setUsers] = useState<User[]>([]);
  const [adminStudents, setAdminStudents] = useState<AdminStudent[]>([]);
  const [adminProfessors, setAdminProfessors] = useState<AdminProfessor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  // Modal states
  const [addStudentModalOpen, setAddStudentModalOpen] = useState(false);
  const [addProfessorModalOpen, setAddProfessorModalOpen] = useState(false);
  const [bulkImportStudentsModalOpen, setBulkImportStudentsModalOpen] = useState(false);
  const [bulkImportProfessorsModalOpen, setBulkImportProfessorsModalOpen] = useState(false);

  // Search state
  const [studentSearchTerm, setStudentSearchTerm] = useState('');
  const [studentCourseFilter, setStudentCourseFilter] = useState('all');
  const [professorSearchTerm, setProfessorSearchTerm] = useState('');
  const [professorDepartmentFilter, setProfessorDepartmentFilter] = useState('all');
  const [courses, setCourses] = useState<string[]>([]);
  const [searchResults, setSearchResults] = useState<{
    students: any[];
    professors: any[];
    studentCount: number;
    professorCount: number;
    studentTotalCount: number;
    professorTotalCount: number;
  }>({
    students: [],
    professors: [],
    studentCount: 0,
    professorCount: 0,
    studentTotalCount: 0,
    professorTotalCount: 0
  });

  useEffect(() => {
    loadData();
    loadCourses();
  }, []);

  // Search effects
  useEffect(() => {
    searchStudents();
  }, [studentSearchTerm, studentCourseFilter]);

  useEffect(() => {
    searchProfessors();
  }, [professorSearchTerm, professorDepartmentFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      const [statsData, usersData, studentsData, professorsData] = await Promise.all([
        apiService.getDashboardStats(),
        apiService.getAllUsers(),
        apiService.getAllAdminStudents(),
        apiService.getAllAdminProfessors()
      ]);
      setStats(statsData);
      setUsers(usersData.users);
      setAdminStudents(studentsData.students);
      setAdminProfessors(professorsData.professors);
    } catch (err: any) {
      setError('Failed to load dashboard data');
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleAddStudent = async (studentData: Omit<AdminStudent, 'id' | 'createdAt' | 'updatedAt'>) => {
    return await apiService.addAdminStudent(studentData);
  };

  const handleStudentAdded = (newStudent: AdminStudent) => {
    setAdminStudents(prev => [...prev, newStudent]);
    setSuccess('Student added successfully!');
    setTimeout(() => setSuccess(''), 3000);
  };

  const handleAddProfessor = async (professorData: Omit<AdminProfessor, 'id' | 'createdAt' | 'updatedAt'>) => {
    return await apiService.addAdminProfessor(professorData);
  };

  const handleProfessorAdded = (newProfessor: AdminProfessor) => {
    setAdminProfessors(prev => [...prev, newProfessor]);
    setSuccess('Professor added successfully!');
    setTimeout(() => setSuccess(''), 3000);
  };

  const handleDeleteStudent = async (id: number) => {
    try {
      await apiService.deleteAdminStudent(id);
      setAdminStudents(prev => prev.filter(student => student.id !== id));
      setSuccess('Student deleted successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError('Failed to delete student');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleDeleteProfessor = async (id: number) => {
    try {
      await apiService.deleteAdminProfessor(id);
      setAdminProfessors(prev => prev.filter(professor => professor.id !== id));
      setSuccess('Professor deleted successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError('Failed to delete professor');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleToggleUserStatus = async (userId: number) => {
    try {
      setError('');
      const updatedUser = await apiService.toggleUserStatus(userId);
      setUsers(users.map(u => u.id === userId ? updatedUser : u));
      setSuccess(`User status updated successfully`);
    } catch (err: any) {
      setError('Failed to update user status');
    }
  };

  // Bulk import handlers
  const handleBulkImportStudents = () => {
    setBulkImportStudentsModalOpen(true);
  };

  const handleBulkImportProfessors = () => {
    setBulkImportProfessorsModalOpen(true);
  };

  const handleStudentBulkImportComplete = () => {
    loadData();
    setSuccess('Students imported successfully');
    setTimeout(() => setSuccess(''), 3000);
  };

  const handleProfessorBulkImportComplete = () => {
    loadData();
    setSuccess('Professors imported successfully');
    setTimeout(() => setSuccess(''), 3000);
  };

  // Search handlers
  const loadCourses = async () => {
    try {
      const response = await apiService.getAllCourses();
      setCourses(response.courses);
    } catch (error) {
      console.error('Failed to load courses:', error);
    }
  };

  const searchStudents = async () => {
    try {
      const result = await apiService.searchAdminStudents(
        studentSearchTerm || undefined,
        studentCourseFilter !== 'all' ? studentCourseFilter : undefined,
        0,
        100 // Load more results for better UX
      );
      setSearchResults(prev => ({
        ...prev,
        students: result.students,
        studentCount: result.students.length,
        studentTotalCount: result.totalCount
      }));
    } catch (error) {
      console.error('Failed to search students:', error);
    }
  };

  const searchProfessors = async () => {
    try {
      const result = await apiService.searchAdminProfessors(
        professorSearchTerm || undefined,
        professorDepartmentFilter !== 'all' ? professorDepartmentFilter : undefined,
        0,
        100 // Load more results for better UX
      );
      setSearchResults(prev => ({
        ...prev,
        professors: result.professors,
        professorCount: result.professors.length,
        professorTotalCount: result.totalCount
      }));
    } catch (error) {
      console.error('Failed to search professors:', error);
    }
  };

  const handleStudentSearchChange = (term: string) => {
    setStudentSearchTerm(term);
  };

  const handleStudentCourseFilterChange = (course: string) => {
    setStudentCourseFilter(course);
  };

  const handleProfessorSearchChange = (term: string) => {
    setProfessorSearchTerm(term);
  };

  const handleProfessorDepartmentFilterChange = (department: string) => {
    setProfessorDepartmentFilter(department);
  };

  const getRoleColor = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return 'error';
      case 'PROFESSOR':
        return 'primary';
      case 'STUDENT':
        return 'success';
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
        Admin Dashboard
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

      {stats && (
        <>
          {/* User Statistics */}
          <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
            User Statistics
          </Typography>
          <Box display="flex" gap={2} mb={3} flexWrap="wrap">
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <PeopleIcon color="error" sx={{ mr: 1 }} />
                  <Box>
                    <Typography color="textSecondary" variant="body2">
                      Admins
                    </Typography>
                    <Typography variant="h6">
                      {stats.users.totalAdmins}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <PeopleIcon color="primary" sx={{ mr: 1 }} />
                  <Box>
                    <Typography color="textSecondary" variant="body2">
                      Professors
                    </Typography>
                    <Typography variant="h6">
                      {stats.users.totalProfessors}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <PeopleIcon color="success" sx={{ mr: 1 }} />
                  <Box>
                    <Typography color="textSecondary" variant="body2">
                      Students
                    </Typography>
                    <Typography variant="h6">
                      {stats.users.totalStudents}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <AssignmentIcon color="warning" sx={{ mr: 1 }} />
                  <Box>
                    <Typography color="textSecondary" variant="body2">
                      Total Requests
                    </Typography>
                    <Typography variant="h6">
                      {stats.requests.totalRequests}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <PdfIcon color="info" sx={{ mr: 1 }} />
                  <Box>
                    <Typography color="textSecondary" variant="body2">
                      PDFs Generated
                    </Typography>
                    <Typography variant="h6">
                      {stats.pdfs.totalPdfs}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>

          {/* Request Statistics */}
          <Typography variant="h6" gutterBottom>
            Request Statistics
          </Typography>
          <Box display="flex" gap={2} mb={3} flexWrap="wrap">
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Pending
                </Typography>
                <Typography variant="h4" color="warning.main">
                  {stats.requests.pendingRequests}
                </Typography>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Approved
                </Typography>
                <Typography variant="h4" color="success.main">
                  {stats.requests.approvedRequests}
                </Typography>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Rejected
                </Typography>
                <Typography variant="h4" color="error.main">
                  {stats.requests.rejectedRequests}
                </Typography>
              </CardContent>
            </Card>
            <Card sx={{ minWidth: 200, flex: 1 }}>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Total File Size
                </Typography>
                <Typography variant="h6">
                  {(stats.pdfs.totalFileSize / 1024).toFixed(1)} KB
                </Typography>
              </CardContent>
            </Card>
          </Box>
        </>
      )}

      {/* Management Tabs */}
      <Card>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={handleTabChange}>
            <Tab label="User Management" />
            <Tab label="Student Management" />
            <Tab label="Professor Management" />
          </Tabs>
        </Box>

        <TabPanel value={tabValue} index={0}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Role</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Registration Details</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold">
                        {user.name}
                      </Typography>
                    </TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>
                      <Chip
                        label={user.role}
                        color={getRoleColor(user.role) as any}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={user.isActive ? 'Active' : 'Inactive'}
                        color={user.isActive ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {user.role === 'STUDENT' && (
                        <Box>
                          <Typography variant="caption" display="block">
                            Reg: {user.registrationNumber}
                          </Typography>
                          <Typography variant="caption" display="block">
                            Exam: {user.examinationNumber}
                          </Typography>
                          <Typography variant="caption" display="block">
                            Course: {user.course}
                          </Typography>
                        </Box>
                      )}
                      {user.role === 'PROFESSOR' && (
                        <Box>
                          <Typography variant="caption" display="block">
                            ID: {user.userId}
                          </Typography>
                          <Typography variant="caption" display="block">
                            Dept: {user.department}
                          </Typography>
                        </Box>
                      )}
                    </TableCell>
                    <TableCell>
                      <FormControlLabel
                        control={
                          <Switch
                            checked={user.isActive}
                            onChange={() => handleToggleUserStatus(user.id)}
                            color="primary"
                          />
                        }
                        label={user.isActive ? 'Active' : 'Inactive'}
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">Student Management</Typography>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button
                variant="outlined"
                startIcon={<UploadIcon />}
                onClick={handleBulkImportStudents}
              >
                Bulk Import
              </Button>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => setAddStudentModalOpen(true)}
              >
                Add Student
              </Button>
            </Box>
          </Box>

          {/* Student Search Bar */}
          <SearchBar
            searchTerm={studentSearchTerm}
            onSearchChange={handleStudentSearchChange}
            filterValue={studentCourseFilter}
            onFilterChange={handleStudentCourseFilterChange}
            filterOptions={[
              { value: 'all', label: 'All Courses' },
              ...courses.map(course => ({ value: course, label: course }))
            ]}
            filterLabel="Course"
            placeholder="Search students by name, registration number, examination number, or course..."
            showResultCount={true}
            resultCount={searchResults.studentCount}
            totalCount={searchResults.studentTotalCount}
          />
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Registration Number</TableCell>
                  <TableCell>Examination Number</TableCell>
                  <TableCell>Course</TableCell>
                  <TableCell>Created At</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {searchResults.students.map((student) => (
                  <TableRow key={student.id}>
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold">
                        {student.name}
                      </Typography>
                    </TableCell>
                    <TableCell>{student.registrationNumber}</TableCell>
                    <TableCell>{student.examinationNumber}</TableCell>
                    <TableCell>{student.course}</TableCell>
                    <TableCell>
                      {new Date(student.createdAt).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <Tooltip title="Delete Student">
                        <IconButton
                          color="error"
                          onClick={() => handleDeleteStudent(student.id)}
                          size="small"
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
                {searchResults.students.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      <Typography color="textSecondary">
                        {studentSearchTerm || studentCourseFilter !== 'all'
                          ? 'No students found matching your search criteria.'
                          : 'No students found. Click "Add Student" to add the first student.'}
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        <TabPanel value={tabValue} index={2}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">Professor Management</Typography>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button
                variant="outlined"
                startIcon={<UploadIcon />}
                onClick={handleBulkImportProfessors}
              >
                Bulk Import
              </Button>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => setAddProfessorModalOpen(true)}
              >
                Add Professor
              </Button>
            </Box>
          </Box>

          {/* Professor Search Bar */}
          <SearchBar
            searchTerm={professorSearchTerm}
            onSearchChange={handleProfessorSearchChange}
            filterValue={professorDepartmentFilter}
            onFilterChange={handleProfessorDepartmentFilterChange}
            filterOptions={[
              { value: 'all', label: 'All Departments' },
              { value: 'Computer Science', label: 'Computer Science' },
              { value: 'Electronics and Communication', label: 'Electronics and Communication' },
              { value: 'Mathematics', label: 'Mathematics' },
              { value: 'Physics', label: 'Physics' },
              { value: 'Chemistry', label: 'Chemistry' },
              { value: 'English', label: 'English' },
              { value: 'Commerce', label: 'Commerce' },
              { value: 'Management', label: 'Management' }
            ]}
            filterLabel="Department"
            placeholder="Search professors by name, user ID, or department..."
            showResultCount={true}
            resultCount={searchResults.professorCount}
            totalCount={searchResults.professorTotalCount}
          />
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>User ID</TableCell>
                  <TableCell>Department</TableCell>
                  <TableCell>Created At</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {searchResults.professors.map((professor) => (
                  <TableRow key={professor.id}>
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold">
                        {professor.name}
                      </Typography>
                    </TableCell>
                    <TableCell>{professor.userId}</TableCell>
                    <TableCell>{professor.department}</TableCell>
                    <TableCell>
                      {new Date(professor.createdAt).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      <Tooltip title="Delete Professor">
                        <IconButton
                          color="error"
                          onClick={() => handleDeleteProfessor(professor.id)}
                          size="small"
                        >
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
                {searchResults.professors.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={5} align="center">
                      <Typography color="textSecondary">
                        {professorSearchTerm || professorDepartmentFilter !== 'all'
                          ? 'No professors found matching your search criteria.'
                          : 'No professors found. Click "Add Professor" to add the first professor.'}
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>
      </Card>

      {/* Add Student Modal */}
      <AddStudentModal
        open={addStudentModalOpen}
        onClose={() => setAddStudentModalOpen(false)}
        onStudentAdded={handleStudentAdded}
        onAddStudent={handleAddStudent}
      />

      {/* Add Professor Modal */}
      <AddProfessorModal
        open={addProfessorModalOpen}
        onClose={() => setAddProfessorModalOpen(false)}
        onProfessorAdded={handleProfessorAdded}
        onAddProfessor={handleAddProfessor}
      />

      {/* Bulk Import Modals */}
      <BulkImportStudentsModal
        open={bulkImportStudentsModalOpen}
        onClose={() => setBulkImportStudentsModalOpen(false)}
        onImportComplete={handleStudentBulkImportComplete}
      />

      <BulkImportProfessorsModal
        open={bulkImportProfessorsModalOpen}
        onClose={() => setBulkImportProfessorsModalOpen(false)}
        onImportComplete={handleProfessorBulkImportComplete}
      />
    </Container>
  );
};

export default AdminDashboard;
