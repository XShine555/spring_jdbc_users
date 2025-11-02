package com.accesdades.ra2.ac1.accesdades_ra2_ac1.controllers;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.models.User;

import org.springframework.web.bind.annotation.PostMapping;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.repository.UserRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<String> postUser(@RequestBody User entity) {
        userRepository.addUser(entity);
        return ResponseEntity.created(null).body("Usuari creat correctament");
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            user.setUltimAcces(new Timestamp(System.currentTimeMillis()));
        }
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable("user_id") int id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            return ResponseEntity.ok(null);
        }

        user.setUltimAcces(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable("user_id") int id, @RequestBody User entity) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }
        
        entity.setId(id);
        userRepository.updateUser(entity);
        return ResponseEntity.ok("Usuari actualitzat correctament");
    }

    @PatchMapping("/users/{user_id}/name")
    public ResponseEntity<String> patchUserNom(@PathVariable("user_id") int id, @RequestParam("name") String name) {
        if (userRepository.getUserById(id) == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }

        User user = userRepository.updateUserName(id, name);
        return ResponseEntity.ok(user.toString());
    }
    
    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") int id) {
        if (userRepository.getUserById(id) == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }

        userRepository.deleteUser(id);
        return ResponseEntity.ok("Usuari eliminat correctament");
    }
}
