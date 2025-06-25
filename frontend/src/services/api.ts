import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
  LoginRequest,
  LoginResponse,
  RegisterStudentRequest,
  RegisterProfessorRequest,
  LorRequest,
  CreateLorRequest,
  User,
  DashboardStats,
  PdfDocument,
  AdminStudent,
  AdminProfessor
} from '../types';
import { performLogout } from '../utils/logout';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to include auth token
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Add response interceptor for error handling
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Use the utility function to ensure complete logout and redirect
          performLogout();
        }
        return Promise.reject(error);
      }
    );
  }

  // Authentication APIs
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/login', credentials);
    return response.data;
  }

  async registerStudent(data: RegisterStudentRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/register/student', data);
    return response.data;
  }

  async registerProfessor(data: RegisterProfessorRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/register/professor', data);
    return response.data;
  }

  // LOR Request APIs
  async createLorRequest(data: CreateLorRequest): Promise<LorRequest> {
    const response: AxiosResponse<LorRequest> = await this.api.post('/lor-requests', data);
    return response.data;
  }

  async getStudentRequests(): Promise<{ requests: LorRequest[]; count: number }> {
    const response = await this.api.get('/lor-requests/student');
    return response.data;
  }

  async getApprovedRequestsForStudent(): Promise<{ requests: LorRequest[]; count: number }> {
    const response = await this.api.get('/lor-requests/student/approved');
    return response.data;
  }

  async getProfessorRequests(): Promise<{ requests: LorRequest[]; count: number }> {
    const response = await this.api.get('/lor-requests/professor');
    return response.data;
  }

  async getPendingRequestsForProfessor(): Promise<{ requests: LorRequest[]; count: number }> {
    const response = await this.api.get('/lor-requests/professor/pending');
    return response.data;
  }

  async approveLorRequest(requestId: number, comments: string): Promise<LorRequest> {
    const response: AxiosResponse<LorRequest> = await this.api.put(`/lor-requests/${requestId}/approve`, { comments });
    return response.data;
  }

  async rejectLorRequest(requestId: number, comments: string): Promise<LorRequest> {
    const response: AxiosResponse<LorRequest> = await this.api.put(`/lor-requests/${requestId}/reject`, { comments });
    return response.data;
  }

  async deleteLorRequest(requestId: number): Promise<{ success: boolean; message: string }> {
    const response = await this.api.delete(`/lor-requests/${requestId}`);
    return response.data;
  }

  async getActiveProfessors(): Promise<{ professors: User[]; count: number }> {
    const response = await this.api.get('/lor-requests/professors');
    return response.data;
  }

  // PDF APIs
  async generatePdf(requestId: number): Promise<{ fileName: string; referenceNumber: string; fileSize: number }> {
    const response = await this.api.post(`/pdf/generate/${requestId}`);
    return response.data;
  }

  async downloadPdf(referenceNumber: string): Promise<Blob> {
    const response = await this.api.get(`/pdf/download/${referenceNumber}`, {
      responseType: 'blob',
    });
    return response.data;
  }

  async getPdfInfo(referenceNumber: string): Promise<PdfDocument> {
    const response: AxiosResponse<PdfDocument> = await this.api.get(`/pdf/info/${referenceNumber}`);
    return response.data;
  }

  // Admin APIs
  async getDashboardStats(): Promise<DashboardStats> {
    const response: AxiosResponse<DashboardStats> = await this.api.get('/admin/dashboard');
    return response.data;
  }

  async getAllUsers(): Promise<{ users: User[]; count: number }> {
    const response = await this.api.get('/admin/users');
    return response.data;
  }

  async toggleUserStatus(userId: number): Promise<User> {
    const response: AxiosResponse<{ user: User }> = await this.api.put(`/admin/users/${userId}/toggle-status`);
    return response.data.user;
  }

  // Admin Student Management
  async getAllAdminStudents(): Promise<{ students: AdminStudent[]; count: number }> {
    const response = await this.api.get('/admin/students');
    return response.data;
  }

  async addAdminStudent(student: Omit<AdminStudent, 'id' | 'createdAt' | 'updatedAt'>): Promise<AdminStudent> {
    const response: AxiosResponse<{ student: AdminStudent }> = await this.api.post('/admin/students', student);
    return response.data.student;
  }

  async updateAdminStudent(id: number, student: Omit<AdminStudent, 'id' | 'createdAt' | 'updatedAt'>): Promise<AdminStudent> {
    const response: AxiosResponse<{ student: AdminStudent }> = await this.api.put(`/admin/students/${id}`, student);
    return response.data.student;
  }

  async deleteAdminStudent(id: number): Promise<void> {
    await this.api.delete(`/admin/students/${id}`);
  }

  // Admin Professor Management
  async getAllAdminProfessors(): Promise<{ professors: AdminProfessor[]; count: number }> {
    const response = await this.api.get('/admin/professors');
    return response.data;
  }

  async addAdminProfessor(professor: Omit<AdminProfessor, 'id' | 'createdAt' | 'updatedAt'>): Promise<AdminProfessor> {
    const response: AxiosResponse<{ professor: AdminProfessor }> = await this.api.post('/admin/professors', professor);
    return response.data.professor;
  }

  async updateAdminProfessor(id: number, professor: Omit<AdminProfessor, 'id' | 'createdAt' | 'updatedAt'>): Promise<AdminProfessor> {
    const response: AxiosResponse<{ professor: AdminProfessor }> = await this.api.put(`/admin/professors/${id}`, professor);
    return response.data.professor;
  }

  async deleteAdminProfessor(id: number): Promise<void> {
    await this.api.delete(`/admin/professors/${id}`);
  }

  async getAllDepartments(): Promise<{ departments: string[]; count: number }> {
    const response = await this.api.get('/admin/departments');
    return response.data;
  }

  // Bulk Import APIs
  async downloadStudentTemplate(): Promise<Blob> {
    const response = await this.api.get('/admin/students/template', {
      responseType: 'blob',
    });
    return response.data;
  }

  async downloadProfessorTemplate(): Promise<Blob> {
    const response = await this.api.get('/admin/professors/template', {
      responseType: 'blob',
    });
    return response.data;
  }

  async bulkImportStudents(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);
    const response = await this.api.post('/admin/students/bulk-import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  async bulkImportProfessors(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);
    const response = await this.api.post('/admin/professors/bulk-import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  // Search APIs
  async searchAdminStudents(searchTerm?: string, course?: string, page: number = 0, size: number = 10): Promise<any> {
    const params = new URLSearchParams();
    if (searchTerm) params.append('q', searchTerm);
    if (course && course !== 'all') params.append('course', course);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get(`/admin/students/search?${params.toString()}`);
    return response.data;
  }

  async searchAdminProfessors(searchTerm?: string, department?: string, page: number = 0, size: number = 10): Promise<any> {
    const params = new URLSearchParams();
    if (searchTerm) params.append('q', searchTerm);
    if (department && department !== 'all') params.append('department', department);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await this.api.get(`/admin/professors/search?${params.toString()}`);
    return response.data;
  }

  async getAllCourses(): Promise<{ courses: string[]; count: number }> {
    const response = await this.api.get('/admin/students/courses');
    return response.data;
  }
}

export const apiService = new ApiService();
export default apiService;
