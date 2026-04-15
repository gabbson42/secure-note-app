package project.service;

import org.mindrot.jbcrypt.BCrypt;
import project.model.User;
import project.repository.UserRepository;

public class AuthService {

    private final UserRepository repository = new UserRepository();
    private User user;

    public boolean register(String username, String password) {
        if (username == null || username.isBlank()) {
            IO.println("Username is empty");
            return false;
        }
        if (password == null || password.isBlank()) {
            IO.println("Password is empty");
            return false;
        }

        return repository.saveUser(username, hashPassword(password), "USER");
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank()) {
            IO.println("Username is empty");
            return null;
        }
        if (password == null || password.isBlank()) {
            IO.println("Password is empty");
            return null;
        }

        user = repository.getUser(username);

        if (checkPassword(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }

    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (checkPassword(oldPassword, user.getPassword())) {
            user.setPassword(hashPassword(newPassword));
            return repository.updatePassword(user.getUsername(), hashPassword(newPassword));
        } else {
            return false;
        }
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    private boolean checkPassword(String password, String storedPassword) {
        return BCrypt.checkpw(password, storedPassword);
    }
}
