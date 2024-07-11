package com.example.learning_spring_boot.rest;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController

public class RestController {

//    Expose "/" for "Hello World"

    @GetMapping("/")
    public String sayHello(){
        return "Hello World";
    }

    @GetMapping("/hello")
    public String hello(){
        return "HELLO";
    }
}
