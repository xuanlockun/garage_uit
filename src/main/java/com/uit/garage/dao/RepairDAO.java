package com.uit.garage.dao;

import com.uit.garage.DBUtil;
import com.uit.garage.model.Part;
import com.uit.garage.model.RepairDetail;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepairDAO {
    public boolean insertRepair(String licensePlate, LocalDate repairDate, List<RepairDetail> details) {
        String findVehicleSql = "SELECT id FROM vehicles WHERE license_plate = ?";
        String insertReceiptSql = "INSERT INTO receipts(vehicle_id, receipt_date) VALUES(?, ?)";
        String insertDetailSql = "INSERT INTO repair_details(receipt_id, content, part_id, quantity, price, labor_cost, total) VALUES(?, ?, ?, ?, ?, ?, ?)";
        String updateDetailSql = "UPDATE repair_details SET content = ?, part_id = ?, quantity = ?, price = ?, labor_cost = ?, total = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            int vehicleId = -1;
            try (PreparedStatement findStmt = conn.prepareStatement(findVehicleSql)) {
                findStmt.setString(1, licensePlate);
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) vehicleId = rs.getInt("id");
                else throw new SQLException("Không tìm thấy xe với biển số " + licensePlate);
            }

            // 🟡 Tạo receipt mới
            int receiptId;
            try (PreparedStatement insertReceiptStmt = conn.prepareStatement(insertReceiptSql, Statement.RETURN_GENERATED_KEYS)) {
                insertReceiptStmt.setInt(1, vehicleId);
                insertReceiptStmt.setString(2, repairDate.toString());
                insertReceiptStmt.executeUpdate();
                ResultSet generated = insertReceiptStmt.getGeneratedKeys();
                if (generated.next()) {
                    receiptId = generated.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // ✅ Lưu chi tiết sửa chữa: insert hoặc update tùy trường hợp
            for (RepairDetail detail : details) {
                if (detail.getId() == null) {
                    try (PreparedStatement insertDetailStmt = conn.prepareStatement(insertDetailSql)) {
                        insertDetailStmt.setInt(1, receiptId);
                        insertDetailStmt.setString(2, detail.getContent());
                        insertDetailStmt.setInt(3, detail.getPart().getId());
                        insertDetailStmt.setInt(4, detail.getQuantity());
                        insertDetailStmt.setDouble(5, detail.getPrice());
                        insertDetailStmt.setDouble(6, detail.getLaborCost());
                        insertDetailStmt.setDouble(7, detail.getTotal());
                        insertDetailStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateDetailSql)) {
                        updateStmt.setString(1, detail.getContent());
                        updateStmt.setInt(2, detail.getPart().getId());
                        updateStmt.setInt(3, detail.getQuantity());
                        updateStmt.setDouble(4, detail.getPrice());
                        updateStmt.setDouble(5, detail.getLaborCost());
                        updateStmt.setDouble(6, detail.getTotal());
                        updateStmt.setInt(7, detail.getId());
                        updateStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<RepairDetail> getRepairsByLicense(String licensePlate) {
        List<RepairDetail> list = new ArrayList<>();

        String query = """
        SELECT rd.id, rd.receipt_id, rd.content, rd.quantity, rd.price, rd.labor_cost, rd.total, 
               p.id as part_id, p.name as part_name
        FROM repair_details rd
        JOIN parts p ON rd.part_id = p.id
        JOIN receipts r ON r.id = rd.receipt_id
        JOIN vehicles v ON v.id = r.vehicle_id
        WHERE v.license_plate = ?
        ORDER BY r.receipt_date DESC
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Part part = new Part(rs.getInt("part_id"), rs.getString("part_name"), 0);
                RepairDetail detail = new RepairDetail(
                        rs.getInt("id"),
                        rs.getInt("receipt_id"),
                        rs.getString("content"),
                        part,
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("labor_cost")
                );
                detail.recalculateTotal();
                list.add(detail);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
