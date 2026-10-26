package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.dao.TodoItemRepository;
import com.pelinhangisi.springboottodoapp.dao.UserRepository;
import com.pelinhangisi.springboottodoapp.models.TodoItem;
import com.pelinhangisi.springboottodoapp.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;



@Controller
@RequiredArgsConstructor
public class MainController {

    private final TodoItemRepository todoItemRepository;
    private final UserRepository userRepository;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/tasklist")
    public String tasklist(Model model, Authentication authentication){
        User user = null;
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = userRepository.findByEmail(authentication.getName());
        }
        model.addAttribute("tasklist", user != null ? user.getTodoItems() : List.of());
        return "tasklist";
    }

    @PostMapping("/add")
    public String addTodoItem(@RequestParam String description, Authentication authentication){
        User user = userRepository.findByEmail(authentication.getName());
        TodoItem todoItem = new TodoItem();
        todoItem.setDescription(description);
        todoItemRepository.save(todoItem);
        user.getTodoItems().add(todoItem);
        userRepository.save(user);
        return "redirect:/tasklist";
    }

    @GetMapping("/edit/{id}")
    public String editTodoItem(@PathVariable Long id, Authentication authentication){
        requireOwnsTodoItem(id, authentication);
        TodoItem todoItem = todoItemRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        todoItem.setCompleted(!todoItem.getCompleted());
        todoItemRepository.save(todoItem);
        return "redirect:/tasklist";
    }

    @GetMapping("/delete/{id}")
    public String deleteTodoItem(@PathVariable Long id, Authentication authentication){
        requireOwnsTodoItem(id, authentication);
        TodoItem todoItem = todoItemRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        for (User user : userRepository.findAll()) {
            if (user.getTodoItems().remove(todoItem)) {
                userRepository.save(user);
            }
        }
        todoItemRepository.delete(todoItem);
        return "redirect:/tasklist";
    }

    private void requireOwnsTodoItem(Long todoItemId, Authentication authentication){
        User currentUser = userRepository.findByEmail(authentication.getName());
        if (currentUser == null) {
            throw new NoSuchElementException();
        }
        User owner = userRepository.findByTodoItemsId(todoItemId).orElseThrow(() -> new NoSuchElementException());
        if (!owner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Cannot access another user's todo item");
        }
    }

}
