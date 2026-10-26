package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.dao.TodoItemRepository;
import com.pelinhangisi.springboottodoapp.dao.UserRepository;
import com.pelinhangisi.springboottodoapp.models.TodoItem;
import com.pelinhangisi.springboottodoapp.models.User;
import com.pelinhangisi.springboottodoapp.request.AddTodoItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoItemRepository todoItemRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

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
    void getUserById_ownAccount_returnsUser() {
        User userA = userA();
        authenticateAs(userA);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));

        User result = userController.getUserById(1L, authentication);

        assertThat(result).isEqualTo(userA);
    }

    @Test
    void getUserById_otherUsersAccount_throwsAccessDenied() {
        authenticateAs(userA());

        assertThatThrownBy(() -> userController.getUserById(2L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void addTodoItem_ownAccount_addsTodoItem() {
        User userA = userA();
        authenticateAs(userA);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
        AddTodoItemRequest request = new AddTodoItemRequest("buy milk");

        userController.addTodoItem(1L, request, authentication);

        assertThat(userA.getTodoItems()).hasSize(1);
        assertThat(userA.getTodoItems().get(0).getDescription()).isEqualTo("buy milk");
    }

    @Test
    void addTodoItem_otherUsersAccount_throwsAccessDenied() {
        authenticateAs(userA());
        AddTodoItemRequest request = new AddTodoItemRequest("buy milk");

        assertThatThrownBy(() -> userController.addTodoItem(2L, request, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void toggleTodoItemCompleted_ownTodoItem_toggles() {
        User userA = userA();
        authenticateAs(userA);
        TodoItem todoItem = new TodoItem();
        todoItem.setId(10L);
        todoItem.setDescription("task");
        todoItem.setCompleted(false);
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userA));
        when(todoItemRepository.findById(10L)).thenReturn(Optional.of(todoItem));

        userController.toggleTodoItemCompleted(10L, authentication);

        assertThat(todoItem.getCompleted()).isTrue();
    }

    @Test
    void toggleTodoItemCompleted_otherUsersTodoItem_throwsAccessDenied() {
        authenticateAs(userA());
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userB()));

        assertThatThrownBy(() -> userController.toggleTodoItemCompleted(10L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteTodo_ownTodoItem_deletes() {
        User userA = userA();
        TodoItem todoItem = new TodoItem();
        todoItem.setId(10L);
        TodoItem otherTodoItem = new TodoItem();
        otherTodoItem.setId(11L);
        userA.getTodoItems().add(todoItem);
        userA.getTodoItems().add(otherTodoItem);
        authenticateAs(userA);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userA));
        when(todoItemRepository.findById(10L)).thenReturn(Optional.of(todoItem));

        userController.deleteTodo(1L, 10L, authentication);

        assertThat(userA.getTodoItems()).containsExactly(otherTodoItem);
        org.mockito.Mockito.verify(userRepository).save(userA);
        org.mockito.Mockito.verify(todoItemRepository).delete(todoItem);
    }

    @Test
    void deleteTodo_otherUsersAccount_throwsAccessDenied() {
        authenticateAs(userA());

        assertThatThrownBy(() -> userController.deleteTodo(2L, 10L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteTodo_otherUsersTodoItem_throwsAccessDenied() {
        User userA = userA();
        authenticateAs(userA);
        when(userRepository.findByTodoItemsId(10L)).thenReturn(Optional.of(userB()));

        assertThatThrownBy(() -> userController.deleteTodo(1L, 10L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteUser_ownAccount_deletes() {
        User userA = userA();
        authenticateAs(userA);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));

        userController.deleteUser(1L, authentication);

        org.mockito.Mockito.verify(userRepository).delete(userA);
    }

    @Test
    void deleteUser_otherUsersAccount_throwsAccessDenied() {
        authenticateAs(userA());

        assertThatThrownBy(() -> userController.deleteUser(2L, authentication))
                .isInstanceOf(AccessDeniedException.class);
    }
}
