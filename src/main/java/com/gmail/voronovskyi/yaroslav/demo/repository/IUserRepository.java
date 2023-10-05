package com.gmail.voronovskyi.yaroslav.demo.repository;

import com.gmail.voronovskyi.yaroslav.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    List<User> findByBirthDateBetweenOrderByBirthDateAsc(@RequestParam("from") LocalDate fromDate, @RequestParam("to") LocalDate toDate);
}
