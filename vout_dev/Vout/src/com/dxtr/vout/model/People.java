package com.dxtr.vout.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class People implements Serializable {
	private String id;
	private String email;
	private String firstName;
	private String lastName;
	private String userImageURL;
	private String status;
//	private long friendDate;
	
	public static final String STATUS_FRIEND = "F";
	public static final String STATUS_FRIEND_REQUEST = "FR";
	public static final String STATUS_NOTIF_FRIEND_REQUEST = STATUS_FRIEND_REQUEST;
	public static final String STATUS_NOT_FRIEND = "NF";
	public static final String STATUS_PENDING_APPROVAL = "PA";
	public static final String STATUS_NOTIF_FRIEND_APPROVAL = "FA";
	
	public People() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserImageURL() {
		return userImageURL;
	}

	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}

//	public long getFriendDate() {
//		return friendDate;
//	}

//	public void setFriendDate(long friendDate) {
//		this.friendDate = friendDate;
//	}
	
}
