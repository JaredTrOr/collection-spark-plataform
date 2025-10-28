package org.jared.trujillo.controller;

import org.jared.trujillo.dto.*;
import org.jared.trujillo.exceptions.ResourceNotFoundException;
import org.jared.trujillo.interfaces.JsonConverter;
import org.jared.trujillo.service.UserService;

import java.util.UUID;

import static spark.Spark.*;

public class UserController {

    private final UserService userService;
    private final JsonConverter json;

    public UserController(UserService userService, JsonConverter jsonConverter) {
        this.userService = userService;
        this.json = jsonConverter;
    }

    public void registerRoutes() {

        get("/:id", (req, res) -> {
            UUID id = UUID.fromString(req.params(":id"));
            UserResponse userResponse = this.userService.getUserById(id);

            res.status(HttpStatus.OK.getStatusCode());
            return this.json.toJson(HttpSimpleResponse.success(userResponse));
        });

        post("", (req, res) -> {
            UserCreateRequest createRequest = this.json.fromJson(req.body(), UserCreateRequest.class);

            UserResponse userResponse = this.userService.registerUser(createRequest);

            res.status(HttpStatus.CREATED.getStatusCode());
            return this.json.toJson(HttpSimpleResponse.success(userResponse, "User created successfully."));
        });

        put("/:id", (req, res) -> {
            UUID id = UUID.fromString(req.params(":id"));
            UserUpdateRequest updateRequest = this.json.fromJson(req.body(), UserUpdateRequest.class);

            UserResponse userResponse = this.userService.updateUser(id, updateRequest);
            res.status(HttpStatus.OK.getStatusCode());
            return json.toJson(HttpSimpleResponse.success(userResponse, "User updated successfully"));

        });

        delete("/:id", (req, res) -> {
            UUID id = UUID.fromString(req.params(":id"));
            this.userService.softDeleteUser(id);

            res.status(HttpStatus.OK.getStatusCode());
            return this.json.toJson(HttpSimpleResponse.success("User deleted successfully."));
        });

        exception(ResourceNotFoundException.class, (exception, req, res) -> {
            res.status(HttpStatus.NOT_FOUND.getStatusCode());
            res.body(json.toJson(HttpSimpleResponse.error(exception.getMessage())));
        });

        exception(IllegalArgumentException.class, (exception, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST.getStatusCode());
            res.body(json.toJson(HttpSimpleResponse.error(exception.getMessage())));
        });
    }

}
