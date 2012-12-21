package com.dxtr.vout.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class DialogManager {

	private static DialogManager instance;
	private Dialog dialog;
	
	private DialogManager() {
	}
	
	public static DialogManager getInstance(){
		if(instance == null){
			instance = new DialogManager();
		}
		return instance;
	}
	
	public void showDialog(Context context, String title, String message) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog = ProgressDialog.show(context, title, message);
		
	}
	
	public void dismissDialog(){
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
