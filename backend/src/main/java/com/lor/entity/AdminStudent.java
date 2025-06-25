package com.lor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing pre-uploaded student data maintained by admin
 */
@Entity
@Table(name = "admin_students")
@EntityListeners(AuditingEntityListener.class)
public class AdminStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Registration number is required")
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @NotBlank(message = "Examination number is required")
    @Size(max = 50, message = "Examination number must not exceed 50 characters")
    @Column(name = "examination_number", nullable = false, unique = true)
    private String examinationNumber;

    @NotBlank(message = "Course is required")
    @Size(max = 255, message = "Course must not exceed 255 characters")
    @Column(nullable = false)
    private String course;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public AdminStudent() {}

    public AdminStudent(String name, String registrationNumber, String examinationNumber, String course) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.examinationNumber = examinationNumber;
        this.course = course;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getExaminationNumber() { return examinationNumber; }
    public void setExaminationNumber(String examinationNumber) { this.examinationNumber = examinationNumber; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "AdminStudent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", examinationNumber='" + examinationNumber + '\'' +
                ", course='" + course + '\'' +
                '}';
    }
}
