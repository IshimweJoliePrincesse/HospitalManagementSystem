// package com.hospital.management;
import java.sql.*;
import java.util.Scanner;

public class Appointment {

    // Method to add an appointment
    public static void addAppointment(Patient patient, Doctors doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter patient ID:");
        int patientId = scanner.nextInt();
        System.out.println("Enter doctor ID:");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Appointment booked successfully!");
                    } else {
                        System.out.println("Failed to book appointment!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Invalid patient ID or doctor ID!");
        }
    }

    // Method to view all appointments
    public static void viewAppointments(Connection connection) {
        String query = "SELECT * FROM appointments";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("id");
                int patientId = resultSet.getInt("patient_id");
                int doctorId = resultSet.getInt("doctor_id");
                String appointmentDate = resultSet.getString("appointment_date");

                System.out.println("Appointment ID: " + appointmentId + ", Patient ID: " + patientId + ", Doctor ID: " + doctorId + ", Date: " + appointmentDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update an appointment
    public static void updateAppointment(Connection connection, Scanner scanner) {
        System.out.println("Enter appointment ID to update:");
        int appointmentId = scanner.nextInt();
        System.out.println("Enter new doctor ID:");
        int doctorId = scanner.nextInt();
        System.out.println("Enter new appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        String updateQuery = "UPDATE appointments SET doctor_id = ?, appointment_date = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            preparedStatement.setInt(3, appointmentId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Appointment updated successfully!");
            } else {
                System.out.println("Appointment update failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete an appointment
    public static void deleteAppointment(Connection connection, Scanner scanner) {
        System.out.println("Enter appointment ID to delete:");
        int appointmentId = scanner.nextInt();

        String deleteQuery = "DELETE FROM appointments WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, appointmentId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Appointment deleted successfully!");
            } else {
                System.out.println("Appointment deletion failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to check if the doctor is available at the given date
    private static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String checkQuery = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkQuery);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            int count = resultSet.getInt(1);
            if (count > 0) {
                System.out.println("Doctor is not available at the chosen time.");
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Main method to run the CRUD operations for appointments
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null; // Establish the connection to the database

        // Ensure to replace this with actual connection initialization
        // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "user", "password");

        while (true) {
            System.out.println("HOSPITAL MANAGEMENT SYSTEM");
            System.out.println("1. Add Appointment");
            System.out.println("2. View Appointments");
            System.out.println("3. Update Appointment");
            System.out.println("4. Delete Appointment");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // You need to pass patient and doctor objects from your system
                    addAppointment(new Patient(connection, scanner), new Doctors(connection), connection, scanner);
                    break;
                case 2:
                    viewAppointments(connection);
                    break;
                case 3:
                    updateAppointment(connection, scanner);
                    break;
                case 4:
                    deleteAppointment(connection, scanner);
                    break;
                case 5:
                    try {
                        if (connection != null) {
                            connection.close();
                            System.out.println("Connection closed!");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
