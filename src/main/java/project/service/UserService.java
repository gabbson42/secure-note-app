package project.service;

import project.repository.UserRepository;

public class UserService {

    private final UserRepository repository = new UserRepository();

    public boolean saveNote(String title, String noteContent, int userId) {
        return repository.saveNoteToDb(title, noteContent, userId);
    }

}
