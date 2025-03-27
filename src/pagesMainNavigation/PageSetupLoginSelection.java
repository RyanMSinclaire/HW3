package pagesMainNavigation;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClassesUser.User;
import tests.filter.FilterTest;

import java.sql.SQLException;
import java.util.List;

import databasePart1.*;

/*
 * The PageSetupLoginSelection class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class PageSetupLoginSelection {
	
    private final DatabaseHelper databaseHelper;

    public PageSetupLoginSelection(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        
    	// Buttons to select Login / Setup / Quit options that redirect to respective pages
        Button setupButton = new Button("SetUp");
        Button loginButton = new Button("Login");
        Button quitButton = new Button("Quit");
        
        Button testAdminLoginButton = new Button("Test Admin Login");
        
        setupButton.setOnAction(a -> {
            new PageSetupAccount(databaseHelper).show(primaryStage);
        });
        loginButton.setOnAction(a -> {
        	new PageUserLogin(databaseHelper).show(primaryStage);
        });
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit();
        });

        testAdminLoginButton.setOnAction(a -> {
	        try {
	
	            User user = new User("Admin", "Admin123!", databaseHelper.getUserRoles("Admin"), databaseHelper.getRealName("Admin"), databaseHelper.getEmail("Admin"));
	            if (databaseHelper.login(user)) {
	                FilterTest ft =new FilterTest(databaseHelper);
	                ft.filterTest();
	                new PageWelcomeLogin(databaseHelper).show(primaryStage);
	            } else {
	            }
	
	        } catch (SQLException e) {
	            System.err.println("Database error: " + e.getMessage());
	            e.printStackTrace();
	        }
	    });
        
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(setupButton, loginButton, quitButton,testAdminLoginButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}