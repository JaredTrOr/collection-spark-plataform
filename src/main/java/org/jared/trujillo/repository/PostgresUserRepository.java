package org.jared.trujillo.repository;

import org.jared.trujillo.db.PostgresDatabaseConnection;
import org.jared.trujillo.dto.Page;
import org.jared.trujillo.interfaces.UserRepository;
import org.jared.trujillo.model.User;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresUserRepository implements UserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND deleted_at IS NULL";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email ILIKE ? AND deleted_at IS NULL";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }

        return Optional.empty();
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (username, name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getObject("id", UUID.class));
                    user.setCreatedAt(generatedKeys.getTimestamp("created_at").toInstant());
                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Page<User> findAllPaginated(int page, int limit) {
        return null;
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> update(UUID uuid, User user) {
        String sql = "UPDATE users SET name = ?, username = ?, email = ? WHERE id = ?";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setObject(4, uuid);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                user.setId(uuid);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating item", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID uuid) {
        return false;
    }

    @Override
    public boolean softDelete(UUID uuid) {
        String sql = "UPDATE users SET deleted_at = ? WHERE id = ? AND deleted_at IS NULL";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.from(Instant.now()));
            stmt.setObject(2, uuid);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error soft-deleting user", e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getObject("id", UUID.class));
        user.setName(rs.getString("name"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at").toInstant());

        Timestamp deletedAt = rs.getTimestamp("deleted_at");
        if (deletedAt != null) {
            user.setDeletedAt(deletedAt.toInstant());
        }
        return user;
    }
}
