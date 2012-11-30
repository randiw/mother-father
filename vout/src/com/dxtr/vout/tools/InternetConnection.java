package com.dxtr.vout.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.dxtr.vout.UserProfile;

import android.util.Log;

public class InternetConnection extends Thread {
	
	protected String url_address;
	protected InternetConnectionListener myListener;
	
	protected UserProfile userProfile = UserProfile.getInstance();
	
	public InternetConnection(String url_address) {
		this.url_address = url_address;
	}

	public void setInternetListener(InternetConnectionListener myListener){
		this.myListener = myListener;
	}
	
	@Override
	public void run() {
		try {
			myListener.onStart();
			Log.d("IC", "url: " + url_address);
			URL url = new URL(url_address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("UUID", userProfile.getUUID());
			connection.addRequestProperty("TOKEN", userProfile.getAccessToken());

			Log.d("IC", "uuid= " + userProfile.getUUID());
			Log.d("IC", "token= " + userProfile.getAccessToken());
			
			connection.setDoInput(true);
			connection.connect();
			
			InputStream inputStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder strBuilder = new StringBuilder();
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuilder.append(line + "\n");
			}

			reader.close();
			inputStream.close();

			String inputString = strBuilder.toString();
			Log.d("IC", "string= " + inputString);
			myListener.onDone(inputString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}