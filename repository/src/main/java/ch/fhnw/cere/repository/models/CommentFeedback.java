package ch.fhnw.cere.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
public class CommentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;


    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private EndUser user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="fk_parent_comment")
    private CommentFeedback parentComment;

    @OneToMany(mappedBy="parentComment")
    private Collection<CommentFeedback> children;

    private String commentText;
    private Date createdAt;
    private Date updatedAt;
    private Boolean bool_is_developer;
    private Boolean activeStatus;

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public void setUser(EndUser user) {
        this.user = user;
    }

    public CommentFeedback getParentComment() {
        return parentComment;
    }

    public void setParentComment(CommentFeedback parentComment) {
        this.parentComment = parentComment;
    }

    public Collection<CommentFeedback> getChildren() {
        return children;
    }

    public void setChildren(Collection<CommentFeedback> children) {
        this.children = children;
    }

    public String getCommentText(){ return commentText;}
    public Boolean check_is_developer() { return bool_is_developer;}
    public Boolean getActiveStatus() { return activeStatus; }
    public long getId() {return id;}
    public Date getCreatedAt(){return createdAt;}
    public Date getUpdatedAt(){return updatedAt;}
    public Feedback getFeedback() { return feedback; }
    public EndUser getUser() { return user;}

    public void setCommentText(String commentText){this.commentText = commentText;}
    public void setBool_is_developer(Boolean is_developer){this.bool_is_developer = is_developer;}
    public void setActiveStatus(Boolean activeStatus){this.activeStatus = activeStatus;}

    @PrePersist
    protected void onCreate(){ createdAt = new Date();}

    @PreUpdate
    protected void onUpdate(){ updatedAt = new Date();}

    public CommentFeedback(){
    }

    public CommentFeedback(Feedback feedback, EndUser user, boolean is_developer, String commentText, boolean activeStatus, CommentFeedback parentComment,
                           Collection<CommentFeedback>... children){
        this.feedback = feedback;
        this.user = user;
        this.bool_is_developer = is_developer;
        this.commentText = commentText;
        this.activeStatus = activeStatus;
        this.parentComment = parentComment;
    }
}
