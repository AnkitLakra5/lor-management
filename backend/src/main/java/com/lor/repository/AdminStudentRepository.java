package com.lor.repository;

import com.lor.entity.AdminStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AdminStudent entity
 */
@Repository
public interface AdminStudentRepository extends JpaRepository<AdminStudent, Long> {

    /**
     * Find admin student by registration number
     */
    Optional<AdminStudent> findByRegistrationNumber(String registrationNumber);

    /**
     * Find admin student by examination number
     */
    Optional<AdminStudent> findByExaminationNumber(String examinationNumber);

    /**
     * Check if registration number exists
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Check if examination number exists
     */
    boolean existsByExaminationNumber(String examinationNumber);

    /**
     * Validate student data for registration
     */
    @Query("SELECT a FROM AdminStudent a WHERE a.name = :name AND a.registrationNumber = :regNum " +
           "AND a.examinationNumber = :examNum AND a.course = :course")
    Optional<AdminStudent> validateStudentData(
            @Param("name") String name,
            @Param("regNum") String registrationNumber,
            @Param("examNum") String examinationNumber,
            @Param("course") String course);

    /**
     * Find by name and registration number
     */
    @Query("SELECT a FROM AdminStudent a WHERE a.name = :name AND a.registrationNumber = :regNum")
    Optional<AdminStudent> findByNameAndRegistrationNumber(
            @Param("name") String name,
            @Param("regNum") String registrationNumber);

    /**
     * Count total admin students
     */
    @Query("SELECT COUNT(a) FROM AdminStudent a")
    long countTotalStudents();

    // ===== SEARCH METHODS =====

    /**
     * Find students by course (case insensitive)
     */
    List<AdminStudent> findByCourseContainingIgnoreCase(String course);

    /**
     * Search students by name, registration number, examination number, or course
     */
    @Query("SELECT s FROM AdminStudent s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.registrationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.examinationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.course) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<AdminStudent> searchStudents(@Param("searchTerm") String searchTerm);

    /**
     * Search students with course filter
     */
    @Query("SELECT s FROM AdminStudent s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.registrationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.examinationNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.course) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "LOWER(s.course) LIKE LOWER(CONCAT('%', :course, '%'))")
    List<AdminStudent> searchStudentsWithCourse(@Param("searchTerm") String searchTerm, @Param("course") String course);

    /**
     * Get all unique courses
     */
    @Query("SELECT DISTINCT s.course FROM AdminStudent s ORDER BY s.course")
    List<String> findAllCourses();
}
