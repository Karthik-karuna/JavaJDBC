package jdbcCallablestatement;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;

public class EmployeeSalarySearchApp {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/company_db";
        String user = "postgres";
        String password = "1530";

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        // Setup table and stored procedure for PostgreSQL
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(50), " +
                    "salary DOUBLE PRECISION, " +
                    "department VARCHAR(50))");

            stmt.executeUpdate("DROP PROCEDURE IF EXISTS getEmployeeSalary(integer)");

            // Procedure with an OUT parameter for salary
            String createProc = "CREATE PROCEDURE getEmployeeSalary(p_id INT, OUT p_salary DOUBLE PRECISION) " +
                    "LANGUAGE plpgsql AS $$ " +
                    "BEGIN " +
                    "    SELECT salary INTO p_salary FROM employee WHERE id = p_id; " +
                    "END; $$";
            stmt.executeUpdate(createProc);
            System.out.println("Employee table and procedure ready!");

        } catch (SQLException e) {
            System.out.println("Setup error: " + e.getMessage());
        }

        do {
            System.out.println("\n===== EMPLOYEE SALARY SEARCH MENU =====");
            System.out.println("1. Search Employee Salary by ID");
            System.out.println("2. Exit");
            System.out.print("Enter your choice (1-2): ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    try (Connection conn = DriverManager.getConnection(url, user, password);
                         CallableStatement cstmt = conn.prepareCall("CALL getEmployeeSalary(?, ?)")) {

                        System.out.print("Enter Employee ID: ");
                        int empId = scanner.nextInt();
                        scanner.nextLine();

                        cstmt.setInt(1, empId);
                        // Register the OUT parameter for the salary
                        cstmt.registerOutParameter(2, Types.DOUBLE);

                        cstmt.execute();

                        double salary = cstmt.getDouble(2);
                        if (cstmt.wasNull()) {
                            System.out.println("Employee not found.");
                        } else {
                            System.out.println("Employee Salary: " + salary);
                        }

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
