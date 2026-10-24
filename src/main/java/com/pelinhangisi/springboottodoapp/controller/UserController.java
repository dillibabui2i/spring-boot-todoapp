package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.dao.TodoItemRepository;
import com.pelinhangisi.springboottodoapp.dao.UserRepository;
import com.pelinhangisi.springboottodoapp.models.TodoItem;
import com.pelinhangisi.springboottodoapp.models.User;
import com.pelinhangisi.springboottodoapp.request.AddTodoItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TodoItemRepository todoItemRepository;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("User not found: " + userId));
    }

    @PostMapping("/{userId}/todos")
    public void addTodoItem(@PathVariable Long userId, @RequestBody AddTodoItemRequest todoItemRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        TodoItem todoItem = new TodoItem();
        todoItem.setDescription(todoItemRequest.getDescription());
        user.getTodoItems().add(todoItem);
        todoItemRepository.save(todoItem);
        userRepository.save(user);
    }

    @PostMapping("/todos/{todoItemId}")
    public void toggleTodoItemCompleted(@PathVariable Long todoItemId){
        TodoItem todoItem = todoItemRepository.findById(todoItemId).orElseThrow(() -> new NoSuchElementException("Todo item not found: " + todoItemId));
        todoItem.setCompleted(!todoItem.getCompleted());
        todoItemRepository.save(todoItem);
    }

    @DeleteMapping("{userId}/todos/{todoItemId}")
    public void deleteTodo(@PathVariable Long userId,@PathVariable Long todoItemId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        TodoItem todoItem = todoItemRepository.findById(todoItemId).orElseThrow(() -> new NoSuchElementException("Todo item not found: " + todoItemId));
        user.getTodoItems().remove(todoItem);
        todoItemRepository.delete(todoItem);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        userRepository.delete(user);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoSuchElementException ex){
        return ex.getMessage();
    }

}
