package pl.kurs.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.User;
import pl.kurs.exception.UserNotFoundException;
import pl.kurs.mapper.UserMapper;
import pl.kurs.service.UserService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") @Min(value = 1, message = "ID must be at least 1") Long id) {
        User user = userService.getUserByIdWithRoles(id);
        return ResponseEntity.ok(userMapper.entityToDto(user));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Validated(Create.class) CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") @Min(value = 1, message = "ID must be at least 1")
            Long id, @RequestBody @Validated(Update.class) CreateUserDto dto) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") @Min(value = 1, message = "ID must be at least 1") Long id) {
        userService.deleteUserById(id);
    }

}
