package com.dxtr.vout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dxtr.vout.composite.FriendListAdapter;
import com.dxtr.vout.model.People;
import com.dxtr.vout.repository.AvatarsRepository;
import com.dxtr.vout.repository.FriendsRepository;

public class FriendListActivity extends SherlockActivity {
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		
		List<People> alreadySelectedFriend = (List<People>)getIntent().getSerializableExtra("friendsSelected");
		
		ListView lvFriends = (ListView)findViewById(R.id.lvFriends);	
		final FriendListAdapter friendAdapter = new FriendListAdapter(this, getFriendListFromDB(), alreadySelectedFriend, getAvatarMap());
		lvFriends.setAdapter(friendAdapter);
		lvFriends.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				FriendListAdapter adapter = (FriendListAdapter)parent.getAdapter();
				adapter.clicked(pos);
				adapter.notifyDataSetChanged();
			}
		});
		
		Button btnPick = (Button)findViewById(R.id.btnPick);
		btnPick.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				ArrayList<People> friends = (ArrayList<People>)friendAdapter.getSelectedItems();
				Intent result = new Intent();
				result.putExtra("friendsSelected", friends);
				setResult(RESULT_OK, result);
				finish();
			}
		});
	}

	private HashMap<String, Bitmap> getAvatarMap(){
		AvatarsRepository ar = new AvatarsRepository(this);
		ar.open();
		Cursor cursorAvatar = ar.getAllAvatar();
		HashMap<String, Bitmap> avMap = new HashMap<String, Bitmap>();
		if (cursorAvatar.moveToFirst()) {
			for (int i = 0; i < cursorAvatar.getCount(); i++) {
				String url = cursorAvatar.getString(cursorAvatar.getColumnIndexOrThrow("url"));
				byte[] imageByte = cursorAvatar.getBlob(cursorAvatar.getColumnIndexOrThrow("image"));
				Bitmap bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
				avMap.put(url, bmp);
				cursorAvatar.moveToNext();
			}
		}
		cursorAvatar.close();
		ar.close();
		return avMap;
	}
	
	private List<People> getFriendListFromDB() {
		FriendsRepository fr = new FriendsRepository(this);
		fr.open();
		Cursor cursorFriend = fr.getAllFriends();
		List<People> listFriends = new ArrayList<People>();
		if (cursorFriend.moveToFirst()) {
			for (int i = 0; i < cursorFriend.getCount(); i++) {
				String id = cursorFriend.getString(cursorFriend.getColumnIndexOrThrow("_id"));
				String firstName = cursorFriend.getString(cursorFriend.getColumnIndexOrThrow("first_name"));
				String lastName = cursorFriend.getString(cursorFriend.getColumnIndexOrThrow("last_name"));
//				long friendDate = cursorFriend.getLong(cursorFriend.getColumnIndexOrThrow("friend_date"));
				String email = cursorFriend.getString(cursorFriend.getColumnIndexOrThrow("email"));
				String userImageURL = cursorFriend.getString(cursorFriend.getColumnIndexOrThrow("user_image_url"));
				String status = cursorFriend.getString(cursorFriend.getColumnIndexOrThrow("status"));
				People friend = new People();
				friend.setId(id);
				friend.setEmail(email);
				friend.setFirstName(firstName);
				friend.setLastName(lastName);
//				friend.setFriendDate(friendDate);
				friend.setUserImageURL(userImageURL);
				friend.setStatus(status);
				listFriends.add(friend);
				cursorFriend.moveToNext();
			}
		}
		cursorFriend.close();
		fr.close();
		return listFriends;
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
