package pagesMainFeatures;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClassesUser.User;
import pagesMainNavigation.PageSetupLoginSelection;
import validationEvaluators.EvaluatorEmail;
import validationEvaluators.RecognizerName;
import validationEvaluators.EvaluatorPassword;
import validationEvaluators.RecognizerUserName;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import databasePart1.*;

/*
 * The PageAdminSetup class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class PageAdminSetup {
	
    private final DatabaseHelper databaseHelper;

    public PageAdminSetup(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(250);

        TextField realNameField = new TextField();
        realNameField.setPromptText("Enter Your Name");
        realNameField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Your Email Address");
        emailField.setMaxWidth(250);        
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button setupButton = new Button("Setup");

        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();
            String passwordConfirmation = confirmPasswordField.getText().trim();
            String realName = realNameField.getText();
            String email = emailField.getText().trim();

            List<String> roles = Arrays.asList("admin"); //Start as admin role

            try {
                // Check if the username already exists
                if (databaseHelper.doesUserExist(userName)) {
                    errorLabel.setText("Error: Username already taken.");
                    return;
                }


                String userNameError = RecognizerUserName.checkForValidUserName(userName);
                if (!userNameError.isEmpty()) {
                    errorLabel.setText(userNameError);
                    return;
                }

                // Password valid check
                String passwordError = EvaluatorPassword.evaluatePassword(password, passwordConfirmation);
                if (!passwordError.isEmpty()) {
                    errorLabel.setText(passwordError);
                    return;
                }
                
                // Name validation check
                String nameError = RecognizerName.checkForValidName(realName);
                if (!nameError.isEmpty()) {
                	errorLabel.setText(nameError);
                	return;
                }

				String emailError = EvaluatorEmail.evaluateEmail(email);
				if (!emailError.isEmpty()) {
				    errorLabel.setText(emailError);
				    return;
				}

                // Create a new User object with admin role and register in the database
                User user = new User(userName, password, roles, realName, email);
                databaseHelper.register(user, roles);
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new PageSetupLoginSelection(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                errorLabel.setText("Database error. Please try again.");
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, confirmPasswordField, realNameField, emailField, setupButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}