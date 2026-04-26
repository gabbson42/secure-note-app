package project.service;

import org.mindrot.jbcrypt.BCrypt;
import project.model.User;
import project.repository.UserRepository;

public class AuthService {

    private final UserRepository repository = new UserRepository();

    public boolean register(String username, String password) {
        return repository.saveUser(username, hashPassword(password), "USER");
    }

    public User login(String username, String password) {
        if (repository.checkIfUserExists(username)) {
            User user = repository.getUser(username);

            if (checkPassword(password, user.getPassword())) {
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!checkPassword(oldPassword, user.getPassword())) {
            return false;
        }

        String newHashedPassword = hashPassword(newPassword);
        boolean updatedPassword = repository.updatePassword(user.getUsername(), newHashedPassword);

        if (updatedPassword) {
            user.setPassword(newHashedPassword);
        }
        return updatedPassword;
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    private boolean checkPassword(String password, String storedPassword) {
        return BCrypt.checkpw(password, storedPassword);
    }
}
