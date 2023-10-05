package com.gmail.voronovskyi.yaroslav.demo.controller;

import com.gmail.voronovskyi.yaroslav.demo.controller.rest.dto.UserDto;
import com.gmail.voronovskyi.yaroslav.demo.model.User;
import com.gmail.voronovskyi.yaroslav.demo.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UsersRestControllerTest {

    private final static long TEST_USER_ID = 1L;
    private final static long MIN_VALID_AGE = 18L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Mock
    private IUserService userServiceMock;
    @Mock
    private ModelMapper modelMapperMock = new ModelMapper();
    @InjectMocks
    private UsersRestController usersRestController;

    @Test
    public void shouldReturnUserDtoDyoById() {
        Mockito.when(modelMapperMock.map(any(), any())).thenReturn(createTestUserDto());
        Mockito.when(userServiceMock.getUserBuId(TEST_USER_ID)).thenReturn(createTestUser());
        assertEquals(usersRestController.getUserById(TEST_USER_ID), createTestUserDto());
    }

    @Test
    public void shouldReturnUserDtosList() {
        Mockito.when(modelMapperMock.map(any(), any())).thenReturn(createTestUserDto());
        Mockito.when(userServiceMock.getAllUsers()).thenReturn(List.of(createTestUser()));
        assertEquals(usersRestController.getAllUsers(), List.of(createTestUserDto()));
    }

    @Test
    public void shouldRegisterNewUser() {
        Mockito.when(userServiceMock.registerUser(modelMapperMock.map(createTestUserDto(), User.class))).thenReturn(createTestUser());
        Mockito.when(modelMapperMock.map(createTestUser(), UserDto.class)).thenReturn(createTestUserDto());
        UserDto userDto = usersRestController.registerNewUser(createTestUserDto(), MIN_VALID_AGE);
        assertEquals(userDto, createTestUserDto());
    }

    @Test
    public void shouldUpdateUserInformation() {
        Mockito.when(modelMapperMock.map(any(), any())).thenReturn(createTestUser());
        UserDto userDto = createTestUserDto();
        userDto.setPhoneNumber("+380976714493");
        usersRestController.updateUser(TEST_USER_ID, userDto);
        Mockito.verify(userServiceMock).updateUser(createTestUser());
    }

    @Test
    public void shouldUpdateSomeUserFields() {
        Mockito.when(modelMapperMock.map(any(), any())).thenReturn(createTestUser());
        UserDto userDto = createTestUserDto();
        userDto.setAddress("Ukraine, Lviv");
        usersRestController.updateSomeUserField(TEST_USER_ID, userDto);
        Mockito.verify(userServiceMock).updateUser(createTestUser());
    }

    @Test
    public void shouldDeleteUserById() {
        usersRestController.deleteUser(TEST_USER_ID);
        Mockito.verify(userServiceMock).deleteUser(TEST_USER_ID);
    }

    private User createTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email("yaroslav.voronovskyi@gmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-1986", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+380976714492")
                .build();
    }

    private UserDto createTestUserDto() {
        return UserDto.builder()
                .id(TEST_USER_ID)
                .email("yaroslav.voronovskyi@gmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-1986", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+380976714492")
                .build();
    }
}
