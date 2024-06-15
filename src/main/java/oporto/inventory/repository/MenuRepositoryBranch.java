package oporto.inventory.repository;

import oporto.inventory.domain.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MenuRepositoryBranch {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String getBranchId(String branchName) {
        String sql = "SELECT id FROM branch WHERE branchName = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, branchName);
        } catch (EmptyResultDataAccessException e) {
            // Handle case when no branch is found
            return null;
        }
    }

    public Integer getBranchMenuQuantity(String branchId, String menuId) {
        String sql = "SELECT branchMenuQuantity FROM branchMenu WHERE branchId = ? AND menuId = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, branchId, menuId);
        } catch (EmptyResultDataAccessException e) {
            return 0; // Return 0 if no row is found
        }
    }

    public Orders saveOrder(Orders orders) {

        String checkCurrentMenuQuantity = "SELECT menuQuantity FROM menu WHERE menuId = ?";
        int currentMenuQuantity = jdbcTemplate.queryForObject(checkCurrentMenuQuantity, Integer.class, orders.getOrderQuantity());
        int branchMenuQuantity = getBranchMenuQuantity(orders.getBranchId(), orders.getMenuId());

        if (orders.getOrderQuantity() <= currentMenuQuantity) {

            // Decrease menu quantity from HQ
            String updateMenuQuantityQuery = "UPDATE menu SET menuQuantity = menuQuantity - ? WHERE menuId = ?";
            jdbcTemplate.update(updateMenuQuantityQuery, orders.getOrderQuantity(), orders.getMenuId());

            // Increase branch menu quantity
            if (branchMenuQuantity > 0) {
                String updateBranchMenuQuantityQuery = "UPDATE branchMenu SET branchMenuQuantity = branchMenuQuantity + ? WHERE menuId = ? AND branchId = ?";
                jdbcTemplate.update(updateBranchMenuQuantityQuery, orders.getOrderQuantity(), orders.getMenuId(), orders.getBranchId());
            } else {
                // create entry if it doesn't exist
                String insertBranchMenuQuery = "INSERT branchMenu (branchId, menuId, branchMenuQuantity) VALUES (?, ?, ?)";
                jdbcTemplate.update(insertBranchMenuQuery, orders.getBranchId(), orders.getMenuId(), orders.getOrderQuantity());
            }

            // Save order details into orders table
            String saveOrderDetailsQuery = "INSERT INTO orders (menuId, orderQuantity, orderDate, branchId) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(saveOrderDetailsQuery, orders.getMenuId(), orders.getOrderQuantity(), orders.getOrderDate(), orders.getBranchId());

        } else {
            throw new IllegalArgumentException("Order quantity exceeds available menu quantity.");
        }

        return orders;
    }
}
