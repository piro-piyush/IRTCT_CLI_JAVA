package org.example.ticket.menus;

import org.example.ticket.entities.User;
import org.example.ticket.services.LoginService;
import org.example.ticket.services.TicketService;
import org.example.ticket.services.UserService;
import org.example.ticket.utils.ColorUtils;

import java.util.Scanner;

/**
 * AppMenu handles the landing menu of the IRCTC Ticket App.
 * <p>
 * Responsibilities:
 * - Show landing options (Create User / Login / Exit)
 * - Handle user registration and login
 * - Initialize UserService and MainMenu after login/registration
 * - Navigate to user menu after successful login
 */
public class AppMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final LoginService loginService;
    private UserService userService = null; // Assigned after login/register
    private MainMenu mainMenu = null;       // Initialized after UserService

    public AppMenu(LoginService loginService) {
        this.loginService = loginService;
    }


    /** Shows the landing menu and returns a valid option */
    public int showLandingOptions() {
        while (true) {
            System.out.println(ColorUtils.CYAN + "====================================================" + ColorUtils.RESET);
            System.out.println(ColorUtils.BLUE + "             WELCOME TO IRCTC TICKET APP            " + ColorUtils.RESET);
            System.out.println(ColorUtils.CYAN + "====================================================" + ColorUtils.RESET);
            System.out.println(ColorUtils.RED + "                Made with " + ColorUtils.MAGENTA + "❤" + ColorUtils.RESET + ColorUtils.RED + " by Piyush" + ColorUtils.RESET);
            System.out.println(ColorUtils.CYAN + "----------------------------------------------------" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "   [1] Create a new user" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "   [2] Login to your account" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "   [3] Exit Application" + ColorUtils.RESET);
            System.out.println(ColorUtils.CYAN + "----------------------------------------------------" + ColorUtils.RESET);
            System.out.print("👉 " + ColorUtils.GREEN + "Enter your option: " + ColorUtils.RESET);

            String input = scanner.nextLine().trim();
            try {
                int option = Integer.parseInt(input);
                if (option >= 1 && option <= 3) return option;
                System.out.println(ColorUtils.RED + "❌ Invalid option! Enter 1, 2, or 3." + ColorUtils.RESET);
            } catch (NumberFormatException e) {
                System.out.println(ColorUtils.RED + "❌ Invalid input! Please enter a number." + ColorUtils.RESET);
            }
        }
    }

    /** Handles the landing menu option */
    public void handleLandingOption(int option) {
            switch (option) {
                case 1 -> {
                    createUser();
                    return;
                }
                case 2 -> {
                    login();
                    return;
                }
                default -> {
                    System.out.println(ColorUtils.RED + "❌ Invalid option! Please choose 1, 2, or 3." + ColorUtils.RESET);
                   return;
                }
            }
    }

    /** User registration flow */
    private void createUser() {
        while (true) {
            System.out.print(ColorUtils.CYAN + "📧 Enter your email (or type 'back' to go back): " + ColorUtils.RESET);
            String email = scanner.nextLine().trim();
            if (email.equalsIgnoreCase("back")) return;

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println(ColorUtils.RED + "⚠ Invalid email format!" + ColorUtils.RESET);
                continue;
            }

            if (loginService.isAlreadyRegistered(email)) {
                System.out.println(ColorUtils.RED + "⚠ Email already registered!" + ColorUtils.RESET);
                continue;
            }

            System.out.print(ColorUtils.CYAN + "👤 Enter your name: " + ColorUtils.RESET);
            String name = scanner.nextLine().trim();

            String password;
            while (true) {
                System.out.print(ColorUtils.CYAN + "🔑 Enter password (min 6 chars): " + ColorUtils.RESET);
                password = scanner.nextLine().trim();
                if (password.length() < 6) {
                    System.out.println(ColorUtils.RED + "⚠ Password too short!" + ColorUtils.RESET);
                    continue;
                }
                break;
            }

            User user = loginService.registerUser(email, name, password);

            // Assign UserService after registration
            userService = new UserService(user, loginService);
            initMainMenu();

            greetSuccessfulLogin(false);
            userMenu();
            return;
        }
    }

    /**
     * Handles user login flow with validations:
     * 1. Checks for valid email format.
     * 2. Checks if email is already registered.
     * 3. Allows 'back' to return to landing menu.
     */
    private void login() {
        while (true) {
            System.out.print(ColorUtils.CYAN + "📧 Enter your email (or 'back' to go back): " + ColorUtils.RESET);
            String email = scanner.nextLine().trim();

            // Go back option
            if (email.equalsIgnoreCase("back")) return;

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println(ColorUtils.RED + "⚠ Invalid email format! Try again." + ColorUtils.RESET);
                continue;
            }

            // Check if email is registered
            if (!loginService.isAlreadyRegistered(email)) {
                System.out.println(ColorUtils.RED + "⚠ Email not registered! Please create an account first." + ColorUtils.RESET);
                continue;
            }

            // Ask for password
            System.out.print(ColorUtils.CYAN + "🔑 Enter your password: " + ColorUtils.RESET);
            String password = scanner.nextLine().trim();

            try {
                User user = loginService.login(email, password);

                // Assign UserService after successful login
                userService = new UserService(user, loginService);
                initMainMenu();
                greetSuccessfulLogin(true);
                userMenu();
                return;
            } catch (Exception e) {
                System.out.println(ColorUtils.RED + "❌ Login failed: " + e.getMessage() + ColorUtils.RESET);
            }
        }
    }


    /** Initializes MainMenu after UserService is ready */
    private void initMainMenu() {
        if (mainMenu == null && userService != null) {
            TicketService ticketService = new TicketService(userService);
            UserMenu userMenu = new UserMenu(loginService);
            mainMenu = new MainMenu(ticketService, userService, userMenu,loginService);
        }
    }

    /** Displays user menu after login or registration */
    /** Displays the main user menu until logout (6) is selected */
    private void userMenu() {
        int option;

        do {
            option = mainMenu.showMenu();
            if (option == 6) { // Logout
                userService.logout();
                int landingOption = showLandingOptions();
                handleLandingOption(landingOption);
                return;
            }
            // Handle other valid options
            mainMenu.handleAction(option);
        } while (true);
    }



    /** Shows greeting after login or registration */
    private void greetSuccessfulLogin(boolean existingUser) {
        if (existingUser) {
            System.out.println(ColorUtils.GREEN + "✔ Login Successful! Welcome back!" + ColorUtils.RESET);
        } else {
            System.out.println(ColorUtils.GREEN + "✔ Account created successfully! Welcome!" + ColorUtils.RESET);
        }
    }
}
