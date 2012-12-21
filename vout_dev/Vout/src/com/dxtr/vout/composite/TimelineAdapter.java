package com.dxtr.vout.composite;

import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxtr.vout.R;
import com.dxtr.vout.lazyloader.BitmapManager;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.model.Question;
import com.dxtr.vout.utils.AppUtil;

@SuppressLint("DefaultLocale")
public class TimelineAdapter extends BaseAdapter {

	private List<Question> listQuestion;
//	private Activity act;
	private LayoutInflater li;
	private Bitmap defaultThumb, defaultOptionImg;
	private int size;
	private Typeface font_bariol, font_bebas, font_roboto, font_helveticaneuel;

	public TimelineAdapter(Activity act, List<Question> list) {
		this.listQuestion = list;
//		this.act = act;
		li = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		InputStream is = act.getResources().openRawResource (R.drawable.default_user);
		defaultThumb = BitmapFactory.decodeStream(is);
		size = (AppUtil.GetScreenWidth(act) / 2) - 3;
		InputStream stream = act.getResources().openRawResource(R.drawable.def);
		defaultOptionImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream), size, size, false);
		
		font_bariol =Typeface.createFromAsset(act.getAssets(), "fonts/Bariol_Regular.otf");
		font_bebas =Typeface.createFromAsset(act.getAssets(), "fonts/BEBAS.TTF");
		font_roboto =Typeface.createFromAsset(act.getAssets(), "fonts/Roboto-Regular.ttf"); 
		font_helveticaneuel =Typeface.createFromAsset(act.getAssets(), "fonts/HelveticaNeueLTStd-Lt_0.otf");
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
		TextView ask, name, question, updateTime, option1Title, option2Title;
		int position;
		ImageView userImg, optionImg1, optionImg2;

		public TextView getName() {
			return name;
		}
		public TextView getAsk() {
			return ask;
		}
		public TextView getOption1Title() {
			return option1Title;
		}
		public TextView getOption2Title() {
			return option2Title;
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
			holder.ask = (TextView) convertView.findViewById(R.id.txtAsks);
			holder.question = (TextView) convertView.findViewById(R.id.txtQuestion);
			holder.updateTime = (TextView) convertView.findViewById(R.id.txtUpdated);
			holder.userImg = (ImageView) convertView.findViewById(R.id.imgUser);
			holder.optionImg1 = (ImageView) convertView.findViewById(R.id.imgOption1);
			holder.optionImg2 = (ImageView) convertView.findViewById(R.id.imgOption2);
			holder.option1Title = (TextView) convertView.findViewById(R.id.txtOption1Title);
			holder.option2Title = (TextView) convertView.findViewById(R.id.txtOption2Title);
			convertView.setTag(holder);
			
			holder.name.setTypeface(font_bebas);
			holder.ask.setTypeface(font_bariol, Typeface.BOLD);
			holder.question.setTypeface(font_roboto);
			holder.updateTime.setTypeface(font_helveticaneuel);
			holder.option1Title.setTypeface(font_roboto);
			holder.option2Title.setTypeface(font_roboto);
		} else {
			holder = (ContentViewHolder) convertView.getTag();
		}

		Question question = listQuestion.get(position);
		Option option1 = question.getListOptions().get(0);
		Option option2 = question.getListOptions().get(1);
		
		//set user image
		BitmapManager.INSTANCE.loadBitmap(question.getUserImageURL(), holder.userImg, defaultThumb.getWidth(), defaultThumb.getHeight(), defaultThumb, true, 7);
		
		//set options image
		String urlOptions1 = option1.getImageUrl();
		String urlOptions2 = option2.getImageUrl();
		
		BitmapManager.INSTANCE.loadBitmap(urlOptions1 + "&image_crop=1&image_width=" + size, holder.optionImg1, size, size, defaultOptionImg, false, 0);
		BitmapManager.INSTANCE.loadBitmap(urlOptions2 + "&image_crop=1&image_width=" + size, holder.optionImg2, size, size, defaultOptionImg, false, 0);
		
		holder.position = position;
		holder.name.setText(question.getFirstName().toUpperCase());
		holder.question.setText("\"" + question.getQuestionContent() + "\"");
		holder.option1Title.setText(option1.getTitle());
		holder.option2Title.setText(option2.getTitle());
		
		CharSequence updated = DateUtils.getRelativeTimeSpanString(question.getCreatedDate() * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		holder.updateTime.setText(updated);

		return convertView;
	}
}
