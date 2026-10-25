package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.RegisterRequestDto;
import com.dsf.librar.dto.request.UserRequestDto;
import com.dsf.librar.dto.response.UserResponseDto;
import com.dsf.librar.entity.Role;
import com.dsf.librar.entity.Sucursal;
import com.dsf.librar.entity.User;
import com.dsf.librar.mapper.UserMapper;
import com.dsf.librar.repository.RoleRepository;
import com.dsf.librar.repository.SucursalRepository;
import com.dsf.librar.repository.UserRepository;
import com.dsf.librar.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SucursalRepository sucursalRepository;

    @Override
    public void createUser(UserRequestDto userRequestDto) {
        Role role = roleRepository.findById(userRequestDto.role())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        User user = userMapper.toEntity(userRequestDto);
        //step over the plain text password with the encrypted version of the password
        user.setPassword(passwordEncoder.encode(userRequestDto.password()));

        user.setRole(role);

        if (userRequestDto.sucursal() != null) {
            Sucursal sucursal = sucursalRepository.findById(userRequestDto.sucursal())
                    .orElseThrow(() -> new RuntimeException("Sucursal not found"));

            user.setSucursal(sucursal);
        } else {
            user.setSucursal(null);
        }

        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public void registerUser(RegisterRequestDto request) {
        if (userRepository.existsByRoleName("ROLE_ADMIN")) {
            throw new RuntimeException("Acceso denegado: El administrador principal del sistema ya ha sido configurado.");
        }

        // when there is no Admin, this will create it
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setCompleteName(request.getCompleteName());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setActive(true);

        // we apply the admin role to the new user
        Role assignedRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_ADMIN no encontrado en la BD"));

        newUser.setRole(assignedRole);
        userRepository.save(newUser);
    }

    @Override
    public List<UserResponseDto> listUser() {
        return userMapper.listUser(userRepository.findAll());
    }

    @Override
    public UserResponseDto listUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getMe() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto editUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(userRequestDto, user);
        if (userRequestDto.password() != null && !userRequestDto.password().isBlank()) {
            // this line would make the passwords plain text instead of saving them encrypted
            // user.setPassword(userRequestDto.getPassword());

            //so now we encode the password before saving it in the DB
            user.setPassword(passwordEncoder.encode(userRequestDto.password()));
        }
        if (userRequestDto.role() != null) {
            Role role = roleRepository.findById(userRequestDto.role())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void restoreUser(Long id) {
        userRepository.restoreById(id);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }
}
