package com.example.Sliderbackend;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class InterestGraphQLResolver {

    private final InterestsService interestsService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public InterestGraphQLResolver(
            InterestsService interestsService,
            UserRepository userRepository,
            ProfileRepository profileRepository
    ) {
        this.interestsService = interestsService;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @QueryMapping
    public List<SharedInterestDTO> findSharedInterests(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUsername(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long profileId = profile.getId();

        return interestsService.getSharedInterests(profileId).stream()
                .map(p -> new SharedInterestDTO(p.getProfileId(), p.getSharedInterests()))
                .toList();
    }
}
