package com.dxtr.vout.detailactivity;
//package com.dxtr.vout;
//
//import java.util.Arrays;
//
//import android.os.Bundle;
//
//import com.facebook.FacebookActivity;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.SessionState;
//import com.facebook.model.GraphUser;
//
//public class FBLoginActivity extends FacebookActivity {
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.openSessionForRead(null, Arrays.asList("email", "user_checkins"));
//	}
//	
//	@Override
//	protected void onSessionStateChange(SessionState state, Exception exception) {
//		// user has either logged in or not ...
//	    if (state.isOpened()) {
//	    // make request to the /me API
//	      
//	      Request request = Request.newMeRequest(this.getSession(), new Request.GraphUserCallback() {
//	        // callback after Graph API response with user object
//	        @Override
//	        public void onCompleted(GraphUser user, Response response) {
//	          if (user != null) {
////	            TextView welcome = (TextView) findViewById(R.id.welcome);
////	            welcome.setText("Hello " + user.getName() + "!");
////	        	  System.out.println("HELLO: " + user.getName());
////	        	  System.out.println("ACCESS TOKEN: " + response.getRequest().getSession().getAccessToken()); 
////	        	  System.out.println("EXPIRED: " + response.getRequest().getSession().getExpirationDate().getTime());
////	        	  System.out.println(user.getInnerJSONObject().toString());
////	        	  System.out.println("EMAIL: " + user.getProperty("email"));
////	        	  System.out.println("CHECKINS: " + user.getProperty("user_checkins"));
//	        	  
//	        	  
//	          }
//	        }
//	      });
//	      Request.executeBatchAsync(request);
//	    }
//	}
//}
