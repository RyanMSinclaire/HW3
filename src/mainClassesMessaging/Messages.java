package mainClassesMessaging;

import databasePart1.DatabaseHelper;
import mainClassesQuestionAnswer.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Messages {
    private final DatabaseHelper databaseHelper;
   // private Connection connection;
    private List<Message> messageList;
    
   /* public Messages(Connection connection) {
        this.connection = connection;
        this.messageList = new ArrayList<>();
        updateAllMessages();
    } */

    public Messages(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        try {
            databaseHelper.connectToDatabase(); // Ensure the database connection is established
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 

    //add answer to database
    public void addMessage(Message message) {
        String query = "INSERT INTO Massages (userName, reciverUserID, massegeText, relatedQuestionLinkID, isReaden) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query)) {
            pstmt.setString(1, message.getSenderUserID());
            pstmt.setString(2, message.getReciverUserID());
            pstmt.setString(3, message.getMessegeText());
            pstmt.setInt(4, message.getRelatedQuestionLinkID());
            //pstmt.setInt(5, message.getRelatedAnswerLinkID());           
            pstmt.setBoolean(5, message.isReaden());           
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Message> getMessagebyUserID(String reciverUserID) {
        List<Message> massageList = new ArrayList<>();
        String query = "SELECT * FROM Massages WHERE reciverUserID = ?";

        try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query)) {
            pstmt.setString(1, reciverUserID); // Bug here
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	Message Message = new Message(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("reciverUserID"),
                        rs.getString("massegeText"),
                        rs.getInt("relatedQuestionLinkID"),
                       // rs.getInt("relatedAnswerLinkID"),
                        rs.getBoolean("isReaden")                       
                );
                massageList.add(Message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return massageList;
    }


    
    // Update an existing answer
    public void updateMessage(Message message) {
        String query = "UPDATE Massages SET userName = ?, reciverUserID = ?, massegeText = ?, relatedQuestionLinkID = ?, isReaden = ? WHERE id = ?";
        try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query)) {
            pstmt.setString(1, message.getSenderUserID());
            pstmt.setString(2, message.getReciverUserID());
            pstmt.setString(3, message.getMessegeText());
            pstmt.setInt(4, message.getRelatedQuestionLinkID());
            ///pstmt.setInt(5, message.getRelatedAnswerLinkID());           
            pstmt.setBoolean(5, message.isReaden());   
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete an answer by ID
    public void deleteAnswer(int id) {
        String query = "DELETE FROM Massages WHERE id = ?";
        try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateAllMessages() {
        messageList.clear();
        String query = "SELECT * FROM Messages";
        try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
            	Message message = new Message(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("reciverUserID"),
                        rs.getString("massegeText"),
                        rs.getInt("relatedQuestionLinkID"),
                       // rs.getInt("relatedAnswerLinkID"),
                        rs.getBoolean("isReaden")                       
                );
                messageList.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
