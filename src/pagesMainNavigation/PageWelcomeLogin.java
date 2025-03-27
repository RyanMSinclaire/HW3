package pagesMainNavigation;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mainClassesUser.User;
import pagesMainAccountHomes.PageHomeAdmin;
import pagesMainAccountHomes.PageHomeInstructor;
import pagesMainAccountHomes.PageHomeReviewer;
import pagesMainAccountHomes.PageHomeStaff;
import pagesMainAccountHomes.PageHomeStudent;
import javafx.application.Platform;
import databasePart1.*;

import java.awt.image.SampleModel;
import java.util.List;


/*
 * The PageWelcomeLogin class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */


public class PageWelcomeLogin {
	
    private final DatabaseHelper databaseHelper;
    private User currentLoggedInUser;
    /**
     * The PageWelcomeLogin class displays a welcome screen for authenticated users.
     * It allows users to navigate to their respective pages based on their role or quit the application.
     */

    public PageWelcomeLogin(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.currentLoggedInUser = databaseHelper.getLoggedInUser();
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-alignment: center; -fx-padding: 10;");

        Label welcomeLabel = new Label("Welcome " + currentLoggedInUser.getUserName() + "!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label userInfoLabel = new Label("[Username: " + currentLoggedInUser.getUserName() + "] [Email: " + currentLoggedInUser.getEmail() + "]");
        userInfoLabel.setStyle("-fx-font-size: 14px;");

        // Depends on what's your role
        List<String> roles = currentLoggedInUser.getRoles();

        // If multiple roles, check for every possible role and assign buttons to each
        buttonContainer.getChildren().clear();
        layout.getChildren().clear();
            if (roles.contains("admin")) {
                Button adminPageButton = new Button("Admin Page");
                adminPageButton.setOnAction(a -> new PageHomeAdmin(databaseHelper).show(primaryStage));
                buttonContainer.getChildren().add(adminPageButton);
            }

            if (roles.contains("student")) {
                Button studentPageButton = new Button("Student Page");
                studentPageButton.setOnAction(a -> new PageHomeStudent(databaseHelper).show(primaryStage));
                buttonContainer.getChildren().add(studentPageButton);
            }

            if (roles.contains("instructor")) {
                Button instructorPageButton = new Button("Instructor Page");
                instructorPageButton.setOnAction(a -> new PageHomeInstructor(databaseHelper).show(primaryStage));
                buttonContainer.getChildren().add(instructorPageButton);
            }

            if (roles.contains("staff")) {
                Button staffPageButton = new Button("Staff Page");
                PageHomeStaff PageHomeStaff = new PageHomeStaff(databaseHelper);
                staffPageButton.setOnAction(a -> new PageHomeStaff(databaseHelper).show(primaryStage));
                buttonContainer.getChildren().add(staffPageButton);
            }

            if (roles.contains("reviewer")) {
                Button reviewerPageButton = new Button("Reviewer Page");
                reviewerPageButton.setOnAction(a -> new PageHomeReviewer(databaseHelper).show(primaryStage));
                buttonContainer.getChildren().add(reviewerPageButton);
            }

            // Log out button
            Button logOutButton = new Button("Log Out");
            logOutButton.setOnAction(a -> {
                databaseHelper.logout();
                new PageSetupLoginSelection(databaseHelper).show(primaryStage);
            });

            layout.getChildren().addAll(welcomeLabel, userInfoLabel, buttonContainer, logOutButton);
            Scene welcomeScene = new Scene(layout, 800, 400);

            primaryStage.setScene(welcomeScene);
            primaryStage.setTitle("Welcome Page");
    }
}