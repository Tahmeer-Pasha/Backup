package com.example.TodoApp.service;

import com.example.TodoApp.entity.Todos;
import com.example.TodoApp.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoServiceImplTest {

    // Service being tested
    @InjectMocks
    private TodoServiceImpl todoService;

    // Dependencies
    @Mock
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllTodos() {
        // Given
        Todos todo1 = Todos.builder().id(1).todo("Todo 1").build();
        Todos todo2 = Todos.builder().id(2).todo("Todo 2").build();
        Todos todo3 = Todos.builder().id(3).todo("Todo 3").build();
        List<Todos> todos = List.of(todo1, todo2, todo3);

        // Mock the calls
        when(todoRepository.findAll()).thenReturn(todos);

        // When
        List<Todos> allTodos = todoService.getAllTodos();

        // Then
        assertEquals(todos, allTodos);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void shouldAddTodo() {
        // Given
        Todos todos = Todos.builder().id(1).todo("Todo 1").build();

        // Mock the calls
        when(todoRepository.save(todos)).thenReturn(todos);

        // When
        Todos savedTodo = todoService.addTodo(todos);

        // Then
        assertEquals(todos.getTodo(), savedTodo.getTodo());
        assertEquals(todos.getId(), savedTodo.getId());
        verify(todoRepository, times(1)).save(todos);
    }

    @Test
    void shouldUpdateTodo() {
        // Given
        Todos todos = Todos.builder().id(1).todo("Todo 1").build();
        Todos updatedTodos = Todos.builder().id(1).todo("Updated Todo").build();

        // Mock the calls
        when(todoRepository.findById(todos.getId())).thenReturn(Optional.of(todos));
        when(todoRepository.save(any(Todos.class))).thenReturn(updatedTodos);

        // When
        Todos result = todoService.updateTodo(todos.getId(), "Updated Todo");

        // Then
        assertEquals(updatedTodos.getId(), result.getId());
        assertEquals(updatedTodos.getTodo(), result.getTodo());
        verify(todoRepository, times(1)).findById(todos.getId());
        verify(todoRepository, times(1)).save(any(Todos.class));
    }

    @Test
    void shouldReturnTodoById() {
        // Given
        Todos todos = Todos.builder().id(1).todo("Todo 1").build();

        // Mock the calls
        when(todoRepository.findById(todos.getId())).thenReturn(Optional.of(todos));

        // When
        Todos getTodo = todoService.getTodoById(1);

        // Then
        assertEquals(todos, getTodo);
        verify(todoRepository, times(1)).findById(1);
    }

    @Test
    void shouldDeleteTodo() {
        // Given
        int todoId = 1;

        // Mock the calls
        doAnswer(Answers.CALLS_REAL_METHODS).when(todoRepository).deleteById(any());
//        doNothing().when(todoRepository).deleteById(todoId);

        // When
        String result = todoService.deleteTodo(todoId);

        // Then
        assertEquals("Delete todo with id:" + todoId + " Successful", result);
        verify(todoRepository, times(1)).deleteById(todoId);
    }
}
