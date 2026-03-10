package com.example.gramride.dto;

public class VoiceRequestDTO {
    private String pickupLocation;
    private String dropLocation;

    public VoiceRequestDTO() {}

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
}


