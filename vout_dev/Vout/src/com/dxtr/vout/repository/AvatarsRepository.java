package com.dxtr.vout.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class AvatarsRepository extends Repository {
	
	private static final String DATABASE_TABLE = "avatars";

	public AvatarsRepository(Context context) {
		super(context);
	}
	
	public void save(String url, byte[] convertedImage){
		if (url == null) 
			return;
		
		ContentValues newFriendValues = new ContentValues();

		newFriendValues.put("url", url);
		newFriendValues.put("image", convertedImage);
		
		Log.i("VOUTDB", "Save to db: " + url);
		_database.insert(DATABASE_TABLE, null, newFriendValues);
	}
	
	public void clearData(){
		_database.delete(DATABASE_TABLE, null, null);
	}
	
	public Bitmap getAvatar(String url) {
		if(url == null)
			return null;
		
		Cursor cursor = _database.query(true, DATABASE_TABLE, new String[] {
				"url", "image" }, "url like '" + url +"'", null, null, null, null, null);

		if (cursor == null || !cursor.moveToFirst())
			return null;

		byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

		cursor.close();
		Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bmp;
	}
	
	public Cursor getAllAvatar(){
		return _database.query(DATABASE_TABLE, new String[] { "url", "image"}, null, null, null, null, null);
	}
}
