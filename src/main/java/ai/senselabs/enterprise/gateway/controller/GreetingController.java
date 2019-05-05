package ai.senselabs.enterprise.gateway.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/hi")
    public String foo() {
        return "Hello World F!";
    }



}
