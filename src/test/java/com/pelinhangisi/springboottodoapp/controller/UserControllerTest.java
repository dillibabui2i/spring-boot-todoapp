package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.dao.TodoItemRepository;
import com.pelinhangisi.springboottodoapp.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoItemRepository todoItemRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userRepository, todoItemRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getUserById_withMissingUser_returnsNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: 99"));
    }

    @Test
    void deleteUser_withMissingUser_returnsNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: 99"));
    }

    @Test
    void toggleTodoItemCompleted_withMissingTodoItem_returnsNotFound() throws Exception {
        when(todoItemRepository.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/todos/{todoItemId}", 42L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Todo item not found: 42"));
    }

    @Test
    void deleteTodo_withMissingTodoItem_returnsNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}/todos/{todoItemId}", 1L, 42L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: 1"));
    }
}
