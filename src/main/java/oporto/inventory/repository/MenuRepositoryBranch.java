package oporto.inventory.repository;

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
            // Handle case when no result is found
            return 0;
        }
    }

}
