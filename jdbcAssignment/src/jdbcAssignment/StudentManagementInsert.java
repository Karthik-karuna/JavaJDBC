package jdbcAssignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentManagementInsert {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/college_db";
        String user = "postgres";
        String password = "1530";

        String sql = "INSERT INTO student (id, name, age, course) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Student 1
            ps.setInt(1, 101);
            ps.setString(2, "John Doe");
            ps.setInt(3, 20);
            ps.setString(4, "Computer Science");
            ps.executeUpdate();
            System.out.println("Student inserted successfully");

            // Student 2
            ps.setInt(1, 102);
            ps.setString(2, "Jane Smith");
            ps.setInt(3, 21);
            ps.setString(4, "Information Technology");
            ps.executeUpdate();
            System.out.println("Student inserted successfully");

            // Student 3
            ps.setInt(1, 103);
            ps.setString(2, "Mike Ross");
            ps.setInt(3, 22);
            ps.setString(4, "Data Science");
            ps.executeUpdate();
            System.out.println("Student inserted successfully");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}