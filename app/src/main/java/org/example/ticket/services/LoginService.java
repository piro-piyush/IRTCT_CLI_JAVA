package org.example.ticket.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.ticket.entities.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling user login and persistence with Jackson + BCrypt.
 */
public class LoginService {
    public static final String FILE_PATH = "app/src/main/java/org/example/ticket/database/users.json";

    private final ObjectMapper objectMapper;
    private List<User> users;

    public LoginService() {
        this.objectMapper = new ObjectMapper();
        // --- FIX: Register JavaTimeModule for LocalDate, LocalDateTime etc ---
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.users = new ArrayList<>();
        loadUsers();System.out.println("Using users file at: " + new File(FILE_PATH).getAbsolutePath());
        System.out.println("Loaded Users: " + this.users.size());
        for (User u : this.users) {
            System.out.println(u.getName() + " | " + u.getId());
        }
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            users = new ArrayList<>();
            return;
        }

        try {
            users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            System.out.println("Error loading users from file : " + e.getMessage());
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
        } catch (IOException e) {
            System.out.println("Error saving users to file : " + e.getMessage());
        }
    }

    /**
     * Registers a new user with hashed password.
     */
    public User registerUser(String email, String name, String password) {
        // trim input
        email = email.trim();
        name = name.trim();

        // hash password
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // generate unique ID
        String id = UUID.randomUUID().toString().substring(0, 4).toUpperCase();


        // create user
        User user = new User(id, name, email, passwordHash);

        // add to list and save
        users.add(user);
        saveUsers();

        return user;
    }


    public User login(String email, String plainPassword) throws Exception {
        // remove leading/trailing spaces
        email = email.trim();

        // find user by email (case-insensitive)
        String finalEmail = email;
        User user = users.stream()
                .filter(u -> u.getEmail().trim().equalsIgnoreCase(finalEmail))
                .findFirst()
                .orElse(null);

        if (user == null) {
            throw new Exception("User not found with this email");
        }

        // check password
        if (!BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            throw new Exception("Wrong password");
        }

        return user;
    }




    public List<User> getUsers() {
        return new ArrayList<>(users);
    }


    // In LoginService
    public void updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                saveUsers();
                return;
            }
        }
    }

    /**
     * Checks if a user with given email is already registered.
     */
    public boolean isAlreadyRegistered(String email) {
        if (email == null || email.trim().isEmpty()) return false;

        String finalEmail = email.trim().toLowerCase(); // normalize
        return users.stream()
                .anyMatch(u -> u.getEmail() != null
                        && u.getEmail().trim().toLowerCase().equals(finalEmail));
    }

}
