package com.accesdades.ra2.ac1.accesdades_ra2_ac1.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.models.User;

import org.springframework.web.bind.annotation.PostMapping;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.services.UserService;

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
    UserService userService;

    @PostMapping("/users")
    public ResponseEntity<String> postUser(@RequestBody User entity) {
        userService.addUser(entity);
        return ResponseEntity.created(null).body("Usuari creat correctament");
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable("user_id") int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable("user_id") int id, @RequestBody User entity) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }
        
        entity.setId(id);
        userService.updateUser(entity);
        return ResponseEntity.ok("Usuari actualitzat correctament");
    }

    @PatchMapping("/users/{user_id}/name")
    public ResponseEntity<String> patchUserNom(@PathVariable("user_id") int id, @RequestParam("name") String name) {
        if (userService.getUserById(id) == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }

        User user = userService.updateUserName(id, name);
        return ResponseEntity.ok(user.toString());
    }
    
    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") int id) {
        if (userService.getUserById(id) == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("Usuari eliminat correctament");
    }

    @PostMapping("/users/{user_id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable("user_id") int id, @RequestParam("image") MultipartFile image) {
        if (userService.getUserById(id) == null) {
            return ResponseEntity.ok("L'usuari no existeix");
        }
        try{
            String imagePath = userService.uploadImagePath(id, image);
            return ResponseEntity.ok("Imatge pujada correctament: " + imagePath);
        } catch (Exception e) {
            return ResponseEntity.ok("Error en pujar la imatge: " + e.getMessage());
        }
    }

    @PostMapping("/users/upload-csv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            int count = userService.uploadCsvFile(file);
            return ResponseEntity.ok("S'han afegit " + count + " usuaris correctament.");
        } catch (Exception e) {
            return ResponseEntity.ok("Error en pujar el fitxer CSV: " + e.getMessage());
        }
    }

    @PostMapping("/users/upload-json")
    public ResponseEntity<String> uploadJSON(@RequestParam("file") MultipartFile file) {
        try {
            int count = userService.uploadJsonFile(file);
            return ResponseEntity.ok("S'han afegit " + count + " usuaris correctament.");
        } catch (Exception e) {
            return ResponseEntity.ok("Error en pujar el fitxer JSON: " + e.getMessage());
        }
    }
}
