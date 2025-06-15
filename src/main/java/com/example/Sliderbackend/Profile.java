package com.example.Sliderbackend;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

import com.example.Sliderbackend.Message.Message;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullname;
    private int age;
    private String bio;
    private String gender;

    // Add this field to match your database schema
    private String username;
    // Keep this for JPA bidirectional relationship but don't map to a column
    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Images> images;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Interests> interests;  // Add this line to map the relationship

    @OneToMany(mappedBy = "sender_id")
    @JsonManagedReference(value = "sent-messages")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver_id")
    @JsonManagedReference(value = "received-messages")
    private List<Message> receivedMessages;




    // Getter and Setter for Interests
    public List<Interests> getInterests() {
        return interests;
    }

    public void setInterests(List<Interests> interests) {
        this.interests = interests;
    }

    // Add getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }


    public String getFullname(){
        return fullname;
    }

    public void setName(String fullname){
        this.fullname = fullname;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    public String getBio(){
        return bio;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public String getGender(){
        return gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }


    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}