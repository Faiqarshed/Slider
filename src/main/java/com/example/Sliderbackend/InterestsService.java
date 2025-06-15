package com.example.Sliderbackend;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterestsService {

    private final InterestsRepository interestsRepository;

    public InterestsService(InterestsRepository interestsRepository) {
        this.interestsRepository = interestsRepository;
    }

    public List<SharedInterestDTO> getSharedInterests(Long profileId) {
        List<Object[]> results = interestsRepository.findSharedInterestsByProfileId(profileId);
        return results.stream()
                .map(row -> new SharedInterestDTO(
                        (long) ((Number) row[0]).intValue(),
                        (String) row[1]
                ))
                .collect(Collectors.toList());
    }
}
