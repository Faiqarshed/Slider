package com.example.Sliderbackend;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "likes")
public class likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "liked")
    private Profile liked;

    // Default constructor
    public likes() {
    }

    // Constructor used in your endpoint
    public likes(Profile profile, Profile liked) {
        this.profile = profile;
        this.liked = liked;
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

    public Profile getLiked() {
        return liked;
    }

    public void setLiked(Profile liked) {
        this.liked = liked;
    }
}