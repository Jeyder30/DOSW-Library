package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
