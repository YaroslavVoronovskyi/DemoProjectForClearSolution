package com.gmail.voronovskyi.yaroslav.demo.controller;

import com.gmail.voronovskyi.yaroslav.demo.Utils;
import com.gmail.voronovskyi.yaroslav.demo.controller.exception.NotValidAgeException;
import com.gmail.voronovskyi.yaroslav.demo.model.User;
import com.gmail.voronovskyi.yaroslav.demo.service.IUserService;
import com.gmail.voronovskyi.yaroslav.demo.controller.rest.dto.UserDto;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersRestController.class);
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UsersRestController(IUserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public UserDto getUserById(@PathVariable("id") long userId) {
        LOGGER.debug("Try get user wih id {}", userId);
        User user = userService.getUserBuId(userId);
        LOGGER.debug("Car wih id {} was successfully got", userId);
        return convertToDto(user);
    }

    @GetMapping()
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getAllUsers() {
        LOGGER.debug("Try get all users");
        List<UserDto> userDtosList = convertToDtoList(userService.getAllUsers());
        LOGGER.debug("All users was successfully got");
        return userDtosList;
    }

    @PostMapping()
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public UserDto registerNewUser(@RequestBody @Valid UserDto userDto, @Value("${application.min.age}") long minAge) {
        LOGGER.debug("Try register new user");
        if (!Utils.isValidEmailAddress(userDto.getEmail())) {
            throw new IllegalArgumentException("Wrong e-mail address!");
        }
        if (LocalDate.from(userDto.getBirthDate()).until(LocalDate.now(), ChronoUnit.YEARS) < minAge) {
            throw new NotValidAgeException("Age not valid, user must be older than 18 years");
        }
        LOGGER.debug("New user was registered");
        return convertToDto(userService.registerUser(convertToEntity(userDto)));
    }

    @PutMapping("/{id}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public UserDto updateUser(@PathVariable("id") long userId, @RequestBody @Valid UserDto userDto) {
        LOGGER.debug("Try update user wih id {}", userId);
        userDto.setId(userId);
        userService.updateUser(convertToEntity(userDto));
        LOGGER.debug("User was updated wih id {}", userId);
        return userDto;
    }

    @PatchMapping("/{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public UserDto updateSomeUserField(@PathVariable("id") long userId, @RequestBody UserDto userDto) {
        LOGGER.debug("Try update user wih id {}", userId);
        userDto.setId(userId);
        userService.updateUser(convertToEntity(userDto));
        LOGGER.debug("User was updated wih id {}", userId);
        return userDto;
    }

    @DeleteMapping("/{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable("id") long userId) {
        LOGGER.debug("Try delete user wih id {}", userId);
        userService.deleteUser(userId);
        LOGGER.debug("User was deleted wih id {}", userId);
    }

    @GetMapping("/search")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> findUserByBirthDate(@RequestParam("from") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                             @RequestParam("to") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {
        LOGGER.debug("Try get users by birth date range");
        List<UserDto> userDtosList = convertToDtoList(userService.findUsersByBirthDate(fromDate, toDate));
        LOGGER.debug("Users by birth date range was successfully got");
        return userDtosList;
    }

    private UserDto convertToDto(User user) {
        try {
            return modelMapper.map(user, UserDto.class);
        } catch (MappingException exception) {
            throw new EntityNotFoundException("User does not exist or has been deleted");
        }
    }

    private User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private List<UserDto> convertToDtoList(List<User> usersList) {
        return usersList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
