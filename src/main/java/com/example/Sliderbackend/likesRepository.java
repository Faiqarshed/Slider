package com.example.Sliderbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface likesRepository extends JpaRepository<likes, Long> {
    List<likes> findByProfile_Id(Long profileId);
    List<likes> findByliked(Profile liked);
}
