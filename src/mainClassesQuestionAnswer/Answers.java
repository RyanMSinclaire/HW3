package mainClassesQuestionAnswer;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Answers {
    private Connection connection;
    
    private List<Answer> answerList;

    public Answers(Connection connection) {
    	this.connection = connection;
    	answerList = new ArrayList<>();
    }

    // Add Answer to the database
    public boolean uploadAnswer(Answer answer) {
        String query = "INSERT INTO Answers ("
        		+ "userName, "
        		+ "answerText, "
        		+ "isCorrect, "
        		+ "masterQuestionId, "
        		+ "relatedAnswerID, "
        		+ "created_at, "
        		+ "updated_at"
        		+ ") VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answer.getUserName());
            pstmt.setString(2, answer.getAnswerText());
            pstmt.setBoolean(3, answer.isCorrect());
            pstmt.setInt(4, answer.getMasterQuestionId()); 
            pstmt.setInt(5, answer.getRelatedAnswerID());     
            pstmt.executeUpdate();
            answerList.add(answer);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Answer> getAnswersList() {
    	return new ArrayList<>(answerList);
    }
    
    public void clearAnswersList() {
    	answerList.clear();
    	return;
    }
    
    public void addAnswer(Answer answer) {
    	answerList.add(answer);
    	return;
    }
    
    public int getAnswersListSize() {
    	return answerList.size();
    }
    
    public Answer getAnswerByID(int Id) {
    	return answerList.get(Id);
    }
    
    public void clearAnswers() {
    	answerList.clear();
    	return;
    }
    
    public int getAnswerListSize() {
    	return answerList.size();
    }
    

    // Get all answers for a specific question
    public List<Answer> getAnswersByQuestionId(int questionId) {
        List<Answer> answerList = new ArrayList<>();
        String query = "SELECT * FROM Answers WHERE masterQuestionId = ? ORDER BY isCorrect = false, isCorrect = true";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Answer answer = new Answer(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("answerText"),
                        rs.getBoolean("isCorrect"),
                        rs.getInt("masterQuestionId"),
                        rs.getInt("relatedAnswerID")                      
                );
                answerList.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answerList;
    }
    
    public void updateAnswersByQuestionId(int questionId) {
    	clearAnswersList();
        String query = "SELECT * FROM Answers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Answer answer = new Answer(
                		rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("answerText"),
                        rs.getBoolean("isCorrect"),
                        rs.getInt("masterQuestionId"),
                        rs.getInt("relatedAnswerID")   
                );
                answerList.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }
    
    // Update an existing answer
    public void updateAnswer(Answer answer) {
        String query = "UPDATE Answers SET answerText = ?, isCorrect = ?, relatedAnswerID = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answer.getAnswerText());
            pstmt.setBoolean(2, answer.isCorrect());
            pstmt.setInt(3, answer.getMasterQuestionId());            
            pstmt.setInt(4, answer.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete an answer by ID
    public void deleteAnswer(int id) {
        String query = "DELETE FROM Answers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
