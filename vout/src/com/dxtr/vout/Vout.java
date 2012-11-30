package com.dxtr.vout;

import org.json.JSONException;
import org.json.JSONObject;

import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnection;
import com.dxtr.vout.tools.InternetConnectionListener;

public class Vout {

	private String access_token;
	private long access_expires;
	
	public Vout() {
		
	}

	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}
	
	public String getAccessToken() {
		return access_token;
	}
	
	public void setAccessExpires(long access_expires) {
		this.access_expires = access_expires;
	}

	/**
     * Retrieve the current session's expiration time (in milliseconds since
     * Unix epoch), or 0 if the session doesn't expire or doesn't exist.
     *
     * @return long - session expiration time
     */
	public long getAccessExpires() {
		return access_expires;
	}
	
	public boolean isSessionValid(){
		return (getAccessToken() != null) && ((getAccessExpires() == 0) || (System.currentTimeMillis() < getAccessExpires()));
	}
	
	public void refreshToken(final StatusMessageListener myListener){
		InternetConnection internetConnection = new InternetConnection(URLAddress.REFRESH_TOKEN);
		internetConnection.setInternetListener(new InternetConnectionListener() {
			
			public void onStart() {
			}
			
			public void onDone(String str) {
				try {
					JSONObject object = new JSONObject(str);
					String status = object.getString("status");
					String message = object.getString("message");
					
					if("1".equals(status)){
						//success refresh token
						String user_id = object.getString("user_id");
						String access_token = object.getString("access_token");
						long access_expires = object.getLong("access_expires");
						
						setAccessToken(access_token);
						setAccessExpires(access_expires);	
					}
					myListener.onComplete(status, message);
					
				} catch (JSONException e) {
					e.printStackTrace();
					myListener.onError(e);
				}
			}
		});
		internetConnection.start();
	}
	
	public interface StatusMessageListener {
		
		public void onComplete(String status, String message);
		
		public void onError(Exception e);
	}
}