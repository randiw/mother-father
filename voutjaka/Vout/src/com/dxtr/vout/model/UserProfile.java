package com.dxtr.vout.model;

public class UserProfile {

	private static UserProfile instance = null;

	private String UUID;
	private String access_token;
	private String email;
	private String user_id;
	private String username;
	private String first_name;
	private String last_name;
	private String image_url;

	private String facebook_id;

	private UserProfile() {
	}

	public static UserProfile getInstance() {
		if (instance == null) {
			instance = new UserProfile();
		}
		return instance;
	}

	public void signOff(){
		access_token = null;
		email = null;
		user_id = null;
		username = null;
		first_name = null;
		last_name = null;
	}
	
	public void setImageUrl(String image_url) {
		this.image_url = image_url;
	}

	public String getImageUrl() {
		return image_url;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setFacebookId(String facebook_id) {
		this.facebook_id = facebook_id;
	}

	public String getFacebookId() {
		return facebook_id;
	}

	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setUserId(String user_id) {
		this.user_id = user_id;
	}

	public String getUserId() {
		return user_id;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setUUID(String UUID) {
		this.UUID = UUID;
	}

	public String getUUID() {
		return UUID;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}