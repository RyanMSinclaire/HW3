package pagesMainAccountHomes;

import databasePart1.DatabaseHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import mainClassesUser.User;
import pagesMainFeatures.PageInvitation;
import pagesMainNavigation.PageWelcomeLogin;

import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;

/*
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */
public class PageHomeAdmin {
    private TableView<User> tableView;
    private DatabaseHelper databaseHelper;
    private User currentUser;

    public PageHomeAdmin(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.currentUser = databaseHelper.getLoggedInUser();
    }
    
    /*
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        tableView = new TableView<>();
        setupTable();  
        loadUsersFromDB(); 

        VBox layout = new VBox(15); 
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin!");	    
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
	    // Button to take you back to Welcome Page
        HBox backBox = new HBox(10);
	    Button back = new Button("BACK");		
	    PageWelcomeLogin pageWelcomeLogin = new PageWelcomeLogin(databaseHelper);
	    back.setOnAction(a -> {
	    	currentUser.setRoles(databaseHelper.getUserRoles(currentUser.getUserName()));//Update New role
	        pageWelcomeLogin.show(primaryStage);
	    });

        // "Invite" code button if it is a Admin user.
        Button inviteButton = new Button("Invite");
        inviteButton.setOnAction(a -> new PageInvitation().show(databaseHelper, primaryStage, currentUser));
        layout.getChildren().add(inviteButton);
	    
	    
        backBox.getChildren().addAll(back,inviteButton);
        backBox.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(backBox, adminLabel, tableView);
        Scene adminScene = new Scene(layout, 900, 500); 

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
    
    private void setupTable() {
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<User, String> realNameColumn = new TableColumn<>("Real Name");
        realNameColumn.setCellValueFactory(new PropertyValueFactory<>("realName"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Add Checkbox for roles that can change 
        TableColumn<User, Void> roleColumn = new TableColumn<>("Roles");
        roleColumn.setCellFactory(tc -> new TableCell<>() {
            private final VBox checkBoxContainer = new VBox();
            private final List<CheckBox> checkBoxes = new ArrayList<>();
            private final List<String> allRoles = List.of("admin", "student", "instructor", "staff", "reviewer");

            {
                for (String role : allRoles) {
                    CheckBox checkBox = new CheckBox(role);
                    checkBoxes.add(checkBox);
                    checkBox.setOnAction(event -> updateRoles());
                }
                checkBoxContainer.getChildren().addAll(checkBoxes);
            }

            private void updateRoles() {
                User user = getTableView().getItems().get(getIndex());
                String loggedInUser = databaseHelper.getLoggedInUsername();

                // if admin trying to remove all roles from their own account 
                if (user.getUserName().equals(loggedInUser) && checkBoxes.stream().noneMatch(CheckBox::isSelected)) {
                    showAlert("Permission Denied", "You cannot remove all roles from your own account.", Alert.AlertType.ERROR);
                    refreshCheckBoxes(user.getRoles());
                    return;
                }
                
                // if admin trying to remove admin role from their account
                if (user.getUserName().equals(loggedInUser) && checkBoxes.stream().noneMatch(cb -> cb.getText().equals("admin") 
                		&& cb.isSelected())) {
                    showAlert("Permission Denied", "You cannot remove the admin role from your own account.", Alert.AlertType.ERROR);
                    refreshCheckBoxes(user.getRoles());
                    return;
                }

                // There should be at least one admin 
                if (user.getRoles().contains("admin") &&
                        checkBoxes.stream().noneMatch(cb -> cb.getText().equals("admin") && cb.isSelected()) &&
                        databaseHelper.countAdmins() <= 1) {
                    showAlert("Permission Denied", "There must be at least one admin.", Alert.AlertType.ERROR);
                    refreshCheckBoxes(user.getRoles());
                    return;
                }

                // Setup new roles
                List<String> newRoles = new ArrayList<>();
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        newRoles.add(checkBox.getText());
                    }
                }

                // Update DB
                databaseHelper.updateUserRoles(user.getUserName(), newRoles);
                user.setRoles(newRoles);
            }

            private void refreshCheckBoxes(List<String> userRoles) {
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setSelected(userRoles.contains(checkBox.getText()));
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    refreshCheckBoxes(user.getRoles());
                    setGraphic(checkBoxContainer);
                }
            }
        });

        TableColumn<User, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(tc -> new TableCell<>() {
            private final Button resetButton = new Button("Reset Password");
            private final Button deleteButton = new Button("Delete");

            {
                resetButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showSetOTPDialog(user);
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    VBox buttonContainer = new VBox(5, resetButton, deleteButton);
                    setGraphic(buttonContainer);
                }
            }
        });

        tableView.getColumns().addAll(userNameColumn, realNameColumn, emailColumn, roleColumn, actionColumn);
    }

    //Create one time password
    private void showSetOTPDialog(User user) {
        Stage otpStage = new Stage();
        otpStage.setTitle("Set One-Time Password");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Allowing only a specific Max OTP Length to avoid OTP overflow
        int maxOTPLength = DatabaseHelper.MAX_OTP_LENGTH;
        Label instructionLabel = new Label("Enter one-time password of length: " + maxOTPLength + " for user: " + user.getUserName());
        TextField otpField = new TextField();
        otpField.setTextFormatter(new TextFormatter<>(change -> {
            return change.getControlNewText().length() <= maxOTPLength ? change : null;
        }));
        
        otpField.setPromptText("Enter OTP");

        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        confirmButton.setOnAction(event -> {
            String otp = otpField.getText();
            if (!otp.isEmpty()) {
                databaseHelper.setOneTimePassword(user.getUserName(), otp);
                otpStage.close();
                showAlert("One-Time Password Set", "OTP successfully set for " + user.getUserName(), Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "OTP cannot be empty!", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(event -> otpStage.close());

        layout.getChildren().addAll(instructionLabel, otpField, confirmButton, cancelButton);
        Scene scene = new Scene(layout, 300, 200);
        otpStage.setScene(scene);
        otpStage.show();
    }

    //When deleting an account, an "Are you sure?" message must be answered with "Yes" to remove  access. 
    private void showDeleteConfirmation(User user) {
        if (user.getRoles().contains("admin") && databaseHelper.countAdmins() <= 1) {
            showAlert("Error", "Cannot delete the last admin!", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("User: " + user.getUserName());

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                databaseHelper.deleteUser(user.getUserName());
                loadUsersFromDB();
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadUsersFromDB() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        userList.addAll(databaseHelper.getUserList());
        tableView.setItems(userList);
    }
}