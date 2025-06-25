package com.lor.controller;

import com.lor.dto.LorRequestDto;
import com.lor.dto.LorPreviewDto;
import com.lor.entity.User;
import com.lor.service.LorRequestService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for LOR request operations
 */
@RestController
@RequestMapping("/lor-requests")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LorRequestController {

    private static final Logger logger = LoggerFactory.getLogger(LorRequestController.class);

    @Autowired
    private LorRequestService lorRequestService;

    /**
     * Create a new LOR request (Student only)
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createLorRequest(@Valid @RequestBody LorRequestDto requestDto) {
        try {
            logger.info("Creating LOR request for professor ID: {}", requestDto.getProfessorId());
            
            LorRequestDto createdRequest = lorRequestService.createLorRequest(requestDto);
            
            logger.info("LOR request created successfully with ID: {}", createdRequest.getId());
            return ResponseEntity.ok(createdRequest);
        } catch (Exception e) {
            logger.error("Failed to create LOR request: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create LOR request");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all LOR requests for current student
     */
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentRequests() {
        try {
            List<LorRequestDto> requests = lorRequestService.getStudentRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", requests);
            response.put("count", requests.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get student requests: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get student requests");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get approved requests for current student (for PDF download)
     */
    @GetMapping("/student/approved")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getApprovedRequestsForStudent() {
        try {
            List<LorRequestDto> requests = lorRequestService.getApprovedRequestsForStudent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", requests);
            response.put("count", requests.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get approved requests: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get approved requests");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all LOR requests for current professor
     */
    @GetMapping("/professor")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getProfessorRequests() {
        try {
            List<LorRequestDto> requests = lorRequestService.getProfessorRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", requests);
            response.put("count", requests.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get professor requests: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get professor requests");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get pending requests for current professor
     */
    @GetMapping("/professor/pending")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getPendingRequestsForProfessor() {
        try {
            List<LorRequestDto> requests = lorRequestService.getPendingRequestsForProfessor();
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", requests);
            response.put("count", requests.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get pending requests: {}", e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get pending requests");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Approve a LOR request (Professor only)
     */
    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> approveLorRequest(@PathVariable Long requestId, 
                                             @RequestBody Map<String, String> requestBody) {
        try {
            String comments = requestBody.getOrDefault("comments", "");
            
            logger.info("Approving LOR request ID: {}", requestId);
            
            LorRequestDto approvedRequest = lorRequestService.approveLorRequest(requestId, comments);
            
            logger.info("LOR request approved successfully: {}", requestId);
            return ResponseEntity.ok(approvedRequest);
        } catch (Exception e) {
            logger.error("Failed to approve LOR request {}: {}", requestId, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to approve LOR request");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Reject a LOR request (Professor only)
     */
    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> rejectLorRequest(@PathVariable Long requestId, 
                                            @RequestBody Map<String, String> requestBody) {
        try {
            String comments = requestBody.getOrDefault("comments", "");
            
            logger.info("Rejecting LOR request ID: {}", requestId);
            
            LorRequestDto rejectedRequest = lorRequestService.rejectLorRequest(requestId, comments);
            
            logger.info("LOR request rejected successfully: {}", requestId);
            return ResponseEntity.ok(rejectedRequest);
        } catch (Exception e) {
            logger.error("Failed to reject LOR request {}: {}", requestId, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to reject LOR request");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get LOR request by ID
     */
    @GetMapping("/{requestId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getLorRequestById(@PathVariable Long requestId) {
        try {
            LorRequestDto request = lorRequestService.getLorRequestById(requestId);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            logger.error("Failed to get LOR request {}: {}", requestId, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get LOR request");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all active professors for dropdown
     */
    @GetMapping("/professors")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getActiveProfessors() {
        try {
            List<User> professors = lorRequestService.getActiveProfessors();

            Map<String, Object> response = new HashMap<>();
            response.put("professors", professors);
            response.put("count", professors.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get active professors: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get active professors");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get LOR preview data for editing (Professor only)
     */
    @GetMapping("/{requestId}/preview")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getLorPreview(@PathVariable Long requestId) {
        try {
            logger.info("Getting LOR preview for request ID: {}", requestId);

            LorPreviewDto preview = lorRequestService.getLorPreview(requestId);

            logger.info("LOR preview generated successfully for request ID: {}", requestId);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            logger.error("Failed to get LOR preview for request {}: {}", requestId, e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get LOR preview");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Generate PDF with custom content (Professor only)
     */
    @PostMapping("/{requestId}/generate-pdf")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> generatePdfWithContent(@PathVariable Long requestId,
                                                   @Valid @RequestBody LorPreviewDto previewDto) {
        try {
            logger.info("Generating PDF with custom content for request ID: {}", requestId);

            String referenceNumber = lorRequestService.generatePdfWithCustomContent(requestId, previewDto);

            Map<String, String> response = new HashMap<>();
            response.put("message", "PDF generated successfully");
            response.put("referenceNumber", referenceNumber);

            logger.info("PDF generated successfully for request ID: {} with reference: {}", requestId, referenceNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate PDF for request {}: {}", requestId, e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate PDF");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get request statistics for dashboard
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getRequestStatistics() {
        try {
            Object statistics = lorRequestService.getRequestStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Failed to get request statistics: {}", e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get request statistics");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a LOR request (Student/Professor/Admin)
     */
    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteLorRequest(@PathVariable Long requestId) {
        try {
            logger.info("Deleting LOR request ID: {}", requestId);

            lorRequestService.deleteLorRequest(requestId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "LOR request deleted successfully");

            logger.info("LOR request deleted successfully: {}", requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete LOR request {}: {}", requestId, e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete LOR request");
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }
}
