package mainClassesMessaging;

public class Message {
    private int id;
    //private int senderUserID;
    private String userName;
    private String reciverUserID;  
    private String messageText;
    private int relatedQuestionLinkID;  
    private int relatedAnswerLinkID;  
    private boolean isRead;

    
    public Message(int id, String userName, String reciverUserID, String massegeText, int relatedQuestionLinkID, boolean isReaden) {
        this.id = id;
        //this.senderUserID = senderUserID;
        this.userName = userName;
        this.messageText = massegeText;
        this.isRead = isReaden;
        this.reciverUserID = reciverUserID;
        this.relatedQuestionLinkID = relatedQuestionLinkID;
        //this.relatedAnswerLinkID = relatedAnswerLinkID;        
        
    }

    public int getId() { return id; }
    public String getSenderUserID() { return userName; }
    public String getReciverUserID() { return reciverUserID; }
    public String getMessegeText() { return messageText; }
    public int getRelatedQuestionLinkID() { return relatedQuestionLinkID; }
    //public int getRelatedAnswerLinkID() { return relatedAnswerLinkID; }
    public boolean isReaden() { return isRead; }

    
    
    public void setuserName(String userName) {this.userName = userName;}
    public void setReciverUserID(String reciverUserID) {this.reciverUserID = reciverUserID;}
    public void setMessegeText(String massegeText) { this.messageText = massegeText; }
    public void setRelatedQuestionLinkID(int relatedQuestionLinkID) {this.relatedQuestionLinkID = relatedQuestionLinkID;}
    //public void setRelatedAnswerID(int relatedAnswerID) {this.relatedAnswerLinkID = relatedAnswerID;}
    public void setReaden(boolean isReaden) { this.isRead = isReaden; }


    @Override
    public String toString() {
        return "[" + id + "] Message by " + userName + ": " + messageText + (isRead ? " (Read)" : " (Not Read Yet)");
    }
}