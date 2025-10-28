package org.jared.trujillo.dto;

import org.jared.trujillo.model.User;

import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String name;
    private String username;
    private String email;

    public UserResponse(UUID id, String name, String username, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }
}
