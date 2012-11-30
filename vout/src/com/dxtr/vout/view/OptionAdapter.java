package com.dxtr.vout.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dxtr.vout.R;

public class OptionAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> arrayList;
	
	public OptionAdapter(Context context, ArrayList<String> arrayList) {
		this.context = context;
		this.arrayList = arrayList;
	}

	public int getCount() {
		int c = 0;
		if(arrayList == null || arrayList.isEmpty()){
			c = 2;
		} else {
			c = arrayList.size() + 1;
		}
		return c;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;
		if(convertView == null) {
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.grid_option, null);
			
			ImageView imageView = (ImageView) gridView.findViewById(R.id.option_image);
			
			if(arrayList == null || arrayList.isEmpty()){
				imageView.setImageResource(R.drawable.add_options_72x72);				
			} else {
				if(position < arrayList.size()) {
					
				} else {
					imageView.setImageResource(R.drawable.add_options_72x72);					
				}
			}
			
		} else {
			gridView = convertView;
		}
		return gridView;
	}

}