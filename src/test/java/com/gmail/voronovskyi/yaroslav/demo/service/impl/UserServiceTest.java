package com.gmail.voronovskyi.yaroslav.demo.service.impl;

import com.gmail.voronovskyi.yaroslav.demo.model.User;
import com.gmail.voronovskyi.yaroslav.demo.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    private final static long TEST_USER_ID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final static LocalDate TEST_DATE_FROM = LocalDate.parse("01-01-1980", DATE_FORMAT);
    private final static LocalDate TEST_DATE_TO = LocalDate.parse("01-01-2000", DATE_FORMAT);

    @Mock
    private IUserRepository userRepositoryMock;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldReturnUserById() {
        Mockito.when(userRepositoryMock.getReferenceById(TEST_USER_ID)).thenReturn(createTestUser());
        assertEquals(userService.getUserBuId(TEST_USER_ID), createTestUser());
    }

    @Test
    public void shouldReturnUsersList() {
        Mockito.when(userRepositoryMock.findAll()).thenReturn(List.of(createTestUser()));
        assertEquals(userService.getAllUsers(), List.of(createTestUser()));
    }

    @Test
    public void shouldRegisterNewUser() {
        User user = createTestUser();
        userService.registerUser(user);
        Mockito.verify(userRepositoryMock).save(user);
    }

    @Test
    public void shouldUpdateUserInformation() {
        Mockito.when(userRepositoryMock.getReferenceById(TEST_USER_ID)).thenReturn(createTestUser());
        User user = userService.getUserBuId(TEST_USER_ID);
        user.setPhoneNumber("+380976714493");
        userService.updateUser(user);
        Mockito.verify(userRepositoryMock).save(user);
    }

    @Test
    public void shouldDeleteUserById() {
        userService.deleteUser(TEST_USER_ID);
        Mockito.verify(userRepositoryMock).deleteById(TEST_USER_ID);
    }

    @Test
    public void shouldReturnUsersListByBirthDate() {
        Mockito.when(userRepositoryMock.findByBirthDateBetweenOrderByBirthDateAsc(TEST_DATE_FROM, TEST_DATE_TO))
                .thenReturn(createTestUsersList());
        assertEquals(userService.findUsersByBirthDate(TEST_DATE_FROM, TEST_DATE_TO), createTestUsersList());
    }

    @Test
    public void shouldThrowExceptionWhenTryGetUserById() {
        Mockito.when(userRepositoryMock.getReferenceById(TEST_USER_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userService.getUserBuId(TEST_USER_ID));
    }

    @Test
    public void shouldThrowExceptionWhenTryGetUserList() {
        Mockito.when(userRepositoryMock.findAll()).thenReturn(List.of());
        assertThrows(EntityNotFoundException.class, () -> userService.getAllUsers());
    }

    @Test
    public void shouldThrowExceptionWhenTryGetUserListByBirthDate() {
        Mockito.when(userRepositoryMock.findByBirthDateBetweenOrderByBirthDateAsc(TEST_DATE_FROM, TEST_DATE_TO)).thenReturn(List.of());
        assertThrows(EntityNotFoundException.class, () -> userService.findUsersByBirthDate(TEST_DATE_FROM, TEST_DATE_TO));
    }

//    @Test
//    public void shouldThrowExceptionWhenTryDeleteUserById() {
//        assertThrows(EntityNotFoundException.class, () -> {
//            doThrow().when(userRepositoryMock).deleteById(isA(Long.class));
//        });
//    }

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

    private List<User> createTestUsersList() {
        return List.of(User.builder()
                        .id(TEST_USER_ID)
                        .email("yaroslav.voronovskyi@gmail.com")
                        .firstName("Yaroslav")
                        .lastName("Voronovskyi")
                        .birthDate(LocalDate.parse("11-11-1986", DATE_FORMAT))
                        .address("Ukraine, Kyiv")
                        .phoneNumber("+380976714492")
                        .build(),
                User.builder()
                        .id(2L)
                        .email("yaroslav.voronovskyi@gmail.com")
                        .firstName("Yaroslav")
                        .lastName("Voronovskyi")
                        .birthDate(LocalDate.parse("11-11-1999", DATE_FORMAT))
                        .address("Ukraine, Kyiv")
                        .phoneNumber("+380976714492")
                        .build());
    }
}
