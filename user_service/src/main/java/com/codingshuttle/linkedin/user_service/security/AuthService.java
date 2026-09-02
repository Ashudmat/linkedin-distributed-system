package com.codingshuttle.linkedin.user_service.security;

import com.codingshuttle.linkedin.user_service.advice.ApiResponse;
import com.codingshuttle.linkedin.user_service.client.UploaderServiceClient;
import com.codingshuttle.linkedin.user_service.dto.*;
import com.codingshuttle.linkedin.user_service.entity.User;
import com.codingshuttle.linkedin.user_service.event.ProfileImageUpdatedEvent;
import com.codingshuttle.linkedin.user_service.event.UserCreatedEvent;
import com.codingshuttle.linkedin.user_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.user_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.user_service.repository.UserRepository;
import com.codingshuttle.linkedin.user_service.util.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoderUtil passwordEncoderUtil;
    private final JWTService jwtService;
    private final KafkaTemplate<String, UserCreatedEvent> userCreatedEventKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;
    private final KafkaTemplate<String, ProfileImageUpdatedEvent> profileImageUpdatedKafkaTemplate;

    public UserResponseDto signup(SignUpRequestDto signUpRequestDto) {
        log.info("Processing signup request");
        if (userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new BadRequestException("An account with this email already exists");
        }
        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setPassword(passwordEncoderUtil.hash(signUpRequestDto.getPassword()));
        User savedUser = userRepository.save(newUser);
        log.info("Signup successful");

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .id(savedUser.getId())
                .build();
        userCreatedEventKafkaTemplate.send("user_created_topic",userCreatedEvent);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Processing login request");
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException("No account found with this email"));
        boolean isPasswordMatch = BCrypt.checkpw(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordMatch){
            throw new BadRequestException("Invalid email or password");
        }
        log.info("Login successful. Access token generated");
        return new LoginResponseDto(jwtService.generateAccessToken(user));
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
    }

    public String uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
        try {
            ResponseEntity<ApiResponse<UploadResponseDto>> response = uploaderServiceClient.uploadFile(file);
            ApiResponse<UploadResponseDto> apiResponse = response.getBody();
            String imageUrl = apiResponse.getData().getFileUrl();

            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);

            profileImageUpdatedKafkaTemplate.send("profile_image_updated_topic",
                    ProfileImageUpdatedEvent.builder()
                            .userId(user.getId())
                            .profileImageUrl(imageUrl)
                            .build()
            );
            return imageUrl;
        } catch (Exception e) {
            log.error("Failed to upload profile image for userId={}", userId, e);
            throw new RuntimeException(e);
        }
    }

    public UserResponseDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto updateProfile(Long userId, UpdateProfileRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        user.setName(requestDto.getName());
        user.setHeadline(requestDto.getHeadline());
        user.setAbout(requestDto.getAbout());
        user.setLocation(requestDto.getLocation());
        user.setSkills(requestDto.getSkills());
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDto.class);
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserResponseDto.class);
    }

    public void deleteProfilePhoto(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setProfileImageUrl(null);
        userRepository.save(user);
    }
}
