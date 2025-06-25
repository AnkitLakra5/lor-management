package com.lor.security;

import com.lor.entity.Role;
import com.lor.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * UserPrincipal implementation for Spring Security
 */
public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private Boolean isActive;
    
    // Student specific fields
    private String registrationNumber;
    private String examinationNumber;
    private String course;
    
    // Professor specific fields
    private String userId;
    private String department;

    public UserPrincipal(Long id, String name, String email, String password, Role role, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    public static UserPrincipal create(User user) {
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getIsActive()
        );
        
        // Set role-specific fields
        if (user.isStudent()) {
            userPrincipal.setRegistrationNumber(user.getRegistrationNumber());
            userPrincipal.setExaminationNumber(user.getExaminationNumber());
            userPrincipal.setCourse(user.getCourse());
        } else if (user.isProfessor()) {
            userPrincipal.setUserId(user.getUserId());
            userPrincipal.setDepartment(user.getDepartment());
        }
        
        return userPrincipal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive != null && isActive;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

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

    // Utility methods
    public boolean isStudent() { return role == Role.STUDENT; }
    public boolean isProfessor() { return role == Role.PROFESSOR; }
    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}
