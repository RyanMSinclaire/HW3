package tests.filter;

/**
 * <p> 
 * Title: FileTest Class
 * <p>
 * This class is to test the methods in the PageFilter class
 * <p>
 * The first method is the recent filter test: {@link recentFilterTest}. 
 * The recent filter positive test checks that the recent filter list 
 * has a recent item and then prints out passed or failed message.
 * The negative test checks if an item outside of the recent list 
 * isn't in the List View.
 * <p>
 * The second method is the category filter test: {@link categoryFilterTest}.
 * This methods positive test checks that the first item that has a category
 * of General from the list view and from Questions are the same. 
 * <p>
 * The third method is the is solved filter test: {@link isSolvedFilterTest}.
 * This methods positive test starts by forcing the first Question in 
 * Questions to have an isSolved of true, then the test separates the two lists.
 * Then the test checks that the first item of each list is the correct item.
 * <p>
 * This last method is my questions filter test. {@link myQuestionsFilterTest}.
 * This method just checks if the list created by my filter is correct.
 * This test is currently not working.   
 * 
 * @param databaseHelper
 *  
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;
import javafx.scene.control.ListView;
import mainClassesQuestionAnswer.Question;
import mainClassesQuestionAnswer.Questions;
import mainClassesUser.User;

public class FilterTest {
	
	private ListView<Question> solvedList;
	private ListView<Question> unsolvedList;
	private ListView<Question> recentList;
	private ListView<Question> categoryList;
	private ListView<Question> testList; 
	private Questions questionList; 
	private Connection connection;
	private DatabaseHelper databaseHelper; 
	private Question testQ;
	private Question rightQ;
	private Question wrongQ; 
	private int i = 1; 

	public FilterTest(DatabaseHelper databaseHelper) throws SQLException {
		this.databaseHelper = databaseHelper; 
		this.connection = databaseHelper.getConnection();
		this.questionList = new Questions(connection);
		recentList = new ListView<>();
		solvedList = new ListView<>();
		unsolvedList = new ListView<>();
		categoryList = new ListView<>();
	}
	
	public void filterTest() {
		recentFilterTest();
		categoryFilterTest();
		isSolvedFilterTest();
		myQuestionFilterTest();
	}
	/**************************Recent filter check**************************/
	private void recentFilterTest() {
		String curTime = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		this.recentList.getItems().addAll(
				questionList.getAllQuestions().stream().filter(q -> databaseHelper.getCreationTime(
																q.getId()).substring(0, 10).equals(
																curTime)).collect(Collectors.toList()));
		this.testQ = recentList.getItems().getFirst();
		

		// Recent filter positive check 
		if (testQ.getContentsText().equals(questionList.getQuestionById(1).getContentsText())) {
			System.out.println("Recent filter positive test passed\n");
		} else {
			System.out.println("Recent filter positive test failed\n");
		}
		
		// Recent filter negative check
		if (!testQ.getContentsText().equals(questionList.getQuestionById(2).getContentsText())) {
			System.out.println("Recent filter negative test passed\n");
		} else {
			System.out.println("Recent filter negative test failed\n");
		}
	}
	
	/**************************Category filter check**************************/
	private void categoryFilterTest() {
		String category = "General";
		
		int a = 0; 
		int b = 0;
		
		// Load rightQ with the correct category
		while ((i < 11) && (b == 0)) {
			this.rightQ = questionList.getQuestionById(i);
			
			if (rightQ.getCategory().equals(category)) {
				b = 1; 
				i = 0;
			}
			i++;
		}
		
		// Load wrongQ with the incorrect category
		while ((i < 11) && (a == 0)) {
			this.wrongQ = questionList.getQuestionById(i);
			
			if (!wrongQ.getCategory().equals(category)) {
				a = 1; 
				i = 0; 
			}
			i++;
		}
		
		// If neither Q is null continue 
		if (!(rightQ == null) && !(wrongQ == null)) {
			
		categoryList.getItems().addAll(questionList.getAllQuestions().stream()
				.filter(q -> q.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList()));
		
		this.testQ = categoryList.getItems().getFirst();
		
		// Category filter positive check
		if (testQ.getContentsText().equals(rightQ.getContentsText())) {
			System.out.println("Category filter positive test passed\n");
		} else {
			System.out.println("Category filter positive test failed\n");
		}
		
		// Category filter negative check
		if (!testQ.getContentsText().equals(wrongQ.getContentsText())) {
			System.out.println("Category filter negative test passed\n");
		} else {
			System.out.println("Category filter negative test failed\n");
		}
		
		}
	}
		
	
		/**********************Is Solved filter test**********************/ 
		private void isSolvedFilterTest() {
		this.testQ = questionList.getQuestionById(1); 
		testQ.setIsSolved(true);
		
		// solved 
		this.solvedList.getItems().addAll(
				questionList.getAllQuestions().stream().filter(q -> q.getIsSolved()).collect(Collectors.toList()));
		
		//unsolved
		this.unsolvedList.getItems().addAll(
				questionList.getAllQuestions().stream().filter(q -> !q.getIsSolved()).collect(Collectors.toList()));
		
		// Positive test
		if (testQ.getContentsText().equals(solvedList.getItems().get(0).getContentsText())){
			System.out.println("Is solved filter positive test passed\n");
		} else {
			System.out.println("Is solved filter positive test failed\n");
		}
		
		// Negative test
		if (!unsolvedList.getItems().get(0).getIsSolved()){
			System.out.println("Is unsolved filter negative test passed\n");
		} else {
			System.out.println("Is unsolved filter negative test failed\n");
		}
	}
		/**********************My Question test**********************/
		private void myQuestionFilterTest() {
		User admin = databaseHelper.getLoggedInUser(); 
		
		categoryList.getItems().addAll(questionList.getAllQuestions().stream()
				.filter(q -> q.getUserName().equals(admin.getUserName())).collect(Collectors.toList()));
		
		int c = 0;
		
		while ((i < 11) && (c == 0)) {
			this.testQ = questionList.getQuestionById(i);
			if (testQ.getUserName().equals("Admin")) {
				c = 1;
				i = 0;
			}
			i++;
		}
		
		// My Question positive test
		if (categoryList.getItems().get(0).getId() == testQ.getId()) {
			System.out.println("My Question filter positive test passed\n");
		} else {
			System.out.println("My Question filter positive test failed\n");
		}
		
		List<User> testUL = databaseHelper.getUserList();
		User testU = new User(); 
		
		int d = 0;
		
		
		while ((i < 11) && (d == 0)) {
		testU = testUL.get(i);
		if (!testU.getUserName().equals("Admin")) {
			d = 1;
			i = 0;
		}
		i++;
		}
		 
		// My Question negative test
		if (testQ.getUserName().equals(testU.getUserName())) {
			System.out.println("My Question filter negative test passed");
		} else {
			System.out.println("My Question filter negative test failed");
		}
	}
}
