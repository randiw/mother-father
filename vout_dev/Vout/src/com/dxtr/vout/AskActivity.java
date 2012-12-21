package com.dxtr.vout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dxtr.vout.composite.OptionAdapter;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.model.People;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.repository.AvatarsRepository;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;
import com.dxtr.vout.utils.ImageUtil;

public class AskActivity extends SherlockActivity {

	private ActionBar actionBar;
	private ArrayList<People> selectedFriends;
	private final static int ACTION_PICK_FRIENDS = 0;
	public final static int ACTION_ADD_OPTION = 1;
	private LinearLayout layoutFriend;
	private ImageView imgAddFriend, imgTime;
	private EditText txtQuestion;
	private List<Option> options;
	private GridView gvOptions;
	private int selectedExpiredHour = 0;
	private Handler handler;
	private ProgressDialog dialog;
	private Typeface font_bebas;
	private TextView txtTimeUp;
	
	String[] timeLabels = new String[] { "1 hour", "2 hours", "6 hours", "12 hours", "1 day", "2 days", "5 days", "1 week" };
	private int[] expiredHour = {1, 2, 6, 12, 24, 48, 120, 168};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		options = new ArrayList<Option>();
		handler = new Handler();
		font_bebas = Typeface.createFromAsset(getAssets(), "fonts/BEBAS.TTF");
		
		txtTimeUp = (TextView) findViewById(R.id.txtTimeUp);
		layoutFriend = (LinearLayout) findViewById(R.id.layoutFriendAvatar);
		txtQuestion = (EditText)findViewById(R.id.txtQuestion);
		imgAddFriend = (ImageView)findViewById(R.id.imgAddFriend);
		imgTime = (ImageView)findViewById(R.id.imgTimeUp);
		Button btnThrowQuestion = (Button)findViewById(R.id.btnThrowQuestion);
		btnThrowQuestion.setTypeface(font_bebas);
		
		selectedExpiredHour = expiredHour[2];
		txtTimeUp.setText(timeLabels[2]);
		
		gvOptions = (GridView)findViewById(R.id.gvOptions);
		gvOptions.setAdapter(new OptionAdapter(this, options));
		gvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == parent.getAdapter().getCount() - 1) {
					Intent i = new Intent(AskActivity.this, OptionListActivity.class);
					startActivityForResult(i, ACTION_ADD_OPTION);
				}
			}
		});
		imgAddFriend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(AskActivity.this, FriendListActivity.class);
				i.putExtra("friendsSelected", selectedFriends);
				startActivityForResult(i, ACTION_PICK_FRIENDS);
			}
		});
		imgTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTimeChooserDialog();
			}
		});
		
		btnThrowQuestion.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				throwQuestion();
			}
		});
	}
	
	private void throwQuestion() {
		PostInternetTask postTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
				handler.post(new Runnable() {
					public void run() {
				    	dialog = ProgressDialog.show(AskActivity.this, "Loading", "Saving option.");				
					}
				});
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					Toast.makeText(AskActivity.this, "Question successfully saved.", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(AskActivity.this, "Failed while saving. Please try again later.", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
		postTask.addPair("question", txtQuestion.getText().toString());
		postTask.addPair("time_limit", "" + selectedExpiredHour);
		postTask.addPair("options", buildOptionsParams());
		postTask.addPair("targets", buildTargetsParams());
		postTask.execute(URLAddress.THROW_QUESTION);
	}
	
	private String buildOptionsParams(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < options.size(); i++) {
			if(options.get(i) != null) sb.append(options.get(i).getId());
			if (i < (options.size() - 2)) {
				sb.append(",");
			}
			
		}
		return sb.toString();
	}

	private String buildTargetsParams(){
		if(selectedFriends == null) return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < selectedFriends.size(); i++) {
			sb.append(selectedFriends.get(i).getId());
			if (i < (selectedFriends.size() - 1)) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	private void showTimeChooserDialog() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, timeLabels);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Time Up in:");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				selectedExpiredHour = expiredHour[item];
				txtTimeUp.setText(timeLabels[item]);
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@SuppressWarnings("unchecked")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case ACTION_PICK_FRIENDS:
					//tampung selectedFriends
					selectedFriends = (ArrayList<People>) data.getSerializableExtra("friendsSelected");
					//refresh friends target listview
					layoutFriend.removeAllViews();
					int size = imgAddFriend.getHeight();
					AvatarsRepository repo = new AvatarsRepository(this);
					repo.open();
					for (int i = 0; i < selectedFriends.size(); i++) {
						ImageView img = new ImageView(this);
						img.setPadding(1, 1, 1, 1);
						if (selectedFriends.get(i).getUserImageURL() != null) {
							Bitmap bitmap = repo.getAvatar(selectedFriends.get(i).getUserImageURL());
							Bitmap bmp = ImageUtil.getRoundedCornerBitmap(bitmap, 7);
							img.setImageBitmap(Bitmap.createScaledBitmap(bmp, size, size, false));
						} else {
							InputStream stream = getResources().openRawResource(R.drawable.default_user);
							Bitmap defaultImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream), size, size, false);
							img.setImageBitmap(defaultImg);
						}
						
						layoutFriend.addView(img);
					}
					repo.close();
					break;
				case ACTION_ADD_OPTION:
					Option option = (Option)data.getSerializableExtra("option");
					((OptionAdapter)gvOptions.getAdapter()).addItemAndRefresh(option);
					break;
				default:
					break;
			}
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