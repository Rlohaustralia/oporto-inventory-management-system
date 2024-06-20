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
            Integer quantity = jdbcTemplate.queryForObject(sql, Integer.class, branchId, menuId);
            return (quantity != null) ? quantity : 0; // Return 0 if quantity is null
        } catch (EmptyResultDataAccessException e) {
            return 0; // Return 0 if no row is found
        }
    }

    public int saveOrder(Orders orders) {

        int currentBranchMenuQuantity = getBranchMenuQuantity(orders.getBranchId(), orders.getMenuId());

        if (orders.getOrderQuantity() <= orders.getHqQuantity()) {

            // Decrease menu quantity from HQ
            String updateMenuQuantityQuery = "UPDATE menu SET menuQuantity = menuQuantity - ? WHERE id = ?";
            jdbcTemplate.update(updateMenuQuantityQuery, orders.getOrderQuantity(), orders.getMenuId());


            // Increase branch menu quantity
            if (currentBranchMenuQuantity > 0) {
                String updateBranchMenuQuantityQuery = "UPDATE branchMenu SET branchMenuQuantity = branchMenuQuantity + ? WHERE menuId = ? AND branchId = ?";
                jdbcTemplate.update(updateBranchMenuQuantityQuery, orders.getOrderQuantity(), orders.getMenuId(), orders.getBranchId());
            } else {
                // create entry if it doesn't exist
                String insertBranchMenuQuery = "INSERT INTO branchMenu (branchId, menuId, branchMenuQuantity) VALUES (?, ?, ?)";
                jdbcTemplate.update(insertBranchMenuQuery, orders.getBranchId(), orders.getMenuId(), orders.getOrderQuantity());
            }

            // Save order details into orders table
            String saveOrderDetailsQuery = "INSERT INTO orders (menuId, orderQuantity, orderDate, branchId) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(saveOrderDetailsQuery, orders.getMenuId(), orders.getOrderQuantity(), orders.getOrderDate(), orders.getBranchId());

            // Fetch orderId for the saved order
            String fetchOrderIdQuery = "SELECT LAST_INSERT_ID()";
            return jdbcTemplate.queryForObject(fetchOrderIdQuery, Integer.class);

        } else {
            throw new IllegalArgumentException("Order quantity exceeds available menu quantity.");
        }


    }

    public void cancelOrderByOrderId(int orderId) {

        // Fetch order details
        String branchIdQuery = "SELECT branchId FROM orders WHERE id = ?";
        String branchId = jdbcTemplate.queryForObject(branchIdQuery, String.class, orderId);

        String menuIdQuery = "SELECT menuId FROM orders WHERE id = ?";
        String menuId = jdbcTemplate.queryForObject(menuIdQuery, String.class, orderId);

        String orderQuantityQuery = "SELECT orderQuantity FROM orders WHERE id = ?";
        Integer orderQuantity = jdbcTemplate.queryForObject(orderQuantityQuery, Integer.class, orderId);

        // Increase menu quantity from HQ
        String updateMenuQuantityQuery = "UPDATE menu SET menuQuantity = menuQuantity + ? WHERE id = ?";
        jdbcTemplate.update(updateMenuQuantityQuery, orderQuantity, menuId);

        // Decrease menu quantity from Branch
        String updateBranchMenuQuantityQuery = "UPDATE branchMenu SET branchMenuQuantity = branchMenuQuantity - ? WHERE menuId = ? AND branchId = ?";
        jdbcTemplate.update(updateBranchMenuQuantityQuery, orderQuantity, menuId, branchId);

        // Delete order history from orders table
        String deleteOrderQuery = "DELETE FROM orders WHERE id = ?";
        jdbcTemplate.update(deleteOrderQuery, orderId);

    }

}
