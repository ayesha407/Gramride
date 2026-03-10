package com.example.gramride.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gramride.dto.FeedbackDTO;
import com.example.gramride.entity.Feedback;
import com.example.gramride.entity.Ride;
import com.example.gramride.enums.FeedbackType;
import com.example.gramride.enums.RideStatus;
import com.example.gramride.repository.FeedbackRepository;
import com.example.gramride.repository.RideRepository;
import com.example.gramride.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private RideRepository rideRepository;
    // Submit feedback
    @PostMapping
    public ResponseEntity<String> submitFeedback(@RequestBody FeedbackDTO dto) {
        Ride ride = rideRepository.findById(dto.getRideId())
            .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Ensure ride is completed
        if (ride.getStatus() != RideStatus.COMPLETED) {
            return ResponseEntity.badRequest().body("You can give feedback only after the ride is completed.");
        }

        Feedback feedback = new Feedback();
        feedback.setRide(ride);
        feedback.setComments(dto.getComments());
        feedback.setFeedbackType(FeedbackType.valueOf(dto.getFeedbackType().toUpperCase()));
        feedback.setRating(dto.getRating()); // ✅ Save rating
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback submitted successfully.");
    }



    // Admin: Get feedback for driver
    @GetMapping("/driver/{driverId}")
    public List<Feedback> getDriverFeedback(@PathVariable Long driverId) {
        return feedbackService.getFeedbackForDriver(driverId);
    }

    // Admin: View all feedback
    @GetMapping
    public List<Feedback> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }
}
