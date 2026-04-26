package project.service;

import project.model.User;
import project.repository.UserRepository;

import java.util.List;

public class AdminService {

    private final UserRepository repository = new UserRepository();

    public List<User> getAllUsers() {
        return repository.getAllUsersFromDb();
    }

    public boolean deleteUser(int userId) {
        return repository.deleteUser(userId);
    }

    public boolean editUserRole(int userId, String newRole) {
        return repository.editUserRole(userId, newRole);
    }
}
