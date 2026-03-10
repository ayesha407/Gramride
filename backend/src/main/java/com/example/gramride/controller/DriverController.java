package com.example.gramride.controller;

import com.example.gramride.entity.Driver;
import com.example.gramride.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")

@CrossOrigin(origins = "http://localhost:3000")
public class DriverController {

    @Autowired
    private DriverService driverService;

    // ➕ Add a new driver
    @PostMapping("/add")
    public ResponseEntity<Driver> addDriver(@RequestBody Driver driver) {
        Driver savedDriver = driverService.addDriver(driver);
        return ResponseEntity.ok(savedDriver);
    }

    // 📋 Get all drivers
    @GetMapping("/all")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // 🔁 Mark a driver available again (optional use)
    @PostMapping("/free/{driverId}")
    public ResponseEntity<Void> markAvailable(@PathVariable Long driverId) {
        driverService.markDriverAvailable(driverId);
        return ResponseEntity.ok().build();
    }
}
