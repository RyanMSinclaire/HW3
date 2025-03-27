package mainClassesQuestionAnswer;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Questions {
    private List<Question> questionList;
    private Connection connection;

    public Questions(Connection connection) {
        this.connection = connection;
        this.questionList = new ArrayList<>();
        updateAllQuestions();
    }


    public void addQuestion(Question question) {
        String query = "INSERT INTO Questions (userName, title, category, content, relatedQuestionID, isSolved) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, question.getUserName());
            pstmt.setString(2, question.getTitleText());
            pstmt.setString(3, question.getCategory());
            pstmt.setString(4, question.getContentsText());
            pstmt.setInt(5, question.getRelatedQuestionID());
            pstmt.setBoolean(6, question.getIsSolved());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        question.setId(generatedKeys.getInt(1));
                    }
                }
                questionList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateAllQuestions() {
        questionList.clear();
        String query = "SELECT * FROM Questions";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("content"),
                        rs.getInt("relatedQuestionID"),
                        rs.getBoolean("isSolved")
                );
                questionList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update Question
    public void updateQuestion(Question question) {
        String query = "UPDATE Questions SET title = ?, category = ?, content = ?, relatedQuestionID = ?, isSolved = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, question.getTitleText());
            pstmt.setString(2, question.getCategory());
            pstmt.setString(3, question.getContentsText());
            pstmt.setInt(4, question.getRelatedQuestionID());
            pstmt.setBoolean(5, question.getIsSolved());
            pstmt.setInt(6, question.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                questionList.replaceAll(q -> q.getId() == question.getId() ? question : q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Question
    public void deleteQuestion(int id) throws SQLException {
        String deleteAnswersQuery = "DELETE FROM Answers WHERE masterQuestionId = ?";
        String deleteQuestionQuery = "DELETE FROM Questions WHERE id = ?";

        connection.setAutoCommit(false);
        try (PreparedStatement deleteAnswersStmt = connection.prepareStatement(deleteAnswersQuery);
             PreparedStatement deleteQuestionStmt = connection.prepareStatement(deleteQuestionQuery)) {

            // 1. Delete Answer	
            deleteAnswersStmt.setInt(1, id);
            deleteAnswersStmt.executeUpdate();

            // 2. Delete Question
            deleteQuestionStmt.setInt(1, id);
            int affectedRows = deleteQuestionStmt.executeUpdate();

            if (affectedRows > 0) {
                connection.commit();
                questionList.removeIf(q -> q.getId() == id);
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }


    public List<Question> getQuestionsByUser(String userName) {
        List<Question> userQuestions = new ArrayList<>();
        for (Question question : questionList) {
            if (question.getUserName().equals(userName)) {
                userQuestions.add(question);
            }
        }
        return userQuestions;
    }


    public Question getQuestionById(int id) {
        return questionList.stream().filter(q -> q.getId() == id).findFirst().orElse(null);
    }


    public List<Question> searchQuestions(String keyword) {
        List<Question> searchResults = new ArrayList<>();
        String query = "SELECT * FROM Questions WHERE title LIKE ? OR content LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("content"),
                        rs.getInt("relatedQuestionID"),
                        rs.getBoolean("isSolved")
                );
                searchResults.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }


    public List<Question> getAllQuestions() {
        return new ArrayList<>(questionList);
    }


    public int getQuestionListSize() {
        return questionList.size();
    }
}
