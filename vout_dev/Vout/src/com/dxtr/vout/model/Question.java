package com.dxtr.vout.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Question implements Serializable {

	private String id;
	private String questionContent;
	private int hitRate;
	private int voutCount;
	private long timeLimit;
	private String location;
	private boolean isPrivate;
	private String[] targetIds;
	private String firstName;
	private String lastName;
	private String userImageURL;
	private long createdDate;
	private long updatedDate;
	private String url;
	private List<Option> listOptions;
	
	public Question() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public int getHitRate() {
		return hitRate;
	}

	public void setHitRate(int hitRate) {
		this.hitRate = hitRate;
	}

	public int getVoutCount() {
		return voutCount;
	}

	public void setVoutCount(int voutCount) {
		this.voutCount = voutCount;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String[] getTargetIds() {
		return targetIds;
	}

	public void setListTargetIds(String[] targetIds) {
		this.targetIds = targetIds;
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

	public String getUserImageURL() {
		return userImageURL;
	}

	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(long updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Option> getListOptions() {
		return listOptions;
	}

	public void setListOptions(List<Option> listOptions) {
		this.listOptions = listOptions;
	}
	
}
