package com.dxtr.vout.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

import com.dxtr.vout.model.UserProfile;
import com.dxtr.vout.prop.ApplicationProperties;

public class InternetTask extends AsyncTask<String, Integer, String> {

	protected UserProfile userProfile = UserProfile.getInstance();
	private InternetConnectionListener listener;
	
	public InternetTask(InternetConnectionListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		listener.onStart();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... strings) {
		String str = new String();
		try {
			URL url = new URL(strings[0]);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("UUID", userProfile.getUUID());
			connection.addRequestProperty("TOKEN", userProfile.getAccessToken());
			connection.addRequestProperty("DEVICE", ApplicationProperties.DEVICE_OS);
			
			connection.setConnectTimeout(60000);
			connection.setDoInput(true);
			connection.connect();
			int responseCode = connection.getResponseCode();

			switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder strBuilder = new StringBuilder();
				
				String line = null;
				while ((line = reader.readLine()) != null) {
					strBuilder.append(line + "\n");
				}

				reader.close();
				inputStream.close();

				str = strBuilder.toString();				
				break;

			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				str = "error:" + str;
				break;
				
			case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
				str = "error:" + str;
				break;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		if(result.startsWith("error")){
			
		} else {
			listener.onDone(result);
		}
		super.onPostExecute(result);
	}
}