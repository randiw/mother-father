package com.dxtr.vout.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dxtr.vout.AnswerActivity;
import com.dxtr.vout.R;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnection;
import com.dxtr.vout.tools.InternetConnectionListener;

public class TimelineFragment extends ListFragment {

	public static final String TAG_ID = "id";
	public static final String TAG_QUESTION = "question";
	public static final String TAG_HIT_RATE = "hit_rate";
	public static final String TAG_TIME_LIMIT = "time_limit";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_IS_PRIVATE = "is_private";
	public static final String TAG_TARGET_ID = "target_id";
	public static final String TAG_QUESTION_IMAGE = "question_image";
	public static final String TAG_FIRST_NAME = "first_name";
	public static final String TAG_LAST_NAME = "last_name";
	public static final String TAG_USER_IMAGE = "user_image";
	public static final String TAG_CREATED_DATE = "created_date";
	public static final String TAG_UPDATED_DATE = "updated_date";
	public static final String TAG_URL = "url";

	private ArrayList<HashMap<String, String>> mapList;
	private Handler handler;
	private ProgressDialog dialog;
	
	public TimelineFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		refreshFeed();
	}
	
	private void refreshFeed(){
		InternetConnection internetConnection = new InternetConnection(URLAddress.GET_QUESTION);
		internetConnection.setInternetListener(new InternetConnectionListener() {
			
			public void onStart() {
				handler.post(new Runnable() {
					
					public void run() {
						dialog = ProgressDialog.show(getActivity(), "Loading", "Asking People");
					}
				});
			}
			
			public void onDone(String str) {
				mapList = new ArrayList<HashMap<String, String>>();
				try {
					JSONArray jsonArray = new JSONArray(str);
					for(int i = 0; i < jsonArray.length(); i++){
						JSONObject object = jsonArray.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
						@SuppressWarnings("unchecked")
						Iterator<String> keys = object.keys();
						while (keys.hasNext()) {
							String key = keys.next();
							String value = object.getString(key);
							map.put(key, value);
						}
						mapList.add(map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				handler.post(new Runnable() {
					
					public void run() {
						SimpleAdapter adapter = new SimpleAdapter(getActivity(), mapList, R.layout.list_question, new String[]{
							"first_name",
							"question",
						}, new int[]{
								R.id.name,
								R.id.question
						});
						setListAdapter(adapter);
						dialog.dismiss();
					}
				});
			}
		});
		internetConnection.start();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		HashMap<String, String> hmap = mapList.get(position);
		Intent intent = new Intent(getActivity(), AnswerActivity.class);
		intent.putExtra("url", hmap.get("url"));
		startActivity(intent);
	}
}