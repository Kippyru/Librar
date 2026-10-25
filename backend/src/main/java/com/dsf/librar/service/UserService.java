package com.dsf.librar.service;

import com.dsf.librar.dto.request.RegisterRequestDto;
import com.dsf.librar.dto.request.UserRequestDto;
import com.dsf.librar.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    //needed to add this method
    void registerUser(RegisterRequestDto request);
    void createUser(UserRequestDto userRequestDto);
    List<UserResponseDto> listUser();
    UserResponseDto listUserId(Long id);
    UserResponseDto editUser(Long id, UserRequestDto userRequestDto);
    void restoreUser(Long id);
    void deleteUser(Long id);
    UserResponseDto getMe();
}

