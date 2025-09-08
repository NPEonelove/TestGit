package com.example.demo.service;

import com.example.demo.dto.jwt.GetJwtUserClaimsResponseDTO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // получение айди и роли для генерации jwt токенов
    public GetJwtUserClaimsResponseDTO getJwtUserClaims(UUID userId) {
        return modelMapper.map(getUserByUUID(userId), GetJwtUserClaimsResponseDTO.class);
    }

    // получение юзера по UUID
    public User getUserByUUID(UUID userId) {
        return userRepository.findUserByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId.toString() + " not found"));
    }

}
