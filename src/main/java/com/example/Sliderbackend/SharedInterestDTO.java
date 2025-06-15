package com.example.Sliderbackend;

public class SharedInterestDTO {
    private Long profileId;
    private String sharedInterests; // Changed from List<String> to String

    // Constructors
    public SharedInterestDTO() {
    }

    public SharedInterestDTO(Long profileId, String sharedInterests) {
        this.profileId = profileId;
        this.sharedInterests = sharedInterests;
    }

    // Getters and setters
    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getSharedInterests() {
        return sharedInterests;
    }

    public void setSharedInterests(String sharedInterests) {
        this.sharedInterests = sharedInterests;
    }
}