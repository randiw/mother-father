package com.dxtr.vout;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionDetail {

	private HashMap<String, String> lists;
	private ArrayList<HashMap<String, String>> comments;
	private ArrayList<HashMap<String, String>> options;
	
	public QuestionDetail() {
		lists = new HashMap<String, String>();
		options = new ArrayList<HashMap<String,String>>();
	}
	
	public void addField(String key, String value){
		lists.put(key, value);
	}
	
	public String getField(String key){
		return lists.get(key);
	}
	
	public HashMap<String, String> getOption_A(){
		return options.get(0);
	}
	
	public String getOptionField_A(String key){
		return options.get(0).get(key);
	}
	
	public HashMap<String, String> getOption_B(){
		return options.get(1);
	}
	
	public String getOptionField_B(String key){
		return options.get(1).get(key);
	}
	
	public void addOption(HashMap<String, String> option){
		options.add(option);
	}
	
	public void addComment(HashMap<String, String> comment){
		if(comments == null) {
			comments = new ArrayList<HashMap<String,String>>();
		}
		comments.add(comment);
	}
}