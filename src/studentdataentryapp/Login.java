/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package studentdataentryapp;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static javafx.application.Application.launch;

public class Login extends Application {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_accounts?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "Ricky2003Munyua";  

    Stage primaryStage;

    private TextField tfUsername;
    private PasswordField pfPassword;
    private Label lblStatus;
    private TextField tfNewUsername;
    private PasswordField pfNewPassword;
    private PasswordField pfConfirmPassword;
    private Label lblSignUpStatus;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Scene sceneLogin = createLoginScene();

        primaryStage.setTitle("Student Data Entry App");
        primaryStage.setScene(sceneLogin);
        primaryStage.show();
    }

    private Scene createLoginScene() {
        tfUsername = new TextField();
        pfPassword = new PasswordField();
        lblStatus = new Label();

        Button btnLogin = new Button("Login");
        Button btnSignUp = new Button("Sign Up");
        tfUsername.setMaxWidth(150); 
        pfPassword.setMaxWidth(150); 
    //styling the login scene
    btnLogin.setStyle("-fx-background-color: #008080; -fx-text-fill: #FFFFFF;");
    btnSignUp.setStyle("-fx-background-color: #4169E1; -fx-text-fill: #FFFFFF;");
    pfPassword.setStyle("-fx-background-color: #333333; -fx-text-fill: #CCCCCC;");
    tfUsername.setStyle("-fx-background-color: #333333; -fx-text-fill: #CCCCCC;");


        VBox vboxLogin = new VBox(10);
        vboxLogin.setAlignment(Pos.CENTER);
        vboxLogin.getChildren().addAll(new Label("Username:"), tfUsername, new Label("Password:"), pfPassword, btnLogin, btnSignUp, lblStatus);
        vboxLogin.setStyle("-fx-background-color: #FFA500;");
        btnLogin.setOnAction(this::handleLoginAction);
        btnSignUp.setOnAction(e -> primaryStage.setScene(createSignUpScene()));

        return new Scene(vboxLogin, 300, 200);
    }

    private Scene createSignUpScene() {
        tfNewUsername = new TextField();
        pfNewPassword = new PasswordField();
        pfConfirmPassword = new PasswordField();
        lblSignUpStatus = new Label();

        Button btnCreateAccount = new Button("Create Account");
        Button btnBackToLogin = new Button("Back to Login");
        tfNewUsername.setMaxWidth(200);
        pfNewPassword.setMaxWidth(200);
        pfConfirmPassword.setMaxWidth(200);

        // Styling the signup scene
        btnCreateAccount.setStyle("-fx-background-color: #4169E1; -fx-text-fill: #FFFFFF;");
        btnBackToLogin.setStyle("-fx-background-color: #2F4F4F; -fx-text-fill: #B0C4DE;");
        pfConfirmPassword.setStyle("-fx-background-color: #2F4F4F; -fx-text-fill: #CCCCCC;");
        pfNewPassword.setStyle("-fx-background-color: #2F4F4F; -fx-text-fill: #CCCCCC;");
        tfNewUsername.setStyle("-fx-background-color: #2F4F4F; -fx-text-fill: #CCCCCC;");

        VBox vboxSignUp = new VBox(10);
        vboxSignUp.setAlignment(Pos.CENTER);
        vboxSignUp.getChildren().addAll(new Label("Username:"), tfNewUsername, new Label("Password:"), pfNewPassword, new Label("Confirm Password:"), pfConfirmPassword, btnCreateAccount, lblSignUpStatus, btnBackToLogin);
        vboxSignUp.setStyle("-fx-background-color: #FFA500;");
        btnCreateAccount.setOnAction(this::handleCreateAccountAction);
        btnBackToLogin.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        return new Scene(vboxSignUp, 300, 200);
    }

    private void handleLoginAction(ActionEvent event) {
        String username = tfUsername.getText();
        String password = pfPassword.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lblStatus.setText("Login successful!");
                } else {
                    lblStatus.setText("Login failed. Invalid username or password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Database error.");
        }
    }

    private void handleCreateAccountAction(ActionEvent event) {
        String newUsername = tfNewUsername.getText();
        String newPassword = pfNewPassword.getText();
        String confirmPassword = pfConfirmPassword.getText();

        if (!newPassword.equals(confirmPassword)) {
            lblSignUpStatus.setText("Passwords do not match.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {

            pstmt.setString(1, newUsername);
            pstmt.setString(2, newPassword);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                lblSignUpStatus.setText("Account successfully created.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblSignUpStatus.setText("Database error.");
        }
    }
}
