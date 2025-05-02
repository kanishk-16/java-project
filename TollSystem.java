
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/toll_system";
    private static final String USER = "root";
    private static final String PASSWORD = "kanishk@16";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

// This is my Base class for Vehicles
abstract class Vehicle {

    protected String vehicleNumber;
    protected String vehicleType;

    public Vehicle(String vehicleNumber, String vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    public abstract double calculateToll();

    public void displayInfo() {
        System.out.println("Vehicle Number: " + vehicleNumber);
        System.out.println("Vehicle Type: " + vehicleType);
        System.out.println("Toll Amount: " + calculateToll());
    }
    
    public String toFileString() {
        return vehicleNumber + "," + vehicleType + "," + calculateToll();
    }

    public abstract String getSubType();
}

// Derived classes for specific vehicle types
class LMV extends Vehicle {

    private String LMVtype;

    public LMV(String vehicleNumber, String LMVtype) {
        super(vehicleNumber, "Light Motor Vehicle");
        this.LMVtype = LMVtype;
    }

    @Override
    public double calculateToll() {
        switch (LMVtype.toLowerCase()) {
            case "suv":
                return 70.0;
            case "sedan":
                return 60.0;
            case "hatchback":
                return 50.0;
            case "jeep":
                return 40.0;
            default:
                return 50.0;
        }
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Car Type: " + LMVtype);
    }

    @Override
    public String toFileString() {
        return vehicleNumber + "," + vehicleType + "," + LMVtype + "," + calculateToll();
    }

    @Override
    public String getSubType() {
        return LMVtype;
    }
}

class LCV extends Vehicle {

    private String LCVtype;

    public LCV(String vehicleNumber, String LCVtype) {
        super(vehicleNumber, "Light Commercial Vehicle");
        this.LCVtype = LCVtype;
    }

    public double calculateToll() {
        switch (LCVtype.toLowerCase()) {
            case "minibus":
                return 100.0;
            case "goods vehicle":
                return 120.0;
            default:
                return 100.0;
        }
    }

    public void displayInfo() {
        super.displayInfo();
        System.out.println("LCV type: " + LCVtype);
    }

    public String toFileString() {
        return vehicleNumber + "," + vehicleType + "," + LCVtype + "," + calculateToll();
    }

    @Override
    public String getSubType() {
        return LCVtype;
    }
}

class HeavyVehicle extends Vehicle {

    private String HeavyVehicleType;

    public HeavyVehicle(String vehicleNumber, String HeavyVehicleType) {
        super(vehicleNumber, "Heavy Vehicle");
        this.HeavyVehicleType = HeavyVehicleType;
    }

    public double calculateToll() {
        switch (HeavyVehicleType.toLowerCase()) {
            case "bus":
                return 150.0;
            case "truck":
                return 170.0;
            case "construction vehicle":
                return 200.0;
            default:
                return 150.0;
        }
    }

    public void displayInfo() {
        super.displayInfo();
        System.out.println("Heavy Vehicle Type: " + HeavyVehicleType);
    }

    public String toFileString() {
        return vehicleNumber + "," + vehicleType + "," + HeavyVehicleType + "," + calculateToll();
    }

    @Override
    public String getSubType() {
        return HeavyVehicleType;
    }
}

// TollBooth class to handle toll collection
class TollBooth {

    private static final String FILE_NAME = "toll_records.txt";
    private Vehicle[] vehicleRecords = new Vehicle[100]; // Used Fixed-size array instead of ArrayList
    private int count = 0;

    public void deleteRecord(String vehicleNumber) {
        String query = "DELETE FROM toll_records WHERE vehicle_number = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
    
            pst.setString(1, vehicleNumber);
            int rows = pst.executeUpdate();
            if (rows > 0)
                System.out.println("Record deleted successfully.");
            else
                System.out.println("Vehicle number not found.");
    
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
      }
    }

    public void collectToll(Vehicle vehicle) {
        System.out.println("Processing Toll for Vehicle: " + vehicle.vehicleNumber);
        vehicle.displayInfo();
        System.out.println("Toll Paid Successfully!\n");
        insertRecordToDB(vehicle);
        generateReceipt(vehicle);
    }

    private void generateReceipt(Vehicle vehicle) {
        String receiptFileName = "Receipt_" + vehicle.vehicleNumber + ".txt";
        try (FileWriter fw = new FileWriter(receiptFileName)) {
            fw.write("------ Toll Payment Receipt ------\n");
            fw.write("Vehicle Number: " + vehicle.vehicleNumber + "\n");
            fw.write("Vehicle Type: " + vehicle.vehicleType + "\n");
            fw.write("Toll Amount Paid: ₹" + vehicle.calculateToll() + "\n");
            fw.write("Payment Status: SUCCESS\n");
            fw.write("----------------------------------\n");
            System.out.println("Receipt Generated: " + receiptFileName);
        } catch (IOException e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
    }

    public void displayRecords() {
        String query = "SELECT * FROM toll_records";
        double totalRevenue = 0.0;
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
    
            System.out.println("--- Toll Collection Records ---");
            while (rs.next()) {
                System.out.println("Vehicle Number: " + rs.getString("vehicle_number"));
                System.out.println("Vehicle Type: " + rs.getString("vehicle_type"));
                System.out.println("Subtype: " + rs.getString("sub_type"));
                System.out.println("Toll Amount: ₹" + rs.getDouble("toll_amount"));
                System.out.println();
                totalRevenue += rs.getDouble("toll_amount");
            }
            System.out.println("Total Revenue Collected: ₹" + totalRevenue);
    
        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    private void insertRecordToDB(Vehicle vehicle) {
        String query = "INSERT INTO toll_records (vehicle_number, vehicle_type, sub_type, toll_amount) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
    
            pst.setString(1, vehicle.vehicleNumber);
            pst.setString(2, vehicle.vehicleType);
            pst.setString(3, vehicle.getSubType());
            pst.setDouble(4, vehicle.calculateToll());
    
            pst.executeUpdate();
            System.out.println("Toll record saved to database!");
    
        } catch (SQLException e) {
            System.out.println("Error saving to database: " + e.getMessage());
        }
    }

    

    public void writeRevenueByVehicleType() {
        double LMVTotal = 0, LCVTotal = 0, HVTotal = 0;
    
        String query = "SELECT * FROM toll_records";
    
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query);
             FileWriter lmvWrite = new FileWriter("LMV_Revenue.txt");
             FileWriter lcvWrite = new FileWriter("LCV_Revenue.txt");
             FileWriter hvWrite = new FileWriter("HV_Revenue.txt")) {
    
            while (rs.next()) {
                String vNum = rs.getString("vehicle_number");
                String vType = rs.getString("vehicle_type");
                String subType = rs.getString("sub_type");
                double toll = rs.getDouble("toll_amount");
    
                String record = vNum + "," + vType + "," + subType + "," + toll + "\n";
    
                switch (vType) {
                    case "Light Motor Vehicle" -> {
                        LMVTotal += toll;
                        lmvWrite.write(record);
                    }
                    case "Light Commercial Vehicle" -> {
                        LCVTotal += toll;
                        lcvWrite.write(record);
                    }
                    case "Heavy Vehicle" -> {
                        HVTotal += toll;
                        hvWrite.write(record);
                    }
                }
            }
    
            // Write total revenue to each file
            lmvWrite.write("Total LMV Revenue: ₹" + LMVTotal + "\n");
            lcvWrite.write("Total LCV Revenue: ₹" + LCVTotal + "\n");
            hvWrite.write("Total Heavy Vehicle Revenue: ₹" + HVTotal + "\n");
    
            System.out.println("Revenue reports generated successfully!");
    
        } catch (Exception e) {
            System.out.println("Error generating revenue files: " + e.getMessage());
        }
    }
}



// Main system controller
public class TollSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TollBooth tollBooth = new TollBooth();

        while (true) {
            System.out.println("\n----- Welcome to my Toll Tax Management System--------");
            System.out.println("1. Pay Toll");
            System.out.println("2. Display Records");
            System.out.println("3. Generate Revenue Report");
            System.out.println("4. Delete Record");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                if (choice == 1) {
                    System.out.print("Enter Vehicle Number: ");
                    String vehicleNumber = scanner.nextLine();
                    System.out.println("Select Vehicle Type: 1. LMV  2. LCV  3. Heavy Vehicle");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle = null;
                    if (type == 1) {
                        System.out.println("Select LMV Type: 1. SUV 2. Sedan 3. Hatchback 4. Jeep");
                        int LMVchoice = scanner.nextInt();
                        scanner.nextLine();
                        String LMVtype = (LMVchoice == 1) ? "SUV" : (LMVchoice == 2) ? "Sedan" : (LMVchoice == 3) ? "Hatchback" : "Jeep";
                        vehicle = new LMV(vehicleNumber, LMVtype);
                    } else if (type == 2) {
                        System.out.println("Select LCV Type: 1. Minibus 2. Goods Vehicle");
                        int LCVchoice = scanner.nextInt();
                        scanner.nextLine();
                        String LCVtype = (LCVchoice == 1) ? "Minibus" : "Goods Vehicle";
                        vehicle = new LCV(vehicleNumber, LCVtype);
                    } else if (type == 3) {
                        System.out.println("Select Heavy Vehicle Type: 1. Bus 2. Truck 3. Construction Vehicle");
                        int HVchoice = scanner.nextInt();
                        scanner.nextLine();
                        String HVtype = (HVchoice == 1) ? "Bus" : (HVchoice == 2) ? "Truck" : "Construction Vehicle";
                        vehicle = new HeavyVehicle(vehicleNumber, HVtype);
                    }
                    tollBooth.collectToll(vehicle);
                } else if (choice == 2) {
                    tollBooth.displayRecords();
                } else if (choice == 3) {
                    tollBooth.writeRevenueByVehicleType();
                } else if (choice == 4) {
                    System.out.print("Enter Vehicle Number to Delete: ");
                    String vehicleNumber = scanner.nextLine();
                    tollBooth.deleteRecord(vehicleNumber);
                } else if (choice == 5) {
                    break;
                } else {
                    System.out.println("Invalid choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter a number please !");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}
