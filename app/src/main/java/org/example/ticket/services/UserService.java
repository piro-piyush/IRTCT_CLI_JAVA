package org.example.ticket.services;

import lombok.Getter;
import org.example.ticket.entities.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final LoginService loginService;

    @Getter
    private User currentUser;

    public UserService(User user, LoginService loginService) {
        this.currentUser = user;
        this.loginService = loginService;
    }

    public void updateName(String newName) {
        currentUser.setName(newName);
        loginService.updateUser(currentUser); // persist changes
    }

    public void updatePhone(String newPhone) {
        currentUser.setPhoneNumber(newPhone);
        loginService.updateUser(currentUser);
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (BCrypt.checkpw(oldPassword, currentUser.getPasswordHash())) {
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            currentUser.setPasswordHash(newHash);
            loginService.updateUser(currentUser);
        } else {
            throw new IllegalArgumentException("Old password incorrect");
        }
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }


}
