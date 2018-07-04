package ch.fhnw.cere.repository.controllers;

import ch.fhnw.cere.repository.models.FeedbackLabel;
import ch.fhnw.cere.repository.services.FeedbackLabelService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "${supersede.base_path.feedback}/{language}/applications/{applicationId}/feedbacks/labels")
public class FeedbackLabelController extends BaseController {

    @Autowired
    private FeedbackLabelService feedbackLabelService;

    @PreAuthorize("@securityService.hasAdminPermission(#applicationId)")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public Map<String, List<String>> getFeedbackLabels(@PathVariable long applicationId) {
        List<String> result = feedbackLabelService.findDistinctLabel();
        Map<String, List<String>> response = new HashMap<>();
        response.put("labels", result);
        return response;
    }

    @PreAuthorize("@securityService.hasAdminPermission(#applicationId)")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public FeedbackLabel getFeedbackLabel(@PathVariable long applicationId, @PathVariable long id) {
        return feedbackLabelService.find(id);
    }


    //TODO: Find out if this is even necessery
    @PreAuthorize("@securityService.hasAdminPermission(#applicationId)")
    @RequestMapping(method = RequestMethod.POST, value = "")
    public List<FeedbackLabel> createFeedbackLabels(HttpEntity<String> labelRequest) {
        /*if (labelRequest.getBody() != null) {
            JSONObject obj = new JSONObject(labelRequest.getBody());
            JSONArray labels = obj.getJSONArray("labels");
            for (int i = 0; i < labels.length(); i++) {
                String currentLabel = labels.getString(i);

            }
        }*/

        return null;

    }

    @PreAuthorize("@securityService.hasAdminPermission(#applicationId)")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteFeedbackLabel(@PathVariable long applicationId, @PathVariable long id) {
        feedbackLabelService.delete(id);
    }
}