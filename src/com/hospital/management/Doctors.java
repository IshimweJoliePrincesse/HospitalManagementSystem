// package com.hospital.management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Doctors {
    private Connection connection;
    private Scanner scanner = new Scanner(System.in);

    // Constructor to initialize the database connection
    public Doctors(Connection connection) {
        this.connection = connection;
    }

    // View all doctors
    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Doctors:");
            System.out.println("+-------------+----------------------+--------------------+");
            System.out.println("| ID          | Doctor Name          | Specialization     |");
            System.out.println("+-------------+----------------------+--------------------+");
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("|%-8d|%-20s|%-20s|\n", id, name, specialization);
                System.out.println("+-------------+----------------------+--------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new doctor
    public void addDoctor() {
        System.out.print("Enter doctor name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter doctor specialization: ");
        String specialization = scanner.nextLine();

        try {
            String query = "INSERT INTO doctors (name, specialization) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, specialization);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor added successfully!");
            } else {
                System.out.println("Failed to add doctor.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a doctor's information
    public void updateDoctor() {
        System.out.print("Enter doctor ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter new specialization: ");
        String specialization = scanner.nextLine();

        try {
            String query = "UPDATE doctors SET name = ?, specialization = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, specialization);
            preparedStatement.setInt(3, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor updated successfully!");
            } else {
                System.out.println("Failed to update doctor.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a doctor by ID
    public void deleteDoctor() {
        System.out.print("Enter doctor ID to delete: ");
        int id = scanner.nextInt();

        try {
            String query = "DELETE FROM doctors WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor deleted successfully!");
            } else {
                System.out.println("Failed to delete doctor. No such doctor found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int doctorId) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                return true; // Doctor exists
            } else {
                return false; // Doctor does not exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // Default to false if an exception occurs
    }
    
}
