package pagesMainAccountHomes;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mainClassesMessaging.Message;
import mainClassesMessaging.Messages;
import mainClassesQuestionAnswer.Answer;
import mainClassesQuestionAnswer.Answers;
import mainClassesQuestionAnswer.Question;
import mainClassesQuestionAnswer.Questions;
import mainClassesUser.User;
import pagesMainFeatures.PageFilter;
import pagesMainFeatures.PageInbox;
import pagesMainNavigation.PageWelcomeLogin;
import validationEvaluators.InappropriateChecker;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PageHomeStudent {

	private final DatabaseHelper databaseHelper;
	private Questions questionList;
	private Messages messageList;
	private Answers answers;
	private Connection connection;
	private User currentUser;
	private Question selectedQuestion = new Question(0,"","","","",0,false);
	private ListView<Question> questionListView;
	private ListView<Message> messageListView;
	private ComboBox<String> categoryComboBox;
	private ComboBox<String> solvedComboBox;
	private ComboBox<String> recentComboBox;

	private VBox mainBackgroundQuestionBox;
	private VBox searchAndQuestions;
	private VBox vBoxSelectedQuestion;
	private VBox vBoxAnswerBox;
	private HBox horizontalViewFilters;
	private HBox hBoxMainOptionButtons;
	private HBox backBox;
	private HBox hBoxAllButtons1;
	private HBox hBoxAllButtons2;
	private VBox titleVbox;

	private Button askQuestionButton;
	private Button askQuestionByOtherQuestion;
	private Button backToOriginalQuestion;
	private Button inboxButton;
	private Button back;
	private Button solvedButton;
	private Button editButton;
	private Button deleteButton;
	private Button answerButton;
	private Button messageButton;
	private TextField searchBar;

	private Label userLabel;
	private Label questionTitleLabel;
	private Label relatedQuestionLabel;
	private Label categoryLabel;
	private Label statusLabel;
	private Label contentLabel;	
	private TextArea questionTextArea;
	private String categoryString = "Category: ";
	private String statusString = "Status: ";	
	private String contentString = "Content: \n";
	private ArrayList<String> categoryArray;
	private ArrayList<String> solvedArray;
	private ArrayList<String> recentArray;
	private List<String> categoryStrings;
	private List<String> solvedStrings;
	private List<String> recentStrings;
	
	private PageFilter newFilter;

	private ListView<Answer> answersListView;
	
	private ScrollPane answerScrollPane;

	private int relatedQuestionID ;
	
	public PageHomeStudent(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.currentUser = databaseHelper.getLoggedInUser();
		this.questionListView = new ListView<>();
		this.messageListView = new ListView<>();
		this.answersListView = new ListView<>();
		this.categoryArray = new ArrayList<>();
		this.solvedArray = new ArrayList<>();
		this.recentArray = new ArrayList<>();
		this.newFilter = new PageFilter(databaseHelper, currentUser); 
	}

	public void show(Stage primaryStage) {

		try {
			this.connection = databaseHelper.getConnection();
			this.questionList = new Questions(connection);
			this.messageList = new Messages(databaseHelper);
			this.answers = new Answers(connection);
			fetchAllQuestionsFromDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Main Page title label
		userLabel = new Label("Hello, " + currentUser.getRealName() + "!");
		userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Adds all the category titles for the combo boxes
		categoryStrings = Arrays.asList("All", "General", "Assignments", "Exams", "Projects", "My Questions");
		solvedStrings = Arrays.asList("All", "Unsolved", "Solved");
		recentStrings = Arrays.asList("All", "Recent");
		categoryArray.addAll(categoryStrings);
		solvedArray.addAll(solvedStrings);
		recentArray.addAll(recentStrings);

		populateQuestionListView();

		BorderPane layout = new BorderPane();
		layout.setStyle("-fx-padding: 10;");

		// Ask a Question Button
		askQuestionButton = new Button("Ask a Question");
		askQuestionButton.setOnAction(e -> 
		{relatedQuestionID = 0;
		askQuestion(currentUser, questionListView);}
		);

		// Inbox Button
		inboxButton = new Button("Inbox");
		PageInbox inboxPage = new PageInbox(databaseHelper);
		inboxButton.setOnAction(e -> {
			
			currentUser.setRoles(databaseHelper.getUserRoles(currentUser.getUserName()));
			inboxPage.show(primaryStage, currentUser);
			
			//showAlert("Inbox Page", "Not Ready, See you in Phase-3");
		});

		// Adding buttons to a master box to hold them together.
		hBoxMainOptionButtons = new HBox(5);
		hBoxMainOptionButtons.getChildren().addAll(askQuestionButton, inboxButton);

		// Search Bar
		searchBar = new TextField();
		searchBar.setPromptText("Search questions...");
		searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
			questionListView.getItems()
					.setAll(questionList.getAllQuestions().stream()
							.filter(q -> q.getTitleText().toLowerCase().contains(newValue.toLowerCase()))
							.collect(Collectors.toList()));
		});
		searchAndQuestions = new VBox(5, searchBar); //Creates search bar.

		/* COMBO BOX SETUP FOR FILTERS
		--------------------------------------
		Creates the combo boxes for the filters.
		Assigns the arrays with their respective combo boxes.
		--------------------------------------
		 */

		// "Category" combo box
		categoryComboBox = new ComboBox<>();
		setupComboBox(categoryComboBox, categoryArray);
		categoryComboBox.setOnAction(e -> questionListView = newFilter.runFilterChecker(questionList, questionListView,
																						categoryComboBox.getValue(),
																						solvedComboBox.getValue(),
																						recentComboBox.getValue()
																						));

		// "Solved" combo box
		solvedComboBox = new ComboBox<>();
		setupComboBox(solvedComboBox, solvedArray);
		solvedComboBox.setOnAction(e -> questionListView = newFilter.runFilterChecker(questionList, questionListView,
																						categoryComboBox.getValue(),
																						solvedComboBox.getValue(),
																						recentComboBox.getValue()
																						));
		
		// "Recent" combo box
		recentComboBox = new ComboBox<>();
		setupComboBox(recentComboBox, recentArray);
		recentComboBox.setOnAction(e -> questionListView = newFilter.runFilterChecker(questionList, questionListView,
																						categoryComboBox.getValue(),
																						solvedComboBox.getValue(),
																						recentComboBox.getValue()
																						)); 

		// Adding combo boxes to a master view box
		horizontalViewFilters = new HBox(5);
		horizontalViewFilters.getChildren().addAll(categoryComboBox, solvedComboBox, recentComboBox);

		/* Main question setup
		--------------------------------------
		Initializes a box to contain the data within.
		Creates a title of **1 question** and then it's answers.
		--------------------------------------
		 */

		// Setting up master background box for the main question that is selected for the right-pane.

		mainBackgroundQuestionBox = new VBox(10);
		mainBackgroundQuestionBox.setStyle("-fx-padding: 20; -fx-border-color: black; -fx-border-width: 2;");

		// Question Title	
		questionTitleLabel = new Label("");
		questionTitleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
		titleVbox = new VBox(5);
		
		relatedQuestionLabel = new Label("");
		relatedQuestionLabel.setStyle("-fx-font-size: 8px;");
		

		titleVbox.getChildren().addAll(questionTitleLabel, relatedQuestionLabel);
		categoryLabel = new Label("");	// Question Category
		statusLabel = new Label("");	// Question Status
		contentLabel = new Label(contentString);	// Question Content
		contentLabel.setWrapText(true);
		
		questionTextArea = new TextArea();
		questionTextArea.setWrapText(true);
		questionTextArea.setEditable(false);
		questionTextArea.setPrefHeight(Region.USE_COMPUTED_SIZE); 
		
		questionTextArea.prefHeightProperty().bind(questionTextArea.heightProperty()); // bind height to content
		//questionTextArea.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.7)); // bind height to proportion
		questionTextArea.setMaxHeight(Double.MAX_VALUE); 
		questionTextArea.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
		questionTextArea.setScrollTop(0);		// Disabling the vertical scrollbar initially
		askQuestionByOtherQuestion = new Button("Asking Question about this Question");		//Joey_UserStory 18 -- References previous question asked for user.

		// Question Buttons
		solvedButton = new Button("Mark as Solved"); 
		editButton = new Button("Edit");
		deleteButton = new Button("Delete");
		messageButton = new Button("PM");
		hBoxAllButtons1 = new HBox(5);	// Will hold top question buttons
		hBoxAllButtons2 = new HBox(5);	// Will hold bottom question buttons
		
		// Question detail view
		vBoxSelectedQuestion = new VBox(5);
		vBoxSelectedQuestion.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
		vBoxSelectedQuestion.getChildren().addAll(titleVbox, categoryLabel, statusLabel, contentLabel, questionTextArea, hBoxAllButtons1, hBoxAllButtons2);


		
		answerButton = new Button("Answer This Question");
		answerButton.setOnAction(a -> {
			
			addAnswer(selectedQuestion);

		});	
		// Setting up a listener so questions from the question list can be selected
		questionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				selectedQuestion = newSelection;
				displayQuestionDetails(newSelection);
			}
		});
		
		
		//Joey_UserStory 18
		askQuestionByOtherQuestion.setOnAction(e -> {
			relatedQuestionID = selectedQuestion.getId();
			askQuestion(currentUser, questionListView);
		});
		backToOriginalQuestion = new Button("");
		vBoxSelectedQuestion.getChildren().addAll(backToOriginalQuestion);
		backToOriginalQuestion.setOnAction(e -> {
			if (selectedQuestion.getRelatedQuestionID() != 0) {	
				displayQuestionDetails(questionList.getQuestionById(selectedQuestion.getRelatedQuestionID()));
				}
			}
			);
		
		// VBox inside ScrollPane (content)
        vBoxAnswerBox = new VBox();
        vBoxAnswerBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        vBoxAnswerBox.setMinHeight(Region.USE_PREF_SIZE);

        // ScrollPane
        answerScrollPane = new ScrollPane(vBoxAnswerBox);
        answerScrollPane.setFitToWidth(true);
        answerScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        answerScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(answerScrollPane, Priority.ALWAYS);
        answerScrollPane.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.4));
		
		backBox = new HBox(10);
		back = new Button("BACK");
		PageWelcomeLogin pageWelcomeLogin = new PageWelcomeLogin(databaseHelper);
		back.setOnAction(a -> {
			currentUser.setRoles(databaseHelper.getUserRoles(currentUser.getUserName()));
			pageWelcomeLogin.show(primaryStage);
		});
		backBox.getChildren().add(back);
		
		VBox leftPane = new VBox(10, userLabel, hBoxMainOptionButtons, searchAndQuestions, horizontalViewFilters,
		        questionListView, backBox);
		leftPane.setStyle("-fx-padding: 10;");
		leftPane.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.33)); // Binding width of left pane to be 1/3 of the scene width
		VBox.setVgrow(leftPane, Priority.ALWAYS);
		layout.setLeft(leftPane);
		layout.setCenter(mainBackgroundQuestionBox);

		Scene userScene = new Scene(layout, 800, 450);
		primaryStage.setScene(userScene);
		primaryStage.setTitle("Student Page");		
	}

	private void setupQuestionButtons(Question question) {
		hBoxAllButtons1.getChildren().clear();
		hBoxAllButtons2.getChildren().clear();
		if (currentUser != null && question.getUserName() != null
				&& (currentUser.getUserName().equals(question.getUserName())
						|| currentUser.getRoles().contains("admin"))) {
			System.out.println("Print Data");
			solvedButton.setOnAction(e -> setQuestionSolved(question, mainBackgroundQuestionBox, currentUser, questionListView));
			editButton.setOnAction(e -> editQuestion(question, mainBackgroundQuestionBox, questionListView));
			deleteButton.setOnAction(e -> deleteQuestion(question, mainBackgroundQuestionBox, questionListView));
			messageButton.setOnAction(e -> privateMessage(currentUser, question.getUserName(), question.getRelatedQuestionID(), messageListView));
			hBoxAllButtons1.getChildren().addAll(askQuestionByOtherQuestion, messageButton);
			hBoxAllButtons2.getChildren().addAll(solvedButton, editButton, deleteButton);

		}else {
			hBoxAllButtons1.getChildren().addAll(askQuestionByOtherQuestion);
			System.out.println("No Print Data");
			System.out.println("currentUser null: " + Boolean.toString(currentUser == null));
			System.out.println("question.getUserName(): " + question.getUserName());
			System.out.println("isAdmin: " + currentUser.getRoles().contains("admin"));
		}
	}
	private void setupComboBox(ComboBox<String> comboBox, List<String> itemStrings) {
		comboBox.getItems().addAll(itemStrings);
		comboBox.setValue(itemStrings.getFirst());

	}

	private void fetchAllQuestionsFromDB() {
	    try {
	        System.out.println("Fetching questions from database...");
	        questionList.updateAllQuestions();  // DB에서 질문 불러오기
	        System.out.println("Questions fetched! Total count: " + questionList.getQuestionListSize());
	    } catch (Exception e) {
	        e.printStackTrace();
	        showAlert("Database Error", "Failed to fetch questions from the database.");
	    }
	}


	private void populateQuestionListView() {
		questionListView.getItems().addAll(questionList.getAllQuestions());
	}

	private void displayQuestionDetails(Question question) {
		selectedQuestion = question;
		setupQuestionButtons(question);
		setupQuestionDetails(question);
		setupAnswerListView(question);
		
		mainBackgroundQuestionBox.getChildren().clear();
		mainBackgroundQuestionBox.getChildren().addAll(vBoxSelectedQuestion, answerButton, answerScrollPane);
	}

	private void setupQuestionDetails(Question question) {
		selectedQuestion = question;
		questionTitleLabel.setText(question.getTitleText());
		int relatedQuestionID = selectedQuestion.getRelatedQuestionID();
		if (selectedQuestion.getRelatedQuestionID() >= 0 && (questionList.getQuestionById(relatedQuestionID) != null)) {
			relatedQuestionLabel.setText("Original Question ID : "+selectedQuestion.getRelatedQuestionID());
			backToOriginalQuestion.setText("Back To Original Question: "+selectedQuestion.getRelatedQuestionID());
			backToOriginalQuestion.setVisible(true);
		}
		else {relatedQuestionLabel.setText("Original");
		backToOriginalQuestion.setVisible(false);
		}
		categoryLabel.setText(categoryString + question.getCategory());
		statusLabel.setText(statusString + question.getIsSolved());
		questionTextArea.setText(question.getContentsText());		
	}
	private void setupAnswerListView(Question question) {
		loadAnswersFromDB(question);

		answersListView.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(Answer answer, boolean empty) {
				super.updateItem(answer, empty);
				if (empty || answer == null) {
					setGraphic(null);
				} else {
					// Create labels and format the item
					VBox vbox = new VBox(5);
					HBox buttonBox = new HBox(10);
					Label solutionLabel = new Label("Solution " + "\u2713" + "\n");
					solutionLabel.setVisible(answer.isCorrect());

					// Ask date
					Label dateLabel = new Label("Replied on: " + question.getTitleText());
					// Asker's name
					Label askerLabel = new Label("By: " + answer.getUserName());
					// Short blurb (first 100 characters of the question)
					String blurb = answer.getAnswerText().length() > 100
							? answer.getAnswerText().substring(0, 100) + "..."
							: answer.getAnswerText();
					Label blurbLabel = new Label("Reply: " + blurb);

					Button editButton = new Button("Edit Reply");
					editButton.setOnAction(e -> editAnswer(answer, question));
					editButton.setVisible((answer.getUserName()).equals(currentUser.getUserName())||currentUser.getRoles().contains("admin"));
					
					Button deleteButton = new Button("Delete Reply");
					deleteButton.setOnAction(e -> deleteAnswer(answer, question));
					deleteButton.setVisible((answer.getUserName()).equals(currentUser.getUserName())||currentUser.getRoles().contains("admin"));
					
					Button markAsSolution = new Button("Mark as Solution");
					markAsSolution.setOnAction(e -> solutionAnswer(answer, question));
					markAsSolution.setVisible(((question.getUserName()).equals(currentUser.getUserName()) && !answer.isCorrect() && !(question.getIsSolved()))||currentUser.getRoles().contains("admin"));
					
					Button viewFullAnswer = new Button("See Full Reply");
					viewFullAnswer.setOnAction(e -> showFullAnswer(answer));
					viewFullAnswer.setVisible((answer.getAnswerText().length() > 100));

					// Add the button to the VBox
					buttonBox.getChildren().addAll(editButton, deleteButton, markAsSolution, viewFullAnswer);
					vbox.getChildren().addAll(solutionLabel, dateLabel, askerLabel, blurbLabel, buttonBox);

					// Set the VBox as the content of the cell
					setGraphic(vbox);
				}
			}
		});
		
		// Resize answers view
		answerScrollPane = new ScrollPane(answersListView);		
		VBox.setVgrow(answersListView, Priority.ALWAYS);
		answersListView.prefWidthProperty().bind(answerScrollPane.widthProperty().subtract(20));
	}

	private void askQuestion(User user, ListView<Question> questionListView) {
		Dialog<Question> dialog = new Dialog<>();
		dialog.setTitle("Ask a Question!");
		ListView<Question> insideQuestionListView = new ListView<>(); //Declares a temp variable to use so it doesn't modify the main list.

		TextField titleField = new TextField();
		titleField.setPromptText("Enter Question Title");

		titleField.textProperty().addListener((observable, oldValue, newValue) -> { //Searches similar questions from list.
			insideQuestionListView.getItems()
					.setAll(questionList.getAllQuestions().stream()
							.filter(q -> q.getTitleText().toLowerCase().contains(newValue.toLowerCase()))
							.collect(Collectors.toList()));
		}); // Filters the search model for keywords.

		insideQuestionListView.getSelectionModel().selectedItemProperty().addListener // Sets up a listener to search for a selection from the user.
				((observable, oldValue, newValue) -> {
					Question selectedQuestion = insideQuestionListView.getSelectionModel().getSelectedItem(); // Assigns a var, for the selected question.
					System.out.println("\nASK A QUESTION: '" + selectedQuestion + "' has been selected"); // Confirmation of data selected and confirm data has been called.
					selectedQuestionDetailWindow(selectedQuestion); // Calls the window once the person selects a search related question from "Ask a Question!"
		});

		VBox content = new VBox(10); // Create a new box

		ComboBox<String> categoryComboBox = new ComboBox<>();
		categoryComboBox.getItems().addAll("General", "Assignments", "Exams", "Projects");
		categoryComboBox.setPromptText("Select Category");
		categoryComboBox.setValue("General");

		TextArea contentField = new TextArea();
		contentField.setPromptText("Enter Your Question Details");

		content.getChildren().addAll(new Label("Title:"), titleField,
				new Label("Category:"), categoryComboBox,
				new Label("Similar Questions:"), insideQuestionListView,
				new Label("Contents:"), contentField);

		dialog.getDialogPane().setContent(content);

		ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == submitButton) {
				if (!(InappropriateChecker.checkForValidContents(titleField.getText())).equals("")) {
					showAlert("Title is not Valid", (InappropriateChecker.checkForValidContents(titleField.getText())));
				} else if (!(InappropriateChecker.checkForValidContents(contentField.getText())).equals("")) {
					showAlert("Contents is not Valid",
							(InappropriateChecker.checkForValidContents(contentField.getText())));
				} else {
					System.out.println(relatedQuestionID);
					Question temp = new Question(0, user.getUserName(), titleField.getText(), categoryComboBox.getValue(),
							contentField.getText(), relatedQuestionID, false);
					temp.setRelatedQuestionID(relatedQuestionID);
					//questionList.addQuestion(temp);
					return temp;
				}
			}
			return null;
		});

		dialog.showAndWait().ifPresent(question -> {
			
			questionList.addQuestion(question);
			questionListView.getItems().add(question);
			showAlert("Question Submitted", "Your question has been added successfully.");
		});
	}

	private void selectedQuestionDetailWindow(Question question) { // Opens a window detail to the selected question.
		System.out.println("\nOpening Sub Window for selected question: '" + question.getTitleText() + "'"); // Confirms if this function is casted.

		Stage subWindow = new Stage(); // Make a new stage for this sub window.
		subWindow.setTitle("Question Thread for " + question.getTitleText()); // Title of said sub window

		VBox layout = new VBox(10); // Init layout for the scene

		// Update details with current question.
		setupQuestionButtons(question);
		setupQuestionDetails(question);
		setupAnswerListView(question);

		layout.getChildren().addAll(vBoxSelectedQuestion, answerButton, answerScrollPane); // Add the content into layout copied from displayQuestionDetails();

		// Finalizes the GUI
		Scene scene = new Scene(layout);
		subWindow.setScene(scene);
		subWindow.show();

	}

	private void editQuestion(Question question, VBox detailView, ListView<Question> questionListView) {
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
				showAlert("Title is not Valid", InappropriateChecker.checkForValidContents(newTitle));
			} else if (!(InappropriateChecker.checkForValidContents(contentArea.getText())).equals("")) {
				showAlert("Contents is not Valid", (InappropriateChecker.checkForValidContents(contentArea.getText())));
			} else {
				question.setTitleText(newTitle);
				question.setCategory(categoryComboBox.getValue());
				question.setContentsText(contentArea.getText());
				questionList.updateQuestion(question);
				questionListView.getItems().clear();
				questionListView.getItems().addAll(questionList.getAllQuestions());
				questionListView.getSelectionModel().select(question);
				displayQuestionDetails(question);

			}

		});
	}

	private void setQuestionSolved(Question question, VBox detailView, User user, ListView<Question> questionListView) {
		question.setIsSolved(question.getIsSolved() ? false : true);
		questionList.updateQuestion(question);
		questionListView.getItems().clear();
		questionListView.getItems().addAll(questionList.getAllQuestions());
		questionListView.getSelectionModel().select(question);
		displayQuestionDetails(question);
	}

	private void deleteQuestion(Question question, VBox detailView, ListView<Question> questionListView) {
		try {
			questionList.deleteQuestion(question.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		questionListView.getItems().remove(question);
		detailView.getChildren().clear();
		questionListView.getItems().clear();
		questionListView.getItems().addAll(questionList.getAllQuestions());

	}

	private void addAnswer(Question question) {
		TextArea answerContent = new TextArea();
		Dialog<Answer> answerDialog = new Dialog<>();
		VBox content = new VBox(10);
		
		answerDialog.setTitle("Suggest an Answer");
		answerContent.setPromptText("Enter your answer to question #" + question.getId() + "...");
		
		content.getChildren().addAll(new Label("Your Answer:"), answerContent);
		
		answerDialog.getDialogPane().setContent(content);

		ButtonType sendButton = new ButtonType("Send Answer", ButtonBar.ButtonData.OK_DONE);
		answerDialog.getDialogPane().getButtonTypes().addAll(sendButton, ButtonType.CANCEL);
		
		
		answerDialog.setResultConverter(button -> {
			if (button == sendButton) {
				if (!(InappropriateChecker.checkForValidContents(answerContent.getText())).equals("")) {
					showAlert("Answer is not Valid", (InappropriateChecker.checkForValidContents(answerContent.getText())));
				}
				else {
					Answer newAnswer = new Answer(0, currentUser.getUserName(), answerContent.getText(), false, question.getId(), -1);
					
					return newAnswer;
				}
			}
			
			return null;
		});
		
		answerDialog.showAndWait().ifPresent(answer -> {
			
			answers.uploadAnswer(answer);
			answers.addAnswer(answer);
			
			displayQuestionDetails(question);
			showAlert("Answer Submitted", "Your answer has been sent successfully!");
		});
		
		
	}
	
	private void editAnswer(Answer answer, Question question) {
		TextArea editedAnswer = new TextArea();
		Dialog<String> editAnswerDialog = new Dialog<>();
		VBox editorContent = new VBox(10);
		
		editAnswerDialog.setTitle("Answer Editor");
		editedAnswer.setPromptText("Edit your Answer Below");
		editedAnswer.setText(answer.getAnswerText());
		
		editorContent.getChildren().addAll(new Label("Edit your Answer Below"), editedAnswer);
		
		editAnswerDialog.getDialogPane().setContent(editorContent);
		
		ButtonType editButton = new ButtonType("Edit Answer", ButtonBar.ButtonData.OK_DONE);
		editAnswerDialog.getDialogPane().getButtonTypes().addAll(editButton, ButtonType.CANCEL);
		
		editAnswerDialog.setResultConverter(button -> {
			if (button == editButton) {
				if (!(InappropriateChecker.checkForValidContents(editedAnswer.getText())).equals("")) {
					showAlert("Edited Answer is not Valid", (InappropriateChecker.checkForValidContents(editedAnswer.getText())));
				}
				else {
					return editedAnswer.getText();
				}
			}
			
			return null;
		});
		
		editAnswerDialog.showAndWait().ifPresent(String -> {
			
			answer.setAnswerText(String);
			answers.updateAnswer(answer);
			
			displayQuestionDetails(question);
			showAlert("Answer Edited", "Your answer has been successfully edited!");
			
		});
	}
	
	private void showFullAnswer(Answer answer) {
		showAlert("Full reply by " + answer.getUserName() + " | Answer ID: " + answer.getId(), answer.getAnswerText());
	}
	
	
	private void solutionAnswer(Answer answer, Question question) {
		answer.setCorrect(true);
		answers.updateAnswer(answer);
		
		if (!question.getIsSolved()) {
			setQuestionSolved(question, mainBackgroundQuestionBox, currentUser, questionListView);
		}
		
		answers.updateAnswersByQuestionId(question.getId());
		displayQuestionDetails(question);
	}

	private void deleteAnswer(Answer answer, Question question) {
		if (answer.isCorrect()) {
			setQuestionSolved(question, mainBackgroundQuestionBox, currentUser, questionListView);
		}
		answers.deleteAnswer(answer.getId());
		displayQuestionDetails(question);
	}

	// This can be deleted if the PageFilter class is satisfactory 
	/*private void filterQuestionsByCategory(String category, ListView<Question> questionListView) {
		questionListView.getItems().clear();
		if ("All".equals(category)) {
			questionListView.getItems().addAll(questionList.getAllQuestions());
		} else if ("My Questions".equals(category)) {
			questionListView.getItems().addAll(questionList.getAllQuestions().stream()
					.filter(q -> q.getUserName().equals(currentUser.getUserName())).collect(Collectors.toList()));
		} else {
			questionListView.getItems().addAll(questionList.getAllQuestions().stream()
					.filter(q -> q.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList()));
		}
	}

	private void filterQuestionsBySolved(String solvedOrNot, ListView<Question> questionListView) {
		questionListView.getItems().clear();
		if ("All".equals(solvedOrNot)) {
			questionListView.getItems().addAll(questionList.getAllQuestions());
		} else if ("Unsolved".equals(solvedOrNot)) {
			questionListView.getItems().addAll(
					questionList.getAllQuestions().stream().filter(q -> !q.getIsSolved()).collect(Collectors.toList()));
		} else if ("Solved".equals(solvedOrNot)) {
			questionListView.getItems().addAll(
					questionList.getAllQuestions().stream().filter(q -> q.getIsSolved()).collect(Collectors.toList()));

		}
	}
	
	// Filter for recently added questions
	private void filterQuestionsByRecent(String recent, ListView<Question> questionListView) {
		
		questionListView.getItems().clear();
		if ("All".equals(recent)) {
			questionListView.getItems().addAll(questionList.getAllQuestions());
		} else if ("Recent".equals(recent)){
		
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		System.out.println(currentTime + "\n");
		String curTime = currentTime.substring(0, 10);
		System.out.println(curTime + "\n");
		
		questionListView.getItems().addAll(
				questionList.getAllQuestions().stream().filter(q -> databaseHelper.getCreationTime(q.getId()).substring(0, 10).equals(curTime)).collect(Collectors.toList()));
		}
	} //*/

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void loadAnswersFromDB(Question question) {
		answers.updateAnswersByQuestionId(question.getId());
		ObservableList<Answer> aList = FXCollections.observableArrayList(answers.getAnswersByQuestionId(question.getId()));
		answersListView.setItems(aList);
	}
	
	// for sending receiving private messages about questions  
	private void privateMessage(User user, String questionUser, int questionID, ListView<Message> messageListView) {
		
		Dialog<Message> dialog = new Dialog<>();
		dialog.setTitle("Ask a Question");

		VBox content = new VBox(10);

		TextField titleField = new TextField();
		titleField.setPromptText("Enter Message Title");

		TextArea contentField = new TextArea();
		contentField.setPromptText("Enter Your Message Details");

		content.getChildren().addAll(contentField);

		dialog.getDialogPane().setContent(content);

		ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

		dialog.setResultConverter(button -> {
			if (button == submitButton) {
				if (!(InappropriateChecker.checkForValidContents(contentField.getText())).equals("")) {
					showAlert("Contents is not Valid",
							(InappropriateChecker.checkForValidContents(contentField.getText())));
				} else {
					System.out.println(relatedQuestionID);
					Message temp = new Message(0, user.getUserName(), questionUser,
							contentField.getText(), questionID, false);
					messageList.addMessage(temp);
					return temp;
				}
			}
			return null;
		});

		dialog.showAndWait().ifPresent(message -> {
			
			messageListView.getItems().add(message);
			showAlert("Message Submitted", "Your message has been added successfully.");
		});
	}
}
