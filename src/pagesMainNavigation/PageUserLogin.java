package pagesMainNavigation;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClassesUser.User;
import pagesMainAccountHomes.PageHomeAdmin;
import pagesMainAccountHomes.PageHomeInstructor;
import pagesMainAccountHomes.PageHomeReviewer;
import pagesMainAccountHomes.PageHomeStaff;
import pagesMainAccountHomes.PageHomeStudent;
import validationEvaluators.EvaluatorPassword;

import java.sql.SQLException;
import java.util.List;

import databasePart1.*;

/*
 * The PageUserLogin class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class PageUserLogin {
	
    private final DatabaseHelper databaseHelper;
    private boolean otpShow = false;
    public PageUserLogin(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

	public void show(Stage primaryStage) {
	    // Input field for the user's user name
	    TextField userNameField = new TextField();
	    userNameField.setPromptText("Enter Username");
	    userNameField.setMaxWidth(250);
	
	    // PasswordField for normal password login
	    PasswordField passwordField = new PasswordField();
	    passwordField.setPromptText("Enter Password");
	    passwordField.setMaxWidth(250);
	
	    // OTP Filed(Hide at first)
	    TextField otpField = new TextField();
	    otpField.setPromptText("Enter OTP (if applicable)");
	    otpField.setMaxWidth(250);
	    otpField.setVisible(false); 

	
	    // Label to display error messages
	    Label errorLabel = new Label();
	    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	
	    Button loginButton = new Button("Login");
	
	    Button OtpShowButton = new Button("Try with OTP");
	    OtpShowButton.setOnAction(event -> {
	    	if (otpShow == true) {
	    		otpField.setVisible(false); 
	    		otpShow = false;
	    	}
	    	else{
	    		otpField.setVisible(true); 
	    		otpShow = true;
			}
	    });
	    
	
	    loginButton.setOnAction(a -> {
	        String userName = userNameField.getText();
	        String password = passwordField.getText();
	        String otp = otpField.getText();
	
	        try {
	            List<String> roles = databaseHelper.getUserRoles(userName);
	            if (roles.isEmpty()) {
	                errorLabel.setText("Error: User account does not exist");
	                return;
	            }
	
	            User user = new User(userName, password, roles, databaseHelper.getRealName(userName), databaseHelper.getEmail(userName));
	
	            // When OTP is visible: log in with OTP
	            if (otpField.isVisible() && !otp.isEmpty() && databaseHelper.loginWithOTP(userName, otp)) {
	                showAlert("Login Successful", "OTP login successful! Please set a new password.", Alert.AlertType.INFORMATION);
	                showSetNewPasswordDialog(primaryStage, userName);
	                //return;
	            }
	
	            if (databaseHelper.login(user)) {
	                ifSingleRole(roles, primaryStage);
	            } else {
	                errorLabel.setText("Error: Incorrect password or OTP");
	            }
	
	        } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }
	    });

        // Button for backing up to PageSetupLoginSelection
        Button back = new Button("BACK");
        PageSetupLoginSelection PageSetupLoginSelection = new PageSetupLoginSelection(databaseHelper);
        back.setOnAction(a ->  PageSetupLoginSelection.show(primaryStage));
	
	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
	    layout.getChildren().addAll(userNameField, passwordField, OtpShowButton ,otpField, errorLabel, loginButton, back);
	
	    primaryStage.setScene(new Scene(layout, 800, 400));
	    primaryStage.setTitle("User Login");
	    primaryStage.show();
	}


    //new password setup box
    private void showSetNewPasswordDialog(Stage primaryStage, String userName) {
        Stage passwordStage = new Stage();
        passwordStage.setTitle("Set New Password");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label instructionLabel = new Label("Set a new password for: " + userName);
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        confirmButton.setOnAction(event -> {
            String newPassword = newPasswordField.getText();
            if (!newPassword.isEmpty() && EvaluatorPassword.evaluatePassword(newPassword, newPassword).isEmpty()) {
                databaseHelper.setNewPassword(userName, newPassword);
                passwordStage.close();
                showAlert("Password Updated", "Your password has been successfully updated. Please log in again.", Alert.AlertType.INFORMATION);
                primaryStage.show(); //back to log in page
            } else {
                showAlert("Error", "Password cannot be empty!", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(event -> passwordStage.close());
        

        layout.getChildren().addAll(instructionLabel, newPasswordField, confirmButton, cancelButton);
        Scene scene = new Scene(layout, 300, 200);
        passwordStage.setScene(scene);
        passwordStage.show();
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void ifSingleRole (List<String> roles, Stage primaryStage) {
    	System.out.println("Attempting single role");
        if (roles.size() == 1) { // Checks if there is only one role before iterating down.

            if (roles.contains("admin")) {
                new PageHomeAdmin(databaseHelper).show(primaryStage);
            }

            if (roles.contains("student")) {
                new PageHomeStudent(databaseHelper).show(primaryStage);
            }

            if (roles.contains("instructor")) {
                new PageHomeInstructor(databaseHelper).show(primaryStage);
            }

            if (roles.contains("staff")) {
                new PageHomeStaff(databaseHelper).show(primaryStage);
            }

            if (roles.contains("reviewer")) {
                new PageHomeReviewer(databaseHelper).show(primaryStage);

            }

        } else { // If there is multiple roles, goes to the welcome login page.
            new PageWelcomeLogin(databaseHelper).show(primaryStage);
        }
    }
}