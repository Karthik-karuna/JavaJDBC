package jdbcCallablestatement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class StudentRegistrationApp {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/college_db";
        String user = "postgres";
        String password = "1530";

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        // Setup table and stored procedure beforehand for PostgreSQL
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 1. Create table using SERIAL for auto-increment in PostgreSQL
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS student (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(50), " +
                    "age INT, " +
                    "department VARCHAR(50))");

            // 2. Drop existing procedure if any
            stmt.executeUpdate("DROP PROCEDURE IF EXISTS registerStudent(varchar, integer, varchar)");

            // 3. Create PostgreSQL compatible PL/pgSQL stored procedure
            String createProc = "CREATE PROCEDURE registerStudent(p_name VARCHAR(50), p_age INT, p_department VARCHAR(50)) " +
                    "LANGUAGE plpgsql AS $$ " +
                    "BEGIN " +
                    "    INSERT INTO student(name, age, department) VALUES (p_name, p_age, p_department); " +
                    "END; $$";
            stmt.executeUpdate(createProc);
            System.out.println("Database table and stored procedure ready!");

        } catch (SQLException e) {
            System.out.println("Setup error: " + e.getMessage());
        }

        do {
            System.out.println("\n===== STUDENT REGISTRATION MENU =====");
            System.out.println("1. Register New Student");
            System.out.println("2. Exit");
            System.out.print("Enter your choice (1-2): ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    try (Connection conn = DriverManager.getConnection(url, user, password);
                         // Use standard CALL syntax without curly braces for PostgreSQL procedures
                         CallableStatement cstmt = conn.prepareCall("CALL registerStudent(?, ?, ?)")) {

                        System.out.print("Enter Student Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Age: ");
                        int age = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter Department: ");
                        String dept = scanner.nextLine();

                        cstmt.setString(1, name);
                        cstmt.setInt(2, age);
                        cstmt.setString(3, dept);

                        cstmt.execute();
                        System.out.println("Student registered successfully via stored procedure!");

                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.println("Exiting program. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please choose 1 or 2.");
            }
        } while (choice != 2);

        scanner.close();
    }
}
