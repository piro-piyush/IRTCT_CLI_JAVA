package org.example.ticket.menus;

import org.example.ticket.entities.*;
import org.example.ticket.services.BookingService;
import org.example.ticket.services.LoginService;
import org.example.ticket.services.TicketService;
import org.example.ticket.services.UserService;
import org.example.ticket.utils.ColorUtils;

import java.util.*;

/**
 * MainMenu handles the user-facing menu after login.
 * Provides options to search trains, view tickets, book/cancel tickets,
 * update user info, or logout.
 */
public class MainMenu {

    private final TicketService ticketService;
    private final UserService userService;
    private final UserMenu userMenu;
    private final Scanner scanner = new Scanner(System.in);
    private final LoginService loginService;

    /**
     * Constructor for MainMenu.
     *
     * @param ticketService TicketService instance
     * @param userService   UserService instance
     * @param userMenu      UserMenu instance
     */
    public MainMenu(TicketService ticketService, UserService userService, UserMenu userMenu,LoginService loginService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.userMenu = userMenu;
        this.loginService = loginService;
    }


    /**
     * Displays the main menu and validates user input.
     *
     * @return selected option as int
     */
    public int showMenu() {
        System.out.println(ColorUtils.CYAN + "====================================================" + ColorUtils.RESET);
        System.out.println(ColorUtils.BLUE + "                 🚆 MAIN MENU                  " + ColorUtils.RESET);
        System.out.println(ColorUtils.CYAN + "====================================================" + ColorUtils.RESET);

        System.out.println(ColorUtils.YELLOW + "   [1] 🔍 Search Trains" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [2] 🎟 See Tickets Booked" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [3] 📝 Book Tickets" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [4] ❌ Cancel Ticket" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [5] ⚙️ Update User" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [6] 🚪 Log Out" + ColorUtils.RESET);
        System.out.println(ColorUtils.CYAN + "----------------------------------------------------" + ColorUtils.RESET);
        System.out.print("👉 " + ColorUtils.GREEN + "Enter your option: " + ColorUtils.RESET);

        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 6) {
                return choice;
            } else {
                System.out.println(ColorUtils.RED + "⚠️ Invalid option! Please select 1-6." + ColorUtils.RESET);
                return showMenu();
            }
        } catch (NumberFormatException e) {
            System.out.println(ColorUtils.RED + "⚠️ Invalid input! Please enter a number." + ColorUtils.RESET);
            return showMenu();
        }
    }

    /**
     * Handles the selected action from the menu.
     *
     * @param option selected option
     */
    public void handleAction(int option) {
        switch (option) {
            case 1 -> BookingService.searchTrains(  userService,
                    ticketService,
                    loginService);
            case 2 -> BookingService.seeTicketsBooked(
                    userService,
                    ticketService,
                    loginService
            );
            case 3 -> BookingService.bookTickets(
                    userService,
                    ticketService,
                    loginService
            );
            case 4 -> BookingService.cancelTicket(
                    userService,
                    ticketService,
                    loginService
            );
            case 5 -> updateUser();
            default ->
                    System.out.println(ColorUtils.RED + "⚠️ Unknown option! This should not happen." + ColorUtils.RESET);
        }
    }





    /**
     * Updates user details until user chooses to go back (0)
     */
    private void updateUser() {
        final User user = userService.getCurrentUser();
        int option;

        do {
            option = userMenu.showMenu(user);
            if (option == -1) {
                continue;
            }
            if (option == 0) {
                return;
            }
            // Handle valid options
            userMenu.handleAction(option, user);

        } while (true);
    }
}


