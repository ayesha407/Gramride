package com.example.gramride.service;

import com.example.gramride.entity.Driver;
import com.example.gramride.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    // Find and return the first available driver
    public Optional<Driver> assignAvailableDriver() {
        Optional<Driver> driverOpt = driverRepository.findFirstByAvailableTrue();

        driverOpt.ifPresent(driver -> {
            driver.setAvailable(false); // Mark driver as busy
            driverRepository.save(driver);
        });

        return driverOpt;
    }

    // Add a new driver
    public Driver addDriver(Driver driver) {
        driver.setAvailable(true); // default
        return driverRepository.save(driver);
    }

    // Get all drivers
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // Set driver available manually (optional)
    public void markDriverAvailable(Long driverId) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        driverOpt.ifPresent(driver -> {
            driver.setAvailable(true);
            driverRepository.save(driver);
        });
    }
}
