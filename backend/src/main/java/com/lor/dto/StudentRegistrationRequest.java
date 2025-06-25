package com.lor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for student registration requests
 */
public class StudentRegistrationRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Registration number is required")
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    private String registrationNumber;

    @NotBlank(message = "Examination number is required")
    @Size(max = 50, message = "Examination number must not exceed 50 characters")
    private String examinationNumber;

    @NotBlank(message = "Course is required")
    @Size(max = 255, message = "Course must not exceed 255 characters")
    private String course;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;

    // Constructors
    public StudentRegistrationRequest() {}

    public StudentRegistrationRequest(String name, String registrationNumber, String examinationNumber, 
                                    String course, String email, String password) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.examinationNumber = examinationNumber;
        this.course = course;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getExaminationNumber() { return examinationNumber; }
    public void setExaminationNumber(String examinationNumber) { this.examinationNumber = examinationNumber; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "StudentRegistrationRequest{" +
                "name='" + name + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", examinationNumber='" + examinationNumber + '\'' +
                ", course='" + course + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
