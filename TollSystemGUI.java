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

public class TollSystemGUI {

    private TollBooth tollBooth;

    public TollSystemGUI() {
        tollBooth = new TollBooth();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Toll Tax Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Welcome To Toll Tax Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.red);
        frame.add(titleLabel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5,1,10,10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        

        JButton payTollButton = new JButton("Pay Toll");
        payTollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        payTollButton.setPreferredSize(new Dimension(450, 50));
        payTollButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        payTollButton.setBackground(Color.CYAN);

        JButton displayRecordsButton = new JButton("Display Records");
        displayRecordsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayRecordsButton.setPreferredSize(new Dimension(450, 50));
        displayRecordsButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        displayRecordsButton.setBackground(Color.CYAN);

        JButton generateReportButton = new JButton("Generate Revenue Report");
        generateReportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateReportButton.setPreferredSize(new Dimension(450, 50));
        generateReportButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        generateReportButton.setBackground(Color.CYAN);

        JButton deleteRecordButton = new JButton("Delete Record");
        deleteRecordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteRecordButton.setPreferredSize(new Dimension(450, 50));
        deleteRecordButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        deleteRecordButton.setBackground(Color.cyan);

        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setPreferredSize(new Dimension(450, 50));
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitButton.setBackground(Color.cyan);

        buttonPanel.add(payTollButton);
        buttonPanel.add(displayRecordsButton);
        buttonPanel.add(generateReportButton);
        buttonPanel.add(deleteRecordButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        // Action Listeners
        payTollButton.addActionListener(e -> openPayTollDialog());
        displayRecordsButton.addActionListener(e -> displayRecords());
        generateReportButton.addActionListener(e -> generateRevenueReport());
        deleteRecordButton.addActionListener(e -> openDeleteRecordDialog());
        exitButton.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private void openPayTollDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Pay Toll");
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel vehicleNumberLabel = new JLabel("Vehicle Number:");
        JTextField vehicleNumberField = new JTextField();

        JLabel vehicleTypeLabel = new JLabel("Vehicle Type:");
        JComboBox<String> vehicleTypeCombo = new JComboBox<>(new String[]{"LMV", "LCV", "Heavy Vehicle"});

        JLabel subTypeLabel = new JLabel("Subtype:");
        JComboBox<String> subTypeCombo = new JComboBox<>();

        vehicleTypeCombo.addActionListener(e -> {
            String selectedType = (String) vehicleTypeCombo.getSelectedItem();
            subTypeCombo.removeAllItems();
            if ("LMV".equals(selectedType)) {
                subTypeCombo.addItem("SUV");
                subTypeCombo.addItem("Sedan");
                subTypeCombo.addItem("Hatchback");
                subTypeCombo.addItem("Jeep");
            } else if ("LCV".equals(selectedType)) {
                subTypeCombo.addItem("Minibus");
                subTypeCombo.addItem("Goods Vehicle");
            } else if ("Heavy Vehicle".equals(selectedType)) {
                subTypeCombo.addItem("Bus");
                subTypeCombo.addItem("Truck");
                subTypeCombo.addItem("Construction Vehicle");
            }
        });

        JButton payButton = new JButton("Pay");
        JButton cancelButton = new JButton("Cancel");

        dialog.add(vehicleNumberLabel);
        dialog.add(vehicleNumberField);
        dialog.add(vehicleTypeLabel);
        dialog.add(vehicleTypeCombo);
        dialog.add(subTypeLabel);
        dialog.add(subTypeCombo);
        dialog.add(new JLabel());
        dialog.add(new JLabel());
        dialog.add(payButton);
        dialog.add(cancelButton);

        payButton.addActionListener(e -> {
            String vehicleNumber = vehicleNumberField.getText();
            String vehicleType = (String) vehicleTypeCombo.getSelectedItem();
            String subType = (String) subTypeCombo.getSelectedItem();

            if (!vehicleNumber.matches("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}")) {
                JOptionPane.showMessageDialog(dialog, "Invalid vehicle number format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (vehicleNumber.isEmpty() || vehicleType == null || subType == null) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Vehicle vehicle = null;
            if ("LMV".equals(vehicleType)) {
                vehicle = new LMV(vehicleNumber, subType);
            } else if ("LCV".equals(vehicleType)) {
                vehicle = new LCV(vehicleNumber, subType);
            } else if ("Heavy Vehicle".equals(vehicleType)) {
                vehicle = new HeavyVehicle(vehicleNumber, subType);
            }

            tollBooth.collectToll(vehicle);
            JOptionPane.showMessageDialog(dialog, "Toll Paid Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void displayRecords() {
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);

        StringBuilder records = new StringBuilder();
        String query = "SELECT * FROM toll_records";
        double totalRevenue = 0.0;

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                records.append("Vehicle Number: ").append(rs.getString("vehicle_number")).append("\n");
                records.append("Vehicle Type: ").append(rs.getString("vehicle_type")).append("\n");
                records.append("Subtype: ").append(rs.getString("sub_type")).append("\n");
                records.append("Toll Amount: ₹").append(rs.getDouble("toll_amount")).append("\n\n");
                totalRevenue += rs.getDouble("toll_amount");
            }
            records.append("Total Revenue Collected: ₹").append(totalRevenue).append("\n");

        } catch (SQLException e) {
            records.append("Error retrieving records: ").append(e.getMessage()).append("\n");
        }

        textArea.setText(records.toString());
        JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Toll Records", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateRevenueReport() {
        tollBooth.writeRevenueByVehicleType();
        JOptionPane.showMessageDialog(null, "Revenue reports generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openDeleteRecordDialog() {
        String vehicleNumber = JOptionPane.showInputDialog(null, "Enter Vehicle Number to Delete:", "Delete Record", JOptionPane.QUESTION_MESSAGE);
        if (vehicleNumber != null && !vehicleNumber.isEmpty()) {
            tollBooth.deleteRecord(vehicleNumber);
            JOptionPane.showMessageDialog(null, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Vehicle number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        new TollSystemGUI();
    }
}
