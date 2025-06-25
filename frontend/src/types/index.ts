// User types
export interface User {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'PROFESSOR' | 'STUDENT';
  isActive: boolean;
  registrationNumber?: string;
  examinationNumber?: string;
  course?: string;
  userId?: string;
  department?: string;
}

// Authentication types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user?: User;
}

export interface RegisterStudentRequest {
  name: string;
  email: string;
  password: string;
  registrationNumber: string;
  examinationNumber: string;
  course: string;
}

export interface RegisterProfessorRequest {
  name: string;
  email: string;
  password: string;
  userId: string;
  department: string;
}

// LOR Request types
export interface LorRequest {
  id: number;
  professorId: number;
  professorName: string;
  professorDepartment: string;
  studentName: string;
  registrationNumber: string;
  examinationNumber: string;
  course: string;
  semester: string;
  session: string;
  classRollNumber: string;
  instituteCompany: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  professorComments?: string;
  requestedAt: string;
  processedAt?: string;
  pdfReferenceNumber?: string;
  pdfFileName?: string;
  hasPdf: boolean;
}

export interface CreateLorRequest {
  professorId: number;
  semester: string;
  session: string;
  classRollNumber: string;
  instituteCompany: string;
}

// PDF types
export interface PdfDocument {
  id: number;
  fileName: string;
  referenceNumber: string;
  fileSize: number;
  generatedAt: string;
  lorRequestId: number;
}

// Admin types
export interface AdminStudent {
  id: number;
  name: string;
  registrationNumber: string;
  examinationNumber: string;
  course: string;
  createdAt: string;
  updatedAt: string;
}

export interface AdminProfessor {
  id: number;
  name: string;
  userId: string;
  department: string;
  createdAt: string;
  updatedAt: string;
}

// Dashboard types
export interface DashboardStats {
  users: {
    totalAdmins: number;
    totalProfessors: number;
    totalStudents: number;
    activeStudents: number;
    activeProfessors: number;
  };
  requests: {
    totalRequests: number;
    approvedRequests: number;
    pendingRequests: number;
    rejectedRequests: number;
  };
  pdfs: {
    totalPdfs: number;
    totalFileSize: number;
  };
  adminData: {
    totalAdminProfessors: number;
    totalAdminStudents: number;
  };
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

// Auth Context types
export interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}
