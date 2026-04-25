package project.service;

import project.model.User;
import project.repository.UserRepository;

public class UserService {

    private final UserRepository repository = new UserRepository();

    public boolean saveNote(String title, String noteContent, int userId) {
        return repository.saveNoteToDb(title, noteContent, userId);
    }

    public boolean editNote(String newTitle, String newNoteContent, int noteId) {
        return repository.saveEditedNote(newTitle,newNoteContent, noteId);
    }

    public boolean deleteNote(int noteId) {
        return repository.deleteNoteFromDb(noteId);
    }

    public User updateCurrentUser(String username) {
        return repository.getUser(username);
    }
}
