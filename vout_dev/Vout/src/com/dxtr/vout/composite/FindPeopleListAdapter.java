package com.dxtr.vout.composite;

import java.io.InputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxtr.vout.R;
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.People;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;

public class FindPeopleListAdapter extends BaseAdapter {

	private List<People> listPeople;
	private Context context;
	private LayoutInflater li;
	private Bitmap defaultThumb;
	private int size;
	private static final String ADD = "Add";
	private static final String PENDING_APPROVAL = "Pending..";
	private static final String APPROVE = "Approve";
	
	private Handler handler;
	private ProgressDialog dialog;

	public FindPeopleListAdapter(Context context, List<People> listPeople) {
		this.context = context;
		this.listPeople = listPeople;
		handler = new Handler();
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		InputStream is = context.getResources().openRawResource (R.drawable.default_user);
		defaultThumb = BitmapFactory.decodeStream(is);
		size = defaultThumb.getWidth();
	}

	@Override
	public int getCount() {
		return listPeople.size();
	}

	@Override
	public People getItem(int position) {
		return listPeople.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<People> getListTimeline(){
		return listPeople;
	}

	public static class ContentViewHolder {
		TextView name;
		int position;
		String friendId;
		ImageView userImg;
		Button btnAction;

		public String getFriendId() {
			return friendId;
		}
		public TextView getName() {
			return name;
		}
		public ImageView getUserImg() {
			return userImg;
		}
		public Button getBtnAction() {
			return btnAction;
		}
		public int getPosition() {
			return position;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ContentViewHolder holder;

		if (convertView == null) {
			convertView = li.inflate(R.layout.search_people_item, null);
			holder = new ContentViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.txtName);
			holder.userImg = (ImageView) convertView.findViewById(R.id.imgPeople);
			holder.btnAction = (Button) convertView.findViewById(R.id.btnPeopleAction);
			holder.btnAction.setOnClickListener(new OnClick(holder));
			convertView.setTag(holder);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}

		People people = listPeople.get(position);
		
		holder.friendId = people.getId();
		holder.position = position;
		holder.name.setText(people.getFirstName() + " " + people.getLastName());
		BitmapManager.INSTANCE.loadBitmap(people.getUserImageURL() + "&image_crop=1&image_width=" + size, holder.userImg, size, size, defaultThumb, true, 7);
		
		if (people.getStatus().equals(People.STATUS_FRIEND)) {
			holder.btnAction.setVisibility(View.GONE);
		} else if(people.getStatus().equals(People.STATUS_NOT_FRIEND)) {
			holder.btnAction.setVisibility(View.VISIBLE);
			holder.btnAction.setText(ADD);
		} else if(people.getStatus().equals(People.STATUS_PENDING_APPROVAL)) {
			holder.btnAction.setVisibility(View.VISIBLE);
			holder.btnAction.setEnabled(false);
			holder.btnAction.setText(PENDING_APPROVAL);
		} else {
			holder.btnAction.setVisibility(View.VISIBLE);
			holder.btnAction.setText(APPROVE);
		}
		return convertView;
	}
	
	class OnClick implements OnClickListener {
		ContentViewHolder holder;
		public OnClick(ContentViewHolder holder) {
			this.holder = holder;
		}
		public void onClick(View v) {
			if (((Button)v).getText().equals(ADD)) {
				requestAddfriend(holder);
			} else if(((Button)v).getText().equals(APPROVE)) {
				approveFriend(holder);
			}
		}
	}
	
	private void approveFriend(final ContentViewHolder holder) {
		PostInternetTask postTask = new PostInternetTask(new InternetConnectionListener() {
			
			public void onStart() {
				handler.post(new Runnable() {
					public void run() {
				    	dialog = ProgressDialog.show(context, "Loading", "Processing friend approval.");				
					}
				});
			}
			
			public void onDone(final String str) {
				handler.post(new Runnable() {
					public void run() {
						JParser parser = new JParser(str);
						if (parser.getStatus()) {
							Toast.makeText(context, "You are now friend.", Toast.LENGTH_SHORT).show();
							holder.btnAction.setVisibility(View.GONE);
						} else {
							Toast.makeText(context, "Approve failed. Please try again later.", Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();
					}
				});
				
			}
		});
		postTask.addPair("friend_id", holder.friendId);
		postTask.execute(URLAddress.APPROVE_FRIEND);
	}
	
	private void requestAddfriend(final ContentViewHolder holder) {
		PostInternetTask postTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
				handler.post(new Runnable() {
					public void run() {
				    	dialog = ProgressDialog.show(context, "Loading", "Processing friend request.");				
					}
				});
			}
			
			public void onDone(final String str) {
				handler.post(new Runnable() {
					public void run() {
						JParser parser = new JParser(str);
						if (parser.getStatus()) {
							Toast.makeText(context, "Friend request sent.", Toast.LENGTH_SHORT).show();
							holder.btnAction.setEnabled(false);
							holder.btnAction.setText(PENDING_APPROVAL);
						} else {
							Toast.makeText(context, "Request failed. Please try again later.", Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();
					}
				});
				
			}
		});
		postTask.addPair("friend_id", holder.friendId);
		postTask.execute(URLAddress.INVITE_FRIEND);
	}
}
