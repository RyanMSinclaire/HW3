package pagesMainNavigation;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClassesUser.User;
import validationEvaluators.EvaluatorEmail;
import validationEvaluators.RecognizerName;
import validationEvaluators.EvaluatorPassword;
import validationEvaluators.RecognizerUserName;

import java.sql.SQLException;
import java.util.List;

import databasePart1.*;

/*
 * PageSetupAccount class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class PageSetupAccount {
	
    private final DatabaseHelper databaseHelper;

    // DatabaseHelper to handle database operations.
    public PageSetupAccount(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /*
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
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

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button setupButton = new Button("Setup Account");
        
        // Button for backing up to PageSetupLoginSelection
	    Button back = new Button("BACK");		
	    PageSetupLoginSelection PageSetupLoginSelection = new PageSetupLoginSelection(databaseHelper);
        back.setOnAction(a ->  PageSetupLoginSelection.show(primaryStage));		
        

        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String passwordConfirmation = confirmPasswordField.getText().trim();
            String realName = realNameField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText().substring(0, DatabaseHelper.MAX_OTP_LENGTH); // Fetch specific OTP length to prevent overflow

            try {
                // Check if there is already user name in DB
                if (databaseHelper.doesUserExist(userName)) {
                    errorLabel.setText("Error: Username is already taken! Please choose another.");
                    return;
                }

                //Invitation code roles validation check
                List<String> roles = databaseHelper.getRolesFromInvitationCode(code);
                if (roles.isEmpty()) {
                    errorLabel.setText("Error: Invalid or expired invitation code.");
                    return;
                }

                // UserName validation check
                String userNameError = RecognizerUserName.checkForValidUserName(userName);
                if (!userNameError.isEmpty()) {
                    errorLabel.setText(userNameError);
                    return;
                }

                // Password validation check
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
                
                // Validates the email and check if it is valid.
                String emailError = EvaluatorEmail.evaluateEmail(email);
                if (!emailError.isEmpty()) {
                errorLabel.setText(emailError);
                return;
                }

                // Register new user
                User newUser = new User(userName, password, roles, realName, email);
                databaseHelper.register(newUser, roles);

                // Back to log in page
                new PageSetupLoginSelection(databaseHelper).show(primaryStage);

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
                errorLabel.setText("Error: Database issue occurred.");
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, confirmPasswordField, realNameField, emailField, inviteCodeField, setupButton, back, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}