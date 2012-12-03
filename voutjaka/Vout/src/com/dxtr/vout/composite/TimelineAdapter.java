package com.dxtr.vout.composite;

import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.Question;
import com.dxtr.vout.utils.AppUtils;
import com.dxtr.vout.utils.ImageUtils;

@SuppressLint("DefaultLocale")
public class TimelineAdapter extends BaseAdapter {

	private List<Question> listQuestion;
	private Activity act;
	private LayoutInflater li;

	public TimelineAdapter(Activity act, List<Question> list) {
		this.listQuestion = list;
		this.act = act;
		li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listQuestion.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<Question> getListTimeline(){
		return listQuestion;
	}

	public static class ContentViewHolder {
		TextView name, question, updateTime;
		int position;
		ImageView userImg, optionImg1, optionImg2;

		public TextView getName() {
			return name;
		}
		public TextView getQuestion() {
			return question;
		}
		public TextView getUpdateTime() {
			return updateTime;
		}
		public ImageView getUserImg() {
			return userImg;
		}
		public ImageView getOptionImg1() {
			return optionImg1;
		}
		public ImageView getOtionImg2() {
			return optionImg2;
		}
		public int getPosition() {
			return position;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ContentViewHolder holder;

		if (convertView == null) {
			convertView = li.inflate(R.layout.timeline_item, null);
			holder = new ContentViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.txtFirstName);
			holder.question = (TextView) convertView.findViewById(R.id.txtQuestion);
			holder.updateTime = (TextView) convertView.findViewById(R.id.txtUpdated);
			holder.userImg = (ImageView) convertView.findViewById(R.id.imgUser);
			holder.optionImg1 = (ImageView) convertView.findViewById(R.id.imgOption1);
			holder.optionImg2 = (ImageView) convertView.findViewById(R.id.imgOption2);
			convertView.setTag(holder);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}

		Question question = listQuestion.get(position);
		
		//set user image
		InputStream is = act.getResources().openRawResource (R.drawable.ic_launcher);
		Bitmap defaultThumb = BitmapFactory.decodeStream(is);
		BitmapManager.INSTANCE.loadBitmap(question.getUserImageURL(), holder.userImg, defaultThumb.getWidth(), defaultThumb.getHeight(), defaultThumb, null);
		
		//set options image
		String urlOptions1 = question.getListOptions().get(0).getImageUrl();
		String urlOptions2 = question.getListOptions().get(1).getImageUrl();
		int size = (AppUtils.GetScreenWidth(act) / 2) - 15;
		InputStream stream = act.getResources().openRawResource(R.drawable.def);
		Bitmap defaultOptionImg = ImageUtils.resizeImage(BitmapFactory.decodeStream(stream), size, size);
		BitmapManager.INSTANCE.loadBitmap(urlOptions1, holder.optionImg1, size, size, defaultOptionImg, null);
		BitmapManager.INSTANCE.loadBitmap(urlOptions2, holder.optionImg2, size, size, defaultOptionImg, null);
		
		holder.position = position;
		holder.name.setText(question.getFirstName().toUpperCase() + " asks,");
		holder.question.setText(question.getQuestionContent());
		
		return convertView;
	}

}
