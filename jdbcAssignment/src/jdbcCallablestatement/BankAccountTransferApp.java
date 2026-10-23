package jdbcCallablestatement;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BankAccountTransferApp {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/bank_db";
        String user = "postgres";
        String password = "1530";

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        // Setup table and stored procedure for PostgreSQL
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS account (" +
                    "account_no INT PRIMARY KEY, " +
                    "holder_name VARCHAR(50), " +
                    "balance DOUBLE PRECISION)");

            stmt.executeUpdate("DROP PROCEDURE IF EXISTS transferAmount(integer, integer, double precision)");

            String createProc = "CREATE PROCEDURE transferAmount(from_acc INT, to_acc INT, transfer_amount DOUBLE PRECISION) " +
                    "LANGUAGE plpgsql AS $$ " +
                    "BEGIN " +
                    "    UPDATE account SET balance = balance - transfer_amount WHERE account_no = from_acc; " +
                    "    UPDATE account SET balance = balance + transfer_amount WHERE account_no = to_acc; " +
                    "END; $$";
            stmt.executeUpdate(createProc);
            System.out.println("Bank table and procedure ready!");

        } catch (SQLException e) {
            System.out.println("Setup error: " + e.getMessage());
        }

        do {
            System.out.println("\n===== BANK ACCOUNT TRANSFER MENU =====");
            System.out.println("1. Transfer ₹5,000 Between Accounts");
            System.out.println("2. Exit");
            System.out.print("Enter your choice (1-2): ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    try (Connection conn = DriverManager.getConnection(url, user, password);
                         CallableStatement cstmt = conn.prepareCall("CALL transferAmount(?, ?, ?)")) {

                        System.out.print("Enter Sender Account Number: ");
                        int sender = scanner.nextInt();
                        System.out.print("Enter Receiver Account Number: ");
                        int receiver = scanner.nextInt();
                        scanner.nextLine();

                        double amount = 5000.0; // Transferring ₹5,000 as required

                        cstmt.setInt(1, sender);
                        cstmt.setInt(2, receiver);
                        cstmt.setDouble(3, amount);

                        cstmt.execute();
                        System.out.println("Successfully transferred ₹5,000 from account " + sender + " to " + receiver + "!");

                    } catch (SQLException e) {
                        System.out.println("Transfer Failed: " + e.getMessage());
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