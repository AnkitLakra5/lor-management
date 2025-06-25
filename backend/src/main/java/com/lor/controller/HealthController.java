package com.lor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Basic health check
     */
    @GetMapping
    public ResponseEntity<?> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "LOR Management System Backend");
        response.put("status", "Running");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Welcome to LOR Management System API");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check database connectivity
            try (Connection connection = dataSource.getConnection()) {
                boolean dbHealthy = connection.isValid(5);
                response.put("database", dbHealthy ? "UP" : "DOWN");
            }
            
            response.put("status", "UP");
            response.put("service", "LOR Management System Backend");
            response.put("timestamp", LocalDateTime.now());
            response.put("uptime", getUptime());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * API information
     */
    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "LOR Management System");
        response.put("description", "Backend API for Letter of Recommendation Management");
        response.put("version", "1.0.0");
        response.put("author", "LOR Management Team");
        response.put("documentation", "/api/swagger-ui.html");
        response.put("endpoints", Map.of(
            "authentication", "/api/auth/*",
            "lor-requests", "/api/lor-requests/*",
            "pdf", "/api/pdf/*",
            "admin", "/api/admin/*"
        ));
        
        return ResponseEntity.ok(response);
    }

    private String getUptime() {
        long uptimeMs = System.currentTimeMillis() - getStartTime();
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        return String.format("%d hours, %d minutes, %d seconds", 
                hours, minutes % 60, seconds % 60);
    }

    private long getStartTime() {
        // This is a simplified version. In a real application, you might want to
        // store the start time when the application starts
        return System.currentTimeMillis() - 
               java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
    }
}
