package com.dxtr.vout.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.os.AsyncTask;

import com.dxtr.vout.model.UserProfile;
import com.dxtr.vout.prop.ApplicationProperties;

public class FileUploaderTask extends AsyncTask<String, Integer, String> {

	protected UserProfile userProfile = UserProfile.getInstance();
	private InternetConnectionListener listener;
	private File file;
	
	public FileUploaderTask(InternetConnectionListener listener, File file) {
		this.listener = listener;
		this.file = file;
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
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpPost postRequest = new HttpPost(strings[0]);
			
			postRequest.addHeader("UUID", userProfile.getUUID());
			postRequest.addHeader("TOKEN", userProfile.getAccessToken());
			postRequest.addHeader("DEVICE", ApplicationProperties.DEVICE_OS);
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			ContentBody cbFile = new FileBody(file, "image/jpeg");
			reqEntity.addPart("image", cbFile);
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			str = s.toString();
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
