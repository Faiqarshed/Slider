package com.example.Sliderbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Change this method if needed
    Optional<Profile> findByUsername(String username);
    Optional<Profile> findById(Long id);

    @Query("SELECT p FROM Profile p " +
            "JOIN p.interests i " +
            "WHERE p.id != :profileId " +
            "AND (i.movies LIKE %:movies% " +
            "OR i.music LIKE %:music% " +
            "OR i.sports LIKE %:sports% " +
            "OR i.technology LIKE %:technology%)")
    List<Profile> findMatchingProfiles(@Param("profileId") Long profileId,
                                       @Param("movies") String movies,
                                       @Param("music") String music,
                                       @Param("sports") String sports,
                                       @Param("technology") String technology);
}