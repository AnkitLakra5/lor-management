package com.lor.repository;

import com.lor.entity.LorRequest;
import com.lor.entity.RequestStatus;
import com.lor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LorRequest entity
 */
@Repository
public interface LorRequestRepository extends JpaRepository<LorRequest, Long> {

    /**
     * Find all requests by student
     */
    List<LorRequest> findByStudentOrderByRequestedAtDesc(User student);

    /**
     * Find all requests by professor
     */
    List<LorRequest> findByProfessorOrderByRequestedAtDesc(User professor);

    /**
     * Find all requests by student ID
     */
    @Query("SELECT l FROM LorRequest l WHERE l.student.id = :studentId ORDER BY l.requestedAt DESC")
    List<LorRequest> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Find all requests by professor ID
     */
    @Query("SELECT l FROM LorRequest l WHERE l.professor.id = :professorId ORDER BY l.requestedAt DESC")
    List<LorRequest> findByProfessorId(@Param("professorId") Long professorId);

    /**
     * Find all requests by status
     */
    List<LorRequest> findByStatusOrderByRequestedAtDesc(RequestStatus status);

    /**
     * Find all pending requests for a professor
     */
    @Query("SELECT l FROM LorRequest l WHERE l.professor.id = :professorId AND l.status = 'PENDING' ORDER BY l.requestedAt ASC")
    List<LorRequest> findPendingRequestsByProfessor(@Param("professorId") Long professorId);

    /**
     * Find all approved requests for a student
     */
    @Query("SELECT l FROM LorRequest l WHERE l.student.id = :studentId AND l.status = 'APPROVED' ORDER BY l.processedAt DESC")
    List<LorRequest> findApprovedRequestsByStudent(@Param("studentId") Long studentId);

    /**
     * Check if student has already requested LOR from same professor
     */
    @Query("SELECT l FROM LorRequest l WHERE l.student.id = :studentId AND l.professor.id = :professorId")
    List<LorRequest> findByStudentAndProfessor(@Param("studentId") Long studentId, @Param("professorId") Long professorId);

    /**
     * Check if student has pending request with same professor
     */
    @Query("SELECT l FROM LorRequest l WHERE l.student.id = :studentId AND l.professor.id = :professorId AND l.status = 'PENDING'")
    Optional<LorRequest> findPendingRequestByStudentAndProfessor(@Param("studentId") Long studentId, @Param("professorId") Long professorId);

    /**
     * Count requests by status
     */
    long countByStatus(RequestStatus status);

    /**
     * Count requests by student
     */
    long countByStudent(User student);

    /**
     * Count requests by professor
     */
    long countByProfessor(User professor);

    /**
     * Count pending requests by professor
     */
    @Query("SELECT COUNT(l) FROM LorRequest l WHERE l.professor.id = :professorId AND l.status = 'PENDING'")
    long countPendingRequestsByProfessor(@Param("professorId") Long professorId);

    /**
     * Find requests by date range
     */
    @Query("SELECT l FROM LorRequest l WHERE l.requestedAt BETWEEN :startDate AND :endDate ORDER BY l.requestedAt DESC")
    List<LorRequest> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find recent requests (last 30 days)
     */
    @Query("SELECT l FROM LorRequest l WHERE l.requestedAt >= :thirtyDaysAgo ORDER BY l.requestedAt DESC")
    List<LorRequest> findRecentRequests(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    /**
     * Get statistics for admin dashboard
     */
    @Query("SELECT l.status, COUNT(l) FROM LorRequest l GROUP BY l.status")
    List<Object[]> getRequestStatistics();
}
