package mainClassesQuestionAnswer;



public class Question {
    private int id;
    private String userName;
    private String titleText;
    private String category;
    private String contentsText;
    private int relatedQuestionID = 0;
    private boolean isSolved;
    private Answers answers;

    //Initial Question
    public Question() {}
    
    public Question(int id, String userName, String titleText, String category, String contentsText, int relatedQuestionID, boolean isSolved) {
        this.id = id;
        this.userName = userName;
        this.titleText = titleText;
        this.category = category;
        this.contentsText = contentsText;
        this.relatedQuestionID = relatedQuestionID;
        this.isSolved = isSolved;
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getTitleText() { return titleText; }
    public String getCategory() { return category; }
    public String getContentsText() { return contentsText; }
    public int getRelatedQuestionID() { return relatedQuestionID;}
    public boolean getIsSolved() { return isSolved;}

    public void setId(int id) {
        this.id = id;
    }   
    public void setTitleText(String titleText) { this.titleText = titleText; }
    public void setCategory(String category) { this.category = category; }
    public void setContentsText(String contentsText) { this.contentsText = contentsText; }
    public void setRelatedQuestionID(int realatedQuestionID) { this.relatedQuestionID= realatedQuestionID;}
    public void setIsSolved(boolean isSolved) { this.isSolved= isSolved;}   
    
    

    @Override
    public String toString() {
        return "[" + id + "] " + titleText + " (Category: " + category + ") - " + contentsText;
    }
    
    
    public int getAnswerNumByQuestionID() {
        if (answers != null) {
            return answers.getAnswersByQuestionId(id).size();
        }
        return 0;
    }
    
}