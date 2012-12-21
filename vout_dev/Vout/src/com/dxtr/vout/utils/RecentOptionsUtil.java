package com.dxtr.vout.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.dxtr.vout.model.Option;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.InternetTask;
import com.dxtr.vout.tools.JParser;

public class RecentOptionsUtil {
	
	private static RecentOptionsUtil instance;
//	private static Context context;
	private List<Option> listRecentOptions;

	private RecentOptionsUtil() {
	}

	public static RecentOptionsUtil GetInstance(Context ctx) {
		if (instance == null) {
			instance = new RecentOptionsUtil();
		}
//		context = ctx;
		return instance;
	}
	
	public List<Option> getRecentOptions(){
		return listRecentOptions;
	}
	
	public void setRecentOptions(List<Option> options){
		listRecentOptions = options;
	}
	
	public void retrieveOptionList(){
		InternetTask internetTask = new InternetTask(new InternetConnectionListener() {
			public void onStart() {
			}
			
			public void onDone(final String str) {
				System.out.println("RESPONSE_OPTIONS: " + str);
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					listRecentOptions = parseOption(parser.getDataJSON());
				} else {
					//response not success
				}
			}
		});
		internetTask.execute(URLAddress.GET_OPTIONS);
	}
	
	public List<Option> parseOption(String dataJSON) {
		try {
			List<Option> options = new ArrayList<Option>();
			JSONArray jsonArr = new JSONArray(dataJSON);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject optionObj = jsonArr.getJSONObject(i);
				Option option = new Option();
				option.setId(optionObj.getString("id"));
				option.setTitle(optionObj.getString("title"));
				option.setDescription(optionObj.getString("description"));
				option.setImageUrl(optionObj.getString("option_image"));
				option.setCreatedDate(optionObj.getLong("created_date"));
				option.setUpdatedDate(optionObj.getLong("updated_date"));
				options.add(option);
			}
			return options;
		} catch (JSONException e) {
			return null;
		}
	}
	
}
