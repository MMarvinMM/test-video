package com.example.metricsdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 87957
 */
@RestController
public class TestController {

    @RequestMapping("/hello")
    public String hello() {
        System.out.println("hello video metrics!");

        return "hello video metrics!";
    }
}
