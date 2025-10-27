package org.jared.trujillo.repository;

import org.jared.trujillo.db.PostgresDatabaseConnection;
import org.jared.trujillo.dto.Page;
import org.jared.trujillo.interfaces.ItemRepository;
import org.jared.trujillo.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresItemRepository implements ItemRepository {

    @Override
    public Item create(Item item) {
        String sql = "INSERT INTO items (name, description, price, inventory) VALUES (?, ?, ?, ?)";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setInt(4, item.getInventory());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getObject(1, UUID.class));
                    return item;
                } else {
                    throw new SQLException("Creating item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating item", e);
        }
    }

    // Add implementation later
    @Override
    public List<Item> findAll() {
        return List.of();
    }

    @Override
    public Page<Item> findAllPaginated(int page, int limit) {
        int offset = (page - 1) * limit;

        String dataSql = "SELECT * FROM items ORDER BY name LIMIT ? OFFSET ?";
        List<Item> items = new ArrayList<>();

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(dataSql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching paginated items", e);
        }

        String countSql = "SELECT COUNT(*) FROM items";
        long totalItems = 0;

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countSql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalItems = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting items", e);
        }

        return new Page<>(items, page, limit, totalItems);
    }

    @Override
    public Optional<Item> findById(UUID id) {
        String sql = "SELECT * FROM items WHERE id = ?";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Use setObject for UUIDs in prepared statements
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding item by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> update(UUID id, Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, price = ?, inventory = ? WHERE id = ?";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setInt(4, item.getInventory());
            stmt.setObject(5, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                item.setId(id);
                return Optional.of(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating item", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID id) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = PostgresDatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting item", e);
        }
    }

    // In case needed soft deletion can be applied
    @Override
    public boolean softDelete(UUID uuid) {
        return false;
    }

    private Item mapRowToItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("inventory")
        );
    }
}
