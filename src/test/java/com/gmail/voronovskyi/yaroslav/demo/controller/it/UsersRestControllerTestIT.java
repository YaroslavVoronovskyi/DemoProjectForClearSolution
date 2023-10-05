package com.gmail.voronovskyi.yaroslav.demo.controller.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.voronovskyi.yaroslav.demo.config.AppConfigTest;
import com.gmail.voronovskyi.yaroslav.demo.controller.rest.dto.UserDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {AppConfigTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsersRestControllerTestIT {

    private final static long TEST_USER_ID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @LocalServerPort
    private Integer port;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    @Order(1)
    @Sql(scripts = {"/ScriptInsertUsersNew.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void shouldReturnExpectedUserDtosList() throws JsonProcessingException {
        String response = restTemplate.getForObject("http://localhost:" + port + "/users/", String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<UserDto> userDtosList = mapper.readerForListOf(UserDto.class).readValue(response);
        assertEquals(userDtosList.size(), 5);
        assertEquals(userDtosList.get(0), createTestUserDto());
        assertEquals(userDtosList.get(0).getId(), TEST_USER_ID);
        assertEquals(userDtosList.get(0).getEmail(), "yaroslav.voronovskyi@gmail.com");
        assertEquals(userDtosList.get(0).getFirstName(), "Yaroslav");
        assertEquals(userDtosList.get(0).getLastName(), "Voronovskyi");
        assertEquals(userDtosList.get(0).getBirthDate(), LocalDate.parse("11-11-1986", DATE_FORMAT));
        assertEquals(userDtosList.get(0).getAddress(), "Ukraine, Kyiv");
        assertEquals(userDtosList.get(0).getPhoneNumber(), "+380976714492");
    }

    @Test
    @Order(2)
    void shouldReturnExpectedUserDtoById() {
        UserDto userDto = restTemplate
                .getForObject("http://localhost:" + port + "/users/1", UserDto.class);
        assertEquals(userDto, createTestUserDto());
        assertNotNull(userDto);
        assertEquals(userDto.getId(), TEST_USER_ID);
        assertEquals(userDto.getEmail(), "yaroslav.voronovskyi@gmail.com");
        assertEquals(userDto.getFirstName(), "Yaroslav");
        assertEquals(userDto.getLastName(), "Voronovskyi");
        assertEquals(userDto.getBirthDate(), LocalDate.parse("11-11-1986", DATE_FORMAT));
        assertEquals(userDto.getAddress(), "Ukraine, Kyiv");
        assertEquals(userDto.getPhoneNumber(), "+380976714492");
    }

    @Test
    @Order(3)
    public void shouldRegisterNewUserDto() throws JsonProcessingException {
        UserDto userDto = createTestUserDtoWithOutId();
        restTemplate.postForEntity("http://localhost:" + port + "/users/", userDto, UserDto.class);
        String response = restTemplate.getForObject("http://localhost:" + port + "/users/", String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<UserDto> userDtosList = mapper.readerForListOf(UserDto.class).readValue(response);
        UserDto userDtoExpected = restTemplate.getForObject("http://localhost:" + port + "/users/6", UserDto.class);
        assertEquals(userDtosList.size(), 6);
        assertNotNull(userDtoExpected);
        assertEquals(userDtoExpected.getId(), 6L);
        assertEquals(userDtoExpected.getEmail(), "yaroslav.voronovskyi-aws@gmail.com");
    }

    @Test
    @Order(4)
    public void shouldUpdateExpectedUserDto() {
        UserDto userDto = restTemplate.getForObject("http://localhost:" + port + "/users/1", UserDto.class);
        userDto.setAddress("Ukraine, Lviv");
        restTemplate.put("http://localhost:" + port + "/users/1", userDto);
        UserDto userDtoExpected = restTemplate.getForObject("http://localhost:" + port + "/users/1", UserDto.class);
        assertNotNull(userDtoExpected);
        assertEquals(userDtoExpected.getId(), 1L);
        assertEquals(userDtoExpected.getAddress(), "Ukraine, Lviv");
    }

    @Test
    @Order(5)
    public void shouldDeleteExpectedUserDtoById() throws JsonProcessingException {
        restTemplate.delete("http://localhost:" + port + "/users/6");
        String response = restTemplate.getForObject("http://localhost:" + port + "/users/", String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<UserDto> userDtosList = mapper.readerForListOf(UserDto.class).readValue(response);
        assertEquals(userDtosList.size(), 5);
    }

    @Test
    @Order(6)
    public void shouldReturnExpectedUserDtosListByBirthDate() throws JsonProcessingException {
        String response = restTemplate.getForObject("http://localhost:" + port + "/users/search/?from=21-09-1986&to=21-09-1988", String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<UserDto> userDtosList = mapper.readerForListOf(UserDto.class).readValue(response);
        assertEquals(userDtosList.size(), 3);
        assertEquals(userDtosList.get(2).getId(), 2);
        assertEquals(userDtosList.get(2).getEmail(), "kseniya.bobyl@gmail.com");
        assertEquals(userDtosList.get(2).getFirstName(), "Kseniya");
        assertEquals(userDtosList.get(2).getLastName(), "Bobyl");
        assertEquals(userDtosList.get(2).getBirthDate(), LocalDate.parse("10-08-1988", DATE_FORMAT));
        assertEquals(userDtosList.get(2).getAddress(), "Ukraine, Lviv");
        assertEquals(userDtosList.get(2).getPhoneNumber(), "+380976714423");
    }

    @Test
    @Order(7)
    public void shouldThrowExceptionWhenTryGetExpectedUserDto() {
        assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject("http://localhost:" + port + "/users/100", UserDto.class));
    }

    @Test
    @Order(8)
    public void shouldThrowExceptionWhenRegisterNewUserDtoNotValidAge() {
        UserDto userDto = createTestUserDtoWithNotValidAge();
        assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.postForObject("http://localhost:" + port + "/users/", userDto, UserDto.class);
        });
    }

    @Test
    @Order(9)
    public void shouldThrowExceptionWhenRegisterNewUserDtoNotValidEmail() {
        UserDto userDto = createTestUserDtoWithNotValidEmail();
        assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.postForObject("http://localhost:" + port + "/users/", userDto, UserDto.class);
        });
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

    private UserDto createTestUserDtoWithOutId() {
        return UserDto.builder()
                .email("yaroslav.voronovskyi-aws@gmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2000", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+380976714493")
                .build();
    }

    private UserDto createTestUserDtoWithNotValidAge() {
        return UserDto.builder()
                .email("yaroslav.voronovskyi-aws-age@gmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2012", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+3809767f4493")
                .build();
    }

    private UserDto createTestUserDtoWithNotValidEmail() {
        return UserDto.builder()
                .email("yaroslav.voronovskyi-aws-aegmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2002", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+3809767f4493")
                .build();
    }
}
