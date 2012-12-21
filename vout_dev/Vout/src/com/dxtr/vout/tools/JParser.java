package com.dxtr.vout.tools;

import org.json.JSONException;
import org.json.JSONObject;

public class JParser {
	
	private String status;
	private String message;
	private String dataJson;
	
	public JParser(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			status = jsonObject.getString("status");
			if(getStatus()){
				dataJson = jsonObject.getString("data");
			} else {
				message = jsonObject.getString("message");				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean getStatus() {
		if("1".equals(status)){
			return true;
		}
		return false;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getDataJSON(){
		return dataJson;
	}	
}