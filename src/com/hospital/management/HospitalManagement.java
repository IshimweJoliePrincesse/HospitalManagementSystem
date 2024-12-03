// package com.hospital.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class HospitalManagement {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successfully!");

            Patient patient = new Patient(connection, scanner);
            Doctors doctor = new Doctors(connection);

            while (true) {
                System.out.println("\nHOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. Update Patient");
                System.out.println("4. Delete Patient");
                System.out.println("5. Add Doctor");
                System.out.println("6. View Doctors");
                System.out.println("7. Update Doctor");
                System.out.println("8. Delete Doctor");
                System.out.println("9. Book Appointment");
                System.out.println("10. View Appointments");
                System.out.println("11. Update Appointment");
                System.out.println("12. Delete Appointment");
                System.out.println("13. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        patient.updatePatient();
                        break;
                    case 4:
                        patient.deletePatient();
                        break;
                    case 5:
                        doctor.addDoctor();
                        break;
                    case 6:
                        doctor.viewDoctors();
                        break;
                    case 7:
                        doctor.updateDoctor();
                        break;
                    case 8:
                        doctor.deleteDoctor();
                        break;
                    case 9:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 10:
                        viewAppointments(connection);
                        break;
                    case 11:
                        updateAppointment(connection, scanner);
                        break;
                    case 12:
                        deleteAppointment(connection, scanner);
                        break;
                    case 13:
                        connection.close();
                        System.out.println("Connection closed!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close the connection: " + e.getMessage());
                }
            }
            scanner.close();
        }
    }

    public static void bookAppointment(Patient patient, Doctors doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter patient ID:");
        int patientId = scanner.nextInt();
        System.out.println("Enter doctor ID:");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int rows = preparedStatement.executeUpdate();
                    if (rows > 0) {
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

    public static void viewAppointments(Connection connection) {
        String query = "SELECT * FROM appointments";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("\nAppointments:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int patientId = resultSet.getInt("patient_id");
                int doctorId = resultSet.getInt("doctor_id");
                String appointmentDate = resultSet.getString("appointment_date");

                System.out.println("ID: " + id + ", Patient ID: " + patientId + ", Doctor ID: " + doctorId + ", Date: " + appointmentDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAppointment(Connection connection, Scanner scanner) {
        System.out.println("Enter appointment ID to update:");
        int appointmentId = scanner.nextInt();
        System.out.println("Enter new doctor ID:");
        int doctorId = scanner.nextInt();
        System.out.println("Enter new appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        String query = "UPDATE appointments SET doctor_id = ?, appointment_date = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            preparedStatement.setInt(3, appointmentId);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                System.out.println("Appointment updated successfully!");
            } else {
                System.out.println("Failed to update appointment!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAppointment(Connection connection, Scanner scanner) {
        System.out.println("Enter appointment ID to delete:");
        int appointmentId = scanner.nextInt();

        String query = "DELETE FROM appointments WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointmentId);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                System.out.println("Appointment deleted successfully!");
            } else {
                System.out.println("Failed to delete appointment!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    System.out.println("Doctor is not available on the specified date!");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
