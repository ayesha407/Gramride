package com.example.gramride.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gramride.dto.RideDTO;
import com.example.gramride.entity.Ride;
import com.example.gramride.enums.RideStatus;
import com.example.gramride.repository.RideRepository;
import com.example.gramride.service.RideService;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private RideRepository rideRepository;
    // ✅ Book a ride
    @PostMapping("/book")
    public ResponseEntity<Ride> bookRide(@RequestBody RideDTO request) {
        Ride bookedRide = rideService.bookRide(request); // Make sure rideService sets driver
        return ResponseEntity.ok(bookedRide);
    }


    // ✅ Get rides for a user
    @GetMapping("/user/{userId}")
    public List<Ride> getUserRides(@PathVariable Long userId) {
        return rideService.getRidesByUserId(userId);
    }

    // ✅ Get rides for a driver
   /* @GetMapping("/driver/{driverId}")
    public List<Ride> getRidesForDriver(@PathVariable Long driverId) {
        return rideService.getRidesByDriverId(driverId);
    }*/

    // ✅ Update ride status
    @PutMapping("/{rideId}/status")
    public ResponseEntity<?> updateRideStatus(@PathVariable Long rideId, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            Ride updated = rideService.updateRideStatus(rideId, status);
            return ResponseEntity.ok("Ride status updated to " + updated.getStatus());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> markRideAsCompleted(@PathVariable Long id) {
        Optional<Ride> optionalRide = rideRepository.findById(id);
        if (optionalRide.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride not found");
        }

        Ride ride = optionalRide.get();
        ride.setStatus(RideStatus.COMPLETED);

        rideRepository.save(ride);
        return ResponseEntity.ok("Ride marked as completed");
    }

    @GetMapping("/driver/{driverId}")
    public List<Ride> getRidesForDriver(@PathVariable Long driverId) {
        return rideService.getRidesByDriverId(driverId);
    }



 // ✅ Get all rides for admin dashboard
    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

}
