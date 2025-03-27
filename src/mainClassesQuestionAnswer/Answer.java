package mainClassesQuestionAnswer;

import java.sql.Timestamp;

public class Answer {
	
    private int id;
	private String userName;
	private String answerText;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private boolean isCorrect;
	private int masterQuestionId;
	private int relatedAnswerID;
    
    public Answer(int id, String userName, String answerText, Timestamp createdAt, Timestamp updatedAt, boolean isCorrect, int masterQuestionId, int relatedAnswerID) {
        this.id = id;
        this.userName = userName;
        this.answerText = answerText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isCorrect = isCorrect;
        this.masterQuestionId = masterQuestionId;
        this.relatedAnswerID = relatedAnswerID;
        
    }
    
    public Answer(int id, String userName, String answerText, boolean isCorrect, int masterQuestionId,int relatedAnswerID) {
        this.id = id;
        this.userName = userName;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.masterQuestionId = masterQuestionId;
        this.relatedAnswerID = 0;
        
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getAnswerText() { return answerText; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public boolean isCorrect() { return isCorrect; }
    public int getMasterQuestionId() { return masterQuestionId; }
    public int getRelatedAnswerID() { return relatedAnswerID; }
    
    public void setId(int id) { this.id = id; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }    
    public void setCorrect(boolean correct) { this.isCorrect = correct; }
    public void setMasterQuestionId(int masterQuestionId) {this.masterQuestionId = masterQuestionId;}
    public void setRelatedAnswerID(int relatedAnswerID) {this.relatedAnswerID = relatedAnswerID;}

    @Override
    public String toString() {
        return "[" + id + "] Answer by " + userName + ": " + answerText + (isCorrect ? " (Correct)" : " (Incorrect)");
    }
}