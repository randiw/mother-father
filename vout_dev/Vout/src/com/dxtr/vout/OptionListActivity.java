package com.dxtr.vout;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dxtr.vout.composite.RecentOptionAdapter;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.InternetTask;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.utils.RecentOptionsUtil;

public class OptionListActivity extends SherlockActivity {

	private ListView lvOptions;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_list_option);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		lvOptions = (ListView)findViewById(R.id.lvOptions);
		
		List<Option> recentOptions = RecentOptionsUtil.GetInstance(this).getRecentOptions();
		if (recentOptions == null) {
			refreshListOption();
		} else {
			lvOptions.setAdapter(new RecentOptionAdapter(this, recentOptions));
		}
		
		lvOptions.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == parent.getAdapter().getCount() - 1) {
					Intent i = new Intent(OptionListActivity.this, AddOptionActivity.class);
					startActivityForResult(i, AskActivity.ACTION_ADD_OPTION);
				} else {
					Option option = (Option)parent.getAdapter().getItem(position);
					Intent result = new Intent();
					result.putExtra("option", option);
					setResult(RESULT_OK, result);
					finish();
				}
			}
		});
		
	}

	private void refreshListOption() {
		InternetTask internetTask = new InternetTask(new InternetConnectionListener() {
			public void onStart() {
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					List<Option> options = RecentOptionsUtil.GetInstance(OptionListActivity.this).parseOption(parser.getDataJSON());
					RecentOptionsUtil.GetInstance(OptionListActivity.this).setRecentOptions(options);
					lvOptions.setAdapter(new RecentOptionAdapter(OptionListActivity.this, options));
				} else {
				}
				setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		});
		internetTask.execute(URLAddress.GET_OPTIONS);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AskActivity.ACTION_ADD_OPTION:
				Intent i = new Intent();
				i.putExtra("option", data.getSerializableExtra("option"));
				setResult(RESULT_OK, i);
				finish();
				break;
			default:
				break;
			}
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
