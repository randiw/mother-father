package com.dxtr.vout;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.composite.FindPeopleListAdapter;
import com.dxtr.vout.model.People;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;

public class FindPeopleActivity extends SherlockActivity {
	private EditText txtSearch;
	private ListView lvPeople;
	private int counter = 0;

	private List<People> listPeople;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_search_people);
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		
		txtSearch = (EditText)findViewById(R.id.txtSearch);
		lvPeople  = (ListView)findViewById(R.id.lvItem);
		
		txtSearch.addTextChangedListener(new TextWatcher() {
			private CountDownTimer countdown;
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(countdown!=null) countdown.cancel();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(final Editable s) {
				counter = 0;
				countdown = new CountDownTimer(1100, 500) {					
					public void onTick(long millisUntilFinished) {	
						counter++;
					}
					
					public void onFinish() {
						if(counter == 2) {
							counter = 0;
							if(s.toString().length() > 0) 
								searchFriends(s.toString());
						}
					}
				}.start();
				
			}
		});
	}
	
	private void searchFriends(String name) {
		PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {

			public void onStart() {
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					listPeople = parseItems(parser.getDataJSON());
					lvPeople.setAdapter(new FindPeopleListAdapter(FindPeopleActivity.this, listPeople));
					setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				}
			}
		});
		internetTask.addPair("name", name);
		internetTask.execute(URLAddress.SEARCH_PEOPLE);
	}
	
	private List<People> parseItems(String json){
		System.out.println("response: " + json);
		try {
			List<People> peoples = new ArrayList<People>();
			JSONArray arrJson = new JSONArray(json);
			for (int i = 0; i < arrJson.length(); i++) {
				People people = new People();
				people.setId(arrJson.getJSONObject(i).getString("id"));
				people.setEmail(arrJson.getJSONObject(i).getString("email"));
				people.setFirstName(arrJson.getJSONObject(i).getString("first_name"));
				people.setLastName(arrJson.getJSONObject(i).getString("last_name"));
				people.setStatus(arrJson.getJSONObject(i).getString("status"));
				peoples.add(people);
			}
			return peoples;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		   {        
		      case android.R.id.home:
		    	 finish();
		         return true;        
		      default:
		         return super.onOptionsItemSelected(item);    
		   }
	}
	
}
