package com.example.Sliderbackend;


import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "interests")
public class Interests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonBackReference
    private Profile profile;

    @Column(columnDefinition = "TEXT")
    private String sports;

    @Column(columnDefinition = "TEXT")
    private String music;

    @Column(columnDefinition = "TEXT")
    private String movies;

    @Column(columnDefinition = "TEXT")
    private String technology;

    public Interests() {
    }

    public Interests(Profile profile, List<String> sports, List<String> music,
                     List<String> movies, List<String> technology) {
        this.profile = profile;
        this.setSports(sports);
        this.setMusic(music);
        this.setMovies(movies);
        this.setTechnology(technology);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    // Convert from List<String> to String using comma as separator
    public List<String> getSportsInterests() {
        if (sports == null || sports.isEmpty()) {
            return List.of();
        }
        return List.of(sports.split(","));
    }

    public void setSports(List<String> sports) {
        if (sports != null && !sports.isEmpty()) {
            this.sports = String.join(",", sports);
        } else {
            this.sports = "";
        }
    }

    public List<String> getMusicInterests() {
        if (music == null || music.isEmpty()) {
            return List.of();
        }
        return List.of(music.split(","));
    }

    public void setMusic(List<String> music) {
        if (music != null && !music.isEmpty()) {
            this.music = String.join(",", music);
        } else {
            this.music = "";
        }
    }

    public List<String> getMoviesInterests() {
        if (movies == null || movies.isEmpty()) {
            return List.of();
        }
        return List.of(movies.split(","));
    }

    public void setMovies(List<String> movies) {
        if (movies != null && !movies.isEmpty()) {
            this.movies = String.join(",", movies);
        } else {
            this.movies = "";
        }
    }

    public List<String> getTechnology() {
        if (technology == null || technology.isEmpty()) {
            return List.of();
        }
        return List.of(technology.split(","));
    }

    public void setTechnology(List<String> technology) {
        if (technology != null && !technology.isEmpty()) {
            this.technology = String.join(",", technology);
        } else {
            this.technology = "";
        }
    }
}