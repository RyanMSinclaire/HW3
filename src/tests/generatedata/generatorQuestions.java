package tests.generatedata;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import databasePart1.DatabaseHelper;
import mainClassesQuestionAnswer.Answers;
import mainClassesQuestionAnswer.Question;
import mainClassesQuestionAnswer.Questions;
import mainClassesUser.User;

public class generatorQuestions {

    // Generates a list of random questions for testing purposes.
    public static Questions generateRandomQuestions(DatabaseHelper databaseHelper, int numberOfQuestions) throws SQLException {

    	
        Random random = new Random();

        // Sample user names and categories
        String[] categories = {"General", "Assignments", "Exams", "Projects"};
        
        List<User> Users = databaseHelper.getUserList();
        
        System.out.printf("%-5s %-15s %-12s %-15s %-30s\n", "ID", "UserName", "Category", "QuestionTitle", "Question");
        System.out.println("-----------------------------------------------------------");
        // Generate questions
        Questions newQuestions = new Questions(databaseHelper.getConnection()); // *** CREATING questions class
        for (int i = 0; i < numberOfQuestions; i++) {
        	User randomUser = Users.get(random.nextInt(Users.size()));
            // Random data for each question
            String category = categories[random.nextInt(categories.length)];
            String textQuestionTitle = "Title " + (i + 1);
            String textQuestionBody = "What is the answer to my question number " + (i + 1) + "?";
            boolean statusAnswered = random.nextBoolean();
            String status = statusAnswered ? "Answered" : "Unanswered";
            boolean statusResolved = random.nextBoolean();
            int relatedQuestionId = (i > 0) ? random.nextInt(i) : 0; // Random related question if applicable
            //int unreadAnswersCount = random.nextInt(random.nextInt(i)); 
            int unreadAnswersCount = (i > 1) ? random.nextInt(i) : 0; // Random number of unread answers

            // Create a new Question object
            Question question = new Question(0, 
            		randomUser.getUserName(),
            		textQuestionTitle,
            		category,
            		textQuestionBody,
            		relatedQuestionId, 
            		false); // *** CREATES new question

           
            
            System.out.printf("%-5d %-15s %-12s %-15s  %-30s \n",
                    question.getId(), question.getUserName(), question.getCategory(),
                    question.getTitleText(), 
                    question.getContentsText()); 

            newQuestions.addQuestion(question);		// *** UPDATES questions list
            
        }						
        return newQuestions;
    }
}
