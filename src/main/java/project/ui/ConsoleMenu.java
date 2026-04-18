package project.ui;

import project.model.User;
import project.service.AuthService;
import project.service.UserService;

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
                    ----Secure Note----
                    1. Login
                    2. Register user
                    0. Quit
                    """);

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
            IO.println("Login successful");
            userMenu();
        } else {
            IO.println("Username or Password is incorrect");
        }
    }

    private void userMenu() {
        boolean running = true;

        while (running) {
            IO.println("""
                    1. Create note
                    2. View notes
                    3. Change password
                    0. Log out
                    """);

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
        } else {
            IO.println("Something went wrong, try again");
        }
    }

    private void viewNotes() {
        IO.println("Not functional at the moment");
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
}
