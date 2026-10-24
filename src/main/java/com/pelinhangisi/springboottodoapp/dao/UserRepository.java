package com.pelinhangisi.springboottodoapp.dao;

import com.pelinhangisi.springboottodoapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    @Query("select u from User u join u.todoItems t where t.id = :todoItemId")
    Optional<User> findByTodoItemsId(@Param("todoItemId") Long todoItemId);
}
