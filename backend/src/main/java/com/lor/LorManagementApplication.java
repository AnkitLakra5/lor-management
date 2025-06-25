package com.lor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for LOR Management System
 * 
 * This application provides a comprehensive system for managing
 * Letter of Recommendation requests between students and professors.
 * 
 * Features:
 * - JWT-based authentication with role-based access control
 * - Student registration and LOR request submission
 * - Professor approval/rejection workflow
 * - PDF generation with unique reference numbers
 * - Admin management of verified student and professor data
 * 
 * @author LOR Management Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class LorManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LorManagementApplication.class, args);
        System.out.println("=================================================");
        System.out.println("LOR Management System Backend Started Successfully");
        System.out.println("Server running on: http://localhost:8080/api");
        System.out.println("=================================================");
    }
}
