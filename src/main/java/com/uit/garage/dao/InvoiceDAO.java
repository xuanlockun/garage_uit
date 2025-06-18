package com.uit.garage.dao;

import com.uit.garage.DBUtil;
import com.uit.garage.model.InvoiceRow;
import com.uit.garage.model.Part;
import com.uit.garage.model.RepairDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    public List<InvoiceRow> getAllInvoiceRows() {
        List<InvoiceRow> list = new ArrayList<>();
//        String sql = "SELECT i.id, v.license_plate, v.brand, c.name AS customer_name, i.total_amount " +
//                "FROM invoices i " +
//                "JOIN vehicles v ON i.vehicle_id = v.id " +
//                "JOIN customers c ON i.customer_id = c.id";
        String sql = """
                    SELECT i.id, v.license_plate, v.brand, c.name AS customer_name,
                           i.total_amount - IFNULL(p.paid, 0) AS debt
                    FROM invoices i
                    JOIN vehicles v ON i.vehicle_id = v.id
                    JOIN customers c ON i.customer_id = c.id
                    LEFT JOIN (
                        SELECT invoice_id, SUM(amount) AS paid
                        FROM payments
                        GROUP BY invoice_id
                    ) p ON i.id = p.invoice_id
                    WHERE i.total_amount - IFNULL(p.paid, 0) > 0
                """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int stt = 1;
            while (rs.next()) {
                InvoiceRow row = new InvoiceRow(
                        rs.getInt("id"),
                        rs.getString("license_plate"),
                        rs.getString("brand"),
                        rs.getString("customer_name"),
                        rs.getDouble("debt")
                );
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<RepairDetail> getRepairDetailsByInvoiceId(int invoiceId) {
        List<RepairDetail> list = new ArrayList<>();
        String sql = """
        SELECT rd.id, rd.receipt_id, rd.content, rd.quantity, rd.price, rd.labor_cost,
               rd.total, p.id as part_id, p.name as part_name
        FROM repair_details rd
        JOIN receipts r ON rd.receipt_id = r.id
        JOIN invoices i ON r.vehicle_id = i.vehicle_id
        JOIN parts p ON rd.part_id = p.id
        WHERE i.id = ?
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Part part = new Part(rs.getInt("part_id"), rs.getString("part_name"), rs.getDouble("price"));
                RepairDetail detail = new RepairDetail(
                        rs.getInt("id"),
                        rs.getInt("receipt_id"),
                        rs.getString("content"),
                        part,
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("labor_cost")
                );
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


}
