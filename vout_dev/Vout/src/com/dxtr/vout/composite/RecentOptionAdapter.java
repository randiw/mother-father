package com.dxtr.vout.composite;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxtr.vout.R;
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.Option;

public class RecentOptionAdapter extends BaseAdapter {

	private List<Option> listOption;
	private LayoutInflater li;
	private Bitmap defaultThumb;
	private int size;
	private Typeface font_bebas;
	private final static int TYPE_OPTION_ITEM = 0;
	private final static int TYPE_ADD_OPTION = 1;

	@SuppressWarnings("unchecked")
	public RecentOptionAdapter(Activity act, List<Option> list) {
		this.listOption = (List<Option>) ((ArrayList<Option>)list).clone();
		this.listOption.add(null);
		li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		InputStream is = act.getResources().openRawResource (R.drawable.default_user);
		defaultThumb = BitmapFactory.decodeStream(is);
		size = defaultThumb.getWidth();
		font_bebas =Typeface.createFromAsset(act.getAssets(), "fonts/BEBAS.TTF");
	}

	@Override
	public int getCount() {
		return listOption.size();
	}

	@Override
	public Option getItem(int position) {
		return listOption.get(position);
	}
	
	@Override
	public int getItemViewType(int position) {
		return (listOption.get(position) != null) ? TYPE_OPTION_ITEM : TYPE_ADD_OPTION;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<Option> getListOptions(){
		return listOption;
	}

	public static class ContentViewHolder {
		TextView title, description;
		int position;
		ImageView imgOption;

		public ImageView getImageOption() {
			return imgOption;
		}
		public TextView getTitle() {
			return title;
		}
		public TextView getDescription() {
			return description;
		}
		public int getPosition() {
			return position;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ContentViewHolder holder;
		int type = getItemViewType(position);
		
		if (convertView == null) {
			holder = new ContentViewHolder();
			switch (type) {
				case TYPE_OPTION_ITEM:
					convertView = li.inflate(R.layout.list_option_item, null);
					holder.title = (TextView) convertView.findViewById(R.id.txtTitle);
					holder.description = (TextView) convertView.findViewById(R.id.txtDesc);
					holder.imgOption = (ImageView) convertView.findViewById(R.id.imgOption);
					break;
				case TYPE_ADD_OPTION:
					convertView = li.inflate(R.layout.add_option_item, null);
					holder.title = (TextView) convertView.findViewById(R.id.txtAddOption);
					break;
			}
			
			convertView.setTag(holder);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}

		if (type == TYPE_OPTION_ITEM) {
			Option option = listOption.get(position);
			
			BitmapManager.INSTANCE.loadBitmap(option.getImageUrl() + "&image_crop=1&image_width=" + size, holder.imgOption, size, size, defaultThumb, true, 7);
			holder.title.setText(option.getTitle());
			holder.description.setText(option.getDescription());
			
			holder.position = position;
		} else {
			holder.title.setTypeface(font_bebas);
		}
		
		return convertView;
	}

}
