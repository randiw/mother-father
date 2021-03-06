package com.dxtr.vout.tools;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dxtr.vout.R;
import com.dxtr.vout.model.UserProfile;
import com.dxtr.vout.prop.ApplicationProperties;
import com.dxtr.vout.prop.URLAddress;

public class VoutWorker {

	private String access_token;
	private long access_expires;
	
	private String email;
	private String password;
	
	private Context context;
	private SharedPreferences myPref;
	private UserProfile userProfile = UserProfile.getInstance();

	private ProgressDialog dialog;
	
	public VoutWorker(Context context) {
		this.context = context;
		myPref = context.getSharedPreferences(ApplicationProperties.NAME, 0);
		setAccessToken(myPref.getString("access_token", null));
		setAccessExpires(myPref.getLong("access_expires", 0));
	}

	public void setEmail(String email) {
		this.email = email;
		userProfile.setEmail(email);
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setAccessExpires(long access_expires) {
		this.access_expires = access_expires;
	}
	
	public void setAccessToken(String access_token) {
		this.access_token = access_token;
		userProfile.setAccessToken(access_token);
	}
	
	public long getAccessExpires() {
		return access_expires;
	}
	
	public String getAccessToken() {
		return access_token;
	}

	public boolean isSessionValid(){
		return (getAccessToken() != null) && ((getAccessExpires() == 0) || (System.currentTimeMillis() < getAccessExpires()));
	}

	public void doFacebookAccess(String jsonFacebook, final StatusMessageListener listener){
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			
			public void onStart() {
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if(parser.getStatus()){
					try {
						JSONObject object = new JSONObject(parser.getDataJSON());
						String user_id = object.getString("user_id");
						userProfile.setUserId(user_id);
						
						listener.onSuccess();
					} catch (JSONException e) {
						e.printStackTrace();
						listener.onError();
					}
				} else {
					listener.onError();
				}
			}
		});
		Log.d("VoutWorker", "access_expires: " + Long.toString(getAccessExpires()));
		internetTask.addPair("facebook_user_info", jsonFacebook);
		internetTask.addPair("access_expires", Long.toString(getAccessExpires()));
		internetTask.execute(URLAddress.FACEBOOK_ACCESS);
	}
	
	public void doSignIn(String url, final StatusMessageListener listener){
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			
			public void onStart() {
				dialog = ProgressDialog.show(context, context.getString(R.string.sign_in), context.getString(R.string.progress_signin));
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if(parser.getStatus()){
					try {
						JSONObject object = new JSONObject(parser.getDataJSON());

						userProfile.setUserId(object.getString("user_id"));
						setEmail(object.getString("email"));						
						setAccessToken(object.getString("access_token"));
						setAccessExpires(object.getLong("access_expires"));

						userProfile.setFirstName(object.getString("first_name"));
						userProfile.setLastName(object.getString("last_name"));
						userProfile.setImageUrl(object.getString("user_image"));

						SharedPreferences.Editor editor = myPref.edit();
						editor.putString("user_id", userProfile.getUserId());
						editor.putString("access_token", getAccessToken());
						editor.putLong("access_expires", getAccessExpires());
						editor.commit();
						
						listener.onSuccess();
					} catch (JSONException e) {
						e.printStackTrace();
						listener.onError();
					}
				} else {
					listener.onError();
				}
				dialog.dismiss();
			}
		});
		internetTask.addPair("email", getEmail());
		internetTask.addPair("password", getPassword());
		internetTask.execute(url);
	}
 
	public void register(String url, final StatusMessageListener listener){
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			
			public void onStart() {				
				dialog = ProgressDialog.show(context, context.getString(R.string.sign_in), context.getString(R.string.progress_signin));
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if(parser.getStatus()){					
					try {
						JSONObject object = new JSONObject(parser.getDataJSON());
						userProfile.setUserId(object.getString("user_id"));
						setEmail(object.getString("email"));
						setAccessToken(object.getString("access_token"));
						setAccessExpires(object.getLong("access_expires"));
						
						SharedPreferences.Editor editor = myPref.edit();
						editor.putString("user_id", userProfile.getUserId());
						editor.putString("access_token", getAccessToken());
						editor.putLong("access_expires", getAccessExpires());
						editor.commit();
						
						listener.onSuccess();
					} catch (JSONException e) {
						e.printStackTrace();
						listener.onError();
					}
				} else {
					listener.onError();
				}
				dialog.dismiss();
			}
		});
		internetTask.addPair("email", getEmail());
		internetTask.addPair("password", getPassword());
		internetTask.execute(url);
	}
	
	public void refreshToken(final StatusMessageListener listener){
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {
			
			public void onStart() {
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if(parser.getStatus()){
					try {
						JSONObject object = new JSONObject(parser.getDataJSON());
						String user_id = object.getString("user_id");
						setAccessToken(object.getString("access_token"));
						setAccessExpires(object.getLong("access_expires"));
						
						SharedPreferences.Editor editor = myPref.edit();
						editor.putString("user_id", user_id);
						editor.putString("access_token", getAccessToken());
						editor.putLong("access_expires", getAccessExpires());
						editor.commit();
						
						listener.onSuccess();
					} catch (JSONException e) {
						e.printStackTrace();
						listener.onError();
					}
					
				}
			}
		});
		internetTask.addPair("access_token", getAccessToken());
		internetTask.addPair("access_expires", Long.toString(getAccessExpires()));
		internetTask.addPair("email", email);
		internetTask.execute(URLAddress.REFRESH_TOKEN);
	}
	
	public interface StatusMessageListener {
		public void onSuccess();
		public void onError();
	}
}