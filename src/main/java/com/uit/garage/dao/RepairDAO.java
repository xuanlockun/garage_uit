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
    private static final String DB_URL = "jdbc:sqlite:garage.db";
    public List<ReceiptDisplay> getAllReceipts() {
        List<ReceiptDisplay> result = new ArrayList<>();
        String sql = """
            SELECT receipts.id AS id, vehicles.license_plate AS license, receipts.receipt_date AS date,
                   receipts.note AS note, receipts.status AS status
            FROM receipts
            JOIN vehicles ON receipts.vehicle_id = vehicles.id
            ORDER BY receipts.id DESC

    """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(new ReceiptDisplay(
                        rs.getInt("id"),
                        rs.getString("license"),
                        rs.getString("date"),
                        rs.getString("note"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    public boolean insertRepairDetails(int receiptId, List<RepairDetail> details) {
        String insertSql = """
        INSERT INTO repair_details (receipt_id, content, part_id, quantity, price, labor_cost, total)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        String updateSql = """
        UPDATE repair_details SET content = ?, part_id = ?, quantity = ?, price = ?, labor_cost = ?, total = ?
        WHERE id = ?
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            for (RepairDetail detail : details) {
                if (detail.getId() != null && detail.getId() > 0) {
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, detail.getContent());
                        stmt.setInt(2, detail.getPart().getId());
                        stmt.setInt(3, detail.getQuantity());
                        stmt.setDouble(4, detail.getPrice());
                        stmt.setDouble(5, detail.getLaborCost());
                        stmt.setDouble(6, detail.getTotal());
                        stmt.setInt(7, detail.getId());
                        stmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                        stmt.setInt(1, receiptId);
                        stmt.setString(2, detail.getContent());
                        stmt.setInt(3, detail.getPart().getId());
                        stmt.setInt(4, detail.getQuantity());
                        stmt.setDouble(5, detail.getPrice());
                        stmt.setDouble(6, detail.getLaborCost());
                        stmt.setDouble(7, detail.getTotal());
                        stmt.executeUpdate();
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
    public boolean markReceiptDoneAndCreateInvoice(int receiptId) {
        String updateStatusSql = "UPDATE receipts SET status = 'done' WHERE id = ?";
        String totalSql = "SELECT SUM(total) FROM repair_details WHERE receipt_id = ?";
        String findVehicleCustomerSql = "SELECT vehicles.id AS vehicle_id, customers.id AS customer_id FROM vehicles JOIN receipts ON receipts.vehicle_id = vehicles.id JOIN customers ON vehicles.customer_id = customers.id WHERE receipts.id = ?";
        String insertInvoiceSql = "INSERT INTO invoices (receipt_id, vehicle_id, customer_id, total_amount) VALUES (?, ?, ?, ?)";
        String updateRepairDetailsSql = "UPDATE repair_details SET invoice_id = ? WHERE receipt_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement updateStatusStmt = conn.prepareStatement(updateStatusSql);
                    PreparedStatement totalStmt = conn.prepareStatement(totalSql);
                    PreparedStatement findVehicleCustomerStmt = conn.prepareStatement(findVehicleCustomerSql);
                    PreparedStatement insertInvoiceStmt = conn.prepareStatement(insertInvoiceSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement updateRepairDetailsStmt = conn.prepareStatement(updateRepairDetailsSql);
            ) {
                // 1. Cập nhật trạng thái done cho receipt
                updateStatusStmt.setInt(1, receiptId);
                updateStatusStmt.executeUpdate();

                // 2. Tính tổng tiền sửa chữa
                totalStmt.setInt(1, receiptId);
                ResultSet rsTotal = totalStmt.executeQuery();
                double totalAmount = rsTotal.next() ? rsTotal.getDouble(1) : 0.0;

                // 3. Lấy vehicle_id và customer_id từ receipt
                findVehicleCustomerStmt.setInt(1, receiptId);
                ResultSet rsInfo = findVehicleCustomerStmt.executeQuery();
                if (!rsInfo.next()) {
                    conn.rollback();
                    return false;
                }
                int vehicleId = rsInfo.getInt("vehicle_id");
                int customerId = rsInfo.getInt("customer_id");

                // 4. Thêm invoice
                insertInvoiceStmt.setInt(1, receiptId);
                insertInvoiceStmt.setInt(2, vehicleId);
                insertInvoiceStmt.setInt(3, customerId);
                insertInvoiceStmt.setDouble(4, totalAmount);
                insertInvoiceStmt.executeUpdate();

                ResultSet generatedKeys = insertInvoiceStmt.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    conn.rollback();
                    return false;
                }
                int invoiceId = generatedKeys.getInt(1);

                // 5. Gán invoice_id vào các repair_details
                updateRepairDetailsStmt.setInt(1, invoiceId);
                updateRepairDetailsStmt.setInt(2, receiptId);
                updateRepairDetailsStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
