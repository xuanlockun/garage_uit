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

            String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    username TEXT NOT NULL UNIQUE," +
                    "    password TEXT NOT NULL," +
                    "    role TEXT NOT NULL CHECK(role IN ('admin', 'staff'))" +
                    ");";
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
                    "note TEXT, " +
                    "status TEXT DEFAULT 'pending', " +
                    "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id))";
            stmt.execute(receiptTable);

            String partsTable = "CREATE TABLE IF NOT EXISTS parts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL)";
            stmt.execute(partsTable);

            String repairDetailTable = "CREATE TABLE IF NOT EXISTS repair_details (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "receipt_id INTEGER, " +
                    "invoice_id INTEGER, " +
                    "content TEXT, " +
                    "part_id INTEGER, " +
                    "quantity INTEGER, " +
                    "price REAL, " +
                    "labor_cost REAL, " +
                    "total REAL, " +
                    "FOREIGN KEY(receipt_id) REFERENCES receipts(id), " +
                    "FOREIGN KEY(part_id) REFERENCES parts(id), " +
                    "FOREIGN KEY(invoice_id) REFERENCES invoices(id)" +
                    ")";
            stmt.execute(repairDetailTable);

            String invoiceTable = "CREATE TABLE IF NOT EXISTS invoices (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "receipt_id INTEGER, " +
                    "vehicle_id INTEGER, " +
                    "customer_id INTEGER, " +
                    "total_amount REAL, " +
                    "FOREIGN KEY(receipt_id) REFERENCES receipts(id), " +
                    "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id), " +
                    "FOREIGN KEY(customer_id) REFERENCES customers(id))";
            stmt.execute(invoiceTable);
            String paymentTable = "CREATE TABLE IF NOT EXISTS payments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "invoice_id INTEGER, " +
                    "payment_date TEXT, " +
                    "amount REAL, " +
                    "FOREIGN KEY(invoice_id) REFERENCES invoices(id))";
            stmt.execute(paymentTable);
            String stockTable = "CREATE TABLE IF NOT EXISTS stock ("+
                    "part_id INTEGER PRIMARY KEY," +
                    "quantity INTEGER NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(part_id) REFERENCES parts(id)" +
            ");";
            stmt.execute(stockTable);
            String stockTransTable = "CREATE TABLE IF NOT EXISTS stock_transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "part_id INTEGER," +
                    "transaction_date TEXT," +
                    "type TEXT CHECK(type IN ('import', 'export'))," +
                    "quantity INTEGER," +
                    "note TEXT," +
                    "FOREIGN KEY(part_id) REFERENCES parts(id)" +
            ");";
            stmt.execute(stockTransTable);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
