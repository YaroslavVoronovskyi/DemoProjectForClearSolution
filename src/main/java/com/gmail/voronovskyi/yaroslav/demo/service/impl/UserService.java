package com.gmail.voronovskyi.yaroslav.demo.service.impl;

import com.gmail.voronovskyi.yaroslav.demo.model.User;
import com.gmail.voronovskyi.yaroslav.demo.repository.IUserRepository;
import com.gmail.voronovskyi.yaroslav.demo.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserBuId(long userId) {
        LOGGER.debug("Try get user wih id {} from DB", userId);
        return userRepository.getReferenceById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        LOGGER.debug("Try get all users from DB");
        List<User> usersList = userRepository.findAll();
        if (usersList.isEmpty()) {
            throw new EntityNotFoundException("Users not fount!");
        }
        LOGGER.debug("All users was successfully got from DB");
        return usersList;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        LOGGER.debug("Try register new user and save in DB");
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        LOGGER.debug("Try update user wih id {} from DB", user.getId());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        LOGGER.debug("Try delete user wih id {} from DB", userId);
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException("User with id " + userId + " does not exist or has been deleted");
        }
        LOGGER.debug("User wih id {} was successfully deleted from DB", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByBirthDate(LocalDate fromDate, LocalDate toDate) {
        LOGGER.debug("Try get users by birth date range from DB");
        List<User> usersList = userRepository.findByBirthDateBetweenOrderByBirthDateAsc(fromDate, toDate);
        if (usersList.isEmpty()) {
            throw new EntityNotFoundException("Users not fount!");
        }
        LOGGER.debug("Users by birth date range was successfully got from DB");
        return usersList;
    }
}
