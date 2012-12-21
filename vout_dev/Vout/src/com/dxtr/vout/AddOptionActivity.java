package com.dxtr.vout;

import java.io.File;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dxtr.vout.model.Option;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.FileUploaderTask;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;
import com.dxtr.vout.utils.AppUtil;
import com.dxtr.vout.utils.ImageUtil;

public class AddOptionActivity extends SherlockActivity {

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	
	private Uri mImageCaptureUri;
	private ImageView imgOption;
	private Button btnSave;
	private Bitmap bitmap;
	private EditText txtTitle, txtDesc;
	private Handler handler;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_option);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2512f")));
		
		handler = new Handler();
		txtTitle = (EditText)findViewById(R.id.txtTitle);
		txtDesc = (EditText)findViewById(R.id.txtDesc);
		imgOption = (ImageView)findViewById(R.id.imgOption);
		setDefaultOptionImage(imgOption);
		imgOption.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showImageSourceChooserDialog();
			}
		});
		
		btnSave = (Button)findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new FileUploaderTask(new InternetConnectionListener() {
					public void onStart() {
						handler.post(new Runnable() {
							public void run() {
						    	dialog = ProgressDialog.show(AddOptionActivity.this, "Loading", "Saving option.");				
							}
						});
					}
					
					public void onDone(String str) {
						JParser parser = new JParser(str);
						if (parser.getStatus()) {
							String url = null;
							try {
								url = new JSONObject(parser.getDataJSON()).getString("image_url");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							//submit option
							submitOption(txtTitle.getText().toString(), txtDesc.getText().toString(), url);
						} else {
							dialog.dismiss();
							Toast.makeText(AddOptionActivity.this, "Error uploading image. Try again later.", Toast.LENGTH_SHORT).show();
						}
					}
				}, ImageUtil.ConvertToFile(AddOptionActivity.this, bitmap, 50, "" + System.currentTimeMillis() + ".jpg"))
				.execute(URLAddress.UPLOAD_IMAGE);
			}
		});
	}
	
	private void submitOption(final String title, final String desc, final String imageUrl){
		PostInternetTask postTask = new PostInternetTask(new InternetConnectionListener() {
			public void onStart() {
			}
			
			public void onDone(String str) {
				System.out.println("submit option: " + str);
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					try {
						bitmap.recycle();
						Option option = new Option();
						option.setId((new JSONObject(parser.getDataJSON())).getString("id"));
						option.setTitle(title);
						option.setDescription(desc);
						option.setImageUrl(imageUrl);
						option.setCreatedDate((new JSONObject(parser.getDataJSON())).getLong("created_date"));
						option.setUpdatedDate((new JSONObject(parser.getDataJSON())).getLong("updated_date"));
						
						Intent result = new Intent();
						result.putExtra("option", option);
						setResult(RESULT_OK, result);
						
						Toast.makeText(AddOptionActivity.this, "Option Saved.", Toast.LENGTH_SHORT).show();
						finish();
					} catch (Exception e) {
					}
				} else {
					Toast.makeText(AddOptionActivity.this, "Option save failed. Please try again later.", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
		postTask.addPair("title", title);
		postTask.addPair("description", desc);
		postTask.addPair("image_url", imageUrl);
		postTask.execute(URLAddress.ADD_OPTION);
	}

	private void setDefaultOptionImage(ImageView imgOption) {
		int size = (AppUtil.GetScreenWidth(this));
		InputStream stream = getResources().openRawResource(R.drawable.photo);
		Bitmap defaultOptionImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(stream), size, size, false);
		imgOption.setImageBitmap(defaultOptionImg);
	}
	
	private void showImageSourceChooserDialog() {
		final String[] items = new String[] { "From Camera", "From SD Card", "From flickr.com" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					startCameraPickerActivity();
				} else if (item == 1) {
					startSDCardPickerActivity();
				} else {
					Toast.makeText(AddOptionActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.show();
	}
	
	private void startCameraPickerActivity(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment.getExternalStorageDirectory(), "tmp_voutimageoption_"
				+ String.valueOf(System.currentTimeMillis())+ ".jpg");
		mImageCaptureUri = Uri.fromFile(file);
		try {
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startSDCardPickerActivity() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;

		String path = "";
		if(bitmap != null) bitmap.recycle();
		if (requestCode == PICK_FROM_FILE) {
			mImageCaptureUri = data.getData();
			path = getRealPathFromURI(mImageCaptureUri); // from Gallery
			if (path == null)
				path = mImageCaptureUri.getPath(); // from File Manager
			if (path != null)
				bitmap = BitmapFactory.decodeFile(path);
		} else if (requestCode == PICK_FROM_CAMERA) {
			path = mImageCaptureUri.getPath();
			bitmap = BitmapFactory.decodeFile(path);
		}

		int width = AppUtil.GetScreenWidth(this);
		int height = (bitmap.getHeight() * width) / bitmap.getWidth();
		Bitmap resizedbBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		imgOption.setImageBitmap(resizedbBitmap);
		resizedbBitmap = null;

	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		
		if (cursor == null)
			return null;

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		   {        
		      case android.R.id.home:
		    	 finish();
		         return true;        
		      default:
		         return super.onOptionsItemSelected(item);    
		   }
	}
}
