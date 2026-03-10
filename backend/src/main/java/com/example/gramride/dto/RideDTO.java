package com.example.gramride.dto;

import lombok.Data;

@Data
public class RideDTO {
    private Long userId;
    private String pickupLocation;
    private String dropLocation;
    private String vehicleType;
    
    private String rideCategory;

    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getPickupLocation() {
		return pickupLocation;
	}
	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}
	public String getDropLocation() {
		return dropLocation;
	}
	public void setDropLocation(String dropLocation) {
		this.dropLocation = dropLocation;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getRideCategory() {
		return rideCategory;
	}
	public void setRideCategory(String rideCategory) {
		this.rideCategory = rideCategory;
	}
	
}
