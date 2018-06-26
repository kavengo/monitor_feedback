package ch.uzh.supersede.feedbacklibrary.stubs;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.*;

import ch.uzh.supersede.feedbacklibrary.beans.FeedbackBean;
import ch.uzh.supersede.feedbacklibrary.beans.FeedbackDetailsBean;
import ch.uzh.supersede.feedbacklibrary.beans.FeedbackResponseBean;
import ch.uzh.supersede.feedbacklibrary.beans.LocalFeedbackBean;
import ch.uzh.supersede.feedbacklibrary.database.FeedbackDatabase;
import ch.uzh.supersede.feedbacklibrary.models.Feedback;
import ch.uzh.supersede.feedbacklibrary.models.AuthenticateResponse;
import ch.uzh.supersede.feedbacklibrary.utils.*;
import ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_STATUS;

import static ch.uzh.supersede.feedbacklibrary.utils.Constants.USER_NAME;
import static ch.uzh.supersede.feedbacklibrary.utils.Enums.FEEDBACK_STATUS.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Enums.SAVE_MODE.*;
import static ch.uzh.supersede.feedbacklibrary.utils.PermissionUtility.USER_LEVEL.ACTIVE;

public class RepositoryStub {

    private RepositoryStub() {
    }

    public static List<FeedbackBean> getFeedback(Context context, int count, int minUpVotes, int maxUpVotes, float ownFeedbackPercent) {
        List<FeedbackBean> feedbackBeans = new ArrayList<>();
        if (ACTIVE.check(context) && FeedbackDatabase.getInstance(context).readBoolean(Constants.USE_STUBS,false)){
            List<LocalFeedbackBean> ownFeedbackBeans = FeedbackDatabase.getInstance(context).getFeedbackBeans(Enums.FETCH_MODE.OWN);
            if (!ownFeedbackBeans.isEmpty()){
                ownFeedbackPercent = 0;
            }
            for (LocalFeedbackBean bean : ownFeedbackBeans){
                feedbackBeans.add(new FeedbackBean.Builder().fromLocalFeedbackBean(context,bean));
            }
        }
        for (int f = 0; f < count; f++) {
            feedbackBeans.add(getFeedback(context, minUpVotes, maxUpVotes, ownFeedbackPercent));
        }
        return feedbackBeans;
    }

    private static List<FeedbackResponseBean> getFeedbackResponses(Context context, int count, long feedbackCreationDate, float developerPercent, float ownerPercent, FeedbackBean feedbackBean) {
        ArrayList<FeedbackResponseBean> feedbackResponseBeans = new ArrayList<>();
        for (int f = 0; f < count; f++) {
            feedbackResponseBeans.add(getFeedbackResponse(context, feedbackCreationDate, developerPercent, ownerPercent, feedbackBean));
        }
        return feedbackResponseBeans;
    }

    public static FeedbackDetailsBean getFeedbackDetails(Context context, FeedbackBean feedbackBean) {
        String[] content = generateDescriptionAndTitle();
        String description = content[0];
        String title = content[1];
        String userName = feedbackBean.getUserName();
        String[] labels = GeneratorStub.BagOfTags.pickRandom(5);
        int upVotes = feedbackBean.getUpVotes();
        long timeStamp = generateTimestamp();
        FEEDBACK_STATUS status = feedbackBean.getFeedbackStatus();
        List<FeedbackResponseBean> feedbackResponses = getFeedbackResponses(context, feedbackBean.getResponses(), timeStamp, 0.1f, 0.1f, feedbackBean);
        return new FeedbackDetailsBean.Builder()
                .withFeedbackId(feedbackBean.getFeedbackId())
                .withFeedbackBean(feedbackBean)
                .withDescription(description)
                .withUserName(userName)
                .withTimestamp(timeStamp)
                .withStatus(status)
                .withUpVotes(upVotes)
                .withResponses(feedbackResponses)
                .build();
    }

    private static FeedbackResponseBean getFeedbackResponse(Context context, long feedbackCreationDate, float developerPercent, float ownerPercent, FeedbackBean feedbackBean) {
        int upperBound = NumberUtility.divide(1, developerPercent);
        boolean feedbackOwner = NumberUtility.randomInt(0, upperBound > 0 ? upperBound - 1 : upperBound) == 0;
        upperBound = NumberUtility.divide(1, ownerPercent);
        boolean developerFeedback = NumberUtility.randomInt(0, upperBound > 0 ? upperBound - 1 : upperBound) == 0;
        String content = generateContent();
        String userName = feedbackOwner ? feedbackBean.getUserName() : generateUserName(context, false);
        long timeStamp = DateUtility.getPastDateAfter(feedbackCreationDate);
        return new FeedbackResponseBean.Builder()
                .withFeedbackId(feedbackBean.getFeedbackId())
                .withContent(content)
                .withUserName(userName)
                .withTimestamp(timeStamp)
                .isDeveloper(developerFeedback)
                .isFeedbackOwner(feedbackOwner)
                .build();
    }

    public static FeedbackResponseBean persist(FeedbackBean feedbackBean, String content, String userName, boolean isDeveloper, boolean isFeedbackOwner) {
        return new FeedbackResponseBean.Builder()
                .withFeedbackId(feedbackBean.getFeedbackId())
                .withContent(content)
                .withUserName(userName)
                .withTimestamp(System.currentTimeMillis())
                .isDeveloper(isDeveloper)
                .isFeedbackOwner(isFeedbackOwner)
                .build();
    }

    private static FeedbackBean getFeedback(Context context, int minUpVotes, int maxUpVotes, float ownFeedbackPercent) {
        long feedbackId = NumberUtility.randomLong();
        boolean ownFeedback = false;
        if (ownFeedbackPercent != 0){
            int upperBound = NumberUtility.divide(1, ownFeedbackPercent);
            ownFeedback = ACTIVE.check(context) && NumberUtility.randomInt(0, upperBound > 0 ? upperBound - 1 : upperBound) == 0;
        }
        FEEDBACK_STATUS feedbackStatus = generateFeedbackStatus();
        String title = generateTitle();
        String[] tags = GeneratorStub.BagOfTags.pickRandom(5);
        String userName = generateUserName(context, ownFeedback);
        long timeStamp = generateTimestamp();
        int upVotes = generateUpVotes(minUpVotes, maxUpVotes, feedbackStatus);
        int responses = generateResponses();
        return new FeedbackBean.Builder()
                .withFeedbackId(feedbackId)
                .withTitle(title)
                .withTags(tags)
                .withUserName(userName)
                .withTimestamp(timeStamp)
                .withUpVotes(upVotes)
                .withMinUpVotes(minUpVotes)
                .withMaxUpVotes(maxUpVotes)
                .withResponses(responses)
                .withStatus(feedbackStatus)
                .isPublic(!ownFeedback)
                .build();
    }

    public static FeedbackBean getFeedback(Context context, LocalFeedbackBean localFeedbackBean) {
        int minUpVotes = -30; //FIXME [jfo]
        int maxUpVotes = 50; //FIXME [jfo]

        long feedbackId = localFeedbackBean.getFeedbackId();
        String title = localFeedbackBean.getTitle();
        FEEDBACK_STATUS feedbackStatus = localFeedbackBean.getFeedbackStatus();
        int upVotes = localFeedbackBean.getVotes() + generateUpVotes(minUpVotes, maxUpVotes, feedbackStatus);
        long timeStamp = localFeedbackBean.getCreationDate();
        int responses = localFeedbackBean.getResponses();
        String userName = FeedbackDatabase.getInstance(context).readString(USER_NAME, null);

        return new FeedbackBean.Builder()
                .withFeedbackId(feedbackId)
                .withTitle(title)
                .withUserName(userName)
                .withTimestamp(timeStamp)
                .withUpVotes(upVotes)
                .withMinUpVotes(minUpVotes)
                .withMaxUpVotes(maxUpVotes)
                .withResponses(responses)
                .withStatus(feedbackStatus)
                .build();
    }

    private static FEEDBACK_STATUS generateFeedbackStatus() {
        FEEDBACK_STATUS[] status = new FEEDBACK_STATUS[]{OPEN, IN_PROGRESS, REJECTED, DUPLICATE, CLOSED};
        return status[NumberUtility.randomPosition(status)];
    }

    private static int generateResponses() {
        return NumberUtility.randomInt(0, 50);
    }

    private static int generateUpVotes(int minUpVotes, int maxUpVotes, FEEDBACK_STATUS feedbackStatus) {
        if (CompareUtility.oneOf(feedbackStatus, REJECTED)) {
            return NumberUtility.randomInt(minUpVotes, -1);
        } else if (CompareUtility.oneOf(feedbackStatus, DUPLICATE)) {
            return 0;
        }
        return NumberUtility.randomInt(0, maxUpVotes);
    }

    private static long generateTimestamp() {
        return DateUtility.getPastDateLong(2);
    }

    @NonNull
    private static String generateUserName(Context context, boolean own) {
        if (own) {
            return FeedbackDatabase.getInstance(context).readString(USER_NAME, null);
        }
        return RepositoryStub.registerAndGetUniqueName(GeneratorStub.BagOfNames.pickRandom(), false);
    }

    @NonNull
    private static String generateTitle() {
        return GeneratorStub.BagOfTags.pickRandom().concat("-Feedback");
    }

    @NonNull
    private static String generateContent() {
        return GeneratorStub.BagOfResponses.pickRandom();
    }

    @NonNull
    private static String[] generateDescriptionAndTitle() {
        return GeneratorStub.BagOfFeedback.pickRandomWithTitle();
    }

    //Should be generated on the Server
    //Return value is something like Jake --> Jake#12345678 (random 8 digits)
    public static String registerAndGetUniqueName(String name, boolean isDeveloper) {
        return name.concat("#").concat(String.valueOf(NumberUtility.multiply(99999999, Math.random())));
    }

    public static void sendUpVote(Context context, FeedbackBean bean) {
        //TheoreticalCallToRepo
        FeedbackDatabase.getInstance(context).writeFeedback(bean, UP_VOTED);
    }

    public static void sendDownVote(Context context, FeedbackBean bean) {
        //TheoreticalCallToRepo
        FeedbackDatabase.getInstance(context).writeFeedback(bean, DOWN_VOTED);
    }

    public static void sendFeedbackResponse(Context context, FeedbackBean bean, String response) {
        //TheoreticalCallToRepo
        FeedbackDatabase.getInstance(context).writeFeedback(bean, RESPONDED);
    }

    public static void sendSubscriptionChange(Context context, FeedbackBean bean, boolean subscribed) {
        //TheoreticalCallToRepo
        FeedbackDatabase.getInstance(context).writeFeedback(bean, subscribed ? SUBSCRIBED : UN_SUBSCRIBED);
    }

    public static FeedbackDetailsBean feedbackToFeedbackBean(Context context, Feedback feedback) {
        int minUpVotes = -30; //FIXME [jfo]
        int maxUpVotes = 50; //FIXME [jfo]


        long feedbackId = NumberUtility.randomLong();
        String title = null;
        String description = null;
        String[] content = new String[0];
        if (!feedback.getTextFeedbackList().isEmpty()) {
            title = feedback.getTextFeedbackList().get(0).getText();
        } else {
            content = generateDescriptionAndTitle();
        }

        String userName = generateUserName(context, true);
        long timeStamp = generateTimestamp();
        int upVotes = 0;
        int responses = 0;

        FeedbackBean feedbackBean = new FeedbackBean.Builder()
                .withFeedbackId(feedbackId)
                .withTitle(title == null ? content[1] : title)
                .withTags(GeneratorStub.BagOfTags.pickRandom(3))
                .withUserName(userName)
                .withTimestamp(timeStamp)
                .withUpVotes(upVotes)
                .withMinUpVotes(minUpVotes)
                .withMaxUpVotes(maxUpVotes)
                .withResponses(responses)
                .withStatus(OPEN)
                .build();

        String[] labels = GeneratorStub.BagOfTags.pickRandom(5);
        FEEDBACK_STATUS status = feedbackBean.getFeedbackStatus();
        List<FeedbackResponseBean> feedbackResponses = getFeedbackResponses(context, feedbackBean.getResponses(), timeStamp, 0.1f, 0.1f, feedbackBean);
        return new FeedbackDetailsBean.Builder()
                .withFeedbackId(feedbackBean.getFeedbackId())
                .withFeedbackBean(feedbackBean)
                .withDescription(description == null ? content[0] : description)
                .withUserName(userName)
                .withTimestamp(timeStamp)
                .withStatus(status)
                .withUpVotes(upVotes)
                .withResponses(feedbackResponses)
                .withBitmap(Utils.loadImageFromDatabase(context))
                .build();
    }

    public static Bitmap loadFeedbackImage(Context context, FeedbackDetailsBean feedbackDetailsBean) {
        //TheoreticalCallToRepo
        return Utils.loadImageFromDatabase(context);
    }

    public static byte[] loadFeedbackAudio(Context context, FeedbackDetailsBean feedbackDetailsBean) {
        //TheoreticalCallToRepo
        return new byte[0];
    }

    public static void makeFeedbackPublic(FeedbackDetailsBean feedbackDetailsBean) {
        //TheoreticalCallToRepo
    }
    public static Map<String,String> getFeedbackTags(Context context) {
        //TheoreticalCallToRepo
        ArrayList<String> loadedTags = new ArrayList<>(
                Arrays.asList(
                        "GUI", "Images", "Scaling", "Performance", "Permissions", "Data-Usage", "Improvement", "Development", "Translations",
                        "Battery", "Privacy", "Crashes", "Bugs", "Functionality", "Ideas", "Updates", "Region-Lock", "Content", "Features",
                        "Design", "Handling", "Usability", "Audio", "Sensors", "Brightness", "GPS", "Accuracy", "Quality"));
        TreeMap<String,String> sortedTags = new TreeMap<>();
        for (String tag : loadedTags){
            sortedTags.put(tag.toLowerCase(),tag);
        }
        for (String tag : FeedbackDatabase.getInstance(context).readTags(null)){
            sortedTags.put(tag.toLowerCase(),tag);
        }
        return sortedTags;
    }

    public static AuthenticateResponse generateAuthenticateResponse() {
        return new AuthenticateResponse(generateToken());
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
