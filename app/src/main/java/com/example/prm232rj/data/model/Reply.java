package com.example.prm232rj.data.model;
import com.google.firebase.Timestamp;

public class Reply {
    private String id;               // Firestore document ID (set sau khi push)
    private String conversationId;   // ID của comment gốc
    private String replyId;          //Id của reply được rep.
    private String userId;           // người gửi
    private String userName;
    private String replyName;        //tên người được nhắc.
    private String avatarUrl;
    private String content;
    private Timestamp createdAt;

    private String userReplyId;       // userId của người bị reply (nếu có)

    public Reply() {
        // Required for Firestore deserialization
    }

    public Reply(String conversationId, String replyId, String userId,
                 String userName, String avatarUrl, String content, Timestamp createdAt,
                 String replyName, String userReplyId) {
        this.conversationId = conversationId;
        this.replyId = replyId;
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.createdAt = createdAt;
        this.replyName = replyName;
        this.userReplyId = userReplyId;
    }

    public String getUserReplyId() {
        return userReplyId;
    }

    public void setUserReplyId(String userReplyId) {
        this.userReplyId = userReplyId;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    // 🔧 Getter & Setter
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }

    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getReplyId() { return replyId; }

    public void setReplyId(String replyId) { this.replyId = replyId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getAvatarUrl() { return avatarUrl; }

    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }

    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

