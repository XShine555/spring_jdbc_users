package com.accesdades.ra2.ac1.accesdades_ra2_ac1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.logging.CustomLogging;
import com.accesdades.ra2.ac1.accesdades_ra2_ac1.models.User;
import com.accesdades.ra2.ac1.accesdades_ra2_ac1.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    CustomLogging logger;

    @PostMapping("/users")
    public ResponseEntity<String> postUser(@RequestBody User entity) {
        userService.addUser(entity);
        logger.info(this.getClass().getSimpleName(), "postUser", "Nou usuari creat: " + entity.toString());
        return ResponseEntity.created(null).body("Usuari creat correctament");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        logger.info(this.getClass().getSimpleName(), "getUsers", "Obtenint tots els usuaris");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable("user_id") int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            logger.error(this.getClass().getSimpleName(), "getUserById", "L'usuari amb ID " + id + " no existeix");
            return ResponseEntity.ok(null);
        }

        logger.info(this.getClass().getSimpleName(), "getUserById", "Obtenint usuari amb ID: " + id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable("user_id") int id, @RequestBody User entity) {
        User user = userService.getUserById(id);
        if (user == null) {
            logger.error(this.getClass().getSimpleName(), "updateUser", "L'usuari amb ID " + id + " no existeix");
            return ResponseEntity.ok("L'usuari no existeix");
        }

        entity.setId(id);
        userService.updateUser(entity);
        logger.info(this.getClass().getSimpleName(), "updateUser", "Usuari amb ID " + id + " actualitzat");
        return ResponseEntity.ok("Usuari actualitzat correctament");
    }

    @PatchMapping("/users/{user_id}/name")
    public ResponseEntity<String> patchUserNom(@PathVariable("user_id") int id, @RequestParam("name") String name) {
        if (userService.getUserById(id) == null) {
            logger.error(this.getClass().getSimpleName(), "patchUserNom", "L'usuari amb ID " + id + " no existeix");
            return ResponseEntity.ok("L'usuari no existeix");
        }

        User user = userService.updateUserName(id, name);
        logger.info(this.getClass().getSimpleName(), "patchUserNom",
                "Nom de l'usuari amb ID " + id + " actualitzat a " + name);
        return ResponseEntity.ok(user.toString());
    }

    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") int id) {
        if (userService.getUserById(id) == null) {
            logger.error(this.getClass().getSimpleName(), "deleteUser", "L'usuari amb ID " + id + " no existeix");
            return ResponseEntity.ok("L'usuari no existeix");
        }

        userService.deleteUser(id);
        logger.info(this.getClass().getSimpleName(), "deleteUser", "Usuari amb ID " + id + " eliminat");
        return ResponseEntity.ok("Usuari eliminat correctament");
    }

    @PostMapping("/users/{user_id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable("user_id") int id,
            @RequestParam("image") MultipartFile image) {
        if (userService.getUserById(id) == null) {
            logger.error(this.getClass().getSimpleName(), "uploadImage", "L'usuari amb ID " + id + " no existeix");
            return ResponseEntity.ok("L'usuari no existeix");
        }
        try {
            String imagePath = userService.uploadImagePath(id, image);
            logger.info(this.getClass().getSimpleName(), "uploadImage",
                    "Imatge pujada per l'usuari amb ID " + id + ": " + imagePath);
            return ResponseEntity.ok("Imatge pujada correctament: " + imagePath);
        } catch (Exception e) {
            logger.error(this.getClass().getSimpleName(), "uploadImage",
                    "Error en pujar la imatge per l'usuari amb ID " + id + ": " + e.getMessage());
            return ResponseEntity.ok("Error en pujar la imatge: " + e.getMessage());
        }
    }

    @PostMapping("/users/upload-csv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            int count = userService.uploadCsvFile(file);
            logger.info(this.getClass().getSimpleName(), "uploadCSV",
                    "Fitxer CSV pujat amb " + count + " usuaris nous");
            return ResponseEntity.ok("S'han afegit " + count + " usuaris correctament.");
        } catch (Exception e) {
            logger.error(this.getClass().getSimpleName(), "uploadCSV",
                    "Error en pujar el fitxer CSV: " + e.getMessage());
            return ResponseEntity.ok("Error en pujar el fitxer CSV: " + e.getMessage());
        }
    }

    @PostMapping("/users/upload-json")
    public ResponseEntity<String> uploadJSON(@RequestParam("file") MultipartFile file) {
        try {
            int count = userService.uploadJsonFile(file);
            logger.info(this.getClass().getSimpleName(), "uploadJSON",
                    "Fitxer JSON pujat amb " + count + " usuaris nous");
            return ResponseEntity.ok("S'han afegit " + count + " usuaris correctament.");
        } catch (Exception e) {
            logger.error(this.getClass().getSimpleName(), "uploadJSON",
                    "Error en pujar el fitxer JSON: " + e.getMessage());
            return ResponseEntity.ok("Error en pujar el fitxer JSON: " + e.getMessage());
        }
    }
}
