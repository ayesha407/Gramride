
package com.example.gramride.entity;

import java.time.LocalDateTime;

import com.example.gramride.enums.FeedbackType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comments;

    private int rating; // from 1 to 5


    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType;

    // Getters and Setters
    public Long getId() { return id; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public FeedbackType getFeedbackType() {
		return feedbackType;
	}
	public void setFeedbackType(FeedbackType feedbackType) {
		this.feedbackType = feedbackType;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
    
}