package pagesMainFeatures;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mainClassesMessaging.*;
import mainClassesQuestionAnswer.*;
import mainClassesUser.User;
import pagesMainNavigation.PageWelcomeLogin;
import validationEvaluators.InappropriateChecker;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PageInbox {

    private final DatabaseHelper databaseHelper;
    private Questions questionList;
    private Answers answers;
    private Messages messageList;    
    private Connection connection;
    private User currentUser;
    

    public PageInbox(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
    	
    	try {
			this.connection = databaseHelper.getConnection();
			this.questionList = new Questions(connection);
			this.answers = new Answers(connection);
			this.currentUser = databaseHelper.getLoggedInUser();
			this.messageList = new Messages(databaseHelper); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-padding: 20;");

        Label userLabel = new Label("Inbox: " + user.getRealName() );
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Message> questionListView = new ListView<>();
        questionListView.getItems().addAll(messageList.getMessagebyUserID(currentUser.getUserName())); 

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("All", "UnRead", "Read");
        categoryComboBox.setValue("All");

        categoryComboBox.setOnAction(e -> filterquestionEditorByCategory(categoryComboBox.getValue(), questionListView));

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search Message...");
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            questionListView.getItems().setAll(
                /*
            		questions.getAllquestionEditor().stream()
                    .filter(q -> q.getTitleText().toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList())
                    */
            );
        });

        VBox searchAndquestionEditor = new VBox(5, searchBar);
        
        VBox detailView = new VBox(10);
        detailView.setStyle("-fx-padding: 20; -fx-border-color: black; -fx-border-width: 2;");

        questionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayQuestionDetails(newSelection, detailView, user, questionListView);
            }
        });

        Button askQuestionButton = new Button("Send a Message");
        askQuestionButton.setOnAction(e -> askQuestion(user, questionListView));

        HBox backBox = new HBox(10);
        Button back = new Button("BACK");
        PageWelcomeLogin pageWelcomeLogin = new PageWelcomeLogin(databaseHelper);
        back.setOnAction(a -> {
            user.setRoles(databaseHelper.getUserRoles(user.getUserName())); // Update New role
            pageWelcomeLogin.show(primaryStage);
        });
        backBox.getChildren().add(back);

        VBox leftPane = new VBox(10, userLabel, askQuestionButton,searchAndquestionEditor ,categoryComboBox, questionListView, backBox);
        leftPane.setStyle("-fx-padding: 10;");

        layout.setLeft(leftPane);
        layout.setCenter(detailView);

        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Inbox Page");
    }

	private void displayQuestionDetails(Message message, VBox detailView, User user, ListView<Message> questionListView) {
	    
		/*detailView.getChildren().clear();
	
	    VBox questionBox = new VBox(5);
	    questionBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
	
	    Label titleLabel = new Label(mainClassesMessaging.getMassegeText());
	    titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
	
	    Label categoryLabel = new Label("Read: " + mainClassesMessaging.getCategory());
	    Label contentLabel = new Label("Content: " + mainClassesMessaging.getContentsText());
	    contentLabel.setWrapText(true);
	
	    HBox buttonBox = new HBox(5);
	    Button editButton = new Button("Edit");
	    Button deleteButton = new Button("Delete");
	
	    if (user != null && user.getUserName() != null &&
	        (user.getUserName().equals(mainClassesMessaging.getUserName()) || user.getRoles().contains("admin"))) {
	        editButton.setOnAction(e -> editQuestion(mainClassesMessaging, detailView, user, questionListView));
	        deleteButton.setOnAction(e -> deleteQuestion(mainClassesMessaging, detailView, questionListView));
	        buttonBox.getChildren().addAll(editButton, deleteButton);
	    }
	
	    questionBox.getChildren().addAll(titleLabel, categoryLabel, contentLabel, buttonBox);
	
	    VBox answerBox = new VBox(5);
	    answerBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
	
	    ScrollPane answerEditorcrollPane = new ScrollPane(answerBox);
	    answerEditorcrollPane.setFitToWidth(true);
	    answerEditorcrollPane.setPrefHeight(200);
	
	    List<Message> relatedanswerEditor = answers.getanswerEditorByQuestionId(mainClassesMessaging.getId());
	    for (Answer ans : relatedanswerEditor) {
	        HBox answerHBox = new HBox(5);
	        Label answerLabel = new Label("- Answer: " + ans.getAnswerText() + " (by " + ans.getUserName() + ")");
	
	        Button editAnswerButton = new Button("Edit");
	        Button deleteAnswerButton = new Button("Delete");
	
	        if (user != null && user.getUserName() != null &&
	            (user.getUserName().equals(ans.getUserName()) || user.getRoles().contains("admin") || user.getRoles().contains("staff"))) {
	            editAnswerButton.setOnAction(e -> editAnswer(ans, detailView, user, questionListView));
	            deleteAnswerButton.setOnAction(e -> deleteAnswer(ans, mainClassesMessaging, detailView, user, questionListView));
	            answerHBox.getChildren().addAll(answerLabel, editAnswerButton, deleteAnswerButton);
	        } else {
	            answerHBox.getChildren().add(answerLabel);
	        }
	        answerBox.getChildren().add(answerHBox);
	    }
	
	    Button answerButton = new Button("Answer This Question");
	    answerButton.setOnAction(e -> addMassage(mainClassesMessaging, detailView, user, questionListView));
	
	    detailView.getChildren().addAll(questionBox, answerButton, answerEditorcrollPane);
	    
	    //*/
	}
	
    private void askQuestion(User user, ListView<Message> questionListView) {
        Dialog<Message> dialog = new Dialog<>();
        dialog.setTitle("Ask a Question");

        VBox content = new VBox(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Enter Question Title");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("General", "Assignments", "Exams", "Projects");
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setValue("General");

        TextArea contentField = new TextArea();
        contentField.setPromptText("Enter Your Question Details");

        content.getChildren().addAll(new Label("Title:"), titleField, new Label("Category:"), categoryComboBox, new Label("Contents:"), contentField);

        dialog.getDialogPane().setContent(content);

        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == submitButton) {
            	if (!(InappropriateChecker.checkForValidContents(titleField.getText())).equals("")) {
            		showAlert("Title is not Valid",(InappropriateChecker.checkForValidContents(titleField.getText())));
                }
            	else if (!(InappropriateChecker.checkForValidContents(contentField.getText())).equals("")) {
            		showAlert("Contents is not Valid",(InappropriateChecker.checkForValidContents(contentField.getText())));
                }          	
            	else {
            		/*
            	return new Question(0, user.getUserName(), titleField.getText(), categoryComboBox.getValue(), contentField.getText(),-1,false);
            	*/
            	}
            }
            return null;
        });

        dialog.showAndWait().ifPresent(question -> {
        	/*
            questions.addQuestion(question);
            */
            questionListView.getItems().add(question);
            showAlert("Question Submitted", "Your question has been added successfully.");
        });
    }
    
    
    private void editQuestion(Question question, VBox detailView, User user, ListView<Message> questionListView) {
        TextInputDialog titleDialog = new TextInputDialog(question.getTitleText());
        titleDialog.setTitle("Edit Question Title");
        titleDialog.setHeaderText("Edit the title:");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("General", "Assignments", "Exams", "Projects");
        categoryComboBox.setValue(question.getCategory());

        TextArea contentArea = new TextArea(question.getContentsText());
        contentArea.setPromptText("Edit content here");

        VBox editContent = new VBox(10, new Label("Category:"), categoryComboBox, new Label("Content:"), contentArea);
        titleDialog.getDialogPane().setContent(new VBox(10, titleDialog.getEditor(), editContent));

        titleDialog.showAndWait().ifPresent(newTitle -> {
        	if (!(InappropriateChecker.checkForValidContents(newTitle)).equals("")) {
        		showAlert("Title is not Valid",InappropriateChecker.checkForValidContents(newTitle));
            }
        	else if (!(InappropriateChecker.checkForValidContents(contentArea.getText())).equals("")) {
        		showAlert("Contents is not Valid",(InappropriateChecker.checkForValidContents(contentArea.getText())));
            }          	
        	else {
                question.setTitleText(newTitle);
                question.setCategory(categoryComboBox.getValue());
                question.setContentsText(contentArea.getText());
                questionList.updateQuestion(question);
                questionListView.getItems().clear();  
                /*
                questionListView.getItems().addAll(questions.getAllquestionEditor());  
                questionListView.getSelectionModel().select(question);
                displayQuestionDetails(question, detailView, user, questionListView);
                */
        	}

        });
    }   
    
    private void deleteQuestion(Question question, VBox detailView, ListView<Message> questionListView) {
        try {
        	questionList.deleteQuestion(question.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        questionListView.getItems().remove(question);
        detailView.getChildren().clear();
    }
    
    private void addAnswer(Question question, VBox detailView, User user, ListView<Message> questionListView) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Answer");
        dialog.setHeaderText("Enter your answer for:\n" + question.getTitleText());

        dialog.showAndWait().ifPresent(answerText -> {
        	if (!(InappropriateChecker.checkForValidContents(answerText)).equals("")) {
        		showAlert("Answer is not Valid",(InappropriateChecker.checkForValidContents(answerText)));
            }  
        	else {
            Answer answer = new Answer(question.getId(), currentUser.getUserName(), answerText, false, -1,-1);
            answers.uploadAnswer(answer);
            
            /*
            displayQuestionDetails(question, detailView, user, questionListView);
            */
            showAlert("Answer Submitted", "Your answer has been added successfully.");
        	}
        });
    }
    
    private void editAnswer(Answer answer, VBox detailView, User user, ListView<Message> questionListView) {
        TextInputDialog dialog = new TextInputDialog(answer.getAnswerText());
        dialog.setTitle("Edit Answer");
        dialog.setHeaderText("Edit your answer:");

        dialog.showAndWait().ifPresent(newAnswer -> {
            answer.setAnswerText(newAnswer);
        	if (!(InappropriateChecker.checkForValidContents(newAnswer)).equals("")) {
        		showAlert("Answer is not Valid",(InappropriateChecker.checkForValidContents(newAnswer)));
            }  
        	else {
            answers.updateAnswer(answer);
            /*
            displayQuestionDetails(questions.getQuestionById(answer.getQuestionId()), detailView, user, questionListView);
            */
        	}
        });
    }

    private void deleteAnswer(Answer answer, Question question, VBox detailView, User user, ListView<Message> questionListView) {
        answers.deleteAnswer(answer.getId());
        /*
        displayQuestionDetails(question, detailView, user, questionListView);
        */
    }

    
    private void filterquestionEditorByCategory(String category, ListView<Message> questionListView) {
        /*
    	questionListView.getItems().clear();
        if ("All".equals(category)) {
            questionListView.getItems().addAll(questions.getAllquestionEditor());
        } else {
            questionListView.getItems().addAll(
                questions.getAllquestionEditor().stream()
                    .filter(q -> q.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList())
            );
        }
        */
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
