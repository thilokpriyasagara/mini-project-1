import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int choice;
        createStudentsTable();

        do {
            System.out.println("\n===== Welcome To The Student Database =====");
            System.out.println("1. Add New Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Search Student by Name");
            System.out.println("6. Exit");
            System.out.println("===========================================");

            System.out.print("Enter your choice (1-6): ");
            choice = getValidInt(scanner);

            switch (choice) {
                case 1:
                    addNewStudent(scanner);
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    updateStudent(scanner);
                    break;
                case 4:
                    deleteStudent(scanner);
                    break;
                case 5:
                    searchStudentByName(scanner);
                    break;
                case 6:
                    System.out.println("Thank You for using Student Database.... Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please Enter Number Between 1 and 6");
            }

        } while (choice != 6);

        scanner.close();
    }

    private static void createStudentsTable() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/studentdb", "root", "1234");
             Statement stmt = con.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS students (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100), " +
                    "age INT, " +
                    "grade VARCHAR(50)" +
                    ")";
            stmt.executeUpdate(sql);
            System.out.println("Table ready for use!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getDbConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdb", "root", "1234");
    }

    private static void addNewStudent(Scanner scanner) {
        scanner.nextLine();
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter student age: ");
        int age = getValidInt(scanner);

        scanner.nextLine();
        System.out.print("Enter student grade: ");
        String grade = scanner.nextLine().trim();

        if (name.isEmpty() || grade.isEmpty()) {
            System.out.println("Name and grade cannot be empty. Operation cancelled.");
            return;
        }

        String sql = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, grade);
            int rowsAffected = pstmt.executeUpdate();
            conn.commit();
            System.out.println(rowsAffected + " student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    private static void viewAllStudents() {
        String sql = "SELECT id, name, age, grade FROM students";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            boolean hasResults = false;
            System.out.println("\nList of students:");
            while (rs.next()) {
                hasResults = true;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String grade = rs.getString("grade");
                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age + ", Grade: " + grade);
            }
            if (!hasResults) {
                System.out.println("No students found.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing students: " + e.getMessage());
        }
    }

    private static void updateStudent(Scanner scanner) {
        System.out.print("Enter student ID to update: ");
        int id = getValidInt(scanner);
        scanner.nextLine(); // consume newline

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine().trim();

        System.out.print("Enter new age: ");
        int newAge = getValidInt(scanner);

        scanner.nextLine(); // consume newline
        System.out.print("Enter new grade: ");
        String newGrade = scanner.nextLine().trim();

        if (newName.isEmpty() || newGrade.isEmpty()) {
            System.out.println("Name and grade cannot be empty. Operation cancelled.");
            return;
        }

        String sql = "UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setString(3, newGrade);
            pstmt.setInt(4, id);
            int rowsAffected = pstmt.executeUpdate();
            conn.commit();
            if (rowsAffected > 0) {
                System.out.println("Student updated successfully.");
            } else {
                System.out.println("No student found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    private static void deleteStudent(Scanner scanner) {
        System.out.print("Enter student ID to delete: ");
        int id = getValidInt(scanner);

        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            conn.commit();
            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("No student found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }

    private static void searchStudentByName(Scanner scanner) {
        scanner.nextLine(); // consume newline
        System.out.print("Enter student name to search: ");
        String searchName = scanner.nextLine().trim();

        if (searchName.isEmpty()) {
            System.out.println("Search name cannot be empty. Operation cancelled.");
            return;
        }

        String sql = "SELECT id, name, age, grade FROM students WHERE name LIKE ?";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasResults = false;
                System.out.println("\nSearch results:");
                while (rs.next()) {
                    hasResults = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    String grade = rs.getString("grade");
                    System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age + ", Grade: " + grade);
                }
                if (!hasResults) {
                    System.out.println("No students found matching '" + searchName + "'.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching students: " + e.getMessage());
        }
    }

    private static int getValidInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
