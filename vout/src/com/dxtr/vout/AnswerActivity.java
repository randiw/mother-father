package com.dxtr.vout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AnswerActivity extends Activity {

	public static final String TAG_ID = "id";
	public static final String TAG_USER_ID = "user_id";
	public static final String TAG_QUESTION = "question";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_HIT_RATE = "hit_rate";
	public static final String TAG_TIME_LIMIT = "time_limit";
	public static final String TAG_IMAGE_ID = "image_id";
	public static final String TAG_OPTIONS = "options";
	public static final String TAG_COMMENTS = "comments";
	public static final String TAG_IS_PRIVATE = "is_private";
	public static final String TAG_TARGET_ID = "target_id";
	public static final String TAG_STATUS = "status";
	public static final String TAG_CREATED_DATE = "created_date";
	public static final String TAG_UPDATED_DATE = "updated_date";
	public static final String TAG_QUESTION_OPTIONS = "question_options";
	public static final String TAG_QUESTION_ID = "question_id";
	public static final String TAG_TITLE = "title";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_WEIGHT = "weight";
	public static final String TAG_QUESTION_COMMENT = "question_comment";
	public static final String TAG_COMMENT = "comment";
	
	private ProgressDialog dialog;
	private String url_question;
	private Handler handler;
	private QuestionDetail detail;
	private Toast t;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        handler = new Handler();
        
        Intent intent = getIntent();
        url_question = intent.getStringExtra("url");
        refreshFeed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_answer, menu);
        return true;
    }
    
    public void onChose(View view){
    	t = Toast.makeText(this, "You Chose", Toast.LENGTH_SHORT);
    	t.show();
    }
    
    public void onTextChose(View view){
    	t = Toast.makeText(this, "You Chose Text ", Toast.LENGTH_SHORT);
    	t.show();
    }
    
    private void refreshFeed(){
    	dialog = ProgressDialog.show(this, "Loading", "Generating Polls");
    	Thread thread = new Thread(new Runnable() {
			
			public void run() {
				Log.d("URL", url_question);
				try {
					URL url = new URL(url_question);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream inputStream = connection.getInputStream();
					BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder strBuilder = new StringBuilder();
					String line = null;
					while ((line = buffReader.readLine()) != null) {
						strBuilder.append(line + "\n");
					}
					inputStream.close();
					String jsonString = strBuilder.toString();
					Log.d("JSON", jsonString);

					detail = parseJSON(jsonString);
					
					handler.post(new Runnable() {
						
						public void run() {
							TextView creator_name = (TextView)findViewById(R.id.question_user_name);
							creator_name.setText(detail.getField(TAG_USER_ID));
							
							TextView question = (TextView)findViewById(R.id.question_detail);
							question.setText(detail.getField(TAG_QUESTION));
							
							TextView title_A = (TextView)findViewById(R.id.option_A_label);
							title_A.setText(detail.getOptionField_A(TAG_TITLE));
							
							TextView description_A = (TextView)findViewById(R.id.option_A_description);
							description_A.setText(detail.getOptionField_A(TAG_DESCRIPTION));
							
							TextView title_B = (TextView)findViewById(R.id.option_B_label);
							title_B.setText(detail.getOptionField_B(TAG_TITLE));
							
							TextView description_B = (TextView)findViewById(R.id.option_B_description);
							description_B.setText(detail.getOptionField_B(TAG_DESCRIPTION));
							
							dialog.dismiss();
						}
					});
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
    	thread.start();
    }
    
    private QuestionDetail parseJSON(String jsonString){
    	QuestionDetail questionDetail = new QuestionDetail();
    	try {
			JSONObject object = new JSONObject(jsonString);
			Iterator<String> keys = object.keys();
			while(keys.hasNext()){
				String key_question = keys.next();
				if(TAG_QUESTION_COMMENT.equals(key_question)){
					JSONArray commentArray = object.getJSONArray(TAG_QUESTION_COMMENT);
					for(int i = 0; i < commentArray.length(); i++) {
						JSONObject commentObject = commentArray.getJSONObject(i);
						Iterator<String> commentKeys = commentObject.keys();
						HashMap<String, String> commentMap = new HashMap<String, String>();
						while(commentKeys.hasNext()){
							String key = commentKeys.next();
							String value = commentObject.getString(key);
							commentMap.put(key, value);
						}
										
						questionDetail.addComment(commentMap);
					}					
				}
				else if(TAG_QUESTION_OPTIONS.equals(key_question)){
					JSONArray optionsArray = object.getJSONArray(TAG_QUESTION_OPTIONS);
					for(int i = 0; i < optionsArray.length(); i++) {
						JSONObject optionObject = optionsArray.getJSONObject(i);
						Iterator<String> optionKeys = optionObject.keys();
						HashMap<String, String> optionMap = new HashMap<String, String>();
						while(optionKeys.hasNext()){
							String key = optionKeys.next();
							String value = optionObject.getString(key);
							optionMap.put(key, value);
						}
						
						questionDetail.addOption(optionMap);
					}					
				}
				else {
					String value_question = object.getString(key_question);
					questionDetail.addField(key_question, value_question);
				}
			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return questionDetail;
    }
}

