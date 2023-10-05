package com.gmail.voronovskyi.yaroslav.demo.controller.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gmail.voronovskyi.yaroslav.demo.config.AppConfigTest;
import com.gmail.voronovskyi.yaroslav.demo.controller.rest.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfigTest.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:test.properties")
public class UsersRestControllerTest {

    private final static long TEST_USER_ID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    private WebApplicationContext appContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.appContext).build();
    }

    @Test
    @Order(1)
    @Sql(scripts = {"/ScriptInsertUsersNew.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void shouldReturnExpectedUserDtosList() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
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
    public void shouldReturnExpectedUserDtoById() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        UserDto userDto = mapper.readValue(response, UserDto.class);
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
    public void shouldRegisterNewUserDto() throws Exception {
        UserDto userDto = createTestUserDtoWithOutId();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/6")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        UserDto userDtoExpected = mapper.readValue(response, UserDto.class);
        assertEquals(userDtoExpected.getId(), 6);
        assertEquals(userDtoExpected.getEmail(), "yaroslav.voronovskyi-aws@gmail.com");
        assertEquals(userDtoExpected.getFirstName(), "Yaroslav");
        assertEquals(userDtoExpected.getLastName(), "Voronovskyi");
        assertEquals(userDtoExpected.getBirthDate(), LocalDate.parse("11-11-2000", DATE_FORMAT));
        assertEquals(userDtoExpected.getAddress(), "Ukraine, Kyiv");
        assertEquals(userDtoExpected.getPhoneNumber(), "+380976714499");
    }

    @Test
    @Order(4)
    public void shouldUpdateExpectedUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("yaroslav.voronovskyi@gmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2000", DATE_FORMAT))
                .address("Ukraine, Lviv")
                .phoneNumber("+380976714792")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(userDto);
        String response = mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        UserDto userDtoExpected = mapper.readValue(response, UserDto.class);
        assertEquals(userDtoExpected.getId(), TEST_USER_ID);
        assertEquals(userDtoExpected.getAddress(), "Ukraine, Lviv");
    }

    @Test
    @Order(5)
    public void shouldUpdateExpectedSomeUserDtoField() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("yaroslav.voronovskyigmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2000", DATE_FORMAT))
                .address("Ukraine, Lviv")
                .phoneNumber("+380976714792")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(userDto);
        String response = mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        UserDto userDtoExpected = mapper.readValue(response, UserDto.class);
        assertEquals(userDtoExpected.getId(), TEST_USER_ID);
        assertEquals(userDtoExpected.getAddress(), "Ukraine, Lviv");
    }

    @Test
    @Order(6)
    public void shouldDeleteExpectedUserDtoById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/5")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<UserDto> userDtosList = mapper.readerForListOf(UserDto.class).readValue(response);
        assertEquals(userDtosList.size(), 5);
    }

    @Test
    @Order(7)
    public void shouldReturnExpectedUserDtosListByBirthDate() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/users/search/?from=21-09-1986&to=21-09-1988")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<UserDto> userDtosList = mapper.readerForListOf(UserDto.class).readValue(response);
        assertEquals(userDtosList.size(), 2);
        assertEquals(userDtosList.get(1).getId(), 2);
        assertEquals(userDtosList.get(1).getEmail(), "kseniya.bobyl@gmail.com");
        assertEquals(userDtosList.get(1).getFirstName(), "Kseniya");
        assertEquals(userDtosList.get(1).getLastName(), "Bobyl");
        assertEquals(userDtosList.get(1).getBirthDate(), LocalDate.parse("10-08-1988", DATE_FORMAT));
        assertEquals(userDtosList.get(1).getAddress(), "Ukraine, Lviv");
    }

    @Test
    @Order(8)
    public void shouldThrowExceptionWhenTryGetExpectedUserDto() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/11")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(9)
    public void shouldThrowExceptionWhenRegisterNewUserDtoNotValidAge() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("yaroslav.voronovskyi@gmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2012", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+380976714792")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isBadRequest());
    }

    @Test
    @Order(10)
    public void shouldThrowExceptionWhenRegisterNewUserDtoNotValidEmail() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("yaroslav.voronovskyigmail.com")
                .firstName("Yaroslav")
                .lastName("Voronovskyi")
                .birthDate(LocalDate.parse("11-11-2000", DATE_FORMAT))
                .address("Ukraine, Kyiv")
                .phoneNumber("+380976714792")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isBadRequest());
    }

    @Test
    @Order(11)
    public void shouldThrowExceptionWhenTryUpdateExpectedUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("yaroslav.voronovskyi@gmail.com")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isBadRequest());
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
                .phoneNumber("+380976714499")
                .build();
    }
}
