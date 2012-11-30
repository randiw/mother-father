package com.dxtr.vout;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.dxtr.vout.prop.ApplicationProperties;
import com.dxtr.vout.view.TimelineFragment;

public class HomeActivity extends FragmentActivity implements ActionBar.OnNavigationListener{

	private static final String STATE_SELECTED = "selected";
			
	private ActionBar actionBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(
				new ArrayAdapter<String>(actionBar.getThemedContext(), 
						android.R.layout.simple_list_item_1, 
						android.R.id.text1, new String[]{
			getString(R.string.title_timeline),
			getString(R.string.title_explore),
			getString(R.string.title_result),
			getString(R.string.title_me),			
		}), this);
		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		if(state.containsKey(STATE_SELECTED)){
			actionBar.setSelectedNavigationItem(state.getInt(STATE_SELECTED));
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED, getActionBar().getSelectedNavigationIndex());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		Log.d("HOME", "optionsItem: " + title);
		if(getString(R.string.menu_add_new).equals(title)){
			Intent intent = new Intent(getApplicationContext(), AskActivity.class);
			startActivity(intent);
		} else if(getString(R.string.menu_settings).equals(title)){
			
		} else if(getString(R.string.menu_signoff).equals(title)){
			UserProfile userProfile = UserProfile.getInstance();
			userProfile.signOff();
			
			SharedPreferences sharedPref = getSharedPreferences(ApplicationProperties.NAME, 0);
			String uuid = sharedPref.getString("uuid", null);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.clear();
			editor.putString("uuid", uuid);
			editor.commit();
			
			finish();
			
			Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
		case 0:
			TimelineFragment timelineFragment = new TimelineFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.container, timelineFragment).commit();
			return true;
			
		default:
			return false;
		}
	}
}