package com.dxtr.vout.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String CREATE_TABLE_FRIEND = "CREATE TABLE IF NOT EXISTS friends (_id text primary key, email text not null, first_name text not null, last_name text, user_image_url text, status text not null);";
	private static final String CREATE_TABLE_AVATAR = "CREATE TABLE IF NOT EXISTS avatars (url text primary key, image BLOB);";
	
	private static final String DATABASE_NAME = "vout";
	private static final int DATABASE_VERSION = 1;

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_FRIEND);
		db.execSQL(CREATE_TABLE_AVATAR);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
