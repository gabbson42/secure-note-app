package project.ui;

import project.model.Note;
import project.model.User;
import project.service.AdminService;
import project.service.AuthService;
import project.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner input = new Scanner(System.in);
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();
    private User currentUser;
    private boolean adminTools = false;

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
            userMenu();

        } else {
            IO.println("Username or Password is incorrect");
        }
    }

    private void userMenu() {
        boolean running = true;

        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            while (running) {
                IO.println("""
                          Secure Notes - ADMIN
                        ------------------------
                        1. Create note
                        2. View notes
                        3. Change password
                        4. Admin tools
                        0. Log out
                        ------------------------""");

                String choice = input.nextLine();

                switch (choice) {
                    case "1" -> createNote();
                    case "2" -> viewNotes(currentUser);
                    case "3" -> changePassword();
                    case "4" -> adminMenu();
                    case "0" -> running = false;
                    default -> IO.println("Invalid choice");
                }
            }
        } else {
            while (running) {
                IO.println("""
                              Secure Notes
                        ------------------------
                        1. Create note
                        2. View notes
                        3. Change password
                        0. Log out
                        ------------------------""");

                String choice = input.nextLine();

                switch (choice) {
                    case "1" -> createNote();
                    case "2" -> viewNotes(currentUser);
                    case "3" -> changePassword();
                    case "0" -> running = false;
                    default -> IO.println("Invalid choice");
                }
            }
        }
    }

    private void adminMenu() {
        boolean running = true;

        while (running) {
            adminTools = true;
            IO.println("""
                          Admin Tools
                    ------------------------
                    1. View all Users
                    2. View all Notes
                    0. Return
                    ------------------------""");

            String choice = input.nextLine();

            switch (choice) {
                case "1" -> viewUsers();
                case "2" -> viewNotes(currentUser);
                case "0" -> {
                    running = false;
                    adminTools = false;
                }
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
            pressEnterToContinue();
        } else {
            IO.println("Something went wrong, try again");
        }
    }

    private void viewNotes(User user) {
        List<Note> notes;

        while (true) {
            if (adminTools) {
                notes = adminService.getAllUsers().stream()
                        .flatMap(u -> u.getNotes().stream()).toList();
                IO.println("""
                               All notes
                        ------------------------""");
                int i = 1;
                for (Note note : notes) {
                    IO.println(i + ". " + note.getTitle());
                    i++;
                }
            } else {
                notes = user.getNotes();
                IO.println("""
                                 Notes
                        ------------------------""");
                int i = 1;
                for (Note note : notes) {
                    IO.println(i + ". " + note.getTitle());
                    i++;
                }
            }
            IO.println("0. Return");
            IO.println("------------------------");

            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("0")) {
                break;
            }

            boolean running = true;

            try {
                while (running) {
                    int noteIndex = Integer.parseInt(choice) - 1;
                    int noteId = notes.get(noteIndex).getId();
                    String noteTitle = notes.get(noteIndex).getTitle();
                    String noteContent = notes.get(noteIndex).getContent();

                    IO.println(noteTitle);
                    IO.println("---------");
                    IO.println(noteContent);
                    IO.println("---------");
                    if (adminTools) {
                        IO.println("1. Delete note, 0. Return");
                    } else {
                        IO.println("1. Edit note, 2. Delete note, 0. Return");
                    }

                    choice = input.nextLine();

                    if (adminTools) {
                        switch (choice) {
                            case "1" -> {
                                if (deleteNote(noteId)) {
                                    IO.println("Note successfully deleted");
                                    user = userService.updateCurrentUser(user.getUsername());
                                }
                                pressEnterToContinue();
                                running = false;
                            }
                            case "0" -> running = false;
                            default -> IO.println("Invalid choice, try again");
                        }
                    } else {
                        switch (choice) {
                            case "1" -> {
                                if (editNote(noteTitle, noteContent, noteId)) {
                                    IO.println("Changes saved successfully!");
                                    user = userService.updateCurrentUser(user.getUsername());
                                }
                                pressEnterToContinue();
                                running = false;
                            }
                            case "2" -> {
                                if (deleteNote(noteId)) {
                                    IO.println("Note successfully deleted");
                                    user = userService.updateCurrentUser(user.getUsername());
                                }
                                pressEnterToContinue();
                                running = false;
                            }
                            case "0" -> running = false;
                            default -> IO.println("Invalid choice, try again");
                        }
                    }
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                IO.println("Invalid input");
            }
        }
    }

    private void viewUsers() {
        while (true) {
            List<User> userList = adminService.getAllUsers();
            User tempUser;
            String choice;

            IO.println("""
                           All Users
                    ------------------------""");
            int i = 1;
            for (User user : userList) {
                IO.println(i + ". " + user.getUsername() + " - " + user.getRole());
                i++;
            }
            IO.println("0. Return");
            IO.println("------------------------");

            choice = input.nextLine();

            if (choice.equalsIgnoreCase("0")) {
                break;
            }

            boolean running = true;

            try {
                while (running) {
                    tempUser = userList.get(Integer.parseInt(choice) - 1);

                    IO.println("""
                                User Information
                            ------------------------""");
                    IO.println("ID: " + tempUser.getId() +
                            "\nUsername: " + tempUser.getUsername() +
                            "\nRole: " + tempUser.getRole() +
                            "\nNumber of notes: " + tempUser.getNotes().size());
                    IO.println("------------------------");
                    IO.println("1. View notes, 2. Edit role, 3. Delete user, 0. Return");

                    choice = input.nextLine();
                    switch (choice) {
                        case "1" -> {
                            adminTools = false;
                            viewNotes(tempUser);
                            running = false;
                        }
                        case "2" -> {
                            if (editRole(tempUser)) {
                                IO.println("User role successfully updated");
                            }
                            pressEnterToContinue();
                            running = false;
                        }
                        case "3" -> {
                            if (deleteUser(tempUser.getId())) {
                                IO.println("User successfully deleted");
                            }
                            pressEnterToContinue();
                            running = false;
                        }
                        case "0" -> running = false;
                        default -> IO.println("Invalid input");
                    }
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                IO.println("Invalid input");
            }
        }
    }

    public boolean editNote(String title, String noteContent, int noteId) {
        boolean running = true;

        String newTitle = title;
        String newNoteContent = noteContent;

        while (running) {
            IO.println("""
                              Edit
                    ------------------------
                    1. Content
                    2. Title
                    3. Both
                    0. Return
                    ------------------------""");

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
                case "0" -> {
                    return false;
                }
                default -> IO.println("Invalid input, try again.");
            }
        }
        return userService.editNote(newTitle, newNoteContent, noteId);
    }

    public boolean deleteNote(int noteId) {
        while (true) {
            IO.println("Are you sure you want to delete the note? (Y/N) ");
            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                return userService.deleteNote(noteId);
            } else if (choice.equalsIgnoreCase("N")) {
                return false;
            } else {
                IO.println("Invalid input, try again");
            }
        }
    }

    private boolean editRole(User user) {
        if (user.getId() == currentUser.getId()) {
            IO.println("You can't edit your own role");
            return false;
        }

        String oppositeRole = user.getRole().equalsIgnoreCase("USER") ? "ADMIN" : "USER";

        while (true) {
            IO.println("User " + user.getUsername() + " has role " + user.getRole() + ".\n" +
                    "Do you want to change the role to " + oppositeRole + "?(Y/N)");

            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                return adminService.editUserRole(user.getId(), oppositeRole);
            } else if (choice.equalsIgnoreCase("N")) {
                return false;
            } else {
                IO.println("Invalid input");
            }
        }
    }

    private boolean deleteUser(int userId) {
        while (true) {
            IO.println("Are you sure you want to delete the user? (Y/N) ");
            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                return adminService.deleteUser(userId);
            } else if (choice.equalsIgnoreCase("N")) {
                return false;
            } else {
                IO.println("Invalid input, try again");
            }
        }
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
                    pressEnterToContinue();
                    running = false;
                } else {
                    IO.println("The old password was incorrect, try again.");
                }
            } else {
                IO.println("The new password didn't match, try again");
            }
        }
    }

    private void pressEnterToContinue() {
        IO.println("-----------------------");
        IO.println("Press enter to continue");
        input.nextLine();
    }
}
