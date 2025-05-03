# Toll Tax Management System

This Java-based Toll Tax Management System is designed to efficiently manage toll collection operations. It features a user-friendly GUI, integrates with a MySQL database, and categorizes vehicles to calculate tolls and generate revenue reports.

---

## 🚀 Features

- **Vehicle Categorization**: Supports various vehicle types including LMV, LCV, Heavy Vehicles, Bikes, Cars, and Trucks.
- **GUI Interface**: Interactive Java Swing-based GUI for seamless user interaction.
- **Database Integration**: Connects to a MySQL database to store and retrieve toll records.
- **Revenue Reports**: Generates revenue reports for different vehicle categories.
- **Data Persistence**: Stores records in both text files and the MySQL database for redundancy.

---

## 🛠️ Technologies Used

- **Programming Language**: Java  
- **GUI Framework**: Java Swing  
- **Database**: MySQL  
- **JDBC Driver**: MySQL Connector/J  

---

## 📁 Project Structure
```
├── .vscode/
├── Bike.class
├── Car.class
├── DBConnection.class
├── DBConnection.java
├── HV_Revenue.txt
├── HeavyVehicle.class
├── LCV.class
├── LCV_Revenue.txt
├── LMV.class
├── LMV_Revenue.txt
├── Toll Tax Management System(Phase-1).docx
├── Toll Tax System [Final].pdf
├── TollBooth.class
├── TollSystem.class
├── TollSystem.java
├── TollSystemGUI.class
├── TollSystemGUI.java
├── Toll_tax.sql
├── Truck.class
├── Vehicle.class
├── mysql-connector-j-9.3.0.jar


```
---

## 🧰 Setup Instructions

### Prerequisites

- Java Development Kit (JDK) installed  
- MySQL Server installed  
- MySQL Connector/J JDBC driver  

### Steps

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/kanishk-16/java-project.git
   cd java-project
2. **Set Up the Database**:

- Open MySQL Workbench or your preferred MySQL client.

- Create a new database (e.g., toll_tax_db).

- Import the Toll_tax.sql file to set up the necessary tables.

3. **Configure Database Connection**:

- Open DBConnection.java and update the credentials:

  ```java
  String url = "jdbc:mysql://localhost:3306/toll_tax_db";
  String user = "your_username";
  String password = "your_password";

4. **Compile the Java Files**:

   ```bash
   javac -cp .;mysql-connector-j-9.3.0.jar *.java
5. **Run the Application**:

   ```bash
   java -cp .;mysql-connector-j-9.3.0.jar TollSystemGUI

## 📄 Documentation
- Toll Tax Management System(Phase-1).docx: Initial project documentation outlining the system's requirements and design.
- Toll Tax System [Final].pdf: Final report detailing the implementation and testing of the system.

## 📌 Notes
- Ensure that the MySQL server is running before launching the application.
- The application stores revenue data in text files (HV_Revenue.txt, LCV_Revenue.txt, LMV_Revenue.txt) as well as in the MySQL database.
- Modify the TollSystemGUI.java file to customize the GUI as per your requirements.


