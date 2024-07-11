package com.example.TodoApp.service.interfaces;

import com.example.TodoApp.entity.Todos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TodoService {

    List<Todos> getAllTodos();
    Todos addTodo(Todos todos);

    Todos updateTodo(int id, String todo);

    Todos getTodoById(int id);

    String deleteTodo(int id);
}
