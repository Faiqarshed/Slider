package com.example.Sliderbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestsRepository extends JpaRepository<Interests, Long> {

    @Query(value = "SELECT " +
            "    COALESCE(movies.profile_id, music.profile_id, sports.profile_id) AS profile_id, " +
            "    CONCAT_WS(', ', " +
            "        movies.shared_movies, " +
            "        music.shared_music, " +
            "        sports.shared_sports " +
            "    ) AS shared_interests " +
            "FROM " +
            "    (SELECT i2.profile_id, GROUP_CONCAT(DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(i1.movies, ',', nums.n), ',', -1)) ORDER BY nums.n SEPARATOR ', ') AS shared_movies " +
            "     FROM interests i1 " +
            "     JOIN interests i2 ON i1.profile_id = :profileId AND i2.profile_id <> :profileId " +
            "     JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) nums " +
            "     ON nums.n <= 1 + (LENGTH(i1.movies) - LENGTH(REPLACE(i1.movies, ',', ''))) " +
            "     WHERE FIND_IN_SET(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(i1.movies, ',', nums.n), ',', -1)), i2.movies) > 0 " +
            "     GROUP BY i2.profile_id) movies " +
            "LEFT JOIN " +
            "    (SELECT i2.profile_id, GROUP_CONCAT(DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(i1.music, ',', nums.n), ',', -1)) ORDER BY nums.n SEPARATOR ', ') AS shared_music " +
            "     FROM interests i1 " +
            "     JOIN interests i2 ON i1.profile_id = :profileId AND i2.profile_id <> :profileId " +
            "     JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) nums " +
            "     ON nums.n <= 1 + (LENGTH(i1.music) - LENGTH(REPLACE(i1.music, ',', ''))) " +
            "     WHERE FIND_IN_SET(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(i1.music, ',', nums.n), ',', -1)), i2.music) > 0 " +
            "     GROUP BY i2.profile_id) music " +
            "ON music.profile_id = movies.profile_id " +
            "LEFT JOIN " +
            "    (SELECT i2.profile_id, GROUP_CONCAT(DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(i1.sports, ',', nums.n), ',', -1)) ORDER BY nums.n SEPARATOR ', ') AS shared_sports " +
            "     FROM interests i1 " +
            "     JOIN interests i2 ON i1.profile_id = :profileId AND i2.profile_id <> :profileId " +
            "     JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) nums " +
            "     ON nums.n <= 1 + (LENGTH(i1.sports) - LENGTH(REPLACE(i1.sports, ',', ''))) " +
            "     WHERE FIND_IN_SET(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(i1.sports, ',', nums.n), ',', -1)), i2.sports) > 0 " +
            "     GROUP BY i2.profile_id) sports " +
            "ON sports.profile_id = movies.profile_id OR sports.profile_id = music.profile_id;",
            nativeQuery = true)
    List<Object[]> findSharedInterestsByProfileId(@Param("profileId") Long profileId);
}
