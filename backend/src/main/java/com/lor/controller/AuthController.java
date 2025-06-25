package com.lor.controller;

import com.lor.dto.*;
import com.lor.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Authenticate user (Admin, Student, or Professor)
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getUsername());
            
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            
            logger.info("Login successful for user: {} with role: {}", 
                    jwtResponse.getEmail(), jwtResponse.getRole());
            
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            logger.error("Login failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Register a new student
     */
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentRegistrationRequest signUpRequest) {
        try {
            logger.info("Student registration attempt for: {}", signUpRequest.getName());
            
            JwtResponse jwtResponse = authService.registerStudent(signUpRequest);
            
            logger.info("Student registration successful for: {}", jwtResponse.getEmail());
            
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            logger.error("Student registration failed for: {} - {}", 
                    signUpRequest.getName(), e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Register a new professor
     */
    @PostMapping("/register/professor")
    public ResponseEntity<?> registerProfessor(@Valid @RequestBody ProfessorRegistrationRequest signUpRequest) {
        try {
            logger.info("Professor registration attempt for: {}", signUpRequest.getName());
            
            JwtResponse jwtResponse = authService.registerProfessor(signUpRequest);
            
            logger.info("Professor registration successful for: {}", jwtResponse.getEmail());
            
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            logger.error("Professor registration failed for: {} - {}", 
                    signUpRequest.getName(), e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        try {
            boolean isAvailable = authService.isEmailAvailable(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            response.put("email", email);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking email availability: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check email availability");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if examination number is available
     */
    @GetMapping("/check-examination-number")
    public ResponseEntity<?> checkExaminationNumberAvailability(@RequestParam String examinationNumber) {
        try {
            boolean isAvailable = authService.isExaminationNumberAvailable(examinationNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            response.put("examinationNumber", examinationNumber);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking examination number availability: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check examination number availability");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Check if user ID is available
     */
    @GetMapping("/check-user-id")
    public ResponseEntity<?> checkUserIdAvailability(@RequestParam String userId) {
        try {
            boolean isAvailable = authService.isUserIdAvailable(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking user ID availability: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check user ID availability");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "LOR Management Authentication Service");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}
