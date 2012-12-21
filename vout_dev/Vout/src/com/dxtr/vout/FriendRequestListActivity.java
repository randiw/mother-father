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
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.composite.FindPeopleListAdapter;
import com.dxtr.vout.model.People;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;

public class FriendRequestListActivity extends SherlockActivity {
	private ListView lvfriendRequest;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_friend_request_list);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		
		lvfriendRequest = (ListView)findViewById(R.id.lvFriendRequest);
		loadFriendRequests();
	}

	private void loadFriendRequests() {
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
			
			public void onDone(final String str) {
				System.out.println("FR: " + str);
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					List<People> listFriends = parseFriends(parser.getDataJSON());
					lvfriendRequest.setAdapter(new FindPeopleListAdapter(FriendRequestListActivity.this, listFriends));
				} else {
					Toast.makeText(FriendRequestListActivity.this, "Failed when load friend requests. Please try again later.", Toast.LENGTH_SHORT).show();
					finish();
				}
				setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		});
		internetTask.addPair("status", People.STATUS_FRIEND_REQUEST);
		internetTask.execute(URLAddress.GET_FRIENDS);
	}
	
	private List<People> parseFriends(String dataJSON) {
		try {
			List<People> friends = new ArrayList<People>();
			JSONArray jsonArr = new JSONArray(dataJSON);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject friendObj = jsonArr.getJSONObject(i);
				People friend = new People();
				friend.setId(friendObj.getString("id"));
				friend.setEmail(friendObj.getString("email"));
				friend.setFirstName(friendObj.getString("first_name"));
				friend.setLastName(friendObj.getString("last_name"));
				friend.setUserImageURL(friendObj.getString("user_image").equals("null") ? null : friendObj.getString("user_image"));
				friend.setStatus(friendObj.getString("status"));
				friends.add(friend);
			}
			return friends;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		   {        
		      case android.R.id.home:
		    	  Intent intent = new Intent(FriendRequestListActivity.this, HomeFragmentActivity.class);            
			      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			      startActivity(intent);
			      finish();
		         return true;        
		      default:
		         return super.onOptionsItemSelected(item);
		   }
	}
}
