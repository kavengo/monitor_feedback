package ch.uzh.supersede.feedbacklibrary.activities;

import android.content.*;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.*;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.*;

import com.nex3z.flowlayout.FlowLayout;

import java.util.*;

import ch.uzh.supersede.feedbacklibrary.R;
import ch.uzh.supersede.feedbacklibrary.stubs.RepositoryStub;
import ch.uzh.supersede.feedbacklibrary.utils.PopUp;

import static android.graphics.Typeface.*;
import static ch.uzh.supersede.feedbacklibrary.utils.Constants.EXTRA_KEY_FEEDBACK_TAGS;
import static ch.uzh.supersede.feedbacklibrary.utils.Constants.EXTRA_KEY_FEEDBACK_TITLE;
import static ch.uzh.supersede.feedbacklibrary.utils.PermissionUtility.USER_LEVEL.PASSIVE;

public class FeedbackIdentityActivity extends AbstractBaseActivity {
    private Map<String,String> loadedTags = new TreeMap<>();
    private List<String> createdTags = new ArrayList<>();
    private Button buttonNext;
    private Button buttonBack;
    private EditText editTitle;
    private EditText editTag;
    private FlowLayout tagContainer;
    private FlowLayout recommendationContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_identity);
        loadedTags = RepositoryStub.getFeedbackTags();
        buttonNext = getView(R.id.identity_button_next, Button.class);
        buttonBack = getView(R.id.identity_button_back, Button.class);
        tagContainer = getView(R.id.identity_container_tags, FlowLayout.class);
        recommendationContainer = getView(R.id.identity_container_recommendations, FlowLayout.class);
        colorViews(0,getView(R.id.identity_root,RelativeLayout.class));
        colorTextOnly(0,
                getView(R.id.identity_text_info_1, TextView.class),
                getView(R.id.identity_text_info_2, TextView.class),
                getView(R.id.identity_text_info_3, TextView.class),
                getView(R.id.identity_edit_tag, EditText.class),
                getView(R.id.identity_edit_title, EditText.class));
        colorViews(1, buttonBack, buttonNext);
        getView(R.id.identity_focus_sink, LinearLayout.class).requestFocus();
        createEditableFields();
        onPostCreate();
    }

    private void createEditableFields() {
        editTitle = getView(R.id.identity_edit_title, EditText.class);
        editTag = getView(R.id.identity_edit_tag, EditText.class);
        editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && "Input Feedback-Title here...".equals(editTitle.getText().toString())){
                    editTitle.setText(null);
                }
            }
        });
        editTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOP
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //NOP
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !"Input Feedback-Tags here...".equals(s.toString())){
                    if (s.toString().toCharArray()[s.length()-1]==' '){
                        if (s.toString().length()<configuration.getMinTagLength()){
                            Toast.makeText(getApplicationContext(),"Tag too short! Min: "+configuration.getMinTagLength(),Toast.LENGTH_SHORT).show();
                            editTag.setText(s.toString().substring(0,s.length()-1));
                            editTag.setSelection(s.length()-1);
                        }else if (s.toString().length()>configuration.getMaxTagLength()){
                            Toast.makeText(getApplicationContext(),"Tag too long! Max: "+configuration.getMaxTagLength(),Toast.LENGTH_SHORT).show();
                            editTag.setText(s.toString().substring(0,s.length()-1));
                            editTag.setSelection(s.length()-1);
                        }else if (createdTags.size()==configuration.getMaxTagNumber()){
                            Toast.makeText(getApplicationContext(),"Maximum number of Tags! Delete some to continue!",Toast.LENGTH_SHORT).show();
                            editTag.setText(s.toString().substring(0,s.length()-1));
                            editTag.setSelection(s.length()-1);
                        }else{
                            if (createdTags.contains(s.toString().substring(0,s.length()-1))){
                                Toast.makeText(getApplicationContext(),"Tag already in your Tag-List!",Toast.LENGTH_SHORT).show();
                                editTag.setText(s.toString().substring(0,s.length()-1));
                                editTag.setSelection(s.length()-1);
                            }else{
                                addTag(s.toString().substring(0,s.length()-1));
                            }
                        }
                    }else{
                        fullTextSearch(s);
                    }
                }else{
                    recommendationContainer.removeAllViews();
                }
            }
        });
        editTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && "Input Feedback-Tags here...".equals(editTag.getText().toString())){
                    editTag.setText(null);
                }
            }
        });
        editTag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    if (createdTags.size()< configuration.getMaxTagNumber()){
                        addTag(editTag.getText().toString());
                    }else{
                        Toast.makeText(getApplicationContext(),"Maximum number of Tags reached! Max: "+configuration.getMaxTagNumber(),Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    private void fullTextSearch(Editable s) {
        recommendationContainer.removeAllViews();
        if (s == null || s.length() == 0){
            return;
        }
        String search = s.toString().toLowerCase();
        int found = 0;
        ArrayList<String> findings = new ArrayList<>();
        ArrayList<String> ignores = new ArrayList<>();
        for (Map.Entry<String, String> tag : loadedTags.entrySet()){
            if (tag.getKey().startsWith(search)){
                findings.add(tag.getValue());
                ignores.add(tag.getKey());
                found++;
            }
            if (found == configuration.getMaxTagRecommendationNumber()){
                break;
            }
        }
        for (Map.Entry<String, String> tag : loadedTags.entrySet()){
            if (tag.getKey().contains(search) && !ignores.contains(tag.getKey())){
                findings.add(tag.getValue());
                found++;
            }
            if (found == configuration.getMaxTagRecommendationNumber()){
                break;
            }
        }
        for (String matchingTag : findings){
            Button b = new Button(this);
            b.setText(matchingTag);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (createdTags.size()<configuration.getMaxTagNumber()){
                        addTag(((Button)v).getText().toString());
                    }else{
                        Toast.makeText(getApplicationContext(),"Maximum number of Tags! Max: "+configuration.getMaxTagLength(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            recommendationContainer.addView(b);
        }
    }

    private void addTag(String s) {
        createdTags.add(s);
        Button b = new Button(this);
        b.setText(s);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTag.setText(((Button)v).getText().toString());
                editTag.setSelection(((Button)v).getText().length());
                createdTags.remove(((Button)v).getText().toString());
                tagContainer.removeView(v);
            }
        });
        tagContainer.addView(b);
        editTag.setText(null);
        recommendationContainer.removeAllViews();
    }

    @Override
    public void onButtonClicked(View view) {
        if (view.getId() == buttonBack.getId()){
            new PopUp(this)
                    .withTitle("Cancel Creation")
                    .withMessage("Do you want to cancel the Feedback-Creation?")
                    .withCustomOk(getString(R.string.hub_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onBackPressed();
                        }
                    }).buildAndShow();
        }else if (view.getId() == buttonNext.getId()){
            String message = validateInput();
            if (message == null){
                Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                intent.putExtra(EXTRA_KEY_FEEDBACK_TITLE,editTitle.toString());
                intent.putExtra(EXTRA_KEY_FEEDBACK_TAGS,createdTags.toArray(new String[createdTags.size()]));
                startActivity(this, FeedbackActivity.class);
            }else{
                new PopUp(this)
                        .withTitle("Input Incomplete")
                        .withMessage(message)
                        .withCustomOk(getString(R.string.hub_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).withoutCancel().buildAndShow();
            }
        }
    }

    private String validateInput() {
        String errorMessage = null;
        if ("Input Feedback-Title here...".equals(editTitle.getText().toString())){
            errorMessage = "Specify a Title!";
        } else if (editTitle.getText().length() < configuration.getMinTitleLength()){
            errorMessage = "Title too short! Minimum Length: "+configuration.getMinTitleLength();
        }else if (editTitle.getText().length() > configuration.getMaxTitleLength()){
            errorMessage = "Title too long! Maximum Length: "+configuration.getMaxTitleLength();
        }else if (createdTags.size() < configuration.getMinTagNumber()){
            errorMessage = "Not enough Tags! Min: "+configuration.getMinTagNumber();
        }
            return errorMessage;
    }
}
