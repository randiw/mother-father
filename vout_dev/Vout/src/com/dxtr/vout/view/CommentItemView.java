package com.dxtr.vout.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dxtr.vout.R;
import com.dxtr.vout.model.Comment;

public class CommentItemView extends LinearLayout {
	
	private View v;

	public CommentItemView(Context context) {
		super(context);
	}
	
	public CommentItemView(Context context, final Comment comment) {
		super(context);
		this.setOrientation(VERTICAL);
		
		v = inflate(context, R.layout.comment_item, null);
		
		v.setTag(comment);
		
		TextView txtName = (TextView)v.findViewById(R.id.txtName);
		TextView txtComment = (TextView)v.findViewById(R.id.txtComment);
		
		txtName.setText(comment.getFirstName() + " " + comment.getLastName());
		txtComment.setText(comment.getComment());
				
		addView(v);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		v.setOnClickListener(l);
	}
}
