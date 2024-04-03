package oporto.inventory.repository;

import oporto.inventory.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Member saveMember(Member member) {

        // Check if the email already exists
        String duplicateEmail = "SELECT COUNT(*) FROM member WHERE memberEmail = ?";
        int count = jdbcTemplate.queryForObject(duplicateEmail, Integer.class, member.getMemberEmail());

        // If there is a duplicate email then return null
        if(count > 0) {
            return null;
        }

        String generatedId = generatedId(member.getMemberPosition());
        member.setId(generatedId);
        String sql = "INSERT INTO member (id, memberEmail, memberPassword, memberName, memberPosition, memberBranch) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                member.getId(),
                member.getMemberEmail(),
                member.getMemberPassword(),
                member.getMemberName(),
                member.getMemberPosition(),
                member.getMemberBranch());
        return member;
    }


    public Optional<Member> login(String memberEmail, String memberPassword) {

        // Retrieve a member from the DB by the email typed by the user.
        String sql = "SELECT memberPassword FROM member WHERE memberEmail = ?";
        Member memberInfo = jdbcTemplate.queryForObject(sql,
                new Object[]{memberEmail},
                new BeanPropertyRowMapper<>(Member.class));

        // Check if the password typed by the user matches the password found in the DB
        // Compare passwords and return Optional accordingly
        if (memberInfo != null && memberInfo.getMemberPassword().equals(memberPassword)) {
            return Optional.of(memberInfo);
        } else {
            return Optional.empty(); // Passwords don't match or member is null
        }
    }

    public String getMemberBranchByEmail(String memberEmail) {

        // Retrieve member's branch by the email
        String sql = "SELECT memberBranch FROM member WHERE memberEmail = ?";
        String branch = jdbcTemplate.queryForObject(sql, String.class, memberEmail);

        return branch;
    }


    private String generatedId(String memberPosition) {

        String prefix;
        if (memberPosition.equalsIgnoreCase("Staff")) {
            prefix = "ST";
        } else if (memberPosition.equalsIgnoreCase("Manager")) {
            prefix = "MA";
        } else if (memberPosition.equalsIgnoreCase("Supervisor")) {
            prefix = "SV";
        } else {
            throw new IllegalArgumentException("Invalid item category");
        }

        // Retrieve the latest ID for the category from the database and increment it
        int count = countItemsByPosition(memberPosition) + 1;
        return prefix + String.format("%03d", count);
    }


    private int countItemsByPosition(String memberPosition) {
        String sql = "SELECT COUNT(*) FROM member WHERE memberPosition = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, memberPosition);
    }
}
