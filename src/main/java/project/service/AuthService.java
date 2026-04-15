package project.service;

import org.mindrot.jbcrypt.BCrypt;
import project.repository.UserRepository;

public class AuthService {

    private final UserRepository repository = new UserRepository();

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

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }
}
