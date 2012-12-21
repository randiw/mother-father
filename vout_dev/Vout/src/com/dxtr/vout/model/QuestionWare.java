package com.dxtr.vout.model;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionWare {
	
//	private static final int limit = 100;
	public static QuestionWare instance = null;
	private ArrayList<HashMap<String, String>> lists;
	
	private QuestionWare() {
		lists = new ArrayList<HashMap<String,String>>();
	}
	
	public static QuestionWare getInstance() {
		if(instance == null) {
			instance = new QuestionWare();
		}
		return instance;
	}
	
	public void add(HashMap<String, String> questionItem){
		lists.add(questionItem);
	}
	
	public void add(int index, HashMap<String, String> questionItem) {
		lists.add(index, questionItem);
	}
	
	public HashMap<String, String> getQuestion(int position) {
		return lists.get(position);
	}
	
//	private boolean contains(int id){
//		for(int i = 0; i < lists.size(); i++) {
//			HashMap<String, String> map = getQuestion(i);
//			if(map.containsValue(id)){
//				return true;
//			}
//		}
//		return false;
//	}
}