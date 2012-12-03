package com.dxtr.vout.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.dxtr.vout.QuestionDetailActivity;
import com.dxtr.vout.R;
import com.dxtr.vout.composite.TimelineAdapter;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.model.Question;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.InternetTask;
import com.dxtr.vout.tools.JParser;

public class TimelineFragment extends SherlockFragment {
	private boolean dualPane;
	private Handler handler;
//	private ProgressDialog dialog;
	private ListView lvTimeline;
	private Activity activity;
	
	public TimelineFragment() {
		handler = new Handler();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getSherlockActivity();
		refreshFeed();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View timelineView = inflater.inflate(R.layout.timeline_fragment, container, false);
		lvTimeline = (ListView) timelineView.findViewById(R.id.lvTimeline);
		

//		View detailsFrame = getSherlockActivity().findViewById(R.id.container_layout_detail);
//		dualPane = detailsFrame != null;
		
//		if(dualPane){
//			lvPage1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//			detailsFrame.setVisibility(View.VISIBLE);
//		} 
		
		lvTimeline.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				int selectedPos = ((TimelineAdapter.ContentViewHolder)view.getTag()).getPosition();
				if (dualPane) {
//					DetailFragment details = (DetailFragment) getFragmentManager().findFragmentById(R.id.container_layout_detail);
//					details = DetailFragment.newInstance(pos);
//	                FragmentTransaction ft = getFragmentManager().beginTransaction();
//	                ft.replace(R.id.container_layout_detail, details);
//	                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//	                ft.commit();
				} else {
					Intent i = new Intent(activity, QuestionDetailActivity.class);	
					String url = ((TimelineAdapter)parent.getAdapter()).getListTimeline().get(selectedPos).getUrl();
					String name = ((TimelineAdapter)parent.getAdapter()).getListTimeline().get(selectedPos).getFirstName();
					String userImg = ((TimelineAdapter)parent.getAdapter()).getListTimeline().get(selectedPos).getUserImageURL();
					i.putExtra("url", url);
					i.putExtra("name", name);
					i.putExtra("user_image", userImg);
					startActivity(i);
				}
			}
		});
		return timelineView;
	}
	
	private void refreshFeed(){
		InternetTask internetTask = new InternetTask(new InternetConnectionListener() {
			public void onStart() {
				((SherlockFragmentActivity)activity).setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
//				handler.post(new Runnable() {
//					public void run() {
//						dialog = ProgressDialog.show(getSherlockActivity(), "Loading", "Retrieving Timeline");
//					}
//				});
			}
			
			public void onDone(final String str) {
				System.out.println("RESPONSE_QUESTIONS: " + str);
				handler.post(new Runnable() {
					public void run() {
						JParser parser = new JParser(str);
						if (parser.getStatus()) {
							List<Question> listQuestion = parseQuestion(parser.getDataJSON());
							if(listQuestion == null) System.out.println("LIST QUESTION NULL");
							lvTimeline.setAdapter(new TimelineAdapter(activity, listQuestion));
						} else {
							System.out.println("status not success");
						}
//						dialog.dismiss();
						((SherlockFragmentActivity) activity).setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
					}
				});
			}
		});
		internetTask.execute(URLAddress.GET_QUESTION);
	}
	
	private List<Question> parseQuestion(String jsonData) {
		List<Question> listQuest = new ArrayList<Question>();
		try {
			JSONArray arrJson = new JSONArray(jsonData);
			for (int i = 0; i < arrJson.length(); i++) {
				Question question = new Question();
				JSONObject questJsonObj = arrJson.getJSONObject(i);
				question.setId(questJsonObj.getString("id"));
				question.setQuestionContent(questJsonObj.getString("question"));
				question.setHitRate(Integer.parseInt(questJsonObj.getString("hit_rate")));
				question.setVoutCount(Integer.parseInt(questJsonObj.getString("vout_count")));
				question.setTimeLimit(questJsonObj.getString("time_limit"));
				question.setLocation(questJsonObj.getString("location"));
				question.setPrivate("1".equals(questJsonObj.getString("is_private")));
				question.setListTargetIds(questJsonObj.getString("target_id").split(","));
				question.setFirstName(questJsonObj.getString("first_name"));
				question.setLastName(questJsonObj.getString("last_name"));
				question.setUserImageURL(questJsonObj.getString("user_image"));
				question.setCreatedDate(questJsonObj.getLong("created_date"));
				question.setUpdatedDate(questJsonObj.getLong("updated_date"));
				question.setUrl(questJsonObj.getString("url"));
				question.setListOptions(parseOption(questJsonObj.getString("options")));
				listQuest.add(question);
			}
		} catch (JSONException e) {
			return null;
		}
		
		return listQuest;
	}
	
	private List<Option> parseOption(String json){
		try {
			List<Option> options = new ArrayList<Option>();
			JSONArray arr = new JSONArray(json);
			for (int i = 0; i < arr.length(); i++) {
				Option option = new Option();
				JSONObject optObject = arr.getJSONObject(i);
				option.setId(optObject.getString("id"));
				option.setTitle(optObject.getString("title"));
				option.setDescription(optObject.getString("description"));
				option.setImageUrl(optObject.getString("option_image"));
				option.setCreatedDate(optObject.getLong("created_date"));
				option.setUpdatedDate(optObject.getLong("updated_date"));
				options.add(option);
			}
			return options;
		} catch (Exception e) {
			return null;
		}	
	}
}
