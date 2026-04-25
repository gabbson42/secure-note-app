package project.ui;

import project.model.Note;
import project.model.User;
import project.service.AuthService;
import project.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner input = new Scanner(System.in);
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private User currentUser;

    public void start() {
        boolean running = true;

        while (running) {
            IO.println("""
                    Welcome to Secure Notes!
                    ------------------------
                    1. Login
                    2. Register user
                    0. Quit
                    ------------------------""");

            String choice = input.nextLine();

            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> running = false;
                default -> IO.println("Invalid choice");
            }
        }
    }

    private void register() {
        IO.println("Enter your username: ");
        String username = input.nextLine();

        IO.println("Enter your password: ");
        String password = input.nextLine();

        boolean success = authService.register(username, password);

        if (success) {
            IO.println("User registered successfully");
        } else {
            IO.println("Username already exists, login or try registering with another username");
        }
    }

    private void login() {
        IO.println("Enter your username: ");
        String username = input.nextLine();

        IO.println("Enter your password: ");
        String password = input.nextLine();

        currentUser = authService.login(username, password);

        if (currentUser != null) {
            IO.println("Login successful!");
            if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                adminMenu();
            } else {
                userMenu();
            }
        } else {
            IO.println("Username or Password is incorrect");
        }
    }

    private void adminMenu() {
        IO.println("Not functional at the moment");
    }

    private void userMenu() {
        clearConsole();
        boolean running = true;

        while (running) {
            IO.println("""
                          Secure Notes
                    -----------------------
                    1. Create note
                    2. View notes
                    3. Change password
                    0. Log out
                    -----------------------""");

            String choice = input.nextLine();

            switch (choice) {
                case "1" -> createNote();
                case "2" -> viewNotes();
                case "3" -> changePassword();
                case "0" -> running = false;
                default -> IO.println("Invalid choice");
            }
        }
    }

    private void createNote() {
        IO.println("Input the title of the note:");
        String title = input.nextLine();

        IO.println("Input your note content and press enter to save");
        String noteContent = input.nextLine();

        if (userService.saveNote(title, noteContent, currentUser.getId())) {
            IO.println("Note created successfully");
            currentUser = userService.updateCurrentUser(currentUser.getUsername());
        } else {
            IO.println("Something went wrong, try again");
        }
    }

    private void viewNotes() {
        clearConsole();
        List<Note> notes = currentUser.getNotes();
        IO.println("""
                Your notes
                ----------""");
        int i = 1;
        for (Note note : notes) {
            IO.println(i + ". " + note.getTitle());
            i++;
        }

        String choice = input.nextLine();
        int noteIndex = Integer.parseInt(choice);
        int noteId = notes.get(noteIndex - 1).getId();
        String noteTitle = notes.get(noteIndex - 1).getTitle();
        String noteContent = notes.get(noteIndex - 1).getContent();

        boolean running = true;

        while (running) {

            IO.println(noteTitle);
            IO.println("---------");
            IO.println(noteContent);
            IO.println("---------");
            IO.println("1. Edit note, 2. Delete note, 0. Return");

            choice = input.nextLine();

            switch (choice) {
                case "1" -> {
                    if (editNote(noteTitle, noteContent, noteId)) {
                        IO.println("Changes saved successfully!");
                        currentUser = userService.updateCurrentUser(currentUser.getUsername());
                        running = false;
                    } else {
                        IO.println("An unexpected error occurred");
                    }
                }
                case "2" -> {
                    if(deleteNote(noteId)) {
                        IO.println("Note successfully deleted");
                        currentUser = userService.updateCurrentUser(currentUser.getUsername());
                        running = false;
                    } else {
                        IO.println("Note was not deleted");
                    }
                }
                case "0" -> running = false;
                default -> IO.println("Invalid choice, try again");

            }
        }
    }

    public boolean editNote(String title, String noteContent, int noteId) {
        boolean running = true;

        String newTitle = title;
        String newNoteContent = noteContent;

        while (running) {
            IO.println("""
                    What would you like to edit?
                    1. Content
                    2. Title
                    3. Both""");

            String choice = input.nextLine();

            switch (choice) {
                case "1" -> {
                    IO.println("Input new content of note and press enter to save:");
                    newNoteContent = input.nextLine();
                    running = false;
                }
                case "2" -> {
                    IO.println("Input new title of note and press enter to save:");
                    newTitle = input.nextLine();
                    running = false;
                }
                case "3" -> {
                    IO.println("Input new title of note:");
                    newTitle = input.nextLine();
                    IO.println("Input new content of note and press enter to save:");
                    newNoteContent = input.nextLine();
                    running = false;
                }
                default -> IO.println("Invalid input, try again.");
            }
        }
        return userService.editNote(newTitle, newNoteContent, noteId);
    }

    public boolean deleteNote(int noteId) {
        boolean running = true;

        while (running) {
            IO.println("Are you sure you want to delete the note? (Y/N) ");
            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                running = false;
            } else if (choice.equalsIgnoreCase("N")) {
                return false;
            } else {
                IO.println("Invalid input, try again");
            }
        }
        return userService.deleteNote(noteId);
    }
    private void changePassword() {
        boolean running = true;

        while (running) {
            IO.println("Input your old password:");
            String oldPassword = input.nextLine();

            IO.println("Input your new password:");
            String newPassword = input.nextLine();

            IO.println("Input your new password again to confirm:");
            String newPasswordValidation = input.nextLine();

            if (newPassword.equals(newPasswordValidation)) {
                if (authService.changePassword(currentUser, oldPassword, newPassword)) {
                    IO.println("Password changed successfully");
                    running = false;
                } else {
                    IO.println("The old password was incorrect, try again.");
                }
            } else {
                IO.println("The new password didn't match, try again");
            }
        }
    }

    private void clearConsole() {
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
    }
}
