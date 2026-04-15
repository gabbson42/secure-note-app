package project.service;

import org.mindrot.jbcrypt.BCrypt;
import project.model.User;
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

    public boolean login(String username, String password){
        if (username == null || username.isBlank()) {
            IO.println("Username is empty");
            return false;
        }
        if (password == null || password.isBlank()) {
            IO.println("Password is empty");
            return false;
        }

        User user = repository.getUser(username);

        return checkPassword(password, user.getPassword());
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    private boolean checkPassword(String password, String storedPassword){
        return BCrypt.checkpw(password,storedPassword);
    }
}
