package com.dxtr.vout.composite;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxtr.vout.R;
import com.dxtr.vout.model.People;
import com.dxtr.vout.utils.ImageUtil;

public class FriendListAdapter extends BaseAdapter {

	private List<People> listFriend;
//	private Context context;
	private LayoutInflater li;
	private HashMap<String, Bitmap> avatarMap;
	private List<People> listSelectedFriend;
	private Bitmap defaultThumb, toggleOn;

	public FriendListAdapter(Context context, List<People> listFriend, List<People> listAlreadySelectedFriend, HashMap<String, Bitmap> avatarMap) {
		if (listAlreadySelectedFriend == null) {
			listSelectedFriend = new ArrayList<People>();
		} else {
			listSelectedFriend = new ArrayList<People>();
			for (int i = 0; i < listAlreadySelectedFriend.size(); i++) {
				listSelectedFriend.add(listAlreadySelectedFriend.get(i));
			}
		}
		
		this.listFriend = listFriend;
		this.avatarMap = avatarMap;
//		this.context = context;
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		InputStream is = context.getResources().openRawResource (R.drawable.default_user);
		defaultThumb = BitmapFactory.decodeStream(is);
		InputStream isOn = context.getResources().openRawResource (R.drawable.picked);
		toggleOn = BitmapFactory.decodeStream(isOn);
	}

	@Override
	public int getCount() {
		return listFriend.size();
	}

	@Override
	public People getItem(int position) {
		return listFriend.get(position);
	}
	
	public List<People> getSelectedItems(){
		return listSelectedFriend;
	}
	
	public void clicked(int position){
		if (contains(listSelectedFriend, listFriend.get(position))) {
			removeSelectedFriendFromList(listFriend.get(position).getId());
		} else {
			listSelectedFriend.add(listFriend.get(position));
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<People> getListTimeline(){
		return listFriend;
	}

	public static class ContentViewHolder {
		TextView name;
		int position;
		ImageView userImg, imgToggle;

		public TextView getName() {
			return name;
		}
		public ImageView getUserImg() {
			return userImg;
		}
		public ImageView getImgToggle() {
			return imgToggle;
		}
		public int getPosition() {
			return position;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ContentViewHolder holder;

		if (convertView == null) {
			convertView = li.inflate(R.layout.list_friend_item, null);
			holder = new ContentViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.txtFriend);
			holder.userImg = (ImageView) convertView.findViewById(R.id.imgFriendAvatar);
			holder.imgToggle = (ImageView) convertView.findViewById(R.id.imgToggle);
			convertView.setTag(holder);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}
		
		People friend = listFriend.get(position);
		
		holder.position = position;
		holder.name.setText(friend.getFirstName() + " " + friend.getLastName());
		if (friend.getUserImageURL() != null) {
			Bitmap bmp = Bitmap.createScaledBitmap(avatarMap.get(friend.getUserImageURL()), defaultThumb.getWidth(), defaultThumb.getHeight(), false);
			holder.userImg.setImageBitmap(ImageUtil.getRoundedCornerBitmap(bmp, 7));
		}
		
		if (contains(listSelectedFriend, friend)) {
			holder.imgToggle.setImageBitmap(toggleOn);
			holder.imgToggle.setVisibility(View.VISIBLE);
		} else holder.imgToggle.setVisibility(View.GONE);
		
		return convertView;
	}
	
	private boolean contains(List<People> listFriends, People friend){
		for (int i = 0; i < listFriends.size(); i++) {
			if (listFriends.get(i).getId().equals(friend.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private void removeSelectedFriendFromList(String id){
		for (int i = 0; i < listSelectedFriend.size(); i++) {
			if (listSelectedFriend.get(i).getId().equals(id)) {
				listSelectedFriend.remove(i);
			}
		}
	}
}
