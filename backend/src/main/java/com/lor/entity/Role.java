package com.lor.entity;

/**
 * Enum representing user roles in the LOR Management System
 */
public enum Role {
    ADMIN("Admin"),
    STUDENT("Student"), 
    PROFESSOR("Professor");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
