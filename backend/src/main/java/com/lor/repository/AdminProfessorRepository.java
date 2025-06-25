package com.lor.repository;

import com.lor.entity.AdminProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AdminProfessor entity
 */
@Repository
public interface AdminProfessorRepository extends JpaRepository<AdminProfessor, Long> {

    /**
     * Find admin professor by user ID
     */
    Optional<AdminProfessor> findByUserId(String userId);

    /**
     * Check if user ID exists
     */
    boolean existsByUserId(String userId);

    /**
     * Validate professor data for registration
     */
    @Query("SELECT a FROM AdminProfessor a WHERE a.name = :name AND a.userId = :userId AND a.department = :department")
    Optional<AdminProfessor> validateProfessorData(
            @Param("name") String name,
            @Param("userId") String userId,
            @Param("department") String department);

    /**
     * Find by name and user ID
     */
    @Query("SELECT a FROM AdminProfessor a WHERE a.name = :name AND a.userId = :userId")
    Optional<AdminProfessor> findByNameAndUserId(
            @Param("name") String name,
            @Param("userId") String userId);

    /**
     * Find all professors by department
     */
    List<AdminProfessor> findByDepartment(String department);

    /**
     * Find all departments
     */
    @Query("SELECT DISTINCT a.department FROM AdminProfessor a ORDER BY a.department")
    List<String> findAllDepartments();

    /**
     * Count total admin professors
     */
    @Query("SELECT COUNT(a) FROM AdminProfessor a")
    long countTotalProfessors();

    /**
     * Count professors by department
     */
    long countByDepartment(String department);

    // ===== SEARCH METHODS =====

    /**
     * Search professors by name, user ID, or department
     */
    @Query("SELECT p FROM AdminProfessor p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.userId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<AdminProfessor> searchProfessors(@Param("searchTerm") String searchTerm);

    /**
     * Search professors with department filter
     */
    @Query("SELECT p FROM AdminProfessor p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.userId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "LOWER(p.department) LIKE LOWER(CONCAT('%', :department, '%'))")
    List<AdminProfessor> searchProfessorsWithDepartment(@Param("searchTerm") String searchTerm, @Param("department") String department);
}
