package com.uit.garage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static final String DB_URL = "jdbc:sqlite:garage.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String userTable = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)";
            stmt.execute(userTable);

            String customerTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "address TEXT, " +
                    "phone TEXT)";
            stmt.execute(customerTable);

            String vehicleTable = "CREATE TABLE IF NOT EXISTS vehicles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "license_plate TEXT NOT NULL, " +
                    "brand TEXT, " +
                    "customer_id INTEGER, " +
                    "FOREIGN KEY(customer_id) REFERENCES customers(id))";
            stmt.execute(vehicleTable);

            String receiptTable = "CREATE TABLE IF NOT EXISTS receipts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "vehicle_id INTEGER, " +
                    "receipt_date TEXT, " +
                    "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id))";
            stmt.execute(receiptTable);

            // ✅ Bảng parts
            String partsTable = "CREATE TABLE IF NOT EXISTS parts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL)";
            stmt.execute(partsTable);

            // ✅ Bảng repair_details
            String repairDetailTable = "CREATE TABLE IF NOT EXISTS repair_details (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "receipt_id INTEGER, " +
                    "content TEXT, " +                         // ✅ dòng này là bắt buộc
                    "part_id INTEGER, " +
                    "quantity INTEGER, " +
                    "price REAL, " +
                    "labor_cost REAL, " +
                    "total REAL, " +
                    "FOREIGN KEY(receipt_id) REFERENCES receipts(id), " +
                    "FOREIGN KEY(part_id) REFERENCES parts(id))";
            stmt.execute(repairDetailTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
