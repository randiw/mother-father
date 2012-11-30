package com.dxtr.vout.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class PostInternetTask extends InternetTask {

	private ArrayList<NameValuePair> pairs;
	
	public PostInternetTask(InternetConnectionListener listener) {
		super(listener);
	}

	public void addPairs(String key, String value){
		if (pairs == null) {
			pairs = new ArrayList<NameValuePair>();
		}
		pairs.add(new BasicNameValuePair(key, value));		
	}
	
	@Override
	protected String doInBackground(String... strings) {
		String str = new String();
		
		String data = "";
		if (!pairs.isEmpty()) {
			data = stringEncoder(pairs);
		}

		try {
			URL url = new URL(strings[0]);

			Log.d("Internet Task", "url: " + strings[0]);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("UUID", userProfile.getUUID());
			connection.addRequestProperty("TOKEN", userProfile.getAccessToken());

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
			
			OutputStreamWriter outWriter = new OutputStreamWriter(connection.getOutputStream());
			outWriter.write(data);
			outWriter.flush();

			InputStream inputStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder strBuilder = new StringBuilder();
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuilder.append(line + "\n");
			}
			
			outWriter.close();
			reader.close();
			inputStream.close();

			str = strBuilder.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Internet Task", "string: " + str);
		return str;
	}
	
	private String stringEncoder(ArrayList<NameValuePair> pairs) {
		String str = "";
		try {
			for (int i = 0; i < pairs.size(); i++) {
				BasicNameValuePair pair = (BasicNameValuePair) pairs.get(i);
				String key = pair.getName();
				String value = pair.getValue();
				if (i > 0) {
					str += "&";
				}
				str += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
}