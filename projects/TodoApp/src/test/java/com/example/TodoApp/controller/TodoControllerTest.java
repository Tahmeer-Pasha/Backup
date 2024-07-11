package com.example.TodoApp.controller;

import com.example.TodoApp.entity.Todos;
import com.example.TodoApp.service.TodoServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoServiceImpl todoService;

    private Todos todo1, todo2, todo3;
    private List<Todos> todosList;

    @BeforeEach
    void setUp() {
        todo1 = Todos.builder().id(1).todo("TODO 1").build();
        todo2 = Todos.builder().id(2).todo("TODO 2").build();
        todo3 = Todos.builder().id(2).todo("TODO 3").build();
        todosList = new ArrayList<>();
        todosList.add(todo1);
        todosList.add(todo2);
        todosList.add(todo3);
    }

    @AfterEach
    void tearDown() {
        todo1=null;
        todo2=null;
        todo3=null;
        todosList=null;
    }

    @Test
    void shouldGetAllTodos() throws Exception {
        when(todoService.getAllTodos()).thenReturn(todosList);
        mockMvc.perform((get("/api/v1/todos")))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void shouldGetTodoById() throws Exception {
        when(todoService.getTodoById(todo1.getId())).thenReturn(todo1);
        mockMvc.perform((get("/api/v1/todos/1")))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void shouldAddTodo() throws Exception {
        String requestJson = convertToJSON(todo1);
        when(todoService.addTodo(todo1)).thenReturn(todo1);
        mockMvc.perform(post("/api/v1/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void updateTodo() throws Exception {
        Todos updatedTodo = Todos.builder()
                .todo("Update todo").id(1).build();
        when(todoService.updateTodo(1,"Update todo")).thenReturn(updatedTodo);
        mockMvc.perform(put("/api/v1/todos")
                .param("id", String.valueOf(1))
                .param("todo","Update todo")
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void deleteTodos() throws Exception {
        doAnswer(Answers.CALLS_REAL_METHODS).when(todoService).deleteTodo(1);
        mockMvc.perform((delete("/api/v1/todos")
                        .param("id","1")))
                .andDo(print()).andExpect(status().isOk());
    }

    String convertToJSON(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(object);
    }

}