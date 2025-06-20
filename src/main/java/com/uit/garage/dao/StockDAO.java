package com.uit.garage.dao;

import com.uit.garage.DBUtil;
import com.uit.garage.model.Part;
import com.uit.garage.model.StockReportRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

//    public static boolean insertStockTransaction(int partId, int quantity, String note) {
//        return importStock(partId, LocalDate.now(), quantity, note);
//    }
    public boolean hasEnoughStock(int partId, int quantity) {
        String sql = "SELECT quantity FROM stock WHERE part_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, partId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    return currentQty >= quantity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean exportStock(Connection conn, int partId, LocalDate date, int quantity, String note) throws SQLException {
        return adjustStock(conn, partId, date, quantity, note, "export");
    }

    public static boolean importStock(Connection conn, int partId, LocalDate date, int quantity, String note) throws SQLException {
        return adjustStock(conn, partId, date, quantity, note, "import");
    }

    public static boolean adjustStock(Connection conn, int partId, LocalDate date, int quantity, String note, String type) throws SQLException {
        String sql = "INSERT INTO stock_transactions(part_id, transaction_date, type, quantity, note) VALUES (?, ?, ?, ?, ?)";
        String updateStock;

        if ("import".equalsIgnoreCase(type)) {
            updateStock = "INSERT INTO stock(part_id, quantity) VALUES (?, ?) " +
                    "ON CONFLICT(part_id) DO UPDATE SET quantity = quantity + ?";
        } else if ("export".equalsIgnoreCase(type)) {
            StockDAO dao = new StockDAO();
            if (!dao.hasEnoughStock(partId, quantity)) {
                System.err.println("Không đủ hàng trong kho để xuất.");
                return false;
            }
            updateStock = "UPDATE stock SET quantity = quantity - ? WHERE part_id = ?";
        } else {
            throw new IllegalArgumentException("Loại giao dịch không hợp lệ: " + type);
        }

        try (
                PreparedStatement transStmt = conn.prepareStatement(sql);
                PreparedStatement stockStmt = conn.prepareStatement(updateStock)
        ) {
            // Ghi giao dịch
            transStmt.setInt(1, partId);
            transStmt.setString(2, date.toString());
            transStmt.setString(3, type);
            transStmt.setInt(4, quantity);
            transStmt.setString(5, note);
            transStmt.executeUpdate();

            // Cập nhật kho
            if ("import".equalsIgnoreCase(type)) {
                stockStmt.setInt(1, partId);
                stockStmt.setInt(2, quantity);
                stockStmt.setInt(3, quantity);
            } else {
                stockStmt.setInt(1, quantity);
                stockStmt.setInt(2, partId);
            }
            stockStmt.executeUpdate();

            return true;
        }
    }

    public static List<StockReportRow> getStockReport(int month, int year) {
        List<StockReportRow> list = new ArrayList<>();
        String monthStr = String.format("%02d", month);
        String yearStr = String.valueOf(year);

        String sql = "SELECT p.name AS part_name, " +
                "COALESCE((SELECT SUM(quantity) FROM stock_transactions st1 WHERE st1.part_id = p.id AND date(st1.transaction_date) < date(?)), 0) AS begin_qty, " +
                "COALESCE((SELECT SUM(quantity) FROM stock_transactions st2 WHERE st2.part_id = p.id AND strftime('%m', st2.transaction_date) = ? AND strftime('%Y', st2.transaction_date) = ?), 0) AS in_out_qty, " +
                "COALESCE((SELECT quantity FROM stock s WHERE s.part_id = p.id), 0) AS end_qty " +
                "FROM parts p";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String dateBefore = year + "-" + monthStr + "-01";
            stmt.setString(1, dateBefore);
            stmt.setString(2, monthStr);
            stmt.setString(3, yearStr);

            ResultSet rs = stmt.executeQuery();
            int stt = 1;
            while (rs.next()) {
                list.add(new StockReportRow(
                        stt++,
                        rs.getString("part_name"),
                        rs.getInt("begin_qty"),
                        rs.getInt("in_out_qty"),
                        rs.getInt("end_qty")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Part> getAllParts() {
        List<Part> list = new ArrayList<>();
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
                list.add(part);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}