package com.bul.controller;


import com.bul.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var result = userActivationService.activation(id);

        if (result) {
            return ResponseEntity.ok().body("Регистрация успешно завершена! УРА!");
        }

        return ResponseEntity.internalServerError().build();
    }
}
