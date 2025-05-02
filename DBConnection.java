import java.sql.*;

class DBConnection {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/toll_system"; // Database details
        String username = "root"; // MySQL credentials
        String password = "kanishk@16";

        // Load and register the driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish connection
        Connection con = DriverManager.getConnection(url, username, password);
        System.out.println("Connection Established successfully");
    }
}
