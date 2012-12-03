package com.dxtr.vout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.model.Question;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.InternetTask;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.utils.AppUtils;
import com.dxtr.vout.utils.ImageUtils;

public class QuestionDetailActivity extends SherlockActivity {
	
	private Handler handler;
	private ActionBar actionBar;
	
	private ImageView imgUser, imgOption1, imgOption2;
	private TextView txtQuestion;
	
	@SuppressLint("DefaultLocale")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_question_detail);
		
		imgUser = (ImageView)findViewById(R.id.imgUser);
		imgOption1 = (ImageView)findViewById(R.id.imgOption1);
		imgOption2 = (ImageView)findViewById(R.id.imgOption2);
		txtQuestion = (TextView)findViewById(R.id.txtQuestion);
		
		handler = new Handler();
		String url = getIntent().getStringExtra("url");
		String userImgUrl = getIntent().getStringExtra("user_image");
		String name = getIntent().getStringExtra("name");
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		setActionBarTitleColor(Color.WHITE);
		
		if(name != null) actionBar.setTitle(name.toUpperCase() + " asks");
		if(url != null) loadDetailData(url);
		
		setUserImage(userImgUrl);
		imgOption1.setOnClickListener(onClickListener);
		imgOption2.setOnClickListener(onClickListener);
	}
	
	private void setActionBarTitleColor(int color) {
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView actionBarTextView = (TextView)findViewById(actionBarTitleId); 
		actionBarTextView.setTextColor(color);
	}

	private void setUserImage(String urlImage){
		InputStream stream = getResources().openRawResource(R.drawable.ic_launcher);
		Bitmap defaultThumb = BitmapFactory.decodeStream(stream);
		BitmapManager.INSTANCE.loadBitmap(urlImage, imgUser, defaultThumb.getWidth(), defaultThumb.getHeight(), defaultThumb, null);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (v == imgOption1) {
				
			}
			if (v == imgOption2) {
				
			}
		}
	};
	
	private void loadDetailData(String url){
		InternetTask internetTask = new InternetTask(new InternetConnectionListener() {
			public void onStart() {
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
			
			public void onDone(final String str) {
				System.out.println("RESPONSE_QUESTIONS: " + str);
				handler.post(new Runnable() {
					public void run() {
						JParser parser = new JParser(str);
						System.out.println("RESPON DETAIL DATA JSON: " + parser.getDataJSON());
						if (parser.getStatus()) {
							int size = (AppUtils.GetScreenWidth(QuestionDetailActivity.this) / 2) - 15;
							Question question = parseQuestion(parser.getDataJSON());
							
							txtQuestion.setText(question.getQuestionContent());
							
							InputStream stream = getResources().openRawResource(R.drawable.def);
							Bitmap defaultOptionImg = ImageUtils.resizeImage(BitmapFactory.decodeStream(stream), size, size);
							BitmapManager.INSTANCE.loadBitmap(question.getListOptions().get(0).getImageUrl(), imgOption1, size, size, defaultOptionImg, null);
							BitmapManager.INSTANCE.loadBitmap(question.getListOptions().get(1).getImageUrl(), imgOption2, size, size, defaultOptionImg, null);
							
							setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
						} else {
							Toast.makeText(QuestionDetailActivity.this, "Error loading data. Try again later.", Toast.LENGTH_SHORT).show();
							finish();
						}
					}
				});
			}
		});
		internetTask.execute(url);
	}
	
	private Question parseQuestion(String dataJSON) {
		try {
			Question question = new Question();
			JSONObject questJsonObj = new JSONObject(dataJSON);
			question.setId(questJsonObj.getString("id"));
			question.setQuestionContent(questJsonObj.getString("question"));
			question.setHitRate(Integer.parseInt(questJsonObj.getString("hit_rate")));
			question.setVoutCount(Integer.parseInt(questJsonObj.getString("vout_count")));
			question.setTimeLimit(questJsonObj.getString("time_limit"));
			question.setLocation(questJsonObj.getString("location"));
			question.setPrivate("1".equals(questJsonObj.getString("is_private")));
			question.setListTargetIds(questJsonObj.getString("target_id").split(","));
			question.setFirstName(questJsonObj.getString("first_name"));
			question.setLastName(questJsonObj.getString("last_name"));
			question.setCreatedDate(questJsonObj.getLong("created_date"));
			question.setUpdatedDate(questJsonObj.getLong("updated_date"));
			question.setListOptions(parseOption(questJsonObj.getString("options")));
			System.out.println("RETURNING QUESTION: " + question.getQuestionContent());
			return question;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Option> parseOption(String json){
		try {
			List<Option> options = new ArrayList<Option>();
			JSONArray arr = new JSONArray(json);
			for (int i = 0; i < arr.length(); i++) {
				Option option = new Option();
				JSONObject optObject = arr.getJSONObject(i);
				option.setId(optObject.getString("id"));
				option.setTitle(optObject.getString("title"));
				option.setDescription(optObject.getString("description"));
				option.setImageUrl(optObject.getString("option_image"));
				option.setCreatedDate(optObject.getLong("created_date"));
				option.setUpdatedDate(optObject.getLong("updated_date"));
				options.add(option);
			}
			return options;
		} catch (Exception e) {
			return null;
		}	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		   {        
		      case android.R.id.home:            
//		         Intent intent = new Intent(this, MainActivity.class);            
//		         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//		         startActivity(intent);
		    	 finish();
		         return true;        
		      default:
		         return super.onOptionsItemSelected(item);    
		   }
	}
}
