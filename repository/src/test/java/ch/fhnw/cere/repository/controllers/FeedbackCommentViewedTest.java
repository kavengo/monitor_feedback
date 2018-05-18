package ch.fhnw.cere.repository.controllers;

import ch.fhnw.cere.repository.models.*;
import ch.fhnw.cere.repository.repositories.*;
import ch.fhnw.cere.repository.services.*;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Aydinli on 01.02.2018.
 */
public class FeedbackCommentViewedTest extends BaseIntegrationTest{
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CommentFeedbackService commentFeedbackService;

    @Autowired
    private CommentFeedbackRepository commentFeedbackRepository;

    @Autowired
    private FeedbackViewedService feedbackViewedService;

    @Autowired
    private FeedbackViewedRepository feedbackViewedRepository;

    @Autowired
    private CommentViewedService commentViewedService;

    @Autowired
    private CommentViewedRepository commentViewedRepository;

    @Autowired
    private EndUserService endUserService;

    @Autowired
    private EndUserRepository endUserRepository;

    @Autowired
    private ApiUserPermissionRepository apiUserPermissionRepository;

    private String basePathEn = "/feedback_repository/en/";

    private Feedback feedback1;
    private Feedback feedback2;
    private Feedback feedback3;


    private CommentFeedback commentFeedback1_1;
    private CommentFeedback commentFeedback1_2;
    private CommentFeedback commentFeedback1_1_1;
    private CommentFeedback commentFeedback1_1_2;

    private CommentFeedback commentFeedback2_1;
    private CommentFeedback commentFeedback2_2;
    private CommentFeedback commentFeedback2_3;

    private CommentFeedback commentFeedback3_1;

    private EndUser endUser1;
    private EndUser endUser2;
    private EndUser endUser3;

    private CommentViewed commentViewed;
    private FeedbackViewed feedbackViewed;

    @Before
    public void setup() throws Exception{
        super.setup();

        feedbackRepository.deleteAllInBatch();
        commentFeedbackRepository.deleteAllInBatch();
        endUserRepository.deleteAllInBatch();
        feedbackViewedRepository.deleteAllInBatch();
        commentViewedRepository.deleteAllInBatch();

        endUser1 = endUserRepository.save(new EndUser(1,"kaydin1"));
        endUser2 = endUserRepository.save(new EndUser(1,"kaydin2"));
        endUser3 = endUserRepository.save(new EndUser(1,"kaydin3"));

        feedback1 = feedbackRepository.save(new Feedback("Feedback 1 App 1",
                endUser1.getId(), 1, 11, "en"));
        feedback2 = feedbackRepository.save(new Feedback("Feedback 2 App 1",
                endUser2.getId(), 1, 11, "en"));
        feedback3 = feedbackRepository.save(new Feedback("Feedback 3 App 1",
                endUser3.getId(), 1, 22, "en"));


        commentFeedback1_1 = commentFeedbackRepository.save(new CommentFeedback(feedback1,
                endUser1,false,"First Comment of Feedback 1",
                false,null));
        commentFeedback1_1_1 = commentFeedbackRepository.save(new CommentFeedback(feedback1,
                endUser1,false,"First Subcomment of Comment 1",
                false,commentFeedback1_1));
        commentFeedback1_1_2 = commentFeedbackRepository.save(new CommentFeedback(feedback1,
                endUser1,false,"Second Subcomment of Comment 1",
                false,commentFeedback1_1));
        commentFeedback1_2 = commentFeedbackRepository.save(new CommentFeedback(feedback1,
                endUser1,false,"Second Comment of Feedback 1",
                false,null));

        commentFeedback2_1 = commentFeedbackRepository.save(new CommentFeedback(feedback2,
                endUser2,false,"First Comment of Feedback 2",
                false,null));
        commentFeedback2_2 = commentFeedbackRepository.save(new CommentFeedback(feedback2,
                endUser2,false,"Second Comment of Feedback 2",
                false,null));
        commentFeedback2_3 = commentFeedbackRepository.save(new CommentFeedback(feedback2,
                endUser2,false,"Third Comment of Feedback 2",
                false,null));

        commentFeedback3_1 = commentFeedbackRepository.save(new CommentFeedback(feedback3,
                endUser3,false,"First Comment of Feedback 3",
                false,null));

        commentViewed = commentViewedRepository.save(new CommentViewed(endUser1,commentFeedback1_1));
        feedbackViewed = feedbackViewedRepository.save(new FeedbackViewed(endUser1,feedback1));

        apiUserPermissionRepository.save(new ApiUserPermission(appAdminUser, 1, true));
        apiUserPermissionRepository.save(new ApiUserPermission(appAdminUser, 20, true));
    }

    @After
    public void cleanUp(){
        super.cleanUp();

        feedbackRepository.deleteAllInBatch();
        commentFeedbackRepository.deleteAllInBatch();
        feedbackViewedRepository.deleteAllInBatch();
        commentViewedRepository.deleteAllInBatch();
        endUserRepository.deleteAllInBatch();
        apiUserPermissionRepository.deleteAllInBatch();
    }

    @Test
    public void testEnduser() throws Exception{
        String adminJWTToken = requestAppAdminJWTToken();

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/end_user")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/end_user" +
                "/username/kaydin1")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("kaydin1")));

        String enduser = new JSONObject()
                .put("username","kaydin4")
                .put("application_id",20)
                .toString();

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/end_user")
                .header("Authorization", adminJWTToken)
                .content(enduser))
                .andExpect(status().isCreated());

        this.mockMvc.perform(delete(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/end_user/"+endUser1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk());

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/end_user/" +
                endUser2.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(endUser2.getUsername())));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/end_user/" +
                0)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCommentViews() throws Exception{
        String adminJWTToken = requestAppAdminJWTToken();

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views" +
                "/comment/"+commentFeedback1_1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        String result = mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views" +
                "/both/"+endUser1.getId()+"/"+commentFeedback1_1.getId())
                .header("Authorization", adminJWTToken)).andReturn().getResponse()
                .getContentAsString();

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views" +
                "/both/"+endUser1.getId()+"/"+commentFeedback1_1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) commentViewed.getId())));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views/" +
                commentViewed.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) commentViewed.getId())));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views/" +
                0)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/comment_views/"+commentViewed.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk());

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/comment_views")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetFeedbackViews() throws Exception{
        String adminJWTToken = requestAppAdminJWTToken();

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/feedback_views")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/feedback_views" +
                "/feedback/"+feedback1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/feedback_views" +
                "/both/"+endUser1.getId()+"/"+feedback1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) feedbackViewed.getId())));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/feedback_views/" +
                feedbackViewed.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) feedbackViewed.getId())));

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/feedback_views/" +
                0)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/feedback_views/"+feedbackViewed.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk());

        mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks/feedback_views")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testViewFeedback() throws Exception {
        String adminJWTToken = requestAppAdminJWTToken();

        String viewFeedback2 = new JSONObject()
                .put("feedback_id",feedback2.getId())
                .put("user_id",endUser1.getId())
                .toString();

        String viewFeedback3 = new JSONObject()
                .put("feedback_id",feedback3.getId())
                .put("user_id",endUser1.getId())
                .toString();

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/feedback_views")
                .content(viewFeedback2)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/feedback_views")
                .header("Authorization", adminJWTToken))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/feedback_views")
                .content(viewFeedback3)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/feedback_views/user/" +
                endUser1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testViewComments() throws Exception {
        String adminJWTToken = requestAppAdminJWTToken();

        String viewComment1 = new JSONObject()
                .put("comment_id",commentFeedback2_1.getId())
                .put("user_id",endUser1.getId())
                .toString();

        String viewComment2 = new JSONObject()
                .put("comment_id",commentFeedback2_2.getId())
                .put("user_id",endUser1.getId())
                .toString();

        String viewComment3 = new JSONObject()
                .put("comment_id",commentFeedback2_3.getId())
                .put("user_id",endUser1.getId())
                .toString();

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/comment_views")
                .content(viewComment1)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/comment_views")
                .content(viewComment2)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/comment_views")
                .content(viewComment3)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get(basePathEn + "applications/" + 1 + "/feedbacks" +
                "/comment_views/user/" +
                endUser1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }
}