package tests.generatedata;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import mainClassesUser.User;
import databasePart1.DatabaseHelper;
import mainClassesQuestionAnswer.Answer;
import mainClassesQuestionAnswer.Answers;
import mainClassesQuestionAnswer.Question;
import mainClassesQuestionAnswer.Questions;

public class generatorAnswers {

    // Generates a list of random answers for each question from the question list.
    // This will help me simulate users responding to questions in the database.
	
    public static Boolean generateRandomAnswers(DatabaseHelper databaseHelper, Questions questionList) throws SQLException {
    	// This variable helps keep track of the success/failure of the answer upload process
        Boolean uploadComplete = false; 
        // Random object to generate random values for answers
        Random random = new Random(); 
        // Example categories for different types of questions
        String[] categories = {"General", "Phase1", "Phase2", "Phase3", "Phase4"}; 
        // Retrieve the list of all users from the database
        List<User> Users = databaseHelper.getUserList();
        Answers answerList = new Answers(databaseHelper.getConnection());
        
        // Print header for debugging, showing what we're processing
        System.out.printf("%-5s %-15s %-5s %-15s\n", "ID", "Username",  "QuestionId", "Answer");
        System.out.println("-----------------------------------------------------------");
        
        int totalQuestions = questionList.getQuestionListSize(); // *** READ the size of a question list size to generate specific answers for *** 
        
        // Iterate over each question in the list
        for (int i = 0; i < totalQuestions; i++) {
        	
            Question question = questionList.getQuestionById(i + 1); // *** READ a master question *** 
            
            // Choosing a random count of answers to generate per question  
            int randomAnswerCount = random.nextInt(3) + 1; 
            // Looping to generate random answers for the question
            for (int j = 0; j < randomAnswerCount; j++) { 
                String questionsUsername = question.getUserName();	
                // Choosing an example ID for each answer
                int answerID = j;									
                // Choosing a random username for the example reply.
                // this also avoids self replies
                List<User> filteredUsers = Users.stream()
                    .filter(user -> !user.getUserName().equals(questionsUsername)) 			
                    .collect(Collectors.toList());
                // Picks a random user (from the filtered user list)
                String answersUsername = filteredUsers.get(random.nextInt(9)).getUserName(); 
                // Sample reply 
                String textReply = "Sample reply from " + answersUsername; 
                
                int masterQuestionId = question.getId(); 
                // Print the generated answer for debugging purposes
                System.out.printf("%-5d %-15s %-5d %-15s\n", answerID, answersUsername,  masterQuestionId, textReply);
                Answer answer = new Answer(answerID, answersUsername, textReply, false, masterQuestionId,-1);
                answerList.addAnswer(answer); // *** UPDATE answer list with new answer *** 
            }
            int totalAnswers = answerList.getAnswersListSize();
            
            // Attempt to upload each answer to the database
            for (int k = 0; k < totalAnswers; k++) {
            	Answer newAnswer = answerList.getAnswerByID(k); 						// *** READ each answer *** 
            	uploadComplete = answerList.uploadAnswer(newAnswer); 
            	// If uploading failed during any iteration, exit the outer loop as well
            	if (!uploadComplete) {
                    break;
                }
            }
            
            answerList.clearAnswers(); 				// *** DELETE/Clear out the answer list to ensure clean slate for next question.
        }
        
        // Return whether the upload was successful or not
        return uploadComplete;
    }
}
