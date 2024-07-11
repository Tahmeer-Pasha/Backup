package com.example.TodoApp.controller;

import com.example.TodoApp.entity.Todos;
import com.example.TodoApp.service.TodoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoServiceImpl todoService;

    @GetMapping
    public ResponseEntity<List<Todos>> getAllTodos(){
        try {
            List<Todos> todos = todoService.getAllTodos();
            return ResponseEntity.ok(todos);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todos> getTodoById(@PathVariable int id){
        try{
            return ResponseEntity.ok(todoService.getTodoById(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Todos> addTodo(@RequestBody Todos todos){
        try{
            return ResponseEntity.ok(todoService.addTodo(todos));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Todos> updateTodo(@RequestParam int id, @RequestParam String todo){
        try{
            return ResponseEntity.ok(todoService.updateTodo(id,todo));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public String deleteTodos(@RequestParam int id){
        try {
            return (todoService.deleteTodo(id));
        }catch (Exception e){
            return e.getMessage();
        }
    }
}