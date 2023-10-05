package com.gmail.voronovskyi.yaroslav.demo.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = " can not be null or empty")
    private String firstName;

    @NotBlank(message = " can not be null or empty")
    private String lastName;

    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

    @NotBlank(message = " can not be null or empty")
    private String address;

    @NotBlank(message = "can not be null or empty")
    private String phoneNumber;
}
