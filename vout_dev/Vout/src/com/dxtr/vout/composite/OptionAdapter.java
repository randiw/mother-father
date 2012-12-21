package com.dxtr.vout.composite;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dxtr.vout.R;
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.utils.AppUtil;
import com.dxtr.vout.utils.ImageUtil;

public class OptionAdapter extends BaseAdapter {

//	private Activity activity;
	private List<Option> optionList;
	private int size;
	private LayoutInflater li;
	private Bitmap defaultOptionImg;
	private Bitmap addOptionImg;
	
	private final static int TYPE_OPTION_ITEM = 0;
	private final static int TYPE_ADD_OPTION = 1;
	
	public OptionAdapter(Activity activity, List<Option> arrayList) {
//		this.activity = activity;
		this.optionList = arrayList;
		this.optionList.add(null);
		size = AppUtil.GetScreenWidth(activity) / 4;
		li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		InputStream stream = activity.getResources().openRawResource(R.drawable.def);
		defaultOptionImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream), size, size, false);
		InputStream is = activity.getResources().openRawResource(R.drawable.default_option);
		addOptionImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), size, size, false);
	}

	public int getCount() {
		return optionList.size();
	}

	public Option getItem(int position) {
		return optionList.get(position);
	}
	
	public void addItemAndRefresh(Option option){
		optionList.add(optionList.size() - 1, option);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getItemViewType(int position) {
		return (optionList.get(position) != null) ? TYPE_OPTION_ITEM : TYPE_ADD_OPTION;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public long getItemId(int pos) {
		return pos;
	}
	
	public static class ContentViewHolder {
		int position;
		ImageView imgOption;

		public ImageView getImage() {
			return imgOption;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ContentViewHolder holder;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ContentViewHolder();
			switch (type) {
				case TYPE_OPTION_ITEM:
					convertView = li.inflate(R.layout.grid_option_item, null);
					holder.imgOption = (ImageView) convertView.findViewById(R.id.option_image);
					break;
				case TYPE_ADD_OPTION:
					convertView = li.inflate(R.layout.grid_add_option_item, null);
					holder.imgOption = (ImageView) convertView.findViewById(R.id.add_option_image);
					holder.imgOption.setImageBitmap(ImageUtil.getRoundedCornerBitmap(addOptionImg, 7));
					break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}
			
		if(type == TYPE_OPTION_ITEM){
			BitmapManager.INSTANCE.loadBitmap(optionList.get(position).getImageUrl() + "&image_crop=1&image_width=" + size, 
					holder.imgOption, size, size, defaultOptionImg, true, 7);
		} 
		
		return convertView;
	}

}