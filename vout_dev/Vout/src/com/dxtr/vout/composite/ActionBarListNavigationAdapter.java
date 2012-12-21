package com.dxtr.vout.composite;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dxtr.vout.R;

public class ActionBarListNavigationAdapter extends BaseAdapter {

	private List<String> listAction;
	private Context context;
	private LayoutInflater li;

	public ActionBarListNavigationAdapter(Context ctx, List<String> listAction) {
		this.listAction = listAction;
		context = ctx;
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listAction.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ContentViewHolder {
		TextView name;
		int position;

		public TextView getName() {
			return name;
		}
		public int getPosition() {
			return position;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ContentViewHolder holder;

		if (convertView == null) {
			convertView = li.inflate(R.layout.navigation_item, null);
			holder = new ContentViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.txtNavigationTitle);
			convertView.setTag(holder);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}

		holder.name.setText(listAction.get(position));
		
		return convertView;
	}

}
