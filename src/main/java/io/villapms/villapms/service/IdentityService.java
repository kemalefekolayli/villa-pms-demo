
package io.villapms.villapms.service;

import io.villapms.villapms.dto.IdentityCreateDto;
import io.villapms.villapms.dto.IdentityUpdateDto;
import io.villapms.villapms.model.Identity.Identity;
import io.villapms.villapms.repository.IdentityRepository;
import io.villapms.villapms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final UserRepository userRepository;

    public IdentityService(IdentityRepository identityRepository, UserRepository userRepository) {
        this.identityRepository = identityRepository;
        this.userRepository = userRepository;
    }

    public Identity createIdentity(IdentityCreateDto identityDto) {
        // Verify user exists
        userRepository.findById(identityDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + identityDto.getUserId()));

        // Check for duplicate identity number
        if (identityDto.getIdentityNumber() != null &&
                identityRepository.existsByIdentityNumber(identityDto.getIdentityNumber())) {
            throw new RuntimeException("Identity number already exists");
        }

        // Check for duplicate passport number
        if (identityDto.getPassportNumber() != null &&
                identityRepository.existsByPassportNumber(identityDto.getPassportNumber())) {
            throw new RuntimeException("Passport number already exists");
        }

        Identity identity = new Identity();
        identity.setName(identityDto.getName());
        identity.setSurname(identityDto.getSurname());
        identity.setPhone(identityDto.getPhone());
        identity.setIdentityNumber(identityDto.getIdentityNumber());
        identity.setPassportNumber(identityDto.getPassportNumber());
        identity.setType(identityDto.getType());
        identity.setIssueDate(identityDto.getIssueDate());
        identity.setExpiryDate(identityDto.getExpiryDate());
        identity.setIssuingAuthority(identityDto.getIssuingAuthority());
        identity.setUserId(identityDto.getUserId());

        return identityRepository.save(identity);
    }

    public Identity getIdentityById(Long id) {
        return identityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Identity not found with id: " + id));
    }

    public List<Identity> getAllIdentities() {
        return identityRepository.findAll();
    }

    public List<Identity> getIdentitiesByUserId(Long userId) {
        return identityRepository.findByUserId(userId);
    }

    public Identity updateIdentity(Long id, IdentityUpdateDto updateDto) {
        Identity identity = getIdentityById(id);

        if (updateDto.getName() != null) {
            identity.setName(updateDto.getName());
        }
        if (updateDto.getSurname() != null) {
            identity.setSurname(updateDto.getSurname());
        }
        if (updateDto.getPhone() != null) {
            identity.setPhone(updateDto.getPhone());
        }
        if (updateDto.getIdentityNumber() != null) {
            // Check for duplicates (excluding current record)
            identityRepository.findByIdentityNumber(updateDto.getIdentityNumber())
                    .filter(existingIdentity -> !existingIdentity.getId().equals(id))
                    .ifPresent(existingIdentity -> {
                        throw new RuntimeException("Identity number already exists");
                    });
            identity.setIdentityNumber(updateDto.getIdentityNumber());
        }
        if (updateDto.getPassportNumber() != null) {
            // Check for duplicates (excluding current record)
            identityRepository.findByPassportNumber(updateDto.getPassportNumber())
                    .filter(existingIdentity -> !existingIdentity.getId().equals(id))
                    .ifPresent(existingIdentity -> {
                        throw new RuntimeException("Passport number already exists");
                    });
            identity.setPassportNumber(updateDto.getPassportNumber());
        }
        if (updateDto.getType() != null) {
            identity.setType(updateDto.getType());
        }
        if (updateDto.getIssueDate() != null) {
            identity.setIssueDate(updateDto.getIssueDate());
        }
        if (updateDto.getExpiryDate() != null) {
            identity.setExpiryDate(updateDto.getExpiryDate());
        }
        if (updateDto.getIssuingAuthority() != null) {
            identity.setIssuingAuthority(updateDto.getIssuingAuthority());
        }

        return identityRepository.save(identity);
    }

    public void deleteIdentity(Long id) {
        Identity identity = getIdentityById(id);
        identityRepository.delete(identity);
    }
}