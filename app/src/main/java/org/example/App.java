package org.example;

import org.example.ticket.menus.AppMenu;
import org.example.ticket.services.LoginService;
import org.example.ticket.utils.ColorUtils;

import java.util.concurrent.TimeUnit;

/**
 * Entry point of the IRCTC Ticket Application.
 * <p>
 * Shows landing menu by default and delegates all options to AppMenu.
 */
public class App {

    public static void main(String[] args) {
        // Initialize services
        LoginService loginService = new LoginService();

        // Initialize app menu
        AppMenu appMenu = new AppMenu(loginService);

        int option;
        do {
            option = appMenu.showLandingOptions();

            switch (option) {
                case 1, 2 -> appMenu.handleLandingOption(option); // Create user or login
                case 3 -> exitApp(); // Exit the app
                default -> System.out.println(ColorUtils.RED + "⚠️ Invalid option! Try again." + ColorUtils.RESET);
            }

        } while (option != 3); // Loop until Exit is chosen
    }


    /**
     * Gracefully exits the application with countdown.
     */
    public static void exitApp() {
        try {
            System.out.println(ColorUtils.YELLOW + "⚠ Exiting app in 5 seconds..." + ColorUtils.RESET);
            for (int i = 5; i > 0; i--) {
                System.out.println("Closing in " + i + "...");
                TimeUnit.SECONDS.sleep(1);
            }
            System.exit(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(ColorUtils.RED + "Sleep interrupted! Exiting immediately." + ColorUtils.RESET);
            System.exit(1);
        }
    }
}
