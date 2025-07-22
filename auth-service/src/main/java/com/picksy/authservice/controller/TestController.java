package com.picksy.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/un")
public class TestController {
    @GetMapping("hello/")
    public String getHello(){
        return "Hello World";
    }
}
