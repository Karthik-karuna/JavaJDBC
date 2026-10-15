package jdbcAssignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProductManagementMenu {
    public static void main(String[] args) {
        String defaultUrl = "jdbc:postgresql://localhost:5432/postgres";
        String dbUrl = "jdbc:postgresql://localhost:5432/shopping_db";
        String user = "postgres";
        String password = "1530";

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n===== PRODUCT MANAGEMENT MENU =====");
            System.out.println("1. Create Database and Table");
            System.out.println("2. Insert Product Records");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
            
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    // Create Database shopping_db
                    try (Connection conn = DriverManager.getConnection(defaultUrl, user, password);
                         Statement stmt = conn.createStatement()) {
                        
                        stmt.executeUpdate("CREATE DATABASE shopping_db");
                        System.out.println("Database 'shopping_db' created successfully!");
                    } catch (SQLException e) {
                        System.out.println("Database creation notice: " + e.getMessage());
                    }

                    // Create Table product
                    String createTableQuery = "CREATE TABLE IF NOT EXISTS product (" +
                                              "product_id INT PRIMARY KEY, " +
                                              "product_name VARCHAR(100), " +
                                              "price DOUBLE PRECISION, " +
                                              "quantity INT)";
                    try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
                         Statement stmt = conn.createStatement()) {
                        
                        stmt.executeUpdate(createTableQuery);
                        System.out.println("Table 'product' is ready!");
                    } catch (SQLException e) {
                        System.out.println("Table creation error: " + e.getMessage());
                    }
                    break;

                case 2:
                    // Insert Product Records using PreparedStatement
                    String insertQuery = "INSERT INTO product (product_id, product_name, price, quantity) VALUES (?, ?, ?, ?)";
                    
                    try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
                         PreparedStatement ps = conn.prepareStatement(insertQuery)) {

                        System.out.print("How many products do you want to insert? ");
                        int count = scanner.nextInt();
                        scanner.nextLine();

                        for (int i = 1; i <= count; i++) {
                            System.out.println("\nEntering details for Product " + i + ":");

                            System.out.print("Enter Product ID: ");
                            int productId = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Enter Product Name: ");
                            String productName = scanner.nextLine();

                            System.out.print("Enter Price: ");
                            double price = scanner.nextDouble();

                            System.out.print("Enter Quantity: ");
                            int quantity = scanner.nextInt();
                            scanner.nextLine();

                            ps.setInt(1, productId);
                            ps.setString(2, productName);
                            ps.setDouble(3, price);
                            ps.setInt(4, quantity);

                            int rows = ps.executeUpdate();
                            if (rows > 0) {
                                System.out.println("Product added successfully");
                            }
                        }

                    } catch (SQLException e) {
                        System.out.println("Insertion Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("Exiting program. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please enter 1, 2, or 3.");
            }
        } while (choice != 3);

        scanner.close();
    }
}
