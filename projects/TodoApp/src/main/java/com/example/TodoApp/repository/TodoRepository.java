package com.example.TodoApp.repository;

import com.example.TodoApp.entity.Todos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todos, Integer> {
}
