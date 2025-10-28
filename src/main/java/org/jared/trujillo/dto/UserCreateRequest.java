package org.jared.trujillo.dto;

public class UserCreateRequest {
    private String name;
    private String username;
    private String email;
    private String password;

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
