package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.dao.TodoItemRepository;
import com.pelinhangisi.springboottodoapp.dao.UserRepository;
import com.pelinhangisi.springboottodoapp.models.TodoItem;
import com.pelinhangisi.springboottodoapp.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {

    @Mock
    private TodoItemRepository todoItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MainController mainController;

    private User userA(){
        User user = new User();
        user.setId(1L);
        user.setEmail("a@example.com");
        return user;
    }

    private User userB(){
        User user = new User();
        user.setId(2L);
        user.setEmail("b@example.com");
        return user;
    }

    private void authenticateAs(User user){
        lenient().when(authentication.getName()).thenReturn(user.getEmail());
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    void addTodoItem_createsTodoItemForCurrentUser_andRedirects() {
        User userA = userA();
        authenticateAs(userA);

        String view = mainController.addTodoItem("Buy milk", authentication);

        assertThat(userA.getTodoItems()).hasSize(1);
        assertThat(userA.getTodoItems().get(0).getDescription()).isEqualTo("Buy milk");
        assertThat(view).isEqualTo("redirect:/tasklist");
    }

    @Test
    void editTodoItem_ownTodoItem_toggles() {
        User userA = userA();
        authenticateAs(userA);
        TodoItem todoItem = new TodoItem();
        todoItem.setId(10L);
        todoItem.setCompleted(false);
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userA));
        when(todoItemRepository.findById(10L)).thenReturn(Optional.of(todoItem));

        String view = mainController.editTodoItem(10L, authentication);

        assertThat(todoItem.getCompleted()).isTrue();
        assertThat(view).isEqualTo("redirect:/tasklist");
    }

    @Test
    void editTodoItem_otherUsersTodoItem_throwsAccessDenied() {
        authenticateAs(userA());
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userB()));

        assertThatThrownBy(() -> mainController.editTodoItem(10L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteTodoItem_ownTodoItem_deletes() {
        User userA = userA();
        TodoItem todoItem = new TodoItem();
        todoItem.setId(10L);
        userA.getTodoItems().add(todoItem);
        authenticateAs(userA);
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userA));
        when(todoItemRepository.findById(10L)).thenReturn(Optional.of(todoItem));
        when(userRepository.findAll()).thenReturn(List.of(userA));

        String view = mainController.deleteTodoItem(10L, authentication);

        assertThat(userA.getTodoItems()).isEmpty();
        assertThat(view).isEqualTo("redirect:/tasklist");
    }

    @Test
    void deleteTodoItem_otherUsersTodoItem_throwsAccessDenied() {
        authenticateAs(userA());
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userB()));

        assertThatThrownBy(() -> mainController.deleteTodoItem(10L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }
}
