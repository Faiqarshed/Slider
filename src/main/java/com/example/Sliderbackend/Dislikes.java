package com.example.Sliderbackend;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dislikes")
public class Dislikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "disliked")
    private Profile disliked;

    // Default constructor
    public Dislikes() {
    }

    // Constructor used in your endpoint
    public Dislikes(Profile profile, Profile disliked) {
        this.profile = profile;
        this.disliked = disliked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getDisLiked() {
        return disliked;
    }

    public void setDisLiked(Profile disliked) {
        this.disliked = disliked;
    }
}