package com.example.Sliderbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface dislikesRepository extends JpaRepository<Dislikes, Long> {
    List<Dislikes> findByProfile_Id(Long profileId);
    List<Dislikes> findByDisliked(Profile disliked);
}
