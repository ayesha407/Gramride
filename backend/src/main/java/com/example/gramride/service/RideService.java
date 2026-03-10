package com.example.gramride.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gramride.dto.RideDTO;
import com.example.gramride.entity.Ride;
import com.example.gramride.entity.User;
import com.example.gramride.enums.RideStatus;
import com.example.gramride.repository.RideRepository;
import com.example.gramride.repository.UserRepository;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    public Ride bookRide(RideDTO request) {
        // 1. Find an available driver
        User driver = userRepository
                .findTopByRoleAndStatusAndAvailableTrue("DRIVER", "ACTIVE")
                .orElseThrow(() -> new RuntimeException("No available drivers"));

        // 2. Estimate distance & fare (static logic for now)
        double distanceKm = 10.0;
        double fare = calculateFare(request.getVehicleType(), distanceKm);

        // 3. Create and save ride
        Ride ride = new Ride();
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setVehicleType(request.getVehicleType());
        ride.setRideCategory(request.getRideCategory()); 
        ride.setDistanceKm(distanceKm);
        ride.setFare(fare);
        
        ride.setBookingTime(LocalDateTime.now());
        ride.setUser(userRepository.findById(request.getUserId()).orElseThrow());
        ride.setDriver(driver);
        ride.setStatus(RideStatus.CONFIRMED);


        // 4. Mark driver as unavailable
        driver.setAvailable(false);
        userRepository.save(driver);

        return rideRepository.save(ride);
    }

    public List<Ride> getRidesByUserId(Long userId) {
        return rideRepository.findByUserId(userId);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride updateRideStatus(Long rideId, String status) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if ("CANCELLED".equalsIgnoreCase(status)) {
            LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);
            if (ride.getBookingTime().isBefore(twoMinutesAgo)) {
                throw new IllegalStateException("Ride cannot be cancelled after 2 minutes.");
            }

            if (ride.getStatus() == RideStatus.CANCELLED || ride.getStatus() == RideStatus.COMPLETED) {
                throw new IllegalStateException("Ride is already " + ride.getStatus());
            }

            // Simulated driver notification
            User driver = ride.getDriver();
            System.out.println("🚨 Notifying driver " + driver.getFullName() + " at " + driver.getEmail() +
                    " that ride #" + ride.getId() + " has been cancelled by user.");
        }

        ride.setStatus(RideStatus.valueOf(status.toUpperCase()));
        return rideRepository.save(ride);
    }


    // Helper: Fare calculation logic
    private double calculateFare(String vehicleType, double distanceKm) {
        int rate;
        switch (vehicleType.toLowerCase()) {
            case "bike": rate = 5; break;
            case "car": rate = 10; break;
            case "auto": rate = 7; break;
            case "tractor": rate = 8; break;
            default: rate = 6;
        }
        return Math.round(distanceKm * rate);
    }
    public void markAsCompleted(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus(RideStatus.COMPLETED);
        ride.getDriver().setAvailable(true); // make driver available again
        rideRepository.save(ride);
    }
    public List<Ride> getRidesByDriverId(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

}
