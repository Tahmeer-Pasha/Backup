package com.example.TodoApp.service;

import com.example.TodoApp.entity.Todos;
import com.example.TodoApp.repository.TodoRepository;
import com.example.TodoApp.service.interfaces.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public List<Todos> getAllTodos() {
        return todoRepository.findAll();
    }

    @Override
    public Todos addTodo(Todos todos) {
        return todoRepository.save(todos);
    }

    @Override
    public Todos updateTodo(int id, String todo) {
        try {
            Optional<Todos> optionalTodos = todoRepository.findById(id);
            if(optionalTodos.isEmpty())return null;
            Todos todos = optionalTodos.get();
            todos.setTodo(todo);
            return todoRepository.save(todos);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Todos getTodoById(int id) {
        try {
            Optional<Todos> optionalTodos = todoRepository.findById(id);
            if (optionalTodos.isEmpty())throw new RuntimeException("Todo Not Found with the given ID!!");
            return optionalTodos.get();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public String deleteTodo(int id) {
        try {
            todoRepository.deleteById(id);
            return "Delete todo with id:" +id+" Successful";
        }catch (Exception e){
            return "Delete todo with id:"+id+" UnSuccessful";
        }
    }
}