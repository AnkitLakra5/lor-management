package com.lor.service;

import com.lor.dto.*;
import com.lor.entity.*;
import com.lor.repository.*;
import com.lor.security.JwtUtils;
import com.lor.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for authentication operations
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminStudentRepository adminStudentRepository;

    @Autowired
    private AdminProfessorRepository adminProfessorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Authenticate user and generate JWT token
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());

        // Find user by different login methods
        User user = findUserByLoginCredentials(loginRequest.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found with provided credentials");
        }

        // Authenticate using email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        JwtResponse jwtResponse = new JwtResponse(jwt, userPrincipal.getId(), 
                userPrincipal.getName(), userPrincipal.getEmail(), userPrincipal.getRole());

        // Set role-specific fields
        if (userPrincipal.isStudent()) {
            jwtResponse.setRegistrationNumber(userPrincipal.getRegistrationNumber());
            jwtResponse.setExaminationNumber(userPrincipal.getExaminationNumber());
            jwtResponse.setCourse(userPrincipal.getCourse());
        } else if (userPrincipal.isProfessor()) {
            jwtResponse.setUserId(userPrincipal.getUserId());
            jwtResponse.setDepartment(userPrincipal.getDepartment());
        }

        logger.info("User authenticated successfully: {} with role: {}", user.getEmail(), user.getRole());
        return jwtResponse;
    }

    /**
     * Register a new student
     */
    public JwtResponse registerStudent(StudentRegistrationRequest request) {
        logger.info("Attempting to register student: {}", request.getName());

        // Validate against admin data
        Optional<AdminStudent> adminStudent = adminStudentRepository.validateStudentData(
                request.getName(), request.getRegistrationNumber(), 
                request.getExaminationNumber(), request.getCourse());

        if (adminStudent.isEmpty()) {
            throw new RuntimeException("Student data validation failed. Please check your details with admin.");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        if (userRepository.existsByExaminationNumber(request.getExaminationNumber())) {
            throw new RuntimeException("Examination number is already registered!");
        }

        // Create new student user
        User student = new User();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setRole(Role.STUDENT);
        student.setRegistrationNumber(request.getRegistrationNumber());
        student.setExaminationNumber(request.getExaminationNumber());
        student.setCourse(request.getCourse());
        student.setIsActive(true);

        User savedStudent = userRepository.save(student);

        // Generate JWT token
        String jwt = jwtUtils.generateTokenFromEmail(savedStudent.getEmail(), savedStudent.getId(), 
                savedStudent.getRole().name(), savedStudent.getName());

        JwtResponse jwtResponse = new JwtResponse(jwt, savedStudent.getId(), 
                savedStudent.getName(), savedStudent.getEmail(), savedStudent.getRole());
        jwtResponse.setRegistrationNumber(savedStudent.getRegistrationNumber());
        jwtResponse.setExaminationNumber(savedStudent.getExaminationNumber());
        jwtResponse.setCourse(savedStudent.getCourse());

        logger.info("Student registered successfully: {}", savedStudent.getEmail());
        return jwtResponse;
    }

    /**
     * Register a new professor
     */
    public JwtResponse registerProfessor(ProfessorRegistrationRequest request) {
        logger.info("Attempting to register professor: {}", request.getName());

        // Validate against admin data
        Optional<AdminProfessor> adminProfessor = adminProfessorRepository.validateProfessorData(
                request.getName(), request.getUserId(), request.getDepartment());

        if (adminProfessor.isEmpty()) {
            throw new RuntimeException("Professor data validation failed. Please check your details with admin.");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        if (userRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("User ID is already registered!");
        }

        // Create new professor user
        User professor = new User();
        professor.setName(request.getName());
        professor.setEmail(request.getEmail());
        professor.setPassword(passwordEncoder.encode(request.getPassword()));
        professor.setRole(Role.PROFESSOR);
        professor.setUserId(request.getUserId());
        professor.setDepartment(request.getDepartment());
        professor.setIsActive(true);

        User savedProfessor = userRepository.save(professor);

        // Generate JWT token
        String jwt = jwtUtils.generateTokenFromEmail(savedProfessor.getEmail(), savedProfessor.getId(), 
                savedProfessor.getRole().name(), savedProfessor.getName());

        JwtResponse jwtResponse = new JwtResponse(jwt, savedProfessor.getId(), 
                savedProfessor.getName(), savedProfessor.getEmail(), savedProfessor.getRole());
        jwtResponse.setUserId(savedProfessor.getUserId());
        jwtResponse.setDepartment(savedProfessor.getDepartment());

        logger.info("Professor registered successfully: {}", savedProfessor.getEmail());
        return jwtResponse;
    }

    /**
     * Find user by login credentials (email, examination number, or user ID)
     */
    private User findUserByLoginCredentials(String username) {
        // Try to find by email first
        Optional<User> userByEmail = userRepository.findByEmail(username);
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }

        // Try to find by examination number (for students)
        Optional<User> userByExamNumber = userRepository.findByExaminationNumber(username);
        if (userByExamNumber.isPresent()) {
            return userByExamNumber.get();
        }

        // Try to find by user ID (for professors)
        Optional<User> userByUserId = userRepository.findByUserId(username);
        return userByUserId.orElse(null);
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }

    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Check if examination number is available
     */
    public boolean isExaminationNumberAvailable(String examinationNumber) {
        return !userRepository.existsByExaminationNumber(examinationNumber);
    }

    /**
     * Check if user ID is available
     */
    public boolean isUserIdAvailable(String userId) {
        return !userRepository.existsByUserId(userId);
    }
}
