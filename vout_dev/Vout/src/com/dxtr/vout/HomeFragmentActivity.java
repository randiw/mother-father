package com.dxtr.vout;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.composite.ActionBarListNavigationAdapter;
import com.dxtr.vout.fragment.BlankFragment;
import com.dxtr.vout.fragment.TimelineFragment;
import com.dxtr.vout.gcm.GCMRegisterer;
import com.dxtr.vout.model.UserProfile;
import com.dxtr.vout.prop.ApplicationProperties;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;

public class HomeFragmentActivity extends SherlockFragmentActivity {
	private static final String STATE_SELECTED = "selected";
	private ActionBar actionBar;
	private List<String> actions = new ArrayList<String>();
	private SharedPreferences myPref;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_home);
		
		myPref = getSharedPreferences(ApplicationProperties.NAME, 0);
		buildNavigationList();
		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowHomeEnabled(false);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, actions);
		actionBar.setListNavigationCallbacks(new ActionBarListNavigationAdapter(this, actions), navigationListener);
        adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        
        registerGCM();
//        long now = System.currentTimeMillis();
//        System.out.println("time: " + now);
//        TimeZone tz = TimeZone.getDefault();
//        System.out.println("timezone: " + ((double)(tz.getOffset(now) / 3600000)));   
	}
	
	private void registerGCM() {
		if (myPref.getString("device_id", null) == null) {
			GCMRegisterer.registerGCM(this);
		}
	}

	private void buildNavigationList(){
		actions.add("Timeline");
		actions.add("Explore");
		actions.add("Result");
		actions.add("Me");
	}
	
	ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        	switch (itemPosition) {
			case 0:
				TimelineFragment timelineFragment = new TimelineFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, timelineFragment).commit();
				break;
			case 1:
				BlankFragment blankFragment = new BlankFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, blankFragment).commit();
				break;
			case 2:
				BlankFragment blankFragment2 = new BlankFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, blankFragment2).commit();
				break;
			case 3:
				BlankFragment blankFragment3 = new BlankFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, blankFragment3).commit();
				break;
			default:
				break;
			}

            return false;
        }
    };
    
    @Override
	protected void onRestoreInstanceState(Bundle state) {
		if(state.containsKey(STATE_SELECTED)){
			actionBar.setSelectedNavigationItem(state.getInt(STATE_SELECTED));
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED, actionBar.getSelectedNavigationIndex());
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int selected = item.getItemId();
		switch (selected) {
			case R.id.menu_add_new:
				Intent intent = new Intent(this, AskActivity.class);
				startActivity(intent);
				break;
			case R.id.menu_find_friends:
				Intent i = new Intent(this, FindPeopleActivity.class);
				startActivity(i);
				break;
			case R.id.menu_settings:
				break;
			case R.id.menu_signoff:
				doSignOff();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void doSignOff(){
		final Handler handler = new Handler();
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
				handler.post(new Runnable() {
					public void run() {
						dialog = ProgressDialog.show(HomeFragmentActivity.this, "", "Logout..");
					}
				});
			}
			
			public void onDone(final String str) {
				System.out.println("logout: " + str);
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					UserProfile userProfile = UserProfile.getInstance();
					userProfile.signOff();
					
					SharedPreferences sharedPref = getSharedPreferences(ApplicationProperties.NAME, 0);
					String uuid = sharedPref.getString("uuid", null);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.clear();
					editor.putString("uuid", uuid);
					editor.commit();
					
					Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(HomeFragmentActivity.this, "Logout failed. Please try again later.", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
		SharedPreferences pref  = this.getSharedPreferences(ApplicationProperties.NAME, 0);
		String deviceId = pref.getString("device_id", null);
		
		internetTask.addPair("device_id", deviceId);
		internetTask.execute(URLAddress.LOGOUT);
	}
}
