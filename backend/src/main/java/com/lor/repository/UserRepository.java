package com.lor.repository;

import com.lor.entity.Role;
import com.lor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by examination number (for students)
     */
    Optional<User> findByExaminationNumber(String examinationNumber);

    /**
     * Find user by user ID (for professors)
     */
    Optional<User> findByUserId(String userId);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if examination number exists
     */
    boolean existsByExaminationNumber(String examinationNumber);

    /**
     * Check if user ID exists
     */
    boolean existsByUserId(String userId);

    /**
     * Find all users by role
     */
    List<User> findByRole(Role role);

    /**
     * Find all active users by role
     */
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);

    /**
     * Find all professors for LOR request dropdown
     */
    @Query("SELECT u FROM User u WHERE u.role = 'PROFESSOR' ORDER BY u.name")
    List<User> findAllActiveProfessors();

    /**
     * Find all students for admin management
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.isActive = true ORDER BY u.name")
    List<User> findAllActiveStudents();

    /**
     * Find user by registration number and examination number (for student validation)
     */
    @Query("SELECT u FROM User u WHERE u.registrationNumber = :regNum AND u.examinationNumber = :examNum")
    Optional<User> findByRegistrationNumberAndExaminationNumber(
            @Param("regNum") String registrationNumber, 
            @Param("examNum") String examinationNumber);

    /**
     * Find professor by user ID and department (for professor validation)
     */
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.department = :department")
    Optional<User> findByUserIdAndDepartment(
            @Param("userId") String userId, 
            @Param("department") String department);

    /**
     * Count users by role
     */
    long countByRole(Role role);

    /**
     * Count active users by role
     */
    long countByRoleAndIsActive(Role role, Boolean isActive);
}
