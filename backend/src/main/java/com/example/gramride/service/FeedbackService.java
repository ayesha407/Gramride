package com.example.gramride.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gramride.dto.FeedbackDTO;
import com.example.gramride.entity.Feedback;
import com.example.gramride.entity.Ride;
import com.example.gramride.enums.FeedbackType;
import com.example.gramride.repository.FeedbackRepository;
import com.example.gramride.repository.RideRepository;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepo;

    @Autowired
    private RideRepository rideRepo;

    public Feedback submitFeedback(FeedbackDTO dto) {
        Ride ride = rideRepo.findById(dto.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Feedback feedback = new Feedback();
        feedback.setComments(dto.getComments());
        feedback.setRating(dto.getRating());


        // 💡 Convert string to enum safely
        FeedbackType type;
        try {
            type = FeedbackType.valueOf(dto.getFeedbackType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid feedback type: " + dto.getFeedbackType());
        }

        feedback.setFeedbackType(type);
        feedback.setRide(ride);

        Feedback saved = feedbackRepo.save(feedback);

        // 🚨 Auto suspend driver if very rash
        if (FeedbackType.VERY_RASH_DRIVE.equals(type)) {
            Long driverId = ride.getDriver().getId();
            long count = feedbackRepo.findByRideDriverId(driverId).stream()
                    .filter(f -> FeedbackType.VERY_RASH_DRIVE.equals(f.getFeedbackType()))
                    .count();

            if (count >= 2) {
                ride.getDriver().setStatus("SUSPENDED");
                rideRepo.save(ride); // or use userRepo.save(driver)
            }
        }

        return saved;
    }

    public List<Feedback> getFeedbackForDriver(Long driverId) {
        return feedbackRepo.findByRideDriverId(driverId);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepo.findAll();
    }
}
