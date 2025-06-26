package io.villapms.villapms.controller;

import io.villapms.villapms.dto.IdentityCreateDto;
import io.villapms.villapms.dto.IdentityUpdateDto;
import io.villapms.villapms.model.Identity.Identity;
import io.villapms.villapms.service.IdentityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/identities")
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createIdentity(@Valid @RequestBody IdentityCreateDto identityDto) {
        try {
            Identity identity = identityService.createIdentity(identityDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Identity created successfully",
                    "identityId", identity.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Identity> getIdentityById(@PathVariable Long id) {
        try {
            Identity identity = identityService.getIdentityById(id);
            return ResponseEntity.ok(identity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Identity>> getAllIdentities(@RequestParam(required = false) Long userId) {
        List<Identity> identities;

        if (userId != null) {
            identities = identityService.getIdentitiesByUserId(userId);
        } else {
            identities = identityService.getAllIdentities();
        }

        return ResponseEntity.ok(identities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateIdentity(
            @PathVariable Long id,
            @Valid @RequestBody IdentityUpdateDto updateDto) {
        try {
            Identity updatedIdentity = identityService.updateIdentity(id, updateDto);
            return ResponseEntity.ok(Map.of(
                    "message", "Identity updated successfully",
                    "identityId", updatedIdentity.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteIdentity(@PathVariable Long id) {
        try {
            identityService.deleteIdentity(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Identity deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}