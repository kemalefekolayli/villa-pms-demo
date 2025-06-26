package io.villapms.villapms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("System is running");
    }

    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        // TODO: Implement version info
        return ResponseEntity.ok("1.0.0");
    }
} 