package com.lor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for LOR preview and editing functionality
 */
public class LorPreviewDto {
    
    // Student Information
    @NotBlank(message = "Student name is required")
    @Size(max = 100, message = "Student name must not exceed 100 characters")
    private String studentName;
    
    @NotBlank(message = "Class roll number is required")
    @Size(max = 50, message = "Class roll number must not exceed 50 characters")
    private String classRollNumber;
    
    @NotBlank(message = "Registration number is required")
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    private String registrationNumber;
    
    @NotBlank(message = "Examination number is required")
    @Size(max = 50, message = "Examination number must not exceed 50 characters")
    private String examinationNumber;
    
    @NotBlank(message = "Course is required")
    @Size(max = 100, message = "Course must not exceed 100 characters")
    private String course;
    
    @NotBlank(message = "Semester is required")
    @Size(max = 20, message = "Semester must not exceed 20 characters")
    private String semester;
    
    @NotBlank(message = "Session is required")
    @Size(max = 50, message = "Session must not exceed 50 characters")
    private String session;
    
    @NotBlank(message = "Institute/Company is required")
    @Size(max = 200, message = "Institute/Company must not exceed 200 characters")
    private String instituteCompany;
    
    // LOR Content
    @NotBlank(message = "Recipient title is required")
    @Size(max = 100, message = "Recipient title must not exceed 100 characters")
    private String recipientTitle;

    @NotBlank(message = "Recipient department is required")
    @Size(max = 100, message = "Recipient department must not exceed 100 characters")
    private String recipientDepartment;

    @NotBlank(message = "Recipient company is required")
    @Size(max = 200, message = "Recipient company must not exceed 200 characters")
    private String recipientCompany;

    @NotBlank(message = "Recipient location is required")
    @Size(max = 100, message = "Recipient location must not exceed 100 characters")
    private String recipientLocation;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @NotBlank(message = "Salutation is required")
    @Size(max = 50, message = "Salutation must not exceed 50 characters")
    private String salutation;

    @NotBlank(message = "Main content is required")
    @Size(max = 2000, message = "Main content must not exceed 2000 characters")
    private String mainContent;

    @Size(max = 50, message = "Paper code must not exceed 50 characters")
    private String paperCode;
    
    // Professor Information
    @NotBlank(message = "Professor name is required")
    @Size(max = 100, message = "Professor name must not exceed 100 characters")
    private String professorName;
    
    @NotBlank(message = "Professor department is required")
    @Size(max = 100, message = "Professor department must not exceed 100 characters")
    private String professorDepartment;
    
    @Size(max = 50, message = "Professor designation must not exceed 50 characters")
    private String professorDesignation;
    
    // Reference Information
    private String referenceNumber;
    private String currentDate;
    
    // Constructors
    public LorPreviewDto() {}
    
    public LorPreviewDto(String studentName, String classRollNumber, String registrationNumber,
                        String examinationNumber, String course, String semester, String session,
                        String instituteCompany, String recipientTitle, String recipientDepartment,
                        String recipientCompany, String recipientLocation, String subject, String salutation,
                        String mainContent, String paperCode, String professorName, String professorDepartment,
                        String professorDesignation, String referenceNumber, String currentDate) {
        this.studentName = studentName;
        this.classRollNumber = classRollNumber;
        this.registrationNumber = registrationNumber;
        this.examinationNumber = examinationNumber;
        this.course = course;
        this.semester = semester;
        this.session = session;
        this.instituteCompany = instituteCompany;
        this.recipientTitle = recipientTitle;
        this.recipientDepartment = recipientDepartment;
        this.recipientCompany = recipientCompany;
        this.recipientLocation = recipientLocation;
        this.subject = subject;
        this.salutation = salutation;
        this.mainContent = mainContent;
        this.paperCode = paperCode;
        this.professorName = professorName;
        this.professorDepartment = professorDepartment;
        this.professorDesignation = professorDesignation;
        this.referenceNumber = referenceNumber;
        this.currentDate = currentDate;
    }
    
    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getClassRollNumber() { return classRollNumber; }
    public void setClassRollNumber(String classRollNumber) { this.classRollNumber = classRollNumber; }
    
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
    
    public String getInstituteCompany() { return instituteCompany; }
    public void setInstituteCompany(String instituteCompany) { this.instituteCompany = instituteCompany; }

    public String getRecipientTitle() { return recipientTitle; }
    public void setRecipientTitle(String recipientTitle) { this.recipientTitle = recipientTitle; }

    public String getRecipientDepartment() { return recipientDepartment; }
    public void setRecipientDepartment(String recipientDepartment) { this.recipientDepartment = recipientDepartment; }

    public String getRecipientCompany() { return recipientCompany; }
    public void setRecipientCompany(String recipientCompany) { this.recipientCompany = recipientCompany; }

    public String getRecipientLocation() { return recipientLocation; }
    public void setRecipientLocation(String recipientLocation) { this.recipientLocation = recipientLocation; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSalutation() { return salutation; }
    public void setSalutation(String salutation) { this.salutation = salutation; }

    public String getMainContent() { return mainContent; }
    public void setMainContent(String mainContent) { this.mainContent = mainContent; }
    
    public String getPaperCode() { return paperCode; }
    public void setPaperCode(String paperCode) { this.paperCode = paperCode; }
    
    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }
    
    public String getProfessorDepartment() { return professorDepartment; }
    public void setProfessorDepartment(String professorDepartment) { this.professorDepartment = professorDepartment; }
    
    public String getProfessorDesignation() { return professorDesignation; }
    public void setProfessorDesignation(String professorDesignation) { this.professorDesignation = professorDesignation; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getCurrentDate() { return currentDate; }
    public void setCurrentDate(String currentDate) { this.currentDate = currentDate; }
}
