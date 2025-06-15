package com.example.Sliderbackend;

import com.example.Sliderbackend.Message.Message;
import com.example.Sliderbackend.Message.MessageDTO;
import com.example.Sliderbackend.Message.MessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private InterestsRepository interestsRepository;
    @Autowired
    private likesRepository likesRepository;
    @Autowired
    private dislikesRepository dislikesRepository;
    @Autowired
    private MessageRepository messageRepository;
    private final AuthService authService;

    public UserController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @GetMapping("/chat/messages/history/{senderId}/{receiverId}")
    public List<MessageDTO> getChatHistory(@PathVariable Long senderId, @PathVariable Long receiverId) {
        System.out.println("FUNCTION GETCHATHISTORY CALLED with " + senderId + receiverId);
        List<Message> messages = messageRepository.findChatHistory(senderId, receiverId);
        return messages.stream().map(MessageDTO::new).collect(Collectors.toList());
    }



    @GetMapping("/chat/messages/{senderId}")
    public ResponseEntity<List<Integer>> getReceiverIdsBySender(@PathVariable int senderId) {
        List<Integer> receiverIds = messageRepository.findDistinctReceiverIdsBySenderId(senderId);
        List<Integer> senderIds = messageRepository.findDistinctSenderIdsByReceiverId(senderId);

        Set<Integer> combinedUserIds = new HashSet<>();
        combinedUserIds.addAll(receiverIds);
        combinedUserIds.addAll(senderIds);

        System.out.println("Chat user ids for user " + senderId + " are: " + combinedUserIds);

        return ResponseEntity.ok(new ArrayList<>(combinedUserIds));
    }

    @GetMapping("/chat/messages/profile/{recId}")
    public ResponseEntity<Map<String, Object>> getProfileDataofRec(@PathVariable int recId) {
        Optional<Profile> profileOptional = profileRepository.findById((long) recId);
        if (profileOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Profile profile = profileOptional.get();
        List<Images> images = imageRepository.findByProfile_Id((long) recId);

        String firstImageUrl = null;
        if (!images.isEmpty()) {
            firstImageUrl = images.get(0).getDownloadurl(); // Make sure getDownloadurl() is correct
        }


        System.out.println("id is " + profile.getId() + "full name is " + profile.getFullname() + "firstImage is " + firstImageUrl);
        Map<String, Object> response = new HashMap<>();
        response.put("id", profile.getId());
        response.put("fullname", profile.getFullname());
        response.put("firstImage", firstImageUrl);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            System.out.println("🔵 Received Sign up request for email: " + registerRequest.getEmail() + registerRequest.getPassword());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        System.out.println("🔵 Received login request for email: " + request.getEmail());

        try {
            // Authenticate the user (e.g., using email/password)
            AuthResponse response = authService.authenticate(request);
            System.out.println("✅ Login successful for: " + request.getEmail());

            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            if (!userOptional.isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found.");
                return ResponseEntity.status(404).body(errorResponse);
            }

            User user = userOptional.get();

            // Now find the profile using the user's email (or username, depends on your schema)
            Optional<Profile> profileOptional = profileRepository.findByUsername(user.getEmail());

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("token", response.getToken());

            if (profileOptional.isPresent()) {
                Profile profile = profileOptional.get();

                // Only now fetch images, after ensuring profile exists
                List<Images> imagesOptional = imageRepository.findByProfile_Id(profile.getId());

                responseBody.put("profileid", profile.getId().toString());

                if (!imagesOptional.isEmpty()) {
                    responseBody.put("profileStatus", "exists");
                    System.out.println("✅ Profile exists: ID = " + profile.getId());
                } else {
                    responseBody.put("profileStatus", "existsnoimage");
                    responseBody.put("profileName", profile.getFullname());
                    System.out.println("✅ Profile exists but no image: ID = " + profile.getId() + " and " + profile.getFullname());
                }
            } else {
                responseBody.put("profileid", "null");
                responseBody.put("profileStatus", "Profile doesn't exist");
                System.out.println("⚠️ No profile found for user: " + user.getUsername());
            }

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }




    // @GetMapping("/error")
    //public String handleError(){
    //  return "error";
    //   }

    @PostMapping("/profiles")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        try {
            // Debug: Check if we're authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication object: " + authentication);

            if (authentication != null && authentication.isAuthenticated()) {
                String currentUserEmail = authentication.getName();
                System.out.println("Authenticated user email: " + currentUserEmail);
            } else {
                System.out.println("No authentication found or not authenticated");
            }

            // Debug: Print profile details
            System.out.println("Attempting to save profile: " + profile.getFullname());

            // Save the profile
            Profile savedProfile = profileRepository.save(profile);
            System.out.println("Profile saved successfully with ID: " + savedProfile.getId());

            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            System.out.println("Error creating profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create profile: " + e.getMessage()));
        }
    }

    @GetMapping("/profiles/me")
    public ResponseEntity<Profile> getProfileByEmail(Authentication authentication) {
        String email = authentication.getName();  // Get the email from the JWT token
        System.out.println("Cunt2 "+ email);

        Optional<Profile> profileOptional = profileRepository.findByUsername(email);  // Assuming you have this query in your repository
        if (profileOptional.isPresent()) {
            System.out.println("Cunt2 Profile is present");
            return ResponseEntity.ok(profileOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Return 404 if the profile is not found
        }
    }

    @PostMapping("profiles/interests")
    public ResponseEntity<String> createInterest(@RequestBody Map<String, List<String>> requestBody, Authentication authentication) {
        try {
            // Extract the email from the authenticated user (JWT token)
            String email = authentication.getName();

            // Find the profile using the email
            Optional<Profile> profileOptional = profileRepository.findByUsername(email);

            if (profileOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Profile not found for this user");
            }

            Profile profile = profileOptional.get();

            // Create a new Interests object
            Interests interests = new Interests();
            interests.setProfile(profile);

            // Fix: Map the field names from Flutter to your backend names
            if (requestBody.containsKey("sports")) {
                interests.setSports(requestBody.get("sports"));
            }
            if (requestBody.containsKey("music")) {
                interests.setMusic(requestBody.get("music"));
            }
            if (requestBody.containsKey("movies")) {
                interests.setMovies(requestBody.get("movies"));
            }
            if (requestBody.containsKey("technology")) {
                interests.setTechnology(requestBody.get("technology"));
            }

            // Log the interests object to see the values before saving
            System.out.println("Interests to be saved: " + interests.getSportsInterests() + interests.getMusicInterests() + interests.getMoviesInterests());

            // Save the interest object to the repository
            interestsRepository.save(interests);

            return ResponseEntity.ok("Interests created successfully");
        } catch (Exception e) {
            // Log the exception for better debugging
            System.err.println("Error creating interests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating interests: " + e.getMessage());
        }
    }

    @GetMapping("/profiles/images/check")
    public ResponseEntity<List<String>> getImagesByProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Profile> profileOptional = profileRepository.findByUsername(email);
        if (profileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        Profile profile = profileOptional.get();
        Long id = profile.getId();

        List<Images> images = imageRepository.findByProfile_Id(id);

        List<String> imageUrls = images.stream()
                .map(Images::getDownloadurl)
                .collect(Collectors.toList());

        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/profiles/images/matches/{profileId}")
    public List<String> getSharedInterestProfileIds(@PathVariable Long profileId) {
        System.out.println("MATCHES CALLED ");
        List<Object[]> rawResults = interestsRepository.findSharedInterestsByProfileId(profileId);

        List<String> profileIds = new ArrayList<>();
        for (Object[] row : rawResults) {
            if (row[0] != null) {
                profileIds.add(row[0].toString());
                System.out.println("tag results are " + row[0].toString());
            }
        }

        return profileIds;
    }

    @GetMapping("/profiles/images/{profileId}")
    public ResponseEntity<?> getImageByProfileId(@PathVariable Long profileId) {
       // List<Images> image = imageRepository.findByProfile_Id(profileId);
        List<Images> images = imageRepository.findByProfile_Id(profileId);
        Optional<Profile> profile = profileRepository.findById(profileId);


        List<String> imageUrls = images.stream()
                .map(Images::getDownloadurl)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("images", imageUrls);
        response.put("profile", Map.of(
                "name", profile.get().getFullname(),
                "age", profile.get().getAge(),
                "bio", profile.get().getBio()
                // Add other fields as needed
        ));

        System.out.println("Response: " + response);
        return ResponseEntity.ok(response);
    }








    /*@GetMapping("/profiles/{id}")
    public ResponseEntity<Profile> getProfile(@PathVariable Long id) {
        Optional<Profile> profile = profileRepository.findById(id);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile.get());  // Return profile including the download URL
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }*/

    @PostMapping("/profiles/dislikes")
    public ResponseEntity<Void> handleDislike(@RequestBody Long dislikedProfileId) {
        System.out.println("Received a left swipe for profile ID: " + dislikedProfileId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("The username is: " + username);

        // Retrieve the profile associated with the username
        Optional<Profile> profileOptional = profileRepository.findByUsername(username);
        Optional<Profile> profile2 = profileRepository.findById(dislikedProfileId);
        Profile profile3 = profileOptional.get();
        Dislikes dislikes = new Dislikes();
        dislikes.setProfile(profile3);
        dislikes.setDisLiked(profile2.get());
        dislikesRepository.save(dislikes);

        return null;
    }




    @PostMapping("/profiles/likes")
    public ResponseEntity<?> addImagesToProfile(@RequestBody Long liked) {
        // Get the current authenticated user via JWT (username)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("The username is: " + username);

        // Retrieve the profile associated with the username
        Optional<Profile> profileOptional = profileRepository.findByUsername(username);
        Optional<Profile> profile2 = profileRepository.findById(liked);
        Profile profile3 = profileOptional.get();
        likes likes = new likes();
        likes.setProfile(profile3);
        likes.setLiked(profile2.get());
        likesRepository.save(likes);
        //Profile profile = profile2.get();
        //profileRepository.save(profile);

        //System.out.println("The liked is : " + profile2.get().getId() + " and their liker is " + profile2.get().getLiker());

        // Check if the profile exists
        if (!profileOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found for username: " + username);
        }
        return null;
    }

    @GetMapping("/profiles/whodisliked")
    public ResponseEntity<Map<String, List<Long>>> getDislikedBy(Authentication authentication) {
        System.out.println("This function ran");

        String email = authentication.getName();
        Optional<Profile> profileOptional = profileRepository.findByUsername(email);

        if (profileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("dislikedBy", List.of()));
        }

        Profile profile1 = profileOptional.get();

        List<Dislikes> dislikes = dislikesRepository.findByDisliked(profile1);
        List<Long> dislikerIds = dislikes.stream()
                .map(d -> d.getProfile().getId())  // Only extract profile ID
                .collect(Collectors.toList());

        Map<String, List<Long>> response = new HashMap<>();
        response.put("dislikedBy", dislikerIds);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/profiles/likesanddislikes")
    public ResponseEntity<Map<String, List<Long>>> getLikesAndDislikes(Authentication authentication) {
        String email = authentication.getName();
        Optional<Profile> profileOptional = profileRepository.findByUsername(email);

        if (profileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Profile profile = profileOptional.get();
        Long id = profile.getId();

        List<likes> likes = likesRepository.findByProfile_Id(id);
        List<Dislikes> dislikes = dislikesRepository.findByProfile_Id(id);

        List<Long> likedIds = likes.stream()
                .map(like -> like.getLiked().getId())
                .collect(Collectors.toList());

        List<Long> dislikedIds = dislikes.stream()
                .map(dislike -> dislike.getDisLiked().getId())
                .collect(Collectors.toList());

        Map<String, List<Long>> result = new HashMap<>();
        result.put("likes", likedIds);
        result.put("dislikes", dislikedIds);

        return ResponseEntity.ok(result);
    }


    @PostMapping("/profiles/getlikes")
    public ResponseEntity<?> getLikes(@RequestBody Long profileId) {
        Optional<Profile> profileOptional = profileRepository.findById(profileId);

        if (!profileOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found.");
        }

        Profile profile = profileOptional.get();

        // Get all likes where this profile is the one being liked
        List<likes> likes = likesRepository.findByliked(profile);

        // Get list of profiles who liked this profile
        List<Profile> likers = likes.stream()
                .map(like -> like.getProfile())
                .collect(Collectors.toList());

        // Create a response list of maps with profile and image URLs
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Profile liker : likers) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", liker.getId());
            entry.put("fullname", liker.getFullname());
            entry.put("age", liker.getAge());
            entry.put("bio", liker.getBio());

            // Get images for each liker
            List<Images> images = imageRepository.findByProfile_Id(liker.getId());
            List<String> imageUrls = images.stream()
                    .map(Images::getDownloadurl)
                    .collect(Collectors.toList());

            entry.put("images", imageUrls); // Can be an empty list

            responseList.add(entry);
        }

        return ResponseEntity.ok(responseList);
    }



    @PostMapping("/profiles/images")
    public ResponseEntity<?> addImagesToProfile(@RequestBody String imageUrlRaw) {
        // Get the current authenticated user via JWT (username)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("The username is: " + username);

        // Retrieve the profile associated with the username
        Optional<Profile> profileOptional = profileRepository.findByUsername(username);

        // Check if the profile exists
        if (!profileOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found for username: " + username);
        }

        Profile profile = profileOptional.get();

        try {
            // Use the raw string directly as the URL
            String imageUrl = imageUrlRaw.trim();
            System.out.println("Using image URL: " + imageUrl);

            Images image = new Images(profile, imageUrl);
            imageRepository.save(image);

            return ResponseEntity.status(HttpStatus.OK).body("Image uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image: " + e.getMessage());
        }
    }
}




    /*@GetMapping("/profile/check")
    public ResponseEntity<?> checkUserProfile(Authentication authentication) {
        // Get the logged-in user's email (or ID) from the authentication object
        String username = authentication.getName();  // or use authentication.getPrincipal()

        // Check if the user already has a profile
        Optional<Profile> profile = profileRepository.findByUsername(username);
        if (profile.isPresent()) {
            // Profile exists, return success status
            return ResponseEntity.ok(new ApiResponse(true, "User has a profile", profile.get()));
        } else {
            // Profile doesn't exist, return a different message
            // Profile doesn't exist, return a different message
            return ResponseEntity.ok(new ApiResponse(false, "User does not have a profile", null));
        }
    }*/

