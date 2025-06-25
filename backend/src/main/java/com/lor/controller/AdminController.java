package com.lor.controller;

import com.lor.entity.*;
import com.lor.service.AdminService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for admin operations
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = adminService.getDashboardStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Failed to get dashboard statistics: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get dashboard statistics");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ===== ADMIN STUDENT MANAGEMENT =====

    /**
     * Get all admin students
     */
    @GetMapping("/students")
    public ResponseEntity<?> getAllAdminStudents() {
        try {
            List<AdminStudent> students = adminService.getAllAdminStudents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("students", students);
            response.put("count", students.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get admin students: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get admin students");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Add new admin student
     */
    @PostMapping("/students")
    public ResponseEntity<?> addAdminStudent(@Valid @RequestBody AdminStudent adminStudent) {
        try {
            AdminStudent savedStudent = adminService.addAdminStudent(adminStudent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin student added successfully");
            response.put("student", savedStudent);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to add admin student: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add admin student");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update admin student
     */
    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateAdminStudent(@PathVariable Long id, 
                                              @Valid @RequestBody AdminStudent adminStudent) {
        try {
            AdminStudent updatedStudent = adminService.updateAdminStudent(id, adminStudent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin student updated successfully");
            response.put("student", updatedStudent);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to update admin student {}: {}", id, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update admin student");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete admin student
     */
    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteAdminStudent(@PathVariable Long id) {
        try {
            adminService.deleteAdminStudent(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin student deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete admin student {}: {}", id, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete admin student");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ===== ADMIN PROFESSOR MANAGEMENT =====

    /**
     * Get all admin professors
     */
    @GetMapping("/professors")
    public ResponseEntity<?> getAllAdminProfessors() {
        try {
            List<AdminProfessor> professors = adminService.getAllAdminProfessors();
            
            Map<String, Object> response = new HashMap<>();
            response.put("professors", professors);
            response.put("count", professors.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get admin professors: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get admin professors");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Add new admin professor
     */
    @PostMapping("/professors")
    public ResponseEntity<?> addAdminProfessor(@Valid @RequestBody AdminProfessor adminProfessor) {
        try {
            AdminProfessor savedProfessor = adminService.addAdminProfessor(adminProfessor);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin professor added successfully");
            response.put("professor", savedProfessor);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to add admin professor: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add admin professor");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update admin professor
     */
    @PutMapping("/professors/{id}")
    public ResponseEntity<?> updateAdminProfessor(@PathVariable Long id, 
                                                @Valid @RequestBody AdminProfessor adminProfessor) {
        try {
            AdminProfessor updatedProfessor = adminService.updateAdminProfessor(id, adminProfessor);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin professor updated successfully");
            response.put("professor", updatedProfessor);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to update admin professor {}: {}", id, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update admin professor");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete admin professor
     */
    @DeleteMapping("/professors/{id}")
    public ResponseEntity<?> deleteAdminProfessor(@PathVariable Long id) {
        try {
            adminService.deleteAdminProfessor(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin professor deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete admin professor {}: {}", id, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete admin professor");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ===== USER MANAGEMENT =====

    /**
     * Get all registered users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("count", users.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get all users: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get all users");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Debug endpoint to check professors in database
     */
    @GetMapping("/debug/professors")
    // @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for debugging
    public ResponseEntity<?> debugProfessors() {
        try {
            List<User> allUsers = adminService.getAllUsers();
            List<User> professors = allUsers.stream()
                .filter(user -> user.getRole().name().equals("PROFESSOR"))
                .collect(java.util.stream.Collectors.toList());

            logger.info("DEBUG: Found {} professors in database", professors.size());
            for (User prof : professors) {
                logger.info("DEBUG Professor: {} (ID: {}, Active: {}, Email: {}, UserID: {})",
                    prof.getName(), prof.getId(), prof.getIsActive(), prof.getEmail(), prof.getUserId());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("professors", professors);
            response.put("count", professors.size());
            response.put("debug", "Check server logs for detailed professor information");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to debug professors: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to debug professors");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<User> users = adminService.getUsersByRole(userRole);
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("role", role);
            response.put("count", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get users by role {}: {}", role, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get users by role");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Toggle user active status
     */
    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        try {
            User updatedUser = adminService.toggleUserStatus(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User status updated successfully");
            response.put("user", updatedUser);
            response.put("isActive", updatedUser.getIsActive());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to toggle user status {}: {}", userId, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to toggle user status");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all departments
     */
    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        try {
            List<String> departments = adminService.getAllDepartments();

            Map<String, Object> response = new HashMap<>();
            response.put("departments", departments);
            response.put("count", departments.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get departments: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get departments");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    // ===== BULK IMPORT FUNCTIONALITY =====

    /**
     * Download CSV template for students
     */
    @GetMapping("/students/template")
    public ResponseEntity<byte[]> downloadStudentTemplate() {
        try {
            String csvContent = adminService.generateStudentCsvTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "student_template.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent.getBytes());
        } catch (Exception e) {
            logger.error("Failed to generate student CSV template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download CSV template for professors
     */
    @GetMapping("/professors/template")
    public ResponseEntity<byte[]> downloadProfessorTemplate() {
        try {
            String csvContent = adminService.generateProfessorCsvTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "professor_template.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent.getBytes());
        } catch (Exception e) {
            logger.error("Failed to generate professor CSV template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Bulk import students from CSV file
     */
    @PostMapping("/students/bulk-import")
    public ResponseEntity<Map<String, Object>> bulkImportStudents(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "File is empty");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Map<String, Object> result = adminService.bulkImportStudents(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to bulk import students: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to import students: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Bulk import professors from CSV file
     */
    @PostMapping("/professors/bulk-import")
    public ResponseEntity<Map<String, Object>> bulkImportProfessors(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "File is empty");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Map<String, Object> result = adminService.bulkImportProfessors(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to bulk import professors: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to import professors: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===== SEARCH FUNCTIONALITY =====

    /**
     * Search admin students with filters
     */
    @GetMapping("/students/search")
    public ResponseEntity<Map<String, Object>> searchAdminStudents(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestParam(value = "course", required = false) String course,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Map<String, Object> result = adminService.searchAdminStudents(searchTerm, course, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to search admin students: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to search students: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Search admin professors with filters
     */
    @GetMapping("/professors/search")
    public ResponseEntity<Map<String, Object>> searchAdminProfessors(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Map<String, Object> result = adminService.searchAdminProfessors(searchTerm, department, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to search admin professors: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to search professors: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all unique courses from admin students
     */
    @GetMapping("/students/courses")
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        try {
            List<String> courses = adminService.getAllCourses();
            Map<String, Object> response = new HashMap<>();
            response.put("courses", courses);
            response.put("count", courses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get courses: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get courses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
