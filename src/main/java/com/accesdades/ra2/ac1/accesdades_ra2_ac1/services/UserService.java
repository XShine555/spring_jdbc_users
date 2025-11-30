package com.accesdades.ra2.ac1.accesdades_ra2_ac1.services;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accesdades.ra2.ac1.accesdades_ra2_ac1.models.User;
import com.accesdades.ra2.ac1.accesdades_ra2_ac1.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {
    private static final String IMAGE_UPLOAD_DIR = "private/images/";
    private static final String CSV_UPLOAD_DIR = "private/csv_processed/";
    private static final String JSON_UPLOAD_DIR = "private/json_processed/";

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    public User addUser(User user) {
        userRepository.addUser(user);
        return user;
    }

    public User deleteUser(int id) {
        User user = userRepository.getUserById(id);
        userRepository.removeUser(id);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            user.setUltimAcces(new Timestamp(System.currentTimeMillis()));
        }
        return users;
    }

    public User getUserById(int id) {
        User user = userRepository.getUserById(id);
        if (user != null) {
            user.setUltimAcces(new Timestamp(System.currentTimeMillis()));
        }
        return user;
    }

    public User updateUserName(int id, String name) {
        return userRepository.updateUserName(id, name);
    }

    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public String uploadImagePath(int id, MultipartFile multipartFile) throws Exception {
        User user = userRepository.getUserById(id);
        if (user == null) {
            return "Usuari no existeix";
        }

        File upload_dir = new File(IMAGE_UPLOAD_DIR);
        if (!upload_dir.exists()) {
            upload_dir.mkdirs();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String imagePath = IMAGE_UPLOAD_DIR + "user_" + id + "_" + originalFilename;
        File dest = new File(imagePath);
        
        Files.copy(multipartFile.getInputStream(), dest.toPath());
        int t = userRepository.updateUserImagePath(id, imagePath);
        
        return imagePath;
    }

    public int uploadCsvFile(MultipartFile file) throws Exception {
        String content = new String(file.getBytes());
        String[] lines = content.split("\n");
        int count = 0;

        int startIndex = 0;
        if (lines.length > 0 && lines[0].toLowerCase().contains("name") && lines[0].toLowerCase().contains("email")) {
            startIndex = 1;
        }
        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i];
            String[] fields = line.split(",");
            if (fields.length >= 4) {
                String name = fields[0].trim();
                String description = fields[1].trim();
                String email = fields[2].trim();
                String password = fields[3].trim();

                User user = new User();
                user.setName(name);
                user.setDescription(description);
                user.setEmail(email);
                user.setPassword(password);

                userRepository.addUser(user);
                count++;
            }
        }

        File upload_dir = new File(CSV_UPLOAD_DIR);
        if (!upload_dir.exists()) {
            upload_dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String csvPath = CSV_UPLOAD_DIR + "upload_" + System.currentTimeMillis() + "_" + originalFilename;
        File dest = new File(csvPath);
        Files.copy(file.getInputStream(), dest.toPath());

        return count;
    }

    public int uploadJsonFile(MultipartFile file) throws Exception {
        JsonNode root = objectMapper.readTree(file.getInputStream());

        JsonNode data = root.path("data");
        String control = data.path("control").asText();
        int totalCount = data.path("count").asInt();
        JsonNode usersNode = data.path("users");

        if (!"OK".equalsIgnoreCase(control)) {
            return 33;
        }

        if (!usersNode.isArray()) {
            return 22;
        }

        if (usersNode.size() != totalCount) {
            return 11;
        }

        int count = 0;
        for (JsonNode userNode : usersNode) {
            String username = userNode.path("name").asText(null);
            String description = userNode.path("description").asText(null);
            String email = userNode.path("email").asText(null);
            String password = userNode.path("password").asText(null);

            if (username == null || description == null || email == null || password == null) {
                continue;
            }

            User user = new User();
            user.setName(username);
            user.setDescription(description);
            user.setEmail(email);
            user.setPassword(password);

            userRepository.addUser(user);
            count++;
        }
        
        File upload_dir = new File(JSON_UPLOAD_DIR);
        if (!upload_dir.exists()) {
            upload_dir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String jsonPath = JSON_UPLOAD_DIR + "upload_" + System.currentTimeMillis() + "_" + originalFilename;
        File dest = new File(jsonPath);
        Files.copy(file.getInputStream(), dest.toPath());

        return count;
    }
}
