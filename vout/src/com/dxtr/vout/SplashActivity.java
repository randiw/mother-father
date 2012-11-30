package com.dxtr.vout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.dxtr.vout.prop.ApplicationProperties;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class SplashActivity extends Activity {

	private SharedPreferences myPref;

	private UserProfile userProfile = UserProfile.getInstance();
	private Handler handler;
	private ProgressDialog dialog;
	
	private Facebook facebook = new Facebook(ApplicationProperties.APP_ID);
	private AsyncFacebookRunner myRunner = new AsyncFacebookRunner(facebook);
	
	private VoutWorker voutWorker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        voutWorker = new VoutWorker(this);
        
        myPref = getSharedPreferences(ApplicationProperties.NAME, 0);
        handler = new Handler();

        String existingUUID = myPref.getString("uuid", null);
        if(existingUUID == null){
            UUID uuid = UUID.randomUUID();
            existingUUID = uuid.toString();

            SharedPreferences.Editor editor = myPref.edit();
            editor.putString("uuid", existingUUID);
            editor.commit();            
        }
        userProfile.setUUID(existingUUID);
        
        String access_token = myPref.getString("access_token", null);
        boolean isFacebook = myPref.getBoolean("facebook", false);
        if(access_token != null){
        	userProfile.setAccessToken(access_token);
        	long access_expires = myPref.getLong("access_expires", 0);

        	if(isFacebook){
        		facebook.setAccessToken(access_token);
        		facebook.setAccessExpires(access_expires);        			
        		
        		if(facebook.isSessionValid()){        			
        			gotoHome();
        		} else {
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
        	} else {
        		if(voutWorker.isSessionValid()){
        			gotoHome();
        		} else {
        			voutWorker.refreshToken(new VoutWorker.StatusMessageListener() {
						
						public void onSuccess() {
							gotoHome();
						}
						
						public void onError() {
							gotoSignIn();
						}
					});        			
                }
        	}    	
        } else {
        	handler.postDelayed(new Runnable() {
				
				public void run() {
					gotoSignIn();
				}
			}, 3000);
        }
    }

    private void gotoSignIn(){
    	finish();
    	
		Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
		startActivity(intent);
	}
    
    private void gotoHome(){
    	finish();
    	
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);								    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }
    
    private void requestMe(){
    	handler.post(new Runnable() {
			
			public void run() {
		    	dialog = ProgressDialog.show(getApplicationContext(), "Loading", "fetching facebook graph");				
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
				try {
					JSONObject object = new JSONObject(response);
					String id = object.getString("id");
					String name = object.getString("name");
					String email = object.getString("email");

					userProfile.setFacebookId(id);
					userProfile.setEmail(email);
					userProfile.setUsername(name);
					
					voutWorker.doFacebookAccess(null, new VoutWorker.StatusMessageListener() {
						
						public void onSuccess() {
							dialog.dismiss();
							gotoHome();
						}
						
						public void onError() {
							dialog.dismiss();
							gotoSignIn();
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
}