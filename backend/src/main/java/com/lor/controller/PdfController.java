package com.lor.controller;

import com.lor.entity.PdfDocument;
import com.lor.service.PdfGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for PDF operations
 */
@RestController
@RequestMapping("/pdf")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private PdfGenerationService pdfGenerationService;

    /**
     * Generate PDF for approved LOR request (Professor only)
     */
    @PostMapping("/generate/{requestId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> generateLorPdf(@PathVariable Long requestId) {
        try {
            logger.info("Generating PDF for LOR request ID: {}", requestId);
            
            PdfDocument pdfDocument = pdfGenerationService.generateLorPdf(requestId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PDF generated successfully");
            response.put("referenceNumber", pdfDocument.getReferenceNumber());
            response.put("fileName", pdfDocument.getFileName());
            response.put("fileSize", pdfDocument.getFileSize());
            response.put("generatedAt", pdfDocument.getGeneratedAt());
            
            logger.info("PDF generated successfully for request ID: {} with reference: {}", 
                    requestId, pdfDocument.getReferenceNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate PDF for request ID {}: {}", requestId, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate PDF");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Download PDF by reference number
     */
    @GetMapping("/download/{referenceNumber}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String referenceNumber) {
        try {
            logger.info("Downloading PDF with reference number: {}", referenceNumber);
            
            Resource resource = pdfGenerationService.downloadPdf(referenceNumber);
            PdfDocument pdfDocument = pdfGenerationService.getPdfByReferenceNumber(referenceNumber);
            
            String contentType = "application/pdf";
            
            logger.info("PDF download initiated for reference: {}", referenceNumber);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + pdfDocument.getFileName() + "\"")
                    .header("X-Reference-Number", referenceNumber)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Failed to download PDF with reference {}: {}", referenceNumber, e.getMessage());
            
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get PDF information by reference number
     */
    @GetMapping("/info/{referenceNumber}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getPdfInfo(@PathVariable String referenceNumber) {
        try {
            PdfDocument pdfDocument = pdfGenerationService.getPdfByReferenceNumber(referenceNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("referenceNumber", pdfDocument.getReferenceNumber());
            response.put("fileName", pdfDocument.getFileName());
            response.put("fileSize", pdfDocument.getFileSize());
            response.put("generatedAt", pdfDocument.getGeneratedAt());
            response.put("generatedBy", pdfDocument.getGeneratedBy().getName());
            response.put("studentName", pdfDocument.getLorRequest().getStudentName());
            response.put("professorName", pdfDocument.getLorRequest().getProfessor().getName());
            response.put("requestId", pdfDocument.getLorRequest().getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get PDF info for reference {}: {}", referenceNumber, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get PDF information");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete PDF by reference number (Admin only)
     */
    @DeleteMapping("/{referenceNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePdf(@PathVariable String referenceNumber) {
        try {
            logger.info("Deleting PDF with reference number: {}", referenceNumber);
            
            pdfGenerationService.deletePdf(referenceNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PDF deleted successfully");
            response.put("referenceNumber", referenceNumber);
            
            logger.info("PDF deleted successfully: {}", referenceNumber);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete PDF with reference {}: {}", referenceNumber, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete PDF");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Preview PDF metadata without downloading
     */
    @GetMapping("/preview/{referenceNumber}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<?> previewPdf(@PathVariable String referenceNumber) {
        try {
            PdfDocument pdfDocument = pdfGenerationService.getPdfByReferenceNumber(referenceNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("referenceNumber", pdfDocument.getReferenceNumber());
            response.put("fileName", pdfDocument.getFileName());
            response.put("fileSize", pdfDocument.getFileSize());
            response.put("generatedAt", pdfDocument.getGeneratedAt());
            
            // LOR Request details
            Map<String, Object> lorDetails = new HashMap<>();
            lorDetails.put("studentName", pdfDocument.getLorRequest().getStudentName());
            lorDetails.put("registrationNumber", pdfDocument.getLorRequest().getRegistrationNumber());
            lorDetails.put("examinationNumber", pdfDocument.getLorRequest().getExaminationNumber());
            lorDetails.put("course", pdfDocument.getLorRequest().getCourse());
            lorDetails.put("semester", pdfDocument.getLorRequest().getSemester());
            lorDetails.put("session", pdfDocument.getLorRequest().getSession());
            lorDetails.put("instituteCompany", pdfDocument.getLorRequest().getInstituteCompany());
            lorDetails.put("professorName", pdfDocument.getLorRequest().getProfessor().getName());
            lorDetails.put("professorDepartment", pdfDocument.getLorRequest().getProfessor().getDepartment());
            lorDetails.put("requestedAt", pdfDocument.getLorRequest().getRequestedAt());
            lorDetails.put("processedAt", pdfDocument.getLorRequest().getProcessedAt());
            
            response.put("lorRequest", lorDetails);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to preview PDF with reference {}: {}", referenceNumber, e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to preview PDF");
            error.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
}
