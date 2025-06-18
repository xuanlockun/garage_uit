package com.uit.garage.dao;

import com.uit.garage.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public static void createPaymentsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "invoice_id INTEGER, " +
                "payment_date TEXT, " +
                "amount REAL, " +
                "FOREIGN KEY(invoice_id) REFERENCES invoices(id))";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertPayment(int invoiceId, LocalDate date, double amount) {
        String sql = "INSERT INTO payments(invoice_id, payment_date, amount) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            stmt.setString(2, date.toString());
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getTotalPaidForInvoice(int invoiceId) {
        String sql = "SELECT SUM(amount) AS total FROM payments WHERE invoice_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static List<String> getPaymentsForInvoice(int invoiceId) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT payment_date, amount FROM payments WHERE invoice_id = ? ORDER BY payment_date";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("payment_date") + " - " + rs.getDouble("amount") + " VND");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
