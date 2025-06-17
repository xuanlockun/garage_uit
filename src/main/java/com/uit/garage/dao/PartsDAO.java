package com.uit.garage.dao;

import com.uit.garage.DBUtil;
import com.uit.garage.model.Part;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartsDAO {
    public static List<Part> getAllParts() {
        List<Part> parts = new ArrayList<>();
        String sql = "SELECT * FROM parts";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Part part = new Part(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                parts.add(part);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parts;
    }
    public Part getPartById(int id) {
        String sql = "SELECT * FROM parts WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Part(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
