package com.uit.garage.dao;

import com.uit.garage.model.Customer;
import com.uit.garage.model.Vehicle;
import com.uit.garage.model.Receipt;

import java.sql.*;

public class ReceiptDAO {

    private static final String DB_URL = "jdbc:sqlite:garage.db";

    public int insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(name, address, phone) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhone());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public int insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles(license_plate, brand, customer_id) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setString(2, vehicle.getBrand());
            stmt.setInt(3, vehicle.getCustomerId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public void insertReceipt(Receipt receipt) throws SQLException {
        String sql = "INSERT INTO receipts(vehicle_id, receipt_date) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receipt.getVehicleId());
            stmt.setString(2, receipt.getReceiptDate().toString());
            stmt.executeUpdate();
        }
    }
}
