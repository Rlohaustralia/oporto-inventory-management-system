package oporto.inventory.repository;

import oporto.inventory.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository // Automatic Bean Registration during component scanning
public class MenuRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Search for all items
    public List<Menu> allItem() {
        String sql = "SELECT * FROM menu";
        return jdbcTemplate.query(sql, itemRowMapper());
    }


    // Search a menu by ID
    public Menu searchMenuById(String id) {
        String sql = "SELECT * FROM menu WHERE id = ?";
        List<Menu> result = jdbcTemplate.query(sql, new Object[]{id}, itemRowMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public String searchMenuNameById(String id) {
        String sql = "SELECT menuName FROM menu WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, id);
    }


    // Save menus
    public Menu saveMenu(Menu menu) {
        String generatedId = generateId(menu.getMenuCategory());
        menu.setId(generatedId);
        String sql = "INSERT INTO menu (id, menuCategory, menuName, menuPrice, menuQuantity) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                menu.getId(),
                menu.getMenuCategory(),
                menu.getMenuName(),
                menu.getMenuPrice(),
                menu.getMenuQuantity()
        );
        return menu;
    }


    // Update menus
    public void updateMenu(String id, Menu updatedMenu) {
        String sql = "UPDATE menu SET menuCategory = ?, menuName = ?, menuPrice = ?, menuQuantity = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                updatedMenu.getMenuCategory(),
                updatedMenu.getMenuName(),
                updatedMenu.getMenuPrice(),
                updatedMenu.getMenuQuantity(),
                id
        );
    }


    // Delete menus
    public void deleteMenu(String id) {
        String sql = "DELETE FROM menu WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public int searchMenuQuantity(String id) {
        String sql = "SELECT menuQuantity FROM menu WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }


    private String generateId(String menuCategory) {
        // Logic to generate item ID based on item category
        String prefix;
        if (menuCategory.equalsIgnoreCase("Burgers")) {
            prefix = "BUR";
        } else if (menuCategory.equalsIgnoreCase("Rappas")) {
            prefix = "RP";
        } else if (menuCategory.equalsIgnoreCase("Bowls")) {
            prefix = "BL";
        } else if (menuCategory.equalsIgnoreCase("Kids")) {
            prefix = "KID";
        } else if (menuCategory.equalsIgnoreCase("Sides")) {
            prefix = "SD";
        } else if (menuCategory.equalsIgnoreCase("Sauces")) {
            prefix = "SC";
        } else if (menuCategory.equalsIgnoreCase("Drinks")) {
            prefix = "DR";
        } else {
            throw new IllegalArgumentException("Invalid item category");
        }

        // Retrieve the latest ID for the category from the database and increment it
        int count = countMenusByCategory(menuCategory) + 1;
        return prefix + String.format("%03d", count);
    }

    private int countMenusByCategory(String menuCategory) {
        String sql = "SELECT COUNT(*) FROM menu WHERE menuCategory =?";
        return jdbcTemplate.queryForObject(sql, Integer.class, menuCategory); // Retrieve a single result object
    }


    private RowMapper<Menu> itemRowMapper() {
        return new RowMapper<Menu>() {
            @Override
            public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
                Menu menu = new Menu();
                menu.setId(rs.getString("id"));
                menu.setMenuCategory(rs.getString("menuCategory"));
                menu.setMenuName(rs.getString("menuName"));
                menu.setMenuPrice(rs.getDouble("menuPrice"));
                menu.setMenuQuantity(rs.getInt("menuQuantity"));
                return menu;
            }
        };
    }

    public List<Menu> searchMenuByKeyword(String keyword) {
        String sql = "SELECT * FROM menu WHERE menuName LIKE ? OR menuCategory LIKE?";
        String likePattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, new Object[]{likePattern, likePattern}, new MenuRowMapper());
    }

    private static class MenuRowMapper implements RowMapper<Menu> {
        @Override
        public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
            Menu menu = new Menu();
            menu.setId(rs.getString("id"));
            menu.setMenuCategory(rs.getString("menuCategory"));
            menu.setMenuName(rs.getString("menuName"));
            menu.setMenuPrice(rs.getDouble("menuPrice"));
            menu.setMenuQuantity(rs.getInt("menuQuantity"));
            return menu;
        }
    }

    // For pagination
    public List<Menu> findMenusByBranchId(String branchId, int offset, int limit) {
        String sql = "SELECT * FROM branchMenu WHERE branchId = ? OFFSET = ? LIMIT = ?";
        return jdbcTemplate.query(sql, new Object[]{branchId, offset, limit}, new MenuRowMapper());
    }

    public int countMenusByBranchId(String branchId) {
        String sql = "SELECT COUNT(*) FROM branchMenu WHERE branchId = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, branchId);
    }

}



