package org.jared.trujillo.service;

import org.jared.trujillo.dto.UserCreateRequest;
import org.jared.trujillo.dto.UserResponse;
import org.jared.trujillo.dto.UserUpdateRequest;
import org.jared.trujillo.exceptions.ResourceNotFoundException;
import org.jared.trujillo.interfaces.UserRepository;
import org.jared.trujillo.model.User;

import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserResponse.fromUser(user);
    }

    public UserResponse registerUser(UserCreateRequest createRequest) {

        if (createRequest.getName() == null || createRequest.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        if (createRequest.getUsername() == null || createRequest.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }
        if (createRequest.getEmail() == null || createRequest.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }
        if (createRequest.getPassword() == null || createRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        if (userRepository.findByUsername(createRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.findByEmail(createRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User newUser = new User();
        newUser.setName(createRequest.getName());
        newUser.setUsername(createRequest.getUsername());
        newUser.setEmail(createRequest.getEmail());
        newUser.setPassword(createRequest.getPassword());

        User savedUser = userRepository.create(newUser);

        return UserResponse.fromUser(savedUser);
    }

    public UserResponse updateUser(UUID id, UserUpdateRequest updateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String newName = updateRequest.getName();
        String newUsername = updateRequest.getUsername();
        String newEmail = updateRequest.getEmail();

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }

        if (!newUsername.equalsIgnoreCase(existingUser.getUsername())) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                throw new IllegalArgumentException("Username is already taken.");
            }
        }

        if (!newEmail.equalsIgnoreCase(existingUser.getEmail())) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("Email is already in use by another account.");
            }
        }

        existingUser.setName(newName);
        existingUser.setUsername(newUsername);
        existingUser.setEmail(newEmail);

        User updatedUser = userRepository.update(id, existingUser)
                .orElseThrow(() -> new RuntimeException("Update failed unexpectedly."));

        return UserResponse.fromUser(updatedUser);
    }

    public void softDeleteUser(UUID id) {
        boolean wasDeleted = userRepository.softDelete(id);

        if (!wasDeleted) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }
}
