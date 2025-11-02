package com.accesdades.ra2.ac1.accesdades_ra2_ac1.repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.models.User;

@Repository
public class UserRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final class CustomRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setDescription(rs.getString("description"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    }

    public int addUser(User user) {
        String sql = "INSERT INTO users (name, description, email, password) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getName(), user.getDescription(), user.getEmail(), user.getPassword());
    }

    public int removeUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new CustomRowMapper());
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new CustomRowMapper(), id);
        return users.isEmpty() ? null : users.get(0);
    }

    public User updateUser(User user) {
        String sql = "UPDATE users SET name = ?, description = ?, email = ?, password = ?, data_updated = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getDescription(), user.getEmail(), user.getPassword(), user.getId());
        return getUserById(user.getId());
    }

    public User updateUserName(int id, String name) {
        String sql = "UPDATE users SET name = ?, data_updated = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, name, id);
        return getUserById(id);
    }

    public int deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
