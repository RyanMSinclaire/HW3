package pagesMainFeatures;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import mainClassesUser.User;
import pagesMainNavigation.PageWelcomeLogin;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * PageInvitation class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */
public class PageInvitation {

    /*
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Title label
        Label userLabel = new Label("Invite");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Select roles
        List<CheckBox> roleCheckBoxes = new ArrayList<>();

        // If you are admin you can invite with many roles, if not, only student.
        User loggedInUser = databaseHelper.getLoggedInUser();
        List<String> userRoles = loggedInUser.getRoles(); // Creates a list of roles by getting list from User

        if (userRoles.contains("admin") || userRoles.contains("instructor") || userRoles.contains("staff")) {
            roleCheckBoxes.add(new CheckBox("student"));
            roleCheckBoxes.add(new CheckBox("instructor"));
            roleCheckBoxes.add(new CheckBox("staff"));
            roleCheckBoxes.add(new CheckBox("reviewer"));
            roleCheckBoxes.add(new CheckBox("admin"));
        } else {
            roleCheckBoxes.add(new CheckBox("student"));
        }

        // Select expiration date
        Label dateLabel = new Label("Select Expiration Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.of(2025, 2, 7)); // default is submit date

        // Create invitation code
        Button showCodeButton = new Button("Generate Invitation Code");

        // Creates a button to send to the generated code screen.
        Label inviteCodeLabel = new Label(""); 
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        showCodeButton.setOnAction(a -> {
            List<String> selectedRoles = new ArrayList<>();
            for (CheckBox checkBox : roleCheckBoxes) {
                if (checkBox.isSelected()) {
                    selectedRoles.add(checkBox.getText());
                }
            }
            // Checks if roles are empty.
            if (selectedRoles.isEmpty()) {
                inviteCodeLabel.setText("⚠ Please select at least one role.");
                return;
            }
            // Selects date and checks if empty.
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                inviteCodeLabel.setText("⚠ Please select a valid date.");
                return;
            }

            // LocalDate → java.sql.Date 
            Date sqlExpirationDate = Date.valueOf(selectedDate);

            // Generate invitation code
            String invitationCode = databaseHelper.generateInvitationCode(sqlExpirationDate, selectedRoles);
            inviteCodeLabel.setText("Invitation Code: " + invitationCode + " | Roles: " + String.join(", ", selectedRoles) + " | Expiration: " + selectedDate);
        });
        
	      // Button to take you back to Welcome Page
        HBox backBox = new HBox(10);
	      Button back = new Button("BACK");		
	      PageWelcomeLogin pageWelcomeLogin = new PageWelcomeLogin(databaseHelper);
        back.setOnAction(a ->  pageWelcomeLogin.show(primaryStage));
        backBox.getChildren().add(back);
        backBox.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(backBox, userLabel);
        layout.getChildren().addAll(roleCheckBoxes); // Add checkboxes
        layout.getChildren().addAll(dateLabel, datePicker);

        layout.getChildren().addAll(showCodeButton, inviteCodeLabel);

        Scene inviteScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invite Page");
    }
}
