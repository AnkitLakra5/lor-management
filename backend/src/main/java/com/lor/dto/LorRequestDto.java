package com.lor.dto;

import com.lor.entity.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for LOR request operations
 */
public class LorRequestDto {

    private Long id;

    @NotNull(message = "Professor ID is required")
    private Long professorId;

    private String professorName;
    private String professorDepartment;

    // Auto-filled student data (read-only)
    private String studentName;
    private String registrationNumber;
    private String examinationNumber;
    private String course;

    // Form fields
    @NotBlank(message = "Semester is required")
    @Size(max = 50, message = "Semester must not exceed 50 characters")
    private String semester;

    @NotBlank(message = "Session is required")
    @Size(max = 50, message = "Session must not exceed 50 characters")
    private String session;

    @NotBlank(message = "Class roll number is required")
    @Size(max = 50, message = "Class roll number must not exceed 50 characters")
    private String classRollNumber;

    @NotBlank(message = "Institute/Company is required")
    @Size(max = 255, message = "Institute/Company must not exceed 255 characters")
    private String instituteCompany;

    // Request status and processing
    private RequestStatus status;
    private String professorComments;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    // PDF information
    private String pdfReferenceNumber;
    private String pdfFileName;
    private boolean hasPdf;

    // Constructors
    public LorRequestDto() {}

    public LorRequestDto(Long professorId, String semester, String session, String classRollNumber, String instituteCompany) {
        this.professorId = professorId;
        this.semester = semester;
        this.session = session;
        this.classRollNumber = classRollNumber;
        this.instituteCompany = instituteCompany;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }

    public String getProfessorDepartment() { return professorDepartment; }
    public void setProfessorDepartment(String professorDepartment) { this.professorDepartment = professorDepartment; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getExaminationNumber() { return examinationNumber; }
    public void setExaminationNumber(String examinationNumber) { this.examinationNumber = examinationNumber; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public String getClassRollNumber() { return classRollNumber; }
    public void setClassRollNumber(String classRollNumber) { this.classRollNumber = classRollNumber; }

    public String getInstituteCompany() { return instituteCompany; }
    public void setInstituteCompany(String instituteCompany) { this.instituteCompany = instituteCompany; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getProfessorComments() { return professorComments; }
    public void setProfessorComments(String professorComments) { this.professorComments = professorComments; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public String getPdfReferenceNumber() { return pdfReferenceNumber; }
    public void setPdfReferenceNumber(String pdfReferenceNumber) { this.pdfReferenceNumber = pdfReferenceNumber; }

    public String getPdfFileName() { return pdfFileName; }
    public void setPdfFileName(String pdfFileName) { this.pdfFileName = pdfFileName; }

    public boolean isHasPdf() { return hasPdf; }
    public void setHasPdf(boolean hasPdf) { this.hasPdf = hasPdf; }

    @Override
    public String toString() {
        return "LorRequestDto{" +
                "id=" + id +
                ", professorId=" + professorId +
                ", professorName='" + professorName + '\'' +
                ", studentName='" + studentName + '\'' +
                ", semester='" + semester + '\'' +
                ", session='" + session + '\'' +
                ", classRollNumber='" + classRollNumber + '\'' +
                ", instituteCompany='" + instituteCompany + '\'' +
                ", status=" + status +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
