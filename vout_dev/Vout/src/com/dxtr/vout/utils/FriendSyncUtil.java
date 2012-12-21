package com.dxtr.vout.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.dxtr.vout.model.People;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.repository.AvatarsRepository;
import com.dxtr.vout.repository.FriendsRepository;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;

public class FriendSyncUtil {
	
	private static FriendSyncUtil instance;
	private static ExecutorService pool;
	private static Context context;

	private FriendSyncUtil() {
	}

	public static FriendSyncUtil GetInstance(Context ctx) {
		if (instance == null) {
			instance = new FriendSyncUtil();
			pool = Executors.newFixedThreadPool(1);
		}
		context = ctx;
		return instance;
	}
	
	public void retrieveFriendList(){
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
			}
			
			public void onDone(final String str) {
				System.out.println("RESPONSE_FRIENDS: " + str);
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					List<People> listFriends = parseFriends(parser.getDataJSON());
					//download image sequencially then save image to db
					saveFriendsToDB(listFriends);
				} else {
					//response not success
				}
			}
		});
		internetTask.addPair("status", People.STATUS_FRIEND);
		internetTask.execute(URLAddress.GET_FRIENDS);
	}
	
	private void saveFriendsToDB(List<People> friends){
		FriendsRepository friendRepo = new FriendsRepository(context);
		friendRepo.open();
		for (int i = 0; i < friends.size(); i++) {
			People friend = friends.get(i);
			friendRepo.save(friend);
			downloadAvatar(friend.getUserImageURL());
		}
		friendRepo.close();
	}
	
	private void downloadAvatar(final String url){
		pool.submit(new Runnable() {
			public void run() {
				DefaultHttpClient mHttpClient = new DefaultHttpClient();
				HttpGet mHttpGet = new HttpGet(url);
				HttpResponse mHttpResponse = null;
				try {
					mHttpResponse = mHttpClient.execute(mHttpGet);
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				  HttpEntity entity = mHttpResponse.getEntity();
				    if ( entity != null) {
						// insert to database
				    	AvatarsRepository repo = new AvatarsRepository(context);
				    	repo.open();
				    	try {
							repo.save(url, EntityUtils.toByteArray(entity));
						} catch (IOException e) {
							e.printStackTrace();
						}
				    	repo.close();
				    }
				}
			}
		});
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
	
}
