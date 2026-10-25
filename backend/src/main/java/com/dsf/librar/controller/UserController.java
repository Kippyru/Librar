package com.dsf.librar.controller;

import com.dsf.librar.dto.request.UserRequestDto;
import com.dsf.librar.dto.response.UserResponseDto;
import com.dsf.librar.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        userService.createUser(userRequestDto);
        return ResponseEntity.ok("User successfully created");
    }

    @GetMapping("/list")
    public List<UserResponseDto> listUsers() {
        return userService.listUser();
    }

    @GetMapping("/list/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.listUserId(id);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @Valid @RequestBody UserRequestDto userRequestDto) {
        userService.editUser(id, userRequestDto);
        return ResponseEntity.ok("User successfully updated");
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok("User successfully restored");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User successfully deleted");
    }
}
