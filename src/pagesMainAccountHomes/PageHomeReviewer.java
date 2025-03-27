package pagesMainAccountHomes;

import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainClassesUser.User;
import pagesMainNavigation.PageWelcomeLogin;


/*
 * This page displays a simple welcome message for the user.
 */

public class PageHomeReviewer {
	
	private final DatabaseHelper databaseHelper;

    public PageHomeReviewer(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {

    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-padding: 0;");
	    
	    // Box for button
	    HBox backBox = new HBox(10);
	    backBox.setStyle(" -fx-padding: 0;");
	    backBox.setAlignment(Pos.TOP_LEFT);

	    
	    // Label to display Hello Reviewer
	    Label userLabel = new Label("Hello, Reviewer!");
	    userLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 150 325 150 325");	    

	    // Button to take you back to Welcome Page where you can switch roles 
	    Button back = new Button("BACK");		
	    PageWelcomeLogin pageWelcomeLogin = new PageWelcomeLogin(databaseHelper);
        back.setOnAction(a ->  pageWelcomeLogin.show(primaryStage));		
        backBox.getChildren().add(back);
	    
        // Gathering children  
        layout.getChildren().addAll(backBox, userLabel);
	    Scene userScene = new Scene(layout, 800, 400);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}