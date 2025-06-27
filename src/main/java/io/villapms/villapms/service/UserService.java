// src/main/java/io/villapms/villapms/service/UserService.java
package io.villapms.villapms.service;

import io.villapms.villapms.dto.UserRegistrationDto;
import io.villapms.villapms.dto.UserUpdateDto;
import io.villapms.villapms.model.User.UserAccount;
import io.villapms.villapms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount registerUser(UserRegistrationDto registrationDto) {
        // Check if email already exists
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        UserAccount user = new UserAccount();
        user.setName(registrationDto.getName());
        user.setSurname(registrationDto.getSurname());
        user.setEmail(registrationDto.getEmail());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setBirthday(registrationDto.getBirthday());

        return userRepository.save(user);
    }

    public UserAccount getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserAccount findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }

    public UserAccount updateUser(Long id, UserUpdateDto updateDto) {
        UserAccount user = getUserById(id);

        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getSurname() != null) {
            user.setSurname(updateDto.getSurname());
        }
        if (updateDto.getEmail() != null) {
            // Check if new email already exists
            userRepository.findByEmail(updateDto.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(id))
                    .ifPresent(existingUser -> {
                        throw new RuntimeException("Email already exists");
                    });
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getBirthday() != null) {
            user.setBirthday(updateDto.getBirthday());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        UserAccount user = getUserById(id);
        userRepository.delete(user);
    }
}