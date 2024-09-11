package org.example.globaledugroup.controller;

import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.entity.FunUser;
import org.example.globaledugroup.repository.FunUserRepository;
import org.example.globaledugroup.service.FunUserService;
import org.example.globaledugroup.dto.UserFunDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advice")
@RequiredArgsConstructor
public class UserController {
    private final FunUserService funUserService;
    private final FunUserRepository funUserRepository;

    @PostMapping
    public HttpEntity<?> addFunUser(@RequestBody UserFunDto userFunDto) {
        funUserService.save(userFunDto);
        return ResponseEntity.ok("saqlandi");
    }

    @GetMapping
    public HttpEntity<?> getAll() {
        return ResponseEntity.ok(funUserRepository.findAll());
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(funUserRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found")));
    }

    @GetMapping("/phone/{phone}")
    public HttpEntity<?> getByPhone(@PathVariable String phone) {
        List<FunUser> funUsers = funUserService.findByPhone(phone);
        return ResponseEntity.ok(funUsers);
    }

    @GetMapping("/name/{name}")
    public HttpEntity<?> getByName(@PathVariable String name) {
        List<FunUser> funUsers = funUserService.findByName(name);
        return ResponseEntity.ok(funUsers);
    }

    @GetMapping("/search/{text}")
    public HttpEntity<?> search(@PathVariable String text) {
        List<FunUser> funUsers = funUserService.searchFunUser(text);
        return ResponseEntity.ok(funUsers);
    }
}
