package com.dxtr.vout;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.composite.ActionBarListNavigationAdapter;
import com.dxtr.vout.fragment.BlankFragment;
import com.dxtr.vout.fragment.TimelineFragment;
import com.dxtr.vout.model.UserProfile;
import com.dxtr.vout.prop.ApplicationProperties;

public class HomeFragmentActivity extends SherlockFragmentActivity {
	private static final String STATE_SELECTED = "selected";
	private ActionBar actionBar;
	private List<String> actions = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_home);
		
		buildNavigationList();
		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, actions);
		actionBar.setListNavigationCallbacks(new ActionBarListNavigationAdapter(this, actions), navigationListener);
        adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
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
//			Intent intent = new Intent(getApplicationContext(), AskActivity.class);
//			startActivity(intent);
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
	}
}
