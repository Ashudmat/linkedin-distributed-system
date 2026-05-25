package com.codingshuttle.linkedin.user_service.security;

import com.codingshuttle.linkedin.user_service.dto.LoginRequestDto;
import com.codingshuttle.linkedin.user_service.dto.LoginResponseDto;
import com.codingshuttle.linkedin.user_service.dto.SignUpRequestDto;
import com.codingshuttle.linkedin.user_service.dto.UserResponseDto;
import com.codingshuttle.linkedin.user_service.entity.User;
import com.codingshuttle.linkedin.user_service.event.UserCreatedEvent;
import com.codingshuttle.linkedin.user_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.user_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.user_service.repository.UserRepository;
import com.codingshuttle.linkedin.user_service.util.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoderUtil passwordEncoderUtil;
    private final JWTService jwtService;
    private final KafkaTemplate<String, UserCreatedEvent> userCreatedEventKafkaTemplate;

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

}
