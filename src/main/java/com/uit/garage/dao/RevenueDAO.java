package com.uit.garage.dao;

import com.uit.garage.DBUtil;
import com.uit.garage.model.RevenueStatRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RevenueDAO {
    public List<RevenueStatRow> getRevenueStats(int month, int year) {
        List<RevenueStatRow> list = new ArrayList<>();
        String monthStr = String.format("%02d", month);

        String sql = "SELECT v.brand, COUNT(DISTINCT r.id) AS repair_count, SUM(p.amount) AS total_amount " +
                "FROM payments p " +
                "JOIN invoices i ON p.invoice_id = i.id " +
                "JOIN vehicles v ON i.vehicle_id = v.id " +
                "JOIN receipts r ON i.receipt_id = r.id " +
                "WHERE strftime('%m', p.payment_date) = ? AND strftime('%Y', p.payment_date) = ? " +
                "GROUP BY v.brand";

        double totalRevenue = 0;
        List<RevenueStatRow> tempList = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, monthStr);
            stmt.setString(2, String.valueOf(year));

            ResultSet rs = stmt.executeQuery();
            int stt = 1;

            while (rs.next()) {
                String brand = rs.getString("brand");
                int count = rs.getInt("repair_count");
                double amount = rs.getDouble("total_amount");
                totalRevenue += amount;
                tempList.add(new RevenueStatRow(stt++, brand, count, amount, 0)); // tỉ lệ cập nhật sau
            }

            for (RevenueStatRow row : tempList) {
                double percentage = totalRevenue > 0 ? (row.getTotalAmount() / totalRevenue) * 100 : 0;
                list.add(new RevenueStatRow(row.getStt(), row.getBrand(), row.getRepairCount(), row.getTotalAmount(), percentage));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return list;
    }
}
