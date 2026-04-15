package project.ui;

import project.model.User;
import project.service.AuthService;

import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner input = new Scanner(System.in);
    private final AuthService service = new AuthService();
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

        boolean success = service.register(username, password);

        if (success) {
            IO.println("User registered successfully");
        } else {
            IO.println("Username or Password is incorrect");
        }
    }

    private void login(){
        IO.println("Enter your username: ");
        String username = input.nextLine();

        IO.println("Enter your password: ");
        String password = input.nextLine();

        currentUser = service.login(username, password);

        if (currentUser != null) {
            IO.println("Login successful");
            userMenu();
        } else {
            IO.println("Username or Password is incorrect");
        }
    }

    private void userMenu(){
        boolean running = true;

        while(running) {
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

    private void createNote(){
        IO.println("Not functional at the moment");
    }

    private void viewNotes(){
        IO.println("Not functional at the moment");
    }

    private void changePassword(){
        IO.println("Not functional at the moment");
    }
}
