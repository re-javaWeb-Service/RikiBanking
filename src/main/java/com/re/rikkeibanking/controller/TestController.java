package com.re.rikkeibanking.controller;


import com.re.rikkeibanking.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test/me")
    public  String me(Authentication authentication){
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return "hello" +principal.getUsername();
    }
}
