package pagesMainFeatures;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;
import javafx.scene.control.ListView;
import mainClassesQuestionAnswer.Question;
import mainClassesQuestionAnswer.Questions;
import mainClassesUser.User;


public class PageFilter {
		
	private ListView<Question> tempView;
	private Questions questionList;
	private User currentUser;
	private DatabaseHelper databaseHelper;
	private int allCheck;
	
	public PageFilter(DatabaseHelper databaseHelper, User currentUser) {
		this.currentUser = currentUser;
		this.databaseHelper = databaseHelper;
		
	}
	
	public ListView<Question> runFilterChecker( Questions questionList, ListView<Question> inputList, String filterCat, String filterSol, String filterRec){
		this.tempView = inputList;
		this.questionList = questionList;
		allCheck = 0;
		tempView.getItems().clear();
		
		tempView = filterQuestionsByRecent(filterRec, tempView);
		tempView = filterQuestionsBySolved(filterSol, tempView);
		tempView = filterQuestionsByCategory(filterCat, tempView);
		tempView = checkIfAll(allCheck, tempView);
		
		
		
		return tempView;
	}
	
	// Filter for recently asked
	private ListView<Question> filterQuestionsByRecent(String recent, ListView<Question> questionListView) {
		
		if ("All".equals(recent)) {
			allCheck++;
		} else if ("Recent".equals(recent)){
			this.allCheck = 0;
			
			String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
			String curTime = currentTime.substring(0, 10);
		
			questionListView.getItems().addAll(
				questionList.getAllQuestions().stream().filter(q -> databaseHelper.getCreationTime(q.getId()).substring(0, 10).equals(curTime)).collect(Collectors.toList()));
		}
		return questionListView; 
	}
	
	// Filter for Solved
	private ListView<Question> filterQuestionsBySolved(String solvedOrNot, ListView<Question> questionListView) {
		
		if ("All".equals(solvedOrNot)) {
			allCheck++;
			
		} else if ("Unsolved".equals(solvedOrNot)) {
			this.allCheck = 0;
			questionListView.getItems().addAll(
					questionList.getAllQuestions().stream().filter(q -> !q.getIsSolved()).collect(Collectors.toList()));
			
		} else if ("Solved".equals(solvedOrNot)) {
			this.allCheck = 0;
			questionListView.getItems().addAll(
					questionList.getAllQuestions().stream().filter(q -> q.getIsSolved()).collect(Collectors.toList()));
			
		}
		return questionListView;
	}
	
	// Filter for category
	private ListView<Question> filterQuestionsByCategory(String category, ListView<Question> questionListView) {

		if ("All".equals(category)) {
			allCheck++;
			
		} else if ("My Questions".equals(category)) {
			this.allCheck = 0;
			questionListView.getItems().addAll(questionList.getAllQuestions().stream()
					.filter(q -> q.getUserName().equals(currentUser.getUserName())).collect(Collectors.toList()));
			
		} else {
			this.allCheck = 0;
			questionListView.getItems().addAll(questionList.getAllQuestions().stream()
					.filter(q -> q.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList()));
			
		}
		return questionListView;

	}
	
	
	private ListView<Question> checkIfAll(int allCheck, ListView<Question> questionListView) {
		
		if (allCheck == 3) {
			questionListView.getItems().addAll(questionList.getAllQuestions());
		}
		return questionListView;
	
	}
	
}
