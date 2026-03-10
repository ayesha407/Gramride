package com.example.gramride.dto;

public class FeedbackDTO {
    private Long rideId;
    private String comments;
    private String feedbackType; // e.g., "rash_drive", "very_rash_drive"
    private int rating; // from 1 to 5

	public Long getRideId() {
		return rideId;
	}
	public void setRideId(Long rideId) {
		this.rideId = rideId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getFeedbackType() {
		return feedbackType;
	}
	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
    
    
}

