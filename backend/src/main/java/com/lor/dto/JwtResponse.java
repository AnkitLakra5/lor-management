package com.lor.dto;

import com.lor.entity.Role;

/**
 * DTO for JWT authentication response
 */
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private Role role;
    
    // Student specific fields
    private String registrationNumber;
    private String examinationNumber;
    private String course;
    
    // Professor specific fields
    private String userId;
    private String department;

    // Constructors
    public JwtResponse() {}

    public JwtResponse(String accessToken, Long id, String name, String email, Role role) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
