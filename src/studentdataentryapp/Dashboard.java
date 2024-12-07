/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package studentdataentryapp;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import java.sql.*;

public class Dashboard extends Application {

    private TableView<Student> table;
    private final String databaseUrl = "jdbc:mysql://localhost:3306/student_details";
    private final String databaseUser = "root"; 
    private final String databasePassword = "Ricky2003Munyua"; 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        table = new TableView<>();
        table.setEditable(true);

        table.setBackground(new Background(new BackgroundFill(Color.web("#ADD8E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        TableColumn<Student, String> registrationNumberCol = new TableColumn<>("Registration Number");
        registrationNumberCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> nationalIdCol = new TableColumn<>("National ID");
        nationalIdCol.setCellValueFactory(new PropertyValueFactory<>("nationalID"));

        TableColumn<Student, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> phoneNumberCol = new TableColumn<>("Phone Number");
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        table.getColumns().addAll(registrationNumberCol, nameCol, nationalIdCol, addressCol, emailCol, phoneNumberCol);

        TextField registrationNumberInput = new TextField();
        registrationNumberInput.setPromptText("Registration Number");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Full Name");

        TextField nationalIdInput = new TextField();
        nationalIdInput.setPromptText("National ID");

        TextField addressInput = new TextField();
        addressInput.setPromptText("Address");

        TextField emailInput = new TextField();
        emailInput.setPromptText("Email");

        TextField phoneNumberInput = new TextField();
        phoneNumberInput.setPromptText("Phone Number");

        Button addButton = new Button("Add Student");
        addButton.setStyle("-fx-background-color: #39FF14;");
        addButton.setOnAction(e -> {
            Student student = new Student(
                    registrationNumberInput.getText(),
                    nameInput.getText(),
                    nationalIdInput.getText(),
                    addressInput.getText(),
                    emailInput.getText(),
                    phoneNumberInput.getText());
            addStudentToDatabase(student); // Modifying the data base
            table.getItems().add(student); //modifying the TableView
            
            // Clearing input fields
            registrationNumberInput.clear();
            nameInput.clear();
            nationalIdInput.clear();
            addressInput.clear();
            emailInput.clear();
            phoneNumberInput.clear();
        });

        Button updateButton = new Button("Update Selected Student");
        updateButton.setStyle("-fx-background-color: #FFFF00;");
        updateButton.setOnAction(e -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                updateStudentInDatabase(selected, new Student(
                        registrationNumberInput.getText(),
                        nameInput.getText(),
                        nationalIdInput.getText(),
                        addressInput.getText(),
                        emailInput.getText(),
                        phoneNumberInput.getText()
                ));
                table.refresh(); // Refresh the TableView to show updated values
            }
        });

        Button deleteButton = new Button("Delete Selected Student");
        deleteButton.setStyle("-fx-background-color: #FF6347;");
        deleteButton.setOnAction(e -> {
            Student selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteStudentFromDatabase(selected.getRegistrationNumber()); // Modifying the database first
                table.getItems().remove(selected); // Then modify the Tableview
            }
        });

        HBox buttonsHBox = new HBox(10);
        buttonsHBox.getChildren().addAll(addButton, updateButton, deleteButton);

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        vBox.setStyle("-fx-background-color: #FFA500;");
        vBox.getChildren().addAll(table, registrationNumberInput, nameInput, nationalIdInput, addressInput, emailInput, phoneNumberInput, buttonsHBox);

        Scene scene = new Scene(vBox);
        primaryStage.setTitle("Student Data Entry App");
        primaryStage.setScene(scene);
        primaryStage.setWidth(550);
        primaryStage.setHeight(600);
        primaryStage.show();

        loadStudentsData();
    }

    private void loadStudentsData() {
        try (
            Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
        ) {
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("registration_number"),
                    rs.getString("name"),
                    rs.getString("national_id"),
                    rs.getString("address"),
                    rs.getString("email"),
                    rs.getString("phone_number")
                );
                table.getItems().add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addStudentToDatabase(Student student) {
        try (
            Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO students (registration_number, name, national_id, address, email, phone_number) VALUES (?, ?, ?, ?, ?, ?)"
            );
        ) {
            pstmt.setString(1, student.getRegistrationNumber());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getNationalID());
            pstmt.setString(4, student.getAddress());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhoneNumber());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStudentInDatabase(Student oldStudent, Student newStudent) {
        try (
            Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE students SET registration_number = ?, name = ?, national_id = ?, address = ?, email = ?, phone_number = ? WHERE registration_number = ?"
            );
        ) {
            pstmt.setString(1, newStudent.getRegistrationNumber());
            pstmt.setString(2, newStudent.getName());
            pstmt.setString(3, newStudent.getNationalID());
            pstmt.setString(4, newStudent.getAddress());
            pstmt.setString(5, newStudent.getEmail());
            pstmt.setString(6, newStudent.getPhoneNumber());
            pstmt.setString(7, oldStudent.getRegistrationNumber());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                oldStudent.setRegistrationNumber(newStudent.getRegistrationNumber());
                oldStudent.setName(newStudent.getName());
                oldStudent.setNationalID(newStudent.getNationalID());
                oldStudent.setAddress(newStudent.getAddress());
                oldStudent.setEmail(newStudent.getEmail());
                oldStudent.setPhoneNumber(newStudent.getPhoneNumber());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteStudentFromDatabase(String registrationNumber) {
        try (
            Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
            PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM students WHERE registration_number = ?"
            );
        ) {
            pstmt.setString(1, registrationNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Student {
        private final SimpleStringProperty registrationNumber;
        private final SimpleStringProperty name;
        private final SimpleStringProperty nationalID;
        private final SimpleStringProperty address;
        private final SimpleStringProperty email;
        private final SimpleStringProperty phoneNumber;

        public Student(String registrationNumber, String name, String nationalID, String address, String email, String phoneNumber) {
            this.registrationNumber = new SimpleStringProperty(registrationNumber);
            this.name = new SimpleStringProperty(name);
            this.nationalID = new SimpleStringProperty(nationalID);
            this.address = new SimpleStringProperty(address);
            this.email = new SimpleStringProperty(email);
            this.phoneNumber = new SimpleStringProperty(phoneNumber);
        }

        public String getRegistrationNumber() {
            return registrationNumber.get();
        }

        public void setRegistrationNumber(String value) {
            registrationNumber.set(value);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String value) {
            name.set(value);
        }

        public String getNationalID() {
            return nationalID.get();
        }

        public void setNationalID(String value) {
            nationalID.set(value);
        }

        public String getAddress() {
            return address.get();
        }

        public void setAddress(String value) {
            address.set(value);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String value) {
            email.set(value);
        }

        public String getPhoneNumber() {
            return phoneNumber.get();
        }

        public void setPhoneNumber(String value) {
            phoneNumber.set(value);
        }
    }
}
