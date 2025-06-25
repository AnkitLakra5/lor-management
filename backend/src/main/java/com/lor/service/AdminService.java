package com.lor.service;

import com.lor.entity.*;
import com.lor.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Service for admin operations
 */
@Service
@Transactional
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminStudentRepository adminStudentRepository;

    @Autowired
    private AdminProfessorRepository adminProfessorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LorRequestRepository lorRequestRepository;

    @Autowired
    private PdfDocumentRepository pdfDocumentRepository;

    @Autowired
    private AuthService authService;

    /**
     * Get all admin students
     */
    public List<AdminStudent> getAllAdminStudents() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access this data");
        }
        return adminStudentRepository.findAll();
    }

    /**
     * Get all admin professors
     */
    public List<AdminProfessor> getAllAdminProfessors() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access this data");
        }
        return adminProfessorRepository.findAll();
    }

    /**
     * Add new admin student
     */
    public AdminStudent addAdminStudent(AdminStudent adminStudent) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can add student data");
        }

        // Check for duplicates
        if (adminStudentRepository.existsByRegistrationNumber(adminStudent.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        if (adminStudentRepository.existsByExaminationNumber(adminStudent.getExaminationNumber())) {
            throw new RuntimeException("Examination number already exists");
        }

        AdminStudent savedStudent = adminStudentRepository.save(adminStudent);
        logger.info("Admin student added: {}", savedStudent.getName());
        return savedStudent;
    }

    /**
     * Add new admin professor
     */
    public AdminProfessor addAdminProfessor(AdminProfessor adminProfessor) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can add professor data");
        }

        // Check for duplicates
        if (adminProfessorRepository.existsByUserId(adminProfessor.getUserId())) {
            throw new RuntimeException("User ID already exists");
        }

        AdminProfessor savedProfessor = adminProfessorRepository.save(adminProfessor);
        logger.info("Admin professor added: {}", savedProfessor.getName());
        return savedProfessor;
    }

    /**
     * Update admin student
     */
    public AdminStudent updateAdminStudent(Long id, AdminStudent adminStudent) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can update student data");
        }

        AdminStudent existingStudent = adminStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin student not found"));

        // Check for duplicates (excluding current record)
        if (!existingStudent.getRegistrationNumber().equals(adminStudent.getRegistrationNumber()) &&
            adminStudentRepository.existsByRegistrationNumber(adminStudent.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        if (!existingStudent.getExaminationNumber().equals(adminStudent.getExaminationNumber()) &&
            adminStudentRepository.existsByExaminationNumber(adminStudent.getExaminationNumber())) {
            throw new RuntimeException("Examination number already exists");
        }

        existingStudent.setName(adminStudent.getName());
        existingStudent.setRegistrationNumber(adminStudent.getRegistrationNumber());
        existingStudent.setExaminationNumber(adminStudent.getExaminationNumber());
        existingStudent.setCourse(adminStudent.getCourse());

        AdminStudent updatedStudent = adminStudentRepository.save(existingStudent);
        logger.info("Admin student updated: {}", updatedStudent.getName());
        return updatedStudent;
    }

    /**
     * Update admin professor
     */
    public AdminProfessor updateAdminProfessor(Long id, AdminProfessor adminProfessor) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can update professor data");
        }

        AdminProfessor existingProfessor = adminProfessorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin professor not found"));

        // Check for duplicates (excluding current record)
        if (!existingProfessor.getUserId().equals(adminProfessor.getUserId()) &&
            adminProfessorRepository.existsByUserId(adminProfessor.getUserId())) {
            throw new RuntimeException("User ID already exists");
        }

        existingProfessor.setName(adminProfessor.getName());
        existingProfessor.setUserId(adminProfessor.getUserId());
        existingProfessor.setDepartment(adminProfessor.getDepartment());

        AdminProfessor updatedProfessor = adminProfessorRepository.save(existingProfessor);
        logger.info("Admin professor updated: {}", updatedProfessor.getName());
        return updatedProfessor;
    }

    /**
     * Delete admin student
     */
    public void deleteAdminStudent(Long id) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can delete student data");
        }

        AdminStudent adminStudent = adminStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin student not found"));

        adminStudentRepository.delete(adminStudent);
        logger.info("Admin student deleted: {}", adminStudent.getName());
    }

    /**
     * Delete admin professor
     */
    public void deleteAdminProfessor(Long id) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can delete professor data");
        }

        AdminProfessor adminProfessor = adminProfessorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin professor not found"));

        adminProfessorRepository.delete(adminProfessor);
        logger.info("Admin professor deleted: {}", adminProfessor.getName());
    }

    /**
     * Get all registered users
     */
    public List<User> getAllUsers() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access user data");
        }
        return userRepository.findAll();
    }

    /**
     * Get users by role
     */
    public List<User> getUsersByRole(Role role) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access user data");
        }
        return userRepository.findByRole(role);
    }

    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStatistics() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access dashboard statistics");
        }

        Map<String, Object> stats = new HashMap<>();

        // User statistics
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalStudents", userRepository.countByRole(Role.STUDENT));
        userStats.put("totalProfessors", userRepository.countByRole(Role.PROFESSOR));
        userStats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        userStats.put("activeStudents", userRepository.countByRoleAndIsActive(Role.STUDENT, true));
        userStats.put("activeProfessors", userRepository.countByRoleAndIsActive(Role.PROFESSOR, true));

        // Admin data statistics
        Map<String, Object> adminDataStats = new HashMap<>();
        adminDataStats.put("totalAdminStudents", adminStudentRepository.countTotalStudents());
        adminDataStats.put("totalAdminProfessors", adminProfessorRepository.countTotalProfessors());

        // LOR request statistics
        Map<String, Object> requestStats = new HashMap<>();
        requestStats.put("totalRequests", lorRequestRepository.count());
        requestStats.put("pendingRequests", lorRequestRepository.countByStatus(RequestStatus.PENDING));
        requestStats.put("approvedRequests", lorRequestRepository.countByStatus(RequestStatus.APPROVED));
        requestStats.put("rejectedRequests", lorRequestRepository.countByStatus(RequestStatus.REJECTED));

        // PDF statistics
        Map<String, Object> pdfStats = new HashMap<>();
        pdfStats.put("totalPdfs", pdfDocumentRepository.countTotalPdfs());
        pdfStats.put("totalFileSize", pdfDocumentRepository.getTotalFileSize());

        stats.put("users", userStats);
        stats.put("adminData", adminDataStats);
        stats.put("requests", requestStats);
        stats.put("pdfs", pdfStats);

        return stats;
    }

    /**
     * Get all departments
     */
    public List<String> getAllDepartments() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access department data");
        }
        return adminProfessorRepository.findAllDepartments();
    }

    /**
     * Activate/Deactivate user
     */
    public User toggleUserStatus(Long userId) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can change user status");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isAdmin()) {
            throw new RuntimeException("Cannot change admin user status");
        }

        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);

        logger.info("User status changed: {} - Active: {}", user.getEmail(), user.getIsActive());
        return updatedUser;
    }

    // ===== BULK IMPORT FUNCTIONALITY =====

    /**
     * Generate CSV template for students
     */
    public String generateStudentCsvTemplate() {
        StringBuilder csv = new StringBuilder();
        csv.append("name,registrationNumber,examinationNumber,course\n");
        csv.append("John Doe,22SXC051718,22VBCA051718,Computer Science\n");
        csv.append("Jane Smith,22SXC051719,22VBCA051719,BCA\n");
        csv.append("# Instructions:\n");
        csv.append("# - name: Full name of the student\n");
        csv.append("# - registrationNumber: University registration number (e.g., 22SXC051718)\n");
        csv.append("# - examinationNumber: University examination number (e.g., 22VBCA051718)\n");
        csv.append("# - course: Course name (e.g., Computer Science, BCA, Mathematics)\n");
        csv.append("# - Remove the example rows and instruction lines before uploading\n");
        return csv.toString();
    }

    /**
     * Generate CSV template for professors
     */
    public String generateProfessorCsvTemplate() {
        StringBuilder csv = new StringBuilder();
        csv.append("name,userId,department\n");
        csv.append("Dr. John Smith,PROF001,Computer Science\n");
        csv.append("Dr. Jane Doe,PROF002,Mathematics\n");
        csv.append("# Instructions:\n");
        csv.append("# - name: Full name of the professor\n");
        csv.append("# - userId: Unique user ID for login (e.g., PROF001, PROF026)\n");
        csv.append("# - department: Department name from the following list:\n");
        csv.append("#   Computer Science, Electronics and Communication, Mathematics,\n");
        csv.append("#   Physics, Chemistry, English, Commerce, Management\n");
        csv.append("# - Remove the example rows and instruction lines before uploading\n");
        return csv.toString();
    }

    /**
     * Bulk import students from CSV file
     */
    public Map<String, Object> bulkImportStudents(MultipartFile file) throws Exception {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can perform bulk import");
        }

        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<AdminStudent> successfulImports = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines and comment lines
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                totalRows++;

                try {
                    String[] fields = line.split(",");
                    if (fields.length != 4) {
                        errors.add("Line " + lineNumber + ": Expected 4 fields, found " + fields.length);
                        errorCount++;
                        continue;
                    }

                    String name = fields[0].trim();
                    String registrationNumber = fields[1].trim();
                    String examinationNumber = fields[2].trim();
                    String course = fields[3].trim();

                    // Validate required fields
                    if (name.isEmpty() || registrationNumber.isEmpty() ||
                        examinationNumber.isEmpty() || course.isEmpty()) {
                        errors.add("Line " + lineNumber + ": All fields are required");
                        errorCount++;
                        continue;
                    }

                    // Check for duplicates
                    if (adminStudentRepository.findByRegistrationNumber(registrationNumber).isPresent()) {
                        errors.add("Line " + lineNumber + ": Student with registration number " +
                                 registrationNumber + " already exists");
                        errorCount++;
                        continue;
                    }

                    if (adminStudentRepository.findByExaminationNumber(examinationNumber).isPresent()) {
                        errors.add("Line " + lineNumber + ": Student with examination number " +
                                 examinationNumber + " already exists");
                        errorCount++;
                        continue;
                    }

                    // Create and save student
                    AdminStudent student = new AdminStudent();
                    student.setName(name);
                    student.setRegistrationNumber(registrationNumber);
                    student.setExaminationNumber(examinationNumber);
                    student.setCourse(course);

                    AdminStudent savedStudent = adminStudentRepository.save(student);
                    successfulImports.add(savedStudent);
                    successCount++;

                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                    errorCount++;
                }
            }
        }

        result.put("success", errorCount == 0);
        result.put("totalRows", totalRows);
        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        result.put("errors", errors);
        result.put("importedStudents", successfulImports);
        result.put("message", String.format("Import completed: %d successful, %d errors out of %d total rows",
                                           successCount, errorCount, totalRows));

        return result;
    }

    /**
     * Bulk import professors from CSV file
     */
    public Map<String, Object> bulkImportProfessors(MultipartFile file) throws Exception {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can perform bulk import");
        }

        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<AdminProfessor> successfulImports = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;
        int errorCount = 0;

        // Valid departments
        List<String> validDepartments = Arrays.asList(
            "Computer Science", "Electronics and Communication", "Mathematics",
            "Physics", "Chemistry", "English", "Commerce", "Management"
        );

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines and comment lines
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                totalRows++;

                try {
                    String[] fields = line.split(",");
                    if (fields.length != 3) {
                        errors.add("Line " + lineNumber + ": Expected 3 fields, found " + fields.length);
                        errorCount++;
                        continue;
                    }

                    String name = fields[0].trim();
                    String userId = fields[1].trim();
                    String department = fields[2].trim();

                    // Validate required fields
                    if (name.isEmpty() || userId.isEmpty() || department.isEmpty()) {
                        errors.add("Line " + lineNumber + ": All fields are required");
                        errorCount++;
                        continue;
                    }

                    // Validate department
                    if (!validDepartments.contains(department)) {
                        errors.add("Line " + lineNumber + ": Invalid department '" + department +
                                 "'. Valid departments: " + String.join(", ", validDepartments));
                        errorCount++;
                        continue;
                    }

                    // Check for duplicates
                    if (adminProfessorRepository.findByUserId(userId).isPresent()) {
                        errors.add("Line " + lineNumber + ": Professor with user ID " +
                                 userId + " already exists");
                        errorCount++;
                        continue;
                    }

                    // Create and save professor
                    AdminProfessor professor = new AdminProfessor();
                    professor.setName(name);
                    professor.setUserId(userId);
                    professor.setDepartment(department);

                    AdminProfessor savedProfessor = adminProfessorRepository.save(professor);
                    successfulImports.add(savedProfessor);
                    successCount++;

                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                    errorCount++;
                }
            }
        }

        result.put("success", errorCount == 0);
        result.put("totalRows", totalRows);
        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        result.put("errors", errors);
        result.put("importedProfessors", successfulImports);
        result.put("message", String.format("Import completed: %d successful, %d errors out of %d total rows",
                                           successCount, errorCount, totalRows));

        return result;
    }

    // ===== SEARCH FUNCTIONALITY =====

    /**
     * Search admin students with filters
     */
    public Map<String, Object> searchAdminStudents(String searchTerm, String course, int page, int size) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can search student data");
        }

        Map<String, Object> result = new HashMap<>();
        List<AdminStudent> students;
        long totalCount;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            // No search term, filter by course only
            if (course == null || course.trim().isEmpty() || "all".equalsIgnoreCase(course)) {
                students = adminStudentRepository.findAll();
                totalCount = adminStudentRepository.count();
            } else {
                students = adminStudentRepository.findByCourseContainingIgnoreCase(course);
                totalCount = students.size();
            }
        } else {
            // Search with term and optional course filter
            if (course == null || course.trim().isEmpty() || "all".equalsIgnoreCase(course)) {
                students = adminStudentRepository.searchStudents(searchTerm.trim());
                totalCount = students.size();
            } else {
                students = adminStudentRepository.searchStudentsWithCourse(searchTerm.trim(), course);
                totalCount = students.size();
            }
        }

        // Apply pagination
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, students.size());
        List<AdminStudent> paginatedStudents = students.subList(startIndex, endIndex);

        result.put("students", paginatedStudents);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("totalPages", (int) Math.ceil((double) totalCount / size));
        result.put("pageSize", size);

        return result;
    }

    /**
     * Search admin professors with filters
     */
    public Map<String, Object> searchAdminProfessors(String searchTerm, String department, int page, int size) {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can search professor data");
        }

        Map<String, Object> result = new HashMap<>();
        List<AdminProfessor> professors;
        long totalCount;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            // No search term, filter by department only
            if (department == null || department.trim().isEmpty() || "all".equalsIgnoreCase(department)) {
                professors = adminProfessorRepository.findAll();
                totalCount = adminProfessorRepository.count();
            } else {
                professors = adminProfessorRepository.findByDepartment(department);
                totalCount = professors.size();
            }
        } else {
            // Search with term and optional department filter
            if (department == null || department.trim().isEmpty() || "all".equalsIgnoreCase(department)) {
                professors = adminProfessorRepository.searchProfessors(searchTerm.trim());
                totalCount = professors.size();
            } else {
                professors = adminProfessorRepository.searchProfessorsWithDepartment(searchTerm.trim(), department);
                totalCount = professors.size();
            }
        }

        // Apply pagination
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, professors.size());
        List<AdminProfessor> paginatedProfessors = professors.subList(startIndex, endIndex);

        result.put("professors", paginatedProfessors);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("totalPages", (int) Math.ceil((double) totalCount / size));
        result.put("pageSize", size);

        return result;
    }

    /**
     * Get all unique courses from admin students
     */
    public List<String> getAllCourses() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new RuntimeException("Only admin can access course data");
        }
        return adminStudentRepository.findAllCourses();
    }
}
