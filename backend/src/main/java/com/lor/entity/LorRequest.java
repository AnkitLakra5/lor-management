package com.lor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a Letter of Recommendation request
 */
@Entity
@Table(name = "lor_requests")
@EntityListeners(AuditingEntityListener.class)
public class LorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @NotNull(message = "Professor is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    // Auto-filled student data
    @NotBlank(message = "Student name is required")
    @Size(max = 255, message = "Student name must not exceed 255 characters")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @NotBlank(message = "Registration number is required")
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @NotBlank(message = "Examination number is required")
    @Size(max = 50, message = "Examination number must not exceed 50 characters")
    @Column(name = "examination_number", nullable = false)
    private String examinationNumber;

    @NotBlank(message = "Course is required")
    @Size(max = 255, message = "Course must not exceed 255 characters")
    @Column(nullable = false)
    private String course;

    // Form fields
    @NotBlank(message = "Semester is required")
    @Size(max = 50, message = "Semester must not exceed 50 characters")
    @Column(nullable = false)
    private String semester;

    @NotBlank(message = "Session is required")
    @Size(max = 50, message = "Session must not exceed 50 characters")
    @Column(nullable = false)
    private String session;

    @NotBlank(message = "Class roll number is required")
    @Size(max = 50, message = "Class roll number must not exceed 50 characters")
    @Column(name = "class_roll_number", nullable = false)
    private String classRollNumber;

    @NotBlank(message = "Institute/Company is required")
    @Size(max = 255, message = "Institute/Company must not exceed 255 characters")
    @Column(name = "institute_company", nullable = false)
    private String instituteCompany;

    // Request status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Size(max = 1000, message = "Professor comments must not exceed 1000 characters")
    @Column(name = "professor_comments")
    private String professorComments;

    // Timestamps
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship with PDF document
    @OneToOne(mappedBy = "lorRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PdfDocument pdfDocument;

    // Constructors
    public LorRequest() {
        this.requestedAt = LocalDateTime.now();
    }

    public LorRequest(User student, User professor, String semester, String session, String classRollNumber, String instituteCompany) {
        this();
        this.student = student;
        this.professor = professor;
        this.semester = semester;
        this.session = session;
        this.classRollNumber = classRollNumber;
        this.instituteCompany = instituteCompany;
        
        // Auto-fill student data
        if (student != null) {
            this.studentName = student.getName();
            this.registrationNumber = student.getRegistrationNumber();
            this.examinationNumber = student.getExaminationNumber();
            this.course = student.getCourse();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public User getProfessor() { return professor; }
    public void setProfessor(User professor) { this.professor = professor; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PdfDocument getPdfDocument() { return pdfDocument; }
    public void setPdfDocument(PdfDocument pdfDocument) { this.pdfDocument = pdfDocument; }

    // Utility methods
    public boolean isPending() { return status == RequestStatus.PENDING; }
    public boolean isApproved() { return status == RequestStatus.APPROVED; }
    public boolean isRejected() { return status == RequestStatus.REJECTED; }

    public void approve(String comments) {
        this.status = RequestStatus.APPROVED;
        this.professorComments = comments;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(String comments) {
        this.status = RequestStatus.REJECTED;
        this.professorComments = comments;
        this.processedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "LorRequest{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", semester='" + semester + '\'' +
                ", session='" + session + '\'' +
                ", instituteCompany='" + instituteCompany + '\'' +
                ", status=" + status +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
