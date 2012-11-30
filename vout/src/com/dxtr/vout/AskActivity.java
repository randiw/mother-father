package com.dxtr.vout;

import com.dxtr.vout.view.OptionAdapter;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class AskActivity extends Activity {

	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask);

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		GridView gridView = (GridView)findViewById(R.id.grid_options);
		gridView.setAdapter(new OptionAdapter(this, null));
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub

			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_ask, menu);
		return true;
	}
}