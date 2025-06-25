package com.lor.service;

import com.lor.dto.LorRequestDto;
import com.lor.dto.LorPreviewDto;
import com.lor.entity.*;
import com.lor.repository.*;
import com.lor.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for LOR request operations
 */
@Service
@Transactional
public class LorRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LorRequestService.class);

    @Autowired
    private LorRequestRepository lorRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfDocumentRepository pdfDocumentRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    /**
     * Create a new LOR request
     */
    public LorRequestDto createLorRequest(LorRequestDto requestDto) {
        logger.info("Creating new LOR request for professor ID: {}", requestDto.getProfessorId());

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isStudent()) {
            throw new RuntimeException("Only students can create LOR requests");
        }

        // Find professor
        User professor = userRepository.findById(requestDto.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        if (!professor.isProfessor() || !professor.getIsActive()) {
            throw new RuntimeException("Invalid professor selected");
        }

        // Check if student already has a pending request with this professor
        Optional<LorRequest> existingRequest = lorRequestRepository
                .findPendingRequestByStudentAndProfessor(currentUser.getId(), professor.getId());

        if (existingRequest.isPresent()) {
            throw new RuntimeException("You already have a pending request with this professor");
        }

        // Create new LOR request
        LorRequest lorRequest = new LorRequest();
        lorRequest.setStudent(currentUser);
        lorRequest.setProfessor(professor);
        lorRequest.setStudentName(currentUser.getName());
        lorRequest.setRegistrationNumber(currentUser.getRegistrationNumber());
        lorRequest.setExaminationNumber(currentUser.getExaminationNumber());
        lorRequest.setCourse(currentUser.getCourse());
        lorRequest.setSemester(requestDto.getSemester());
        lorRequest.setSession(requestDto.getSession());
        lorRequest.setClassRollNumber(requestDto.getClassRollNumber());
        lorRequest.setInstituteCompany(requestDto.getInstituteCompany());
        lorRequest.setStatus(RequestStatus.PENDING);
        lorRequest.setRequestedAt(LocalDateTime.now());

        LorRequest savedRequest = lorRequestRepository.save(lorRequest);

        logger.info("LOR request created successfully with ID: {}", savedRequest.getId());
        return convertToDto(savedRequest);
    }

    /**
     * Get all LOR requests for current student
     */
    public List<LorRequestDto> getStudentRequests() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isStudent()) {
            throw new RuntimeException("Only students can view their requests");
        }

        List<LorRequest> requests = lorRequestRepository.findByStudentId(currentUser.getId());
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get all LOR requests for current professor
     */
    public List<LorRequestDto> getProfessorRequests() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can view their requests");
        }

        List<LorRequest> requests = lorRequestRepository.findByProfessorId(currentUser.getId());
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get pending requests for current professor
     */
    public List<LorRequestDto> getPendingRequestsForProfessor() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can view pending requests");
        }

        List<LorRequest> requests = lorRequestRepository.findPendingRequestsByProfessor(currentUser.getId());
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get approved requests for current student
     */
    public List<LorRequestDto> getApprovedRequestsForStudent() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.isStudent()) {
            throw new RuntimeException("Only students can view their approved requests");
        }

        List<LorRequest> requests = lorRequestRepository.findApprovedRequestsByStudent(currentUser.getId());
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Approve a LOR request
     */
    public LorRequestDto approveLorRequest(Long requestId, String comments) {
        logger.info("Approving LOR request ID: {}", requestId);

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can approve requests");
        }

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Verify professor owns this request
        if (!lorRequest.getProfessor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only approve your own requests");
        }

        if (!lorRequest.isPending()) {
            throw new RuntimeException("Request has already been processed");
        }

        // Use empty string if no comments provided for approval
        String approvalComments = (comments != null && !comments.trim().isEmpty()) ? comments : "";
        lorRequest.approve(approvalComments);
        LorRequest savedRequest = lorRequestRepository.save(lorRequest);

        logger.info("LOR request approved successfully: {}", requestId);
        return convertToDto(savedRequest);
    }

    /**
     * Reject a LOR request
     */
    public LorRequestDto rejectLorRequest(Long requestId, String comments) {
        logger.info("Rejecting LOR request ID: {}", requestId);

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can reject requests");
        }

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Verify professor owns this request
        if (!lorRequest.getProfessor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only reject your own requests");
        }

        if (!lorRequest.isPending()) {
            throw new RuntimeException("Request has already been processed");
        }

        lorRequest.reject(comments);
        LorRequest savedRequest = lorRequestRepository.save(lorRequest);

        logger.info("LOR request rejected successfully: {}", requestId);
        return convertToDto(savedRequest);
    }

    /**
     * Get LOR request by ID
     */
    public LorRequestDto getLorRequestById(Long requestId) {
        User currentUser = authService.getCurrentUser();

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Check access permissions
        if (currentUser.isStudent() && !lorRequest.getStudent().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view your own requests");
        }

        if (currentUser.isProfessor() && !lorRequest.getProfessor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view your own requests");
        }

        return convertToDto(lorRequest);
    }

    /**
     * Delete a LOR request
     */
    public void deleteLorRequest(Long requestId) {
        logger.info("Deleting LOR request ID: {}", requestId);

        User currentUser = authService.getCurrentUser();

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Check permissions based on user role
        if (currentUser.isStudent()) {
            // Students can only delete their own requests
            if (!lorRequest.getStudent().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only delete your own requests");
            }
            // Students can delete pending requests freely, but warn for approved/rejected
            if (lorRequest.isApproved()) {
                logger.warn("Student deleting approved LOR request: {} for student: {}",
                    requestId, currentUser.getId());
            } else if (lorRequest.isRejected()) {
                logger.warn("Student deleting rejected LOR request: {} for student: {}",
                    requestId, currentUser.getId());
            }
        } else if (currentUser.isProfessor()) {
            // Professors can delete requests assigned to them
            if (!lorRequest.getProfessor().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only delete requests assigned to you");
            }
            // Professors can delete any status, but warn for approved requests with PDFs
            if (lorRequest.isApproved()) {
                logger.warn("Professor deleting approved LOR request: {} by professor: {}",
                    requestId, currentUser.getId());
                if (lorRequest.getPdfDocument() != null) {
                    logger.warn("Deleting approved request with existing PDF: {}", requestId);
                }
            }
        } else if (currentUser.isAdmin()) {
            // Admins can delete any request, but warn about important deletions
            if (lorRequest.isApproved()) {
                logger.warn("Admin deleting approved LOR request: {}", requestId);
            }
            if (lorRequest.getPdfDocument() != null) {
                logger.warn("Admin deleting LOR request with existing PDF: {}", requestId);
            }
        } else {
            throw new RuntimeException("Unauthorized to delete LOR requests");
        }

        // If there's an associated PDF, delete it first
        if (lorRequest.getPdfDocument() != null) {
            try {
                pdfGenerationService.deletePdfInternal(lorRequest.getPdfDocument().getReferenceNumber(), false);
                logger.info("Associated PDF deleted for request ID: {}", requestId);
            } catch (Exception e) {
                logger.error("Failed to delete associated PDF for request ID: {}", requestId, e);
                // Continue with request deletion even if PDF deletion fails
            }
        }

        // Delete the LOR request
        lorRequestRepository.delete(lorRequest);
        logger.info("LOR request deleted successfully: {}", requestId);
    }

    /**
     * Get all active professors for dropdown
     */
    public List<User> getActiveProfessors() {
        return userRepository.findAllActiveProfessors();
    }

    /**
     * Get request statistics for dashboard
     */
    public Object getRequestStatistics() {
        User currentUser = authService.getCurrentUser();

        if (currentUser.isStudent()) {
            return getStudentStatistics(currentUser.getId());
        } else if (currentUser.isProfessor()) {
            return getProfessorStatistics(currentUser.getId());
        } else if (currentUser.isAdmin()) {
            return getAdminStatistics();
        }

        throw new RuntimeException("Invalid user role for statistics");
    }

    /**
     * Get LOR preview data for editing
     */
    public LorPreviewDto getLorPreview(Long requestId) {
        logger.info("Getting LOR preview for request ID: {}", requestId);

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can preview LOR content");
        }

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Verify professor owns this request
        if (!lorRequest.getProfessor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only preview your own requests");
        }

        if (!lorRequest.isApproved()) {
            throw new RuntimeException("Request must be approved before preview");
        }

        // Generate default content
        String defaultContent = generateDefaultContent(lorRequest);
        String referenceNumber = generateSXCReferenceNumber(lorRequest);
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        LorPreviewDto preview = new LorPreviewDto();
        preview.setStudentName(lorRequest.getStudentName());
        preview.setClassRollNumber(lorRequest.getClassRollNumber());
        preview.setRegistrationNumber(lorRequest.getRegistrationNumber());
        preview.setExaminationNumber(lorRequest.getExaminationNumber());
        preview.setCourse(lorRequest.getCourse());
        preview.setSemester(lorRequest.getSemester());
        preview.setSession(lorRequest.getSession());
        preview.setInstituteCompany(lorRequest.getInstituteCompany());

        // Recipient information
        preview.setRecipientTitle("The General Manager");
        preview.setRecipientDepartment("Human Resource Department");
        preview.setRecipientCompany(lorRequest.getInstituteCompany());
        preview.setRecipientLocation("Ranchi");

        // Letter content
        preview.setSubject("Permission regarding internship in your esteemed organization.");
        preview.setSalutation("Dear Sir / Madam,");
        preview.setMainContent(defaultContent);
        preview.setPaperCode("DSE4A");

        // Professor information
        preview.setProfessorName(lorRequest.getProfessor().getName());
        preview.setProfessorDepartment(lorRequest.getProfessor().getDepartment());
        preview.setProfessorDesignation("Assistant Professor");

        // Reference information
        preview.setReferenceNumber(referenceNumber);
        preview.setCurrentDate(currentDate);

        logger.info("LOR preview generated successfully for request ID: {}", requestId);
        return preview;
    }

    /**
     * Generate PDF with custom content
     */
    public String generatePdfWithCustomContent(Long requestId, LorPreviewDto previewDto) {
        logger.info("Generating PDF with custom content for request ID: {}", requestId);

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can generate PDFs");
        }

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Verify professor owns this request
        if (!lorRequest.getProfessor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only generate PDFs for your own requests");
        }

        if (!lorRequest.isApproved()) {
            throw new RuntimeException("Request must be approved before generating PDF");
        }

        // Generate PDF with custom content
        try {
            String referenceNumber = pdfGenerationService.generatePdfWithCustomContent(lorRequest, previewDto);
            logger.info("PDF generated successfully for request ID: {} with reference: {}", requestId, referenceNumber);
            return referenceNumber;
        } catch (IOException e) {
            logger.error("Failed to generate PDF for request ID: {}", requestId, e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    /**
     * Generate default LOR content
     */
    private String generateDefaultContent(LorRequest lorRequest) {
        return String.format(
                "With reference to the above, this is to inform you that Mr. %s bearing " +
                "Class Roll Number %s, Registration Number %s and Examination Roll Number " +
                "%s is a bonafide student of %s, " +
                "Department of Computer Science, St. Xavier's College, Ranchi. He is currently studying in %s " +
                "semester. In %s semester he has to undertake an internship to meet the credit requirement as per " +
                "paper BCA%s.\n\n" +

                "He is keenly interested in undertaking internship in your esteemed organization.\n\n" +

                "We request you kindly to grant him permission to undertake the internship. It is required to be " +
                "ensured that the time slot allotted to him to undergo the internship is not matching with his class " +
                "timings. After successful completion of the internship, he has to submit a project report duly signed " +
                "/ verified by your organization along with attendance report to the Department of Computer " +
                "Science, St. Xavier's College, Ranchi.\n\n" +

                "His character and conduct is good to the best of our knowledge.\n\n" +

                "Thank you in advance.\n" +
                "With regards,",

                lorRequest.getStudentName(),
                lorRequest.getClassRollNumber(),
                lorRequest.getRegistrationNumber(),
                lorRequest.getExaminationNumber(),
                lorRequest.getCourse(),
                lorRequest.getSemester(),
                lorRequest.getSemester(),
                "DSE4A"
        );
    }

    /**
     * Generate St. Xavier's College format reference number
     */
    private String generateSXCReferenceNumber(LorRequest lorRequest) {
        LocalDateTime now = LocalDateTime.now();
        String academicYear = getAcademicYear(now);
        String date = now.format(DateTimeFormatter.ofPattern("d.M.yyyy"));

        return String.format("SXC/BCA/Internship/%s/%s/%s",
                academicYear,
                lorRequest.getClassRollNumber(),
                date);
    }

    /**
     * Get academic year in format 2024-25
     */
    private String getAcademicYear(LocalDateTime date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        // Academic year starts in July (month 7)
        if (month >= 7) {
            return year + "-" + String.valueOf(year + 1).substring(2);
        } else {
            return (year - 1) + "-" + String.valueOf(year).substring(2);
        }
    }

    /**
     * Convert LorRequest entity to DTO
     */
    private LorRequestDto convertToDto(LorRequest lorRequest) {
        LorRequestDto dto = new LorRequestDto();
        dto.setId(lorRequest.getId());
        dto.setProfessorId(lorRequest.getProfessor().getId());
        dto.setProfessorName(lorRequest.getProfessor().getName());
        dto.setProfessorDepartment(lorRequest.getProfessor().getDepartment());
        dto.setStudentName(lorRequest.getStudentName());
        dto.setRegistrationNumber(lorRequest.getRegistrationNumber());
        dto.setExaminationNumber(lorRequest.getExaminationNumber());
        dto.setCourse(lorRequest.getCourse());
        dto.setSemester(lorRequest.getSemester());
        dto.setSession(lorRequest.getSession());
        dto.setClassRollNumber(lorRequest.getClassRollNumber());
        dto.setInstituteCompany(lorRequest.getInstituteCompany());
        dto.setStatus(lorRequest.getStatus());
        dto.setProfessorComments(lorRequest.getProfessorComments());
        dto.setRequestedAt(lorRequest.getRequestedAt());
        dto.setProcessedAt(lorRequest.getProcessedAt());

        // Check if PDF exists
        Optional<PdfDocument> pdfDocument = pdfDocumentRepository.findByLorRequest(lorRequest);
        if (pdfDocument.isPresent()) {
            dto.setHasPdf(true);
            dto.setPdfReferenceNumber(pdfDocument.get().getReferenceNumber());
            dto.setPdfFileName(pdfDocument.get().getFileName());
        } else {
            dto.setHasPdf(false);
        }

        return dto;
    }

    private Object getStudentStatistics(Long studentId) {
        // Implementation for student statistics
        return new Object(); // Placeholder
    }

    private Object getProfessorStatistics(Long professorId) {
        // Implementation for professor statistics
        return new Object(); // Placeholder
    }

    private Object getAdminStatistics() {
        // Implementation for admin statistics
        return new Object(); // Placeholder
    }
}
