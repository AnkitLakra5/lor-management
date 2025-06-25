package com.lor.config;

import com.lor.entity.Role;
import com.lor.entity.User;
import com.lor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data initializer to create default admin user and sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminUser();
        createDemoUsers();
        fixExistingProfessorsActiveStatus();
    }

    private void createDefaultAdminUser() {
        try {
            // Check if admin user already exists
            if (userRepository.findByEmail("admin@lor.system").isPresent()) {
                logger.info("Default admin user already exists");
                return;
            }

            // Create default admin user
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail("admin@lor.system");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);

            User savedAdmin = userRepository.save(admin);
            logger.info("Default admin user created successfully with ID: {}", savedAdmin.getId());
            logger.info("Admin login credentials:");
            logger.info("  Email: admin@lor.system");
            logger.info("  Password: Admin123!");
            
        } catch (Exception e) {
            logger.error("Failed to create default admin user: {}", e.getMessage());
        }
    }

    private void createDemoUsers() {
        try {
            createDemoStudent();
            createDemoProfessor();
        } catch (Exception e) {
            logger.error("Failed to create demo users: {}", e.getMessage());
        }
    }

    private void createDemoStudent() {
        try {
            // Check if demo student already exists
            if (userRepository.findByExaminationNumber("22VBCA051718").isPresent()) {
                logger.info("Demo student user already exists");
                return;
            }

            // Create demo student user
            User student = new User();
            student.setName("Ankit Lakra");
            student.setEmail("ankit.lakra@student.university.edu");
            student.setPassword(passwordEncoder.encode("Student123!"));
            student.setRole(Role.STUDENT);
            student.setRegistrationNumber("22SXC051718");
            student.setExaminationNumber("22VBCA051718");
            student.setCourse("Computer Science");
            student.setIsActive(true);

            User savedStudent = userRepository.save(student);
            logger.info("Demo student user created successfully with ID: {}", savedStudent.getId());
            logger.info("Student login credentials:");
            logger.info("  Examination Number: 22VBCA051718");
            logger.info("  Email: ankit.lakra@student.university.edu");
            logger.info("  Password: Student123!");

        } catch (Exception e) {
            logger.error("Failed to create demo student user: {}", e.getMessage());
        }
    }

    private void createDemoProfessor() {
        try {
            // Check if demo professor already exists
            if (userRepository.findByUserId("PROF001").isPresent()) {
                logger.info("Demo professor user already exists");
                return;
            }

            // Create demo professor user
            User professor = new User();
            professor.setName("A");
            professor.setEmail("prof.a@university.edu");
            professor.setPassword(passwordEncoder.encode("Prof123!"));
            professor.setRole(Role.PROFESSOR);
            professor.setUserId("PROF001");
            professor.setDepartment("Computer Science");
            professor.setIsActive(true);

            User savedProfessor = userRepository.save(professor);
            logger.info("Demo professor user created successfully with ID: {}", savedProfessor.getId());
            logger.info("Professor login credentials:");
            logger.info("  User ID: PROF001");
            logger.info("  Email: prof.a@university.edu");
            logger.info("  Password: Prof123!");

        } catch (Exception e) {
            logger.error("Failed to create demo professor user: {}", e.getMessage());
        }
    }

    private void fixExistingProfessorsActiveStatus() {
        try {
            logger.info("Checking and fixing existing professors' active status...");

            // Get all professors
            List<User> allProfessors = userRepository.findByRole(Role.PROFESSOR);
            logger.info("Found {} professors in database", allProfessors.size());

            int fixedCount = 0;
            for (User professor : allProfessors) {
                if (professor.getIsActive() == null) {
                    logger.info("Fixing professor {} (ID: {}) - setting isActive to true",
                        professor.getName(), professor.getId());
                    professor.setIsActive(true);
                    userRepository.save(professor);
                    fixedCount++;
                } else {
                    logger.info("Professor {} (ID: {}) already has isActive = {}",
                        professor.getName(), professor.getId(), professor.getIsActive());
                }
            }

            if (fixedCount > 0) {
                logger.info("Fixed {} professors' active status", fixedCount);
            } else {
                logger.info("All professors already have proper active status");
            }

        } catch (Exception e) {
            logger.error("Failed to fix professors' active status: {}", e.getMessage());
        }
    }
}
