package com.gmail.voronovskyi.yaroslav.demo.service;

import com.gmail.voronovskyi.yaroslav.demo.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IUserService {

    User getUserBuId(long userId);
    List<User> getAllUsers();
    User registerUser(User user);
    User updateUser(User user);
    void deleteUser(long userId);
    List<User> findUsersByBirthDate(LocalDate fromDate, LocalDate toDate);
}
