package com.example.gramride.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.gramride.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByRideDriverId(Long driverId);
}
