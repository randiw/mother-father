package com.dxtr.vout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.dxtr.vout.model.UserProfile;
import com.dxtr.vout.prop.ApplicationProperties;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;
import com.dxtr.vout.tools.VoutWorker;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class SignInActivity extends SherlockActivity {
	
	private EditText txtEmail;
	private EditText passwordForm;
	
	private Handler handler;
	private ProgressDialog dialog;
	private Dialog passwordDialog;
	
	private SharedPreferences myPref;
	private Facebook facebook = new Facebook(ApplicationProperties.APP_ID);
	private AsyncFacebookRunner myRunner = new AsyncFacebookRunner(facebook);

	private UserProfile userProfile = UserProfile.getInstance();
	private VoutWorker voutWorker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        
        voutWorker = new VoutWorker(this);
        myPref = getSharedPreferences(ApplicationProperties.NAME, 0);
        
        handler = new Handler();
        
        txtEmail = (EditText)findViewById(R.id.insertemail);
        txtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN){
					String email = txtEmail.getText().toString();
					sendEmail(email);
				}
				return false;
			}
		});
        txtEmail.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
    }
    
    public void onSignInFacebook(View view){
		facebook.authorize(this, new String[] {
				"email", "publish_checkins",
		}, new DialogListener() {
			
			public void onFacebookError(FacebookError e) {
			}
			
			public void onError(DialogError e) {
			}
			
			public void onComplete(Bundle values) {
				SharedPreferences.Editor editor = myPref.edit();
				
				String access_token = facebook.getAccessToken();
				userProfile.setAccessToken(access_token);
				voutWorker.setAccessExpires(facebook.getAccessExpires());
				Log.d("Facebook", "fb access token = " + access_token);
				editor.putString("access_token", access_token);
				editor.putLong("access_expires", facebook.getAccessExpires());				
				editor.putBoolean("facebook", true);
				editor.commit();
				
				requestMe();
			}
			
			public void onCancel() {
			}
		});			
    }
    
    private void requestMe(){
    	handler.post(new Runnable() {
			public void run() {
		    	dialog = ProgressDialog.show(SignInActivity.this, "Loading", "fetching facebook graph");				
			}
		});
    	
    	myRunner.request("me", new AsyncFacebookRunner.RequestListener() {
			
			public void onMalformedURLException(MalformedURLException e, Object state) {
				// TODO Auto-generated method stub
			}
			
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
			}
			
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub
			}
			
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub						
			}
			
			public void onComplete(String response, Object state) {
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(response);
					String id = object.getString("id");
					String name = object.getString("name");
					String email = object.getString("email");
					String firstname = object.getString("first_name");
					String middlename = object.getString("middle_name");
					String lastname = object.getString("last_name");

					userProfile.setFacebookId(id);
					userProfile.setEmail(email);
					userProfile.setUsername(name);
			
					JSONObject userJson = new JSONObject();
					userJson.put("id", id);
					userJson.put("name", name);
					userJson.put("email", email);
					userJson.put("first_name", firstname);
					userJson.put("middle_name", middlename);
					userJson.put("last_name", lastname);
					
					voutWorker.doFacebookAccess(userJson.toString(), new VoutWorker.StatusMessageListener() {
						
						public void onSuccess() {
							gotoHome();
						}
						
						public void onError() {
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    private void gotoHome(){
    	Intent intent = new Intent(this, HomeFragmentActivity.class);
		startActivity(intent);
		finish();								    	
    }

    private void sendEmail(String email){
    	PostInternetTask postInternetTask = new PostInternetTask(new InternetConnectionListener() {
			
			public void onStart() {
				dialog = ProgressDialog.show(SignInActivity.this, getString(R.string.signing_in), getString(R.string.progress_signin));										
			}
			
			public void onDone(String str) {
				try {
					JParser parser = new JParser(str);
					if(parser.getStatus()){
						JSONObject object = new JSONObject(parser.getDataJSON());
						final String emailStatus = object.getString("email_status");
						final String email = object.getString("email");
						final String url = object.getString("url");
						
						passwordDialog = new Dialog(SignInActivity.this);
						if("1".equals(emailStatus)){
							passwordDialog.setContentView(R.layout.dialog_ins_password);
							passwordForm = (EditText)passwordDialog.findViewById(R.id.your_password);
							passwordDialog.setTitle(R.string.sign_in);							
						} else {
							passwordDialog.setContentView(R.layout.dialog_ins_new_password);
							passwordForm = (EditText)passwordDialog.findViewById(R.id.new_password);
							passwordDialog.setTitle(R.string.sign_up);							
						}
						
						passwordForm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
							
							public void onFocusChange(View v, boolean hasFocus) {
								if(hasFocus){
									passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
								}
							}
						});
						
						passwordForm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
							
							public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
								if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN){
									String pswd = passwordForm.getText().toString();
									voutWorker.setEmail(email);
									voutWorker.setPassword(pswd);
									if("1".equals(emailStatus)){
										voutWorker.doSignIn(url, new VoutWorker.StatusMessageListener() {
											
											public void onSuccess() {
												gotoHome();
											}
											
											public void onError() {
											}
										});
									} else {
										voutWorker.register(url, new VoutWorker.StatusMessageListener() {
											
											public void onSuccess() {
												gotoHome();
											}
											
											public void onError() {
											}
										});
									}
									passwordDialog.dismiss();
								}										
								return false;
							}
						});
						passwordForm.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
						passwordDialog.show();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});
    	postInternetTask.addPairs("email", email);
    	postInternetTask.execute(URLAddress.CHECK_USER);    	
    }
}