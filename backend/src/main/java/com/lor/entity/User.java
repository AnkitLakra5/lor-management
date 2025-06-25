package com.lor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing all users in the system (Admin, Student, Professor)
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Student specific fields
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    @Column(name = "registration_number")
    private String registrationNumber;

    @Size(max = 50, message = "Examination number must not exceed 50 characters")
    @Column(name = "examination_number", unique = true)
    private String examinationNumber;

    @Size(max = 255, message = "Course must not exceed 255 characters")
    private String course;

    // Professor specific fields
    @Size(max = 50, message = "User ID must not exceed 50 characters")
    @Column(name = "user_id", unique = true)
    private String userId;

    @Size(max = 255, message = "Department must not exceed 255 characters")
    private String department;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LorRequest> studentRequests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LorRequest> professorRequests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "generatedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PdfDocument> generatedPdfs = new ArrayList<>();

    // Constructors
    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getExaminationNumber() { return examinationNumber; }
    public void setExaminationNumber(String examinationNumber) { this.examinationNumber = examinationNumber; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @JsonIgnore
    public List<LorRequest> getStudentRequests() { return studentRequests; }
    public void setStudentRequests(List<LorRequest> studentRequests) { this.studentRequests = studentRequests; }

    @JsonIgnore
    public List<LorRequest> getProfessorRequests() { return professorRequests; }
    public void setProfessorRequests(List<LorRequest> professorRequests) { this.professorRequests = professorRequests; }

    @JsonIgnore
    public List<PdfDocument> getGeneratedPdfs() { return generatedPdfs; }
    public void setGeneratedPdfs(List<PdfDocument> generatedPdfs) { this.generatedPdfs = generatedPdfs; }

    // Utility methods
    public boolean isStudent() { return role == Role.STUDENT; }
    public boolean isProfessor() { return role == Role.PROFESSOR; }
    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}
