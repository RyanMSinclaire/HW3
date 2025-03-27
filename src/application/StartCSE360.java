package application;

import javafx.application.Application;
import javafx.stage.Stage;
import mainClassesQuestionAnswer.Question;
import mainClassesQuestionAnswer.Questions;
import mainClassesUser.User;
import pagesMainNavigation.PageFirst;
import pagesMainNavigation.PageSetupLoginSelection;
import tests.filter.FilterTest;
import tests.generatedata.generatorAnswers;
import tests.generatedata.generatorQuestions;
import tests.generatedata.generatorUsers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class StartCSE360 extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			databaseHelper.connectToDatabase(); // Connect to the database
			prepareSampleDatabase(databaseHelper.getConnection());
			if (databaseHelper.isDatabaseEmpty()) {

				new PageFirst(databaseHelper).show(primaryStage);
			} else {
				new PageSetupLoginSelection(databaseHelper).show(primaryStage);

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// This populates sample databases
	private void prepareSampleDatabase(Connection connection) {
		Boolean isUserDatabseEmpty = true;
		Boolean isQuestionDatabseEmpty = true;
		Boolean isAnswerDatabseEmpty = true;
		Boolean isAdminSetup = false;
		int adminCount = 0;
		
		Questions newQuestions = new Questions(connection);
		try {
			isAdminSetup = databaseHelper.doesUserExist("Admin");
			isUserDatabseEmpty = databaseHelper.isDatabaseEmpty();
			isQuestionDatabseEmpty = databaseHelper.isQuestionDatabaseEmpty();
			isAnswerDatabseEmpty = databaseHelper.isAnswerDatabaseEmpty();

			if (!isAdminSetup) {
				System.out.println("Main Admin missing! Generating test admin.");
				User admin = generatorUsers.generateAdmin();
				try {
					databaseHelper.register(admin, admin.getRoles());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Error registering user of username: " + admin.getUserName());
					e.printStackTrace();
				}
			} else {
				System.out.println("Main Admin account exists.");
			}

			if (isUserDatabseEmpty) {
				System.out.println("User database empty! Creating test users.");
				generateSampleUsers();
			} else {
				System.out.println("User database contains users.");
			}
			if (isQuestionDatabseEmpty) {
				System.out.println("Question database empty! Creating sample questions.");
				newQuestions = generateQuestions();
			} else {
				System.out.println("Question database contains questions.");
			}
			if (isAnswerDatabseEmpty) {
				System.out.println("Answer database empty! Creating sample responses to questions.");
				generateAnswers(newQuestions);
			} else {
				System.out.println("Answer database contains answers.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Boolean generateSampleUsers() {
		boolean allUsersRegistered = true;
		System.out.println("--------------------------Generating Users------------------------------------");
		List<User> generatedUsers = generatorUsers.generateRandomUsers(10); // Generates 10 users for testing
		for (User user : generatedUsers) {
			try {
				databaseHelper.register(user, user.getRoles());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error registering user of username: " + user.getUserName());
				e.printStackTrace();
				allUsersRegistered = false;
			}
		}
		System.out.println("--------------------------Generation Complete---------------------------------\n");
		return allUsersRegistered;
	}

	private Questions generateQuestions() throws SQLException {
		System.out.println("--------------------------GeneratingQuestions---------------------------------");
		Questions generatedQuestions = generatorQuestions.generateRandomQuestions(databaseHelper, 10); // Generates
		generatedQuestions.updateAllQuestions();
		System.out.println("--------------------------Generation Complete---------------------------------\n");
		return generatedQuestions;
	}

	private void generateAnswers(Questions questionList) throws SQLException {

		System.out.println("--------------------------GeneratingAnswers------------------------------------");
		Boolean generatedAnswers = generatorAnswers.generateRandomAnswers(databaseHelper, questionList); // Generates
																											// answers
																											// for
																											// testing
		if (!generatedAnswers) {
			System.out.println("Error Generating answers.");
		}
		System.out.println("--------------------------Generation Complete---------------------------------\n");
	}

}