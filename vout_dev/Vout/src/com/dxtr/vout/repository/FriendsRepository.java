package com.dxtr.vout.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dxtr.vout.model.People;

public class FriendsRepository extends Repository {
	
	private static final String DATABASE_TABLE = "friends";

	public FriendsRepository(Context context) {
		super(context);
	}
	
	public void save(People friend){
		ContentValues newFriendValues = new ContentValues();

		newFriendValues.put("_id", friend.getId());
		newFriendValues.put("email", friend.getEmail());
		newFriendValues.put("first_name", friend.getFirstName());
		newFriendValues.put("last_name", friend.getLastName());
//		newFriendValues.put("friend_date", friend.getFriendDate());
		newFriendValues.put("status", friend.getStatus());
		newFriendValues.put("user_image_url", friend.getUserImageURL());
		
		Log.i("VOUTDB", "Save to db: " + friend.getFirstName());
		_database.insert(DATABASE_TABLE, null, newFriendValues);
	}
	
	public void clearData(){
		_database.delete(DATABASE_TABLE, null, null);
	}
	
	public People getFriend(int id) {
		Cursor cursor = _database.query(true, DATABASE_TABLE, new String[] {
				"_id", "email", "first_name", "last_name", "user_image_url", "status" }, "_id=" + id, null, null, null,
				null, null);

		if (cursor == null || !cursor.moveToFirst())
			return null;

		People friend = new People();
		friend.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
		friend.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
		friend.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
		friend.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
		friend.setUserImageURL(cursor.getString(cursor.getColumnIndexOrThrow("user_image_url")));
//		friend.setFriendDate(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("user_date"))));
		friend.setStatus(People.STATUS_FRIEND);

		cursor.close();
		return friend;
	}
	
	public Cursor getAllFriends(){
		return _database.query(DATABASE_TABLE, new String[] { "_id", "email", "first_name", 
				"last_name", "user_image_url", "status"}, null, null, null, null,
				"first_name ASC");
	}
}
