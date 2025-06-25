package com.lor.service;

// Note: Using fully qualified name for iText PdfDocument to avoid conflict with our entity
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.lor.dto.LorPreviewDto;
import com.lor.entity.*;
import com.lor.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for PDF generation and management
 */
@Service
@Transactional
public class PdfGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(PdfGenerationService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private LorRequestRepository lorRequestRepository;

    @Autowired
    private PdfDocumentRepository pdfDocumentRepository;

    @Autowired
    private AuthService authService;

    /**
     * Generate PDF for approved LOR request
     */
    public com.lor.entity.PdfDocument generateLorPdf(Long requestId) throws IOException {
        logger.info("Generating PDF for LOR request ID: {}", requestId);

        User currentUser = authService.getCurrentUser();
        if (!currentUser.isProfessor()) {
            throw new RuntimeException("Only professors can generate LOR PDFs");
        }

        LorRequest lorRequest = lorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LOR request not found"));

        // Verify professor owns this request
        if (!lorRequest.getProfessor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only generate PDFs for your own requests");
        }

        if (!lorRequest.isApproved()) {
            throw new RuntimeException("Can only generate PDF for approved requests");
        }

        // Check if PDF already exists
        Optional<com.lor.entity.PdfDocument> existingPdf = pdfDocumentRepository.findByLorRequest(lorRequest);
        if (existingPdf.isPresent()) {
            logger.info("PDF already exists for request ID: {}", requestId);
            return existingPdf.get();
        }

        // Generate unique reference number
        String referenceNumber = generateReferenceNumber();

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate file name
        String fileName = String.format("LOR_%s_%s_%s.pdf", 
                lorRequest.getExaminationNumber(),
                lorRequest.getProfessor().getUserId(),
                referenceNumber);

        Path filePath = uploadPath.resolve(fileName);

        // Generate PDF content
        generatePdfContent(lorRequest, referenceNumber, filePath.toString());

        // Get file size
        long fileSize = Files.size(filePath);

        // Save PDF document metadata
        com.lor.entity.PdfDocument pdfDocumentEntity = new com.lor.entity.PdfDocument();
        pdfDocumentEntity.setLorRequest(lorRequest);
        pdfDocumentEntity.setReferenceNumber(referenceNumber);
        pdfDocumentEntity.setFilePath(filePath.toString());
        pdfDocumentEntity.setFileName(fileName);
        pdfDocumentEntity.setFileSize(fileSize);
        pdfDocumentEntity.setGeneratedBy(currentUser);
        pdfDocumentEntity.setGeneratedAt(LocalDateTime.now());

        com.lor.entity.PdfDocument savedPdf = pdfDocumentRepository.save(pdfDocumentEntity);

        logger.info("PDF generated successfully for request ID: {} with reference: {}", 
                requestId, referenceNumber);

        return savedPdf;
    }

    /**
     * Generate PDF content using iText with St. Xavier's College format
     */
    private void generatePdfContent(LorRequest lorRequest, String referenceNumber, String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Set fonts
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont smallFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // College Header
        Paragraph collegeHeader = new Paragraph("DEPARTMENT OF COMPUTER SCIENCE")
                .setFont(titleFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(3);
        document.add(collegeHeader);

        Paragraph collegeName = new Paragraph("ST. XAVIER'S COLLEGE")
                .setFont(headerFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(1);
        document.add(collegeName);

        Paragraph collegeAffiliation = new Paragraph("(AFFILIATED TO RANCHI UNIVERSITY, RANCHI)")
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(1);
        document.add(collegeAffiliation);

        Paragraph collegeLocation = new Paragraph("RANCHI, JHARKHAND")
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);
        document.add(collegeLocation);

        // Contact details (right aligned)
        Paragraph contactDetails = new Paragraph(
                "Tel: 0651-2214 301, 2214 935\n" +
                "Fax: 0651-2207 672\n" +
                "E-Mail: info@sxcran.org\n" +
                "Website: www.sxcran.org")
                .setFont(smallFont)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(-70)
                .setMarginBottom(20);
        document.add(contactDetails);

        // Generate reference number in St. Xavier's format
        String sxcReferenceNumber = generateSXCReferenceNumber(lorRequest);
        Paragraph refNumber = new Paragraph("Reference Number: - " + sxcReferenceNumber)
                .setFont(normalFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
        document.add(refNumber);

        // Recipient Address
        Paragraph toAddress = new Paragraph("To,")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(3);
        document.add(toAddress);

        Paragraph recipientDetails = new Paragraph(
                "The General Manager\n" +
                "IT department\n" +
                lorRequest.getInstituteCompany() + ",\n" +
                "Ranchi")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(12);
        document.add(recipientDetails);

        // Subject
        Paragraph subject = new Paragraph("Sub: - Permission regarding internship in your esteemed organization.")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(10);
        document.add(subject);

        // Salutation
        Paragraph greeting = new Paragraph("Dear Sir / Madam,")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(10);
        document.add(greeting);

        // Main content in St. Xavier's format
        String content = String.format(
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
                "DSE4A" // Changed from DSEA to DSE4A
        );

        Paragraph mainContent = new Paragraph(content)
                .setFont(normalFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(25);
        document.add(mainContent);

        // Signature section
        Paragraph signatureLine = new Paragraph("_________________________")
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(3);
        document.add(signatureLine);

        // Professor name in parentheses
        Paragraph professorNameSig = new Paragraph("(" + lorRequest.getProfessor().getName() + ")")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(2);
        document.add(professorNameSig);

        // Professor designation
        Paragraph professorDesignation = new Paragraph("Assistant Professor,")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(2);
        document.add(professorDesignation);

        // Department
        Paragraph professorDept = new Paragraph("Department of " + lorRequest.getProfessor().getDepartment())
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(2);
        document.add(professorDept);

        // College name
        Paragraph collegeNameSig = new Paragraph("St. Xavier's College, Ranchi.")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(30);
        document.add(collegeNameSig);

        document.close();
    }

    /**
     * Generate unique reference number
     */
    private String generateReferenceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "LOR" + timestamp + uuid;
    }

    /**
     * Generate St. Xavier's College format reference number
     * Format: SXC/BCA/Internship/2024-25/ClassRollNumber/Date
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
     * Generate PDF with custom content from preview
     */
    public String generatePdfWithCustomContent(LorRequest lorRequest, LorPreviewDto previewDto) throws IOException {
        logger.info("Generating PDF with custom content for request ID: {}", lorRequest.getId());

        // Check if PDF already exists
        Optional<com.lor.entity.PdfDocument> existingPdf = pdfDocumentRepository.findByLorRequest(lorRequest);
        if (existingPdf.isPresent()) {
            logger.info("PDF already exists for request ID: {}", lorRequest.getId());
            return existingPdf.get().getReferenceNumber();
        }

        // Generate unique reference number
        String referenceNumber = generateReferenceNumber();

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate file name
        String fileName = String.format("LOR_%s_%s_%s.pdf",
                lorRequest.getExaminationNumber(),
                lorRequest.getProfessor().getUserId(),
                referenceNumber);

        Path filePath = uploadPath.resolve(fileName);

        // Generate PDF content with custom data
        generatePdfContentWithCustomData(previewDto, filePath.toString());

        // Get file size
        long fileSize = Files.size(filePath);

        // Save PDF document metadata
        com.lor.entity.PdfDocument pdfDocumentEntity = new com.lor.entity.PdfDocument();
        pdfDocumentEntity.setLorRequest(lorRequest);
        pdfDocumentEntity.setReferenceNumber(referenceNumber);
        pdfDocumentEntity.setFilePath(filePath.toString());
        pdfDocumentEntity.setFileName(fileName);
        pdfDocumentEntity.setFileSize(fileSize);
        pdfDocumentEntity.setGeneratedBy(authService.getCurrentUser());
        pdfDocumentEntity.setGeneratedAt(LocalDateTime.now());

        pdfDocumentRepository.save(pdfDocumentEntity);

        logger.info("PDF generated successfully for request ID: {} with reference: {}",
                lorRequest.getId(), referenceNumber);

        return referenceNumber;
    }

    /**
     * Generate PDF content with custom data from preview
     */
    private void generatePdfContentWithCustomData(LorPreviewDto previewDto, String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Set fonts
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont smallFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // College Header
        Paragraph collegeHeader = new Paragraph("DEPARTMENT OF COMPUTER SCIENCE")
                .setFont(titleFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(3);
        document.add(collegeHeader);

        Paragraph collegeName = new Paragraph("ST. XAVIER'S COLLEGE")
                .setFont(headerFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(1);
        document.add(collegeName);

        Paragraph collegeAffiliation = new Paragraph("(AFFILIATED TO RANCHI UNIVERSITY, RANCHI)")
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(1);
        document.add(collegeAffiliation);

        Paragraph collegeLocation = new Paragraph("RANCHI, JHARKHAND")
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);
        document.add(collegeLocation);

        // Contact details (right aligned)
        Paragraph contactDetails = new Paragraph(
                "Tel: 0651-2214 301, 2214 935\n" +
                "Fax: 0651-2207 672\n" +
                "E-Mail: info@sxcran.org\n" +
                "Website: www.sxcran.org")
                .setFont(smallFont)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(-70)
                .setMarginBottom(20);
        document.add(contactDetails);

        // Reference number
        Paragraph refNumber = new Paragraph("Reference Number: - " + previewDto.getReferenceNumber())
                .setFont(normalFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
        document.add(refNumber);

        // Recipient Address
        Paragraph toAddress = new Paragraph("To,")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(3);
        document.add(toAddress);

        Paragraph recipientDetails = new Paragraph(
                previewDto.getRecipientTitle() + "\n" +
                previewDto.getRecipientDepartment() + "\n" +
                previewDto.getRecipientCompany() + ",\n" +
                previewDto.getRecipientLocation())
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(12);
        document.add(recipientDetails);

        // Subject
        Paragraph subject = new Paragraph("Sub: - " + previewDto.getSubject())
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(10);
        document.add(subject);

        // Salutation
        Paragraph greeting = new Paragraph(previewDto.getSalutation())
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(10);
        document.add(greeting);

        // Main content (using custom content from preview)
        Paragraph mainContent = new Paragraph(previewDto.getMainContent())
                .setFont(normalFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(25);
        document.add(mainContent);

        // Signature section
        Paragraph signatureLine = new Paragraph("_________________________")
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(3);
        document.add(signatureLine);

        // Professor name in parentheses
        Paragraph professorNameSig = new Paragraph("(" + previewDto.getProfessorName() + ")")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(2);
        document.add(professorNameSig);

        // Professor designation
        Paragraph professorDesignation = new Paragraph(previewDto.getProfessorDesignation() + ",")
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(2);
        document.add(professorDesignation);

        // Department
        Paragraph professorDept = new Paragraph("Department of " + previewDto.getProfessorDepartment())
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(2);
        document.add(professorDept);

        // College name
        Paragraph collegeNameSig = new Paragraph("St. Xavier's College, Ranchi.")
                .setFont(normalFont)
                .setFontSize(11);
        document.add(collegeNameSig);

        document.close();
    }

    /**
     * Download PDF by reference number
     */
    public Resource downloadPdf(String referenceNumber) throws MalformedURLException {
        logger.info("Downloading PDF with reference number: {}", referenceNumber);

        User currentUser = authService.getCurrentUser();

        com.lor.entity.PdfDocument pdfDocument = pdfDocumentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("PDF not found with reference number: " + referenceNumber));

        // Check access permissions
        if (currentUser.isStudent()) {
            if (!pdfDocument.getLorRequest().getStudent().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only download your own LOR PDFs");
            }
        } else if (currentUser.isProfessor()) {
            if (!pdfDocument.getGeneratedBy().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You can only download PDFs you generated");
            }
        }
        // Admin can download any PDF

        Path filePath = Paths.get(pdfDocument.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            logger.info("PDF downloaded successfully: {}", referenceNumber);
            return resource;
        } else {
            throw new RuntimeException("PDF file not found or not readable");
        }
    }

    /**
     * Get PDF document by reference number
     */
    public com.lor.entity.PdfDocument getPdfByReferenceNumber(String referenceNumber) {
        return pdfDocumentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("PDF not found with reference number: " + referenceNumber));
    }

    /**
     * Delete PDF (Admin only or internal service call)
     */
    public void deletePdf(String referenceNumber) throws IOException {
        deletePdfInternal(referenceNumber, true);
    }

    /**
     * Delete PDF without admin check (for internal service calls)
     */
    public void deletePdfInternal(String referenceNumber, boolean checkAdminRole) throws IOException {
        if (checkAdminRole) {
            User currentUser = authService.getCurrentUser();
            if (!currentUser.isAdmin()) {
                throw new RuntimeException("Only admin can delete PDFs");
            }
        }

        com.lor.entity.PdfDocument pdfDocument = pdfDocumentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("PDF not found"));

        // Delete physical file
        Path filePath = Paths.get(pdfDocument.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete database record
        pdfDocumentRepository.delete(pdfDocument);

        logger.info("PDF deleted successfully: {}", referenceNumber);
    }
}
