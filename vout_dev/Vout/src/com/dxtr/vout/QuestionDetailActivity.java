package com.dxtr.vout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.model.Question;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.InternetTask;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;
import com.dxtr.vout.utils.AppUtil;

public class QuestionDetailActivity extends SherlockActivity {
	
	private ProgressDialog dialog;
	private Handler handler;
	private ActionBar actionBar;
	
	private Question retrievedQuestion;
	private ImageView imgUser, imgOption1, imgOption2;
	private TextView txtQuestion, txtUpdate, txtName, txtOptionText1, txtOptionText2;
	private LinearLayout layoutImageOption;
	
	private String url;
	
	@SuppressLint("DefaultLocale")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_question_detail);
		
		Typeface font_bebas = Typeface.createFromAsset(getAssets(), "fonts/BEBAS.TTF");
		Typeface font_helveticaneuel = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTStd-Lt_0.otf");
		
		layoutImageOption = (LinearLayout) findViewById(R.id.layoutImage);
		imgUser = (ImageView)findViewById(R.id.imgUser);
		txtName = (TextView)findViewById(R.id.txtName);
		imgOption1 = (ImageView)findViewById(R.id.imgOption1);
		imgOption2 = (ImageView)findViewById(R.id.imgOption2);
		txtQuestion = (TextView)findViewById(R.id.txtQuestion);
		txtUpdate = (TextView)findViewById(R.id.txtUpdateTime);
		txtOptionText1 = (TextView)findViewById(R.id.txtOption1Title);
		txtOptionText2 = (TextView)findViewById(R.id.txtOption2Title);
		
		handler = new Handler();
		Question question = (Question) getIntent().getSerializableExtra("question");
		url = question.getUrl();
		String userImgUrl = question.getUserImageURL();
		
		CharSequence updated = DateUtils.getRelativeTimeSpanString(question.getCreatedDate() * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		txtUpdate.setText(updated);
		txtUpdate.setTypeface(font_helveticaneuel);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
//		setActionBarTitleColor(Color.WHITE);
		
		setUserImage(userImgUrl);
		txtName.setTypeface(font_bebas);
		txtName.setText(question.getFirstName());
		imgOption1.setOnClickListener(onClickListener);
		imgOption2.setOnClickListener(onClickListener);
		imgOption1.setOnLongClickListener(onLongClickListener);
		imgOption2.setOnLongClickListener(onLongClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		layoutImageOption.setVisibility(View.INVISIBLE);
		if(url != null) loadDetailData(url);
	}
	
//	private void setActionBarTitleColor(int color) {
////		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
////		TextView actionBarTextView = (TextView)findViewById(actionBarTitleId);
////		actionBarTextView.setTextColor(color);
//	}

	private void setUserImage(String urlImage){
		InputStream stream = getResources().openRawResource(R.drawable.default_user);
		Bitmap defaultThumb = BitmapFactory.decodeStream(stream);
		BitmapManager.INSTANCE.loadBitmap(urlImage, imgUser, defaultThumb.getWidth(), defaultThumb.getHeight(), defaultThumb, true, 7);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (v == imgOption1) {
				showConfirmationAndCommentDialog(retrievedQuestion.getId(), retrievedQuestion.getListOptions().get(0));
			}
			if (v == imgOption2) {
				showConfirmationAndCommentDialog(retrievedQuestion.getId(), retrievedQuestion.getListOptions().get(1));
			}
		}
	};
	
	@SuppressLint("DefaultLocale")
	private void showConfirmationAndCommentDialog(final String questionID, final Option selectedOption) {
		final Dialog confirmDialog = new Dialog(this);
		confirmDialog.setTitle("Your vout");
		confirmDialog.setContentView(R.layout.dialog_answer_confirm);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(confirmDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    confirmDialog.show();
	    confirmDialog.getWindow().setAttributes(lp);
		((TextView)confirmDialog.findViewById(R.id.txtTitle)).setText(selectedOption.getTitle().toUpperCase());
		confirmDialog.findViewById(R.id.btnVout).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				submitAnswer(confirmDialog, questionID, selectedOption.getId(), ((EditText)confirmDialog.findViewById(R.id.txtComment)).getText().toString());
			}
		});
	}
	
	private void showOptionDetail(Option option) {
		final Dialog confirmDialog = new Dialog(this);
		confirmDialog.setTitle("Option Detail");
		confirmDialog.setContentView(R.layout.dialog_option_detail);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(confirmDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    confirmDialog.show();
	    confirmDialog.getWindow().setAttributes(lp);
	    ImageView imgOption = (ImageView)confirmDialog.findViewById(R.id.imgOption);
	    int width = (AppUtil.GetScreenWidth(this) - 20);
		BitmapManager.INSTANCE.loadBitmap(option.getImageUrl() + "&image_width=" + width, imgOption, 0, 0, null, false, 0);
		((TextView)confirmDialog.findViewById(R.id.txtTitle)).setText(option.getTitle());
		((TextView)confirmDialog.findViewById(R.id.txtDesc)).setText(option.getDescription());
	}
	
	private void submitAnswer(final Dialog confirmDialog, String questionId, String optionId, String comment) {
		PostInternetTask postInternetTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
				handler.post(new Runnable() {					
					public void run() {
						dialog = ProgressDialog.show(QuestionDetailActivity.this, "", "Submitting your answer");
					}
				});	
			}
			
			public void onDone(String str) {
				System.out.println("rispun: " + str);
				JParser jparser = new JParser(str);
				String message = "";
				try {
					message = new JSONObject(jparser.getDataJSON()).getString("message") ;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (jparser.getStatus()) {
					confirmDialog.dismiss();
					Intent i = new Intent(QuestionDetailActivity.this, AnswerResultActivity.class);
					i.putExtra("resultData", str);
					startActivity(i);
				}
				Toast.makeText(QuestionDetailActivity.this, message, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
    	postInternetTask.addPair("question_id", questionId);
    	postInternetTask.addPair("option_id", optionId);
    	postInternetTask.addPair("comment", comment);
    	postInternetTask.execute(URLAddress.ANSWER);
	}
	
	private OnLongClickListener onLongClickListener = new OnLongClickListener() {
		public boolean onLongClick(final View v) {
			final String[] items = new String[] { "Vout!", "View Detail" };
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(QuestionDetailActivity.this, android.R.layout.select_dialog_item, items);
			AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetailActivity.this);
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						if (v == imgOption1) {
							showConfirmationAndCommentDialog(retrievedQuestion.getId(), retrievedQuestion.getListOptions().get(0));
						}
						if (v == imgOption2) {
							showConfirmationAndCommentDialog(retrievedQuestion.getId(), retrievedQuestion.getListOptions().get(1));
						}
					} else {
						if (v == imgOption1) {
							showOptionDetail(retrievedQuestion.getListOptions().get(0));
						}
						if (v == imgOption2) {
							showOptionDetail(retrievedQuestion.getListOptions().get(1));
						}
					}
					dialog.dismiss();
				}
			});
			builder.show();
			return false;
		}
	};
	
	private void loadDetailData(String url){
		InternetTask internetTask = new InternetTask(new InternetConnectionListener() {
			public void onStart() {
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
			
			public void onDone(final String str) {
				System.out.println("detail question: " + str);
				handler.post(new Runnable() {

					public void run() {
						JParser parser = new JParser(str);
						if (parser.getStatus()) {
							int size = (AppUtil.GetScreenWidth(QuestionDetailActivity.this) / 2) - 10;
							retrievedQuestion = parseQuestion(parser.getDataJSON());
							
							txtQuestion.setText("\"" + retrievedQuestion.getQuestionContent() + "\"");
							
							layoutImageOption.setVisibility(View.VISIBLE);
							layoutImageOption.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, size + 10));
							InputStream stream = getResources().openRawResource(R.drawable.def);
							Bitmap defaultOptionImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream), size, size, false);
							BitmapManager.INSTANCE.loadBitmap(retrievedQuestion.getListOptions().get(0).getImageUrl() + "&image_crop=1&image_width=" + size, imgOption1, size, size, defaultOptionImg, false, 0);
							BitmapManager.INSTANCE.loadBitmap(retrievedQuestion.getListOptions().get(1).getImageUrl() + "&image_crop=1&image_width=" + size, imgOption2, size, size, defaultOptionImg, false, 0);
							txtOptionText1.setText(retrievedQuestion.getListOptions().get(0).getTitle());
							txtOptionText2.setText(retrievedQuestion.getListOptions().get(1).getTitle());
							
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
			question.setTimeLimit(questJsonObj.getLong("time_limit"));
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
		    	 finish();
		         return true;        
		      default:
		         return super.onOptionsItemSelected(item);    
		   }
	}
}
