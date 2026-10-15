

package jdbcAssignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EmployeeManagementMenu {
    public static void main(String[] args) {
        String defaultUrl = "jdbc:postgresql://localhost:5432/postgres";
        String dbUrl = "jdbc:postgresql://localhost:5432/company_db";
        String user = "postgres";
        String password = "1530";

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n===== EMPLOYEE MANAGEMENT MENU =====");
            System.out.println("1. Create Database and Table");
            System.out.println("2. Insert Employee Records");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
            
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    // Create Database
                    try (Connection conn = DriverManager.getConnection(defaultUrl, user, password);
                         Statement stmt = conn.createStatement()) {
                        
                        stmt.executeUpdate("CREATE DATABASE company_db");
                        System.out.println("Database 'company_db' created successfully!");
                    } catch (SQLException e) {
                        System.out.println("Database creation notice: " + e.getMessage());
                    }

                    // Create Table
                    String createTableQuery = "CREATE TABLE IF NOT EXISTS employee (" +
                                              "emp_id INT PRIMARY KEY, " +
                                              "emp_name VARCHAR(50), " +
                                              "salary DOUBLE PRECISION, " +
                                              "department VARCHAR(50))";
                    try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
                         Statement stmt = conn.createStatement()) {
                        
                        stmt.executeUpdate(createTableQuery);
                        System.out.println("Table 'employee' is ready!");
                    } catch (SQLException e) {
                        System.out.println("Table creation error: " + e.getMessage());
                    }
                    break;

                case 2:
                    // Insert Employee Records using PreparedStatement
                    String insertQuery = "INSERT INTO employee (emp_id, emp_name, salary, department) VALUES (?, ?, ?, ?)";
                    
                    try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
                         PreparedStatement ps = conn.prepareStatement(insertQuery)) {

                        System.out.print("How many employees do you want to insert? ");
                        int count = scanner.nextInt();
                        scanner.nextLine();

                        for (int i = 1; i <= count; i++) {
                            System.out.println("\nEntering details for Employee " + i + ":");

                            System.out.print("Enter Employee ID: ");
                            int empId = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Enter Employee Name: ");
                            String empName = scanner.nextLine();

                            System.out.print("Enter Salary: ");
                            double salary = scanner.nextDouble();
                            scanner.nextLine();

                            System.out.print("Enter Department: ");
                            String department = scanner.nextLine();

                            ps.setInt(1, empId);
                            ps.setString(2, empName);
                            ps.setDouble(3, salary);
                            ps.setString(4, department);

                            int rows = ps.executeUpdate();
                            if (rows > 0) {
                                System.out.println("Inserted Employee ID: " + empId + ", Name: " + empName);
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