package com.example.demo.service;

import com.example.demo.dto.jwt.JwtAuthenticationDTO;
import com.example.demo.dto.jwt.RefreshTokenDTO;
import com.example.demo.dto.user.UserCredentialsRequestDTO;
import com.example.demo.exception.LoginAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.model.UserRoleEnum;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SecurityService securityService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Value("${refresh-token.ttl}")
    private int refreshTokenTtl;

    // регистрация пользователя
    @Transactional
    public JwtAuthenticationDTO signUp(UserCredentialsRequestDTO userCredentialsRequestDTO) {

        if (!isEmailUnique(userCredentialsRequestDTO.getLogin())) {
            throw new LoginAlreadyExistsException("Login already exists");
        }

        User user = new User();

        user.setUserId(UUID.randomUUID());
        user.setLogin(userCredentialsRequestDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userCredentialsRequestDTO.getPassword()));

        user.setRole(UserRoleEnum.USER);

        userRepository.save(user);

        return jwtService.generateAuthToken(user.getUserId());
    }

    // авторизация пользователя
    @Transactional
    public JwtAuthenticationDTO signIn(UserCredentialsRequestDTO userCredentialsRequestDTO) throws AuthenticationException {

        User user = userRepository.findUserByLogin((userCredentialsRequestDTO.getLogin()))
                .orElseThrow(() -> new UserNotFoundException("User with email " + userCredentialsRequestDTO.getLogin() + " not found"));

        if (!passwordEncoder.matches(userCredentialsRequestDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid password");
        }

        return jwtService.generateAuthToken(user.getUserId());
    }

    // обновление пароля пользователя
//    @Transactional
//    public ChangePasswordResponseDTO changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
//
//        User user = userRepository.findUserByUserId(securityService.getUUIDFromSecurityContext())
//                .orElseThrow(() ->  new UserNotFoundException("User with id " + securityService.getUUIDFromSecurityContext() + " not found"));
//
//        String userPassword = user.getPassword();
//
//        if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(),  userPassword)) {
//            throw new PasswordChangeException("Old password is incorrect");
//        }
//
//        if (passwordEncoder.matches(changePasswordRequestDTO.getNewPassword(), userPassword)) {
//            throw new PasswordChangeException("New password must differ from the old one");
//        }
//
//        user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
//
//        return modelMapper.map(userRepository.save(user), ChangePasswordResponseDTO.class);
//
//    }

    // генерация access токена по refresh токену
    public JwtAuthenticationDTO refreshAccessToken(RefreshTokenDTO refreshTokenDTO) throws AuthenticationException {

        String refreshToken = refreshTokenDTO.getRefreshToken();

        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = userService.getUserByUUID(UUID.fromString(jwtService.getUserIdFromJwtToken(refreshToken)));
            return jwtService.refreshAccessToken(user.getUserId(), refreshToken);
        }

        throw new AuthenticationException("Invalid refresh token");
    }

    // проверка уникальности email
    private Boolean isEmailUnique(String email) {
        return !userRepository.existsUserByLogin(email);
    }

}
