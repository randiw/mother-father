package com.dxtr.vout;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dxtr.vout.model.Comment;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.view.CommentItemView;

public class AnswerResultActivity extends SherlockActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answer_result);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		
		String dataResult = getIntent().getStringExtra("resultData");
		
		JParser parser = new JParser(dataResult);
		String dataJson = parser.getDataJSON();
		
		try {
			JSONObject obj = new JSONObject(dataJson);
			JSONObject questionObject = obj.getJSONObject("question");
			JSONObject optionObject = obj.getJSONObject("option");
			String question = questionObject.getString("question");
//			int minuteLeft = Integer.parseInt(questionObject.getString("time_left") == null ? "0" : questionObject.getString("time_left"));
			List<Comment> listComment = parseComments(questionObject);
			String optionTitle = optionObject.getString("title");
			String optionPopularity = optionObject.getString("popularity");
			String optionRank = optionObject.getString("rank");
			
			((TextView)findViewById(R.id.txtQuestion)).setText(question);
			((TextView)findViewById(R.id.txtOption)).setText(optionTitle);
			((TextView)findViewById(R.id.txtPopularity)).setText("Popularity: " + optionPopularity + "%");
			((TextView)findViewById(R.id.txtRank)).setText("Rank: " + optionRank);
			
			Button btnVoutAgain = (Button)findViewById(R.id.btnVoteAgain);
			btnVoutAgain.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});

			Button btnGoToTimeline = (Button)findViewById(R.id.btnGoToHome);
			btnGoToTimeline.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
			         Intent intent = new Intent(AnswerResultActivity.this, HomeFragmentActivity.class);            
			         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			         startActivity(intent);
				}
			});
			
			LinearLayout layoutComment = (LinearLayout)findViewById(R.id.layoutComments);
		    for (int i = 0; i < listComment.size(); i++) {
		    	CommentItemView commentItem = new CommentItemView(this, listComment.get(i));
		    	layoutComment.addView(commentItem);
			}
		} catch (JSONException e) {
			finish();
		}
	}

	private List<Comment> parseComments(JSONObject questionObject) {
		try {
			List<Comment> listComment = new ArrayList<Comment>();
			JSONArray arrComment = questionObject.getJSONArray("comments");
			for (int i = 0; i < arrComment.length(); i++) {
				JSONObject commentObj = arrComment.getJSONObject(i);
				Comment comment = new Comment();
				comment.setId(commentObj.getString("id"));
				comment.setComment(commentObj.getString("comment"));
				comment.setFirstName(commentObj.getString("first_name"));
				comment.setLastName(commentObj.getString("last_name"));
				comment.setUserId(commentObj.getString("user_id"));
				comment.setUserImageURL(commentObj.getString("user_image"));
				comment.setCreatedDate(commentObj.getLong("created_date"));
				comment.setUpdatedDate(commentObj.getLong("updated_date"));
				listComment.add(comment);
			}
			return listComment;
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		   {        
		      case android.R.id.home:
		    	 finish();
		         return true;        
		      default:
		         return super.onOptionsItemSelected(item);    
		   }
	}
}
