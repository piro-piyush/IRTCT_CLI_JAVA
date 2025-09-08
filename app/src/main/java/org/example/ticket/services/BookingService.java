package org.example.ticket.services;

import org.example.ticket.entities.*;
import org.example.ticket.utils.ColorUtils;
import org.example.ticket.utils.SeatUtils;

import java.time.LocalDate;
import java.util.*;

public class BookingService {
    public static  Scanner scanner = new Scanner(System.in);

    public static void bookTickets(UserService userService,TicketService ticketService,LoginService loginService) {
        final User user = userService.getCurrentUser();

        // ---------------- VERIFY USER ----------------
        if (!user.hasVerified()) {
            System.out.println(ColorUtils.RED +
                    "⚠ You need to fill your Aadhaar and Phone number to proceed!" +
                    ColorUtils.RESET);
            return;
        }

        // ---------------- ASK TRAIN NUMBER ----------------
        System.out.print(ColorUtils.CYAN + "Enter train Id or 'back' to cancel: " + ColorUtils.RESET);
        String trainNumber = scanner.nextLine().trim();
        if (trainNumber.equalsIgnoreCase("back")) return;

        // ---------------- FETCH TRAIN ----------------
        Train selectedTrain = ticketService.getTrainByNumber(trainNumber);
        if (selectedTrain == null) {
            System.out.println(ColorUtils.RED + "❌ Train not found!" + ColorUtils.RESET);
            return;
        }

        // Show train info
        System.out.println(ColorUtils.CYAN + "\n✅ Train Found!" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "Train ID: " + ColorUtils.RESET + selectedTrain.getId());
        System.out.println(ColorUtils.YELLOW + "Train Name: " + ColorUtils.RESET + selectedTrain.getName());
        System.out.println(ColorUtils.YELLOW + "Source: " + ColorUtils.RESET +
                selectedTrain.getSource().getCode() + " - " + selectedTrain.getSource().getName());
        System.out.println(ColorUtils.YELLOW + "Destination: " + ColorUtils.RESET +
                selectedTrain.getDestination().getCode() + " - " + selectedTrain.getDestination().getName());

        // ---------------- SHOW COACHES ----------------
        System.out.println(ColorUtils.CYAN + "\nAvailable Coaches:" + ColorUtils.RESET);
        for (Coach coach : selectedTrain.getCoaches()) {
            System.out.println(ColorUtils.YELLOW +
                    "Coach ID: " + ColorUtils.RESET + coach.getId() +
                    " | Type: " + coach.getType() +
                    " | Price: ₹" + coach.getPrice() +
                    " | Seats: " + coach.getAvailableSeats() + "/" + coach.getTotalSeats());
        }

        // ---------------- ASK COACH ----------------
        Coach selectedCoach = null;
        while (selectedCoach == null) {
            System.out.print(ColorUtils.CYAN + "Enter coach ID: " + ColorUtils.RESET);
            String coachId = scanner.nextLine().trim();

            selectedCoach = selectedTrain.getCoaches().stream()
                    .filter(c -> c.getId().equalsIgnoreCase(coachId))
                    .findFirst()
                    .orElse(null);

            if (selectedCoach == null) {
                System.out.println(ColorUtils.RED + "⚠ Invalid coach ID! Try again." + ColorUtils.RESET);
            }
        }

        // ---------------- CHECK SEAT AVAILABILITY ----------------
        int availableSeats = selectedCoach.getAvailableSeats();
        if (availableSeats <= 0) {
            System.out.println(ColorUtils.RED +
                    "⚠ No seats available in this coach!" +
                    ColorUtils.RESET);
            return;
        }

        System.out.println(ColorUtils.GREEN + "✅ " + availableSeats +
                " seats available in coach " + selectedCoach.getId() +
                ColorUtils.RESET);

        // ---------------- ADD PASSENGERS ----------------
        List<Passenger> passengers = new ArrayList<>();
        boolean addingPassengers = true;

        while (addingPassengers) {
            System.out.print(ColorUtils.CYAN + "Enter passenger name: " + ColorUtils.RESET);
            String name = scanner.nextLine().trim();

            System.out.print(ColorUtils.CYAN + "Enter passenger age: " + ColorUtils.RESET);
            int age;
            try {
                age = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(ColorUtils.RED + "⚠ Invalid age! Try again." + ColorUtils.RESET);
                continue;
            }

            passengers.add(new Passenger(name, age, selectedCoach.getId(), 0));

            if (passengers.size() < availableSeats) {
                System.out.print(ColorUtils.CYAN + "Add another passenger? (y/n): " + ColorUtils.RESET);
                String more = scanner.nextLine().trim();
                if (!more.equalsIgnoreCase("y")) addingPassengers = false;
            } else {
                System.out.println(ColorUtils.YELLOW + "Maximum seats reached in this coach." + ColorUtils.RESET);
                addingPassengers = false;
            }
        }

        // ---------------- SHOW SUMMARY BEFORE PAYMENT ----------------
        double totalFare = selectedCoach.getPrice() * passengers.size();
        System.out.println(ColorUtils.CYAN + "\nBooking Summary:" + ColorUtils.RESET);
        System.out.println("Coach: " + selectedCoach.getId() + " | Type: " + selectedCoach.getType());
        System.out.println("Passengers: " + passengers.size());
        System.out.println("Total Fare: ₹" + totalFare);

        System.out.print("Do you want to proceed to payment? (y/n): ");
        String proceed = scanner.nextLine().trim();
        if (!proceed.equalsIgnoreCase("y")) {
            System.out.println("❌ Booking cancelled by user." + ColorUtils.RESET);
            return;
        }

        // ---------------- PAYMENT ----------------
        boolean paymentDone = false;
        long startTime = System.currentTimeMillis();

        while (!paymentDone && System.currentTimeMillis() - startTime < 5 * 60 * 1000) {
            System.out.println(ColorUtils.CYAN + "\nSelect payment method:" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "[1] UPI" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "[2] Credit Card" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "[3] Net Banking" + ColorUtils.RESET);

            System.out.print(ColorUtils.CYAN + "Enter payment option: " + ColorUtils.RESET);
            String paymentChoice = scanner.nextLine().trim();
            switch (paymentChoice) {
                case "1", "2", "3" -> paymentDone = true;
                default -> System.out.println(ColorUtils.RED + "⚠ Invalid option! Try again." + ColorUtils.RESET);
            }
        }

        if (!paymentDone) {
            System.out.println(ColorUtils.RED + "❌ Payment timeout! Booking failed." + ColorUtils.RESET);
            return;
        }

        // ---------------- GENERATE TICKET AND ALLOCATE SEATS ----------------
        String ticketId = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String secret = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Ticket ticket = new Ticket(ticketId, selectedTrain.getId(), user.getId(),
                secret, LocalDate.now(), totalFare, passengers.size());

        for (Passenger passenger : passengers) {
            int seatNumber = SeatUtils.getFirstAvailableSeat(selectedCoach);
            passenger.setSeatNumber(seatNumber);
            ticket.addPassenger(selectedCoach.getId(), String.valueOf(seatNumber), passenger);
            selectedTrain.bookSeat(selectedCoach.getId(), seatNumber, passenger);
        }

        // ---------------- SAVE TICKET ----------------
        user.addTicket(ticket);
        loginService.updateUser(user);
        ticketService.saveOrUpdateTrain(selectedTrain);
        System.out.println(ColorUtils.GREEN + "✅ Booking successful! Ticket details:" + ColorUtils.RESET);
        System.out.println(ticket.getAsciiTicket());
    }


    public static void cancelTicket(UserService userService, TicketService ticketService, LoginService loginService) {
        final User user = userService.getCurrentUser();

        // Get user's booked tickets
        List<Ticket> tickets = user.getTickets();
        if (tickets.isEmpty()) {
            System.out.println(ColorUtils.YELLOW + "⚠ You have no booked tickets." + ColorUtils.RESET);
            return;
        }

        // Show list of tickets
        System.out.println(ColorUtils.CYAN + "\nYour Tickets:" + ColorUtils.RESET);
        for (Ticket t : tickets) {
            System.out.println("- Ticket ID: " + ColorUtils.YELLOW + t.getId() + ColorUtils.RESET
                    + " | Train: " + t.getTrainId()
                    + " | Date: " + t.getFormattedJourneyDate()
                    + " | Cancelled: " + (t.isHasCancelled() ? "Yes" : "No"));
        }

        Scanner scanner = new Scanner(System.in);

        // Ask for Ticket ID
        System.out.print(ColorUtils.CYAN + "\nEnter Ticket ID to cancel or 'back': " + ColorUtils.RESET);
        String ticketId = scanner.nextLine().trim();
        if (ticketId.equalsIgnoreCase("back")) return;

        Ticket ticketToCancel = tickets.stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElse(null);

        if (ticketToCancel == null) {
            System.out.println(ColorUtils.RED + "❌ Ticket not found!" + ColorUtils.RESET);
            return;
        }

        if (ticketToCancel.isHasCancelled()) {
            System.out.println(ColorUtils.YELLOW + "⚠ Ticket is already cancelled." + ColorUtils.RESET);
            return;
        }

        // Ask for security number
        System.out.print(ColorUtils.CYAN + "Enter security number for this ticket: " + ColorUtils.RESET);
        String securityInput = scanner.nextLine().trim();

        if (!ticketToCancel.verifySecurityNumber(securityInput)) {
            System.out.println(ColorUtils.RED + "❌ Invalid security number. Cannot cancel ticket." + ColorUtils.RESET);
            return;
        }

        // Cancel ticket
        ticketToCancel.cancelTicket();
        user.cancelTicket(ticketToCancel.getId());
        loginService.updateUser(user);

        // Free booked seats in train
        Train train = ticketService.getTrainByNumber(ticketToCancel.getTrainId());
        if (train != null) {
            for (Map.Entry<String, Passenger> entry : ticketToCancel.getPassengersSafe().entrySet()) {
                String key = entry.getKey();
                Passenger passenger = entry.getValue();
                train.cancelSeat(passenger.getCoach(), passenger.getSeatNumber());
            }
            ticketService.saveOrUpdateTrain(train);
            loginService.updateUser(user);
        }

        System.out.println(ColorUtils.GREEN + "✅ Ticket cancelled successfully!" + ColorUtils.RESET);
    }

    public static void seeTicketsBooked(UserService userService, TicketService ticketService, LoginService loginService) {
        List<Ticket> tickets = ticketService.getBookedTickets();
        if (tickets.isEmpty()) {
            System.out.println(ColorUtils.RED + "\n❌ No tickets booked yet!" + ColorUtils.RESET);
            return;
        }

        int count = 1;
        for (Ticket ticket : tickets) {
            System.out.println(ColorUtils.CYAN + "\n====================================================" + ColorUtils.RESET);
            System.out.println(ColorUtils.YELLOW + "Ticket #" + count++ + ColorUtils.RESET);
            System.out.println(ColorUtils.GREEN + "🆔 Ticket ID: " + ColorUtils.RESET + ticket.getId());
            System.out.println(ColorUtils.GREEN + "🚆 Train ID: " + ColorUtils.RESET + ticket.getTrainId());
            System.out.println(ColorUtils.GREEN + "📅 Journey Date: " + ColorUtils.RESET + ticket.getJourneyDate());
            System.out.println(ColorUtils.GREEN + "💰 Price (per seat): " + ColorUtils.RESET + "₹" + ticket.getPrice());
            System.out.println(ColorUtils.GREEN + "👥 Seats Booked: " + ColorUtils.RESET + ticket.getSeats());
            System.out.println(ColorUtils.GREEN + "❌ Cancelled: " + ColorUtils.RESET + (ticket.isHasCancelled() ? "Yes" : "No"));

            if (!ticket.getPassengers().isEmpty()) {
                System.out.println(ColorUtils.CYAN + "----------------------------------------------------" + ColorUtils.RESET);
                System.out.println(ColorUtils.BLUE + "   👤 Passenger Details:" + ColorUtils.RESET);
                for (Map.Entry<String, Passenger> e : ticket.getPassengers().entrySet()) {
                    Passenger p = e.getValue();
                    System.out.println("   - " + ColorUtils.YELLOW + p.getName() + ColorUtils.RESET +
                            " | Age: " + p.getAge() + " | Coach: " + p.getCoach() +
                            " | Seat: " + p.getSeatNumber());
                }
            }
        }
        System.out.println(ColorUtils.CYAN + "====================================================" + ColorUtils.RESET);
    }



    public static void searchTrains(UserService userService, TicketService ticketService, LoginService loginService) {
        while (true) {
            System.out.print("\nEnter train name or number (or 'exit' to go back): ");
            String query = scanner.nextLine().trim();
            if (query.equalsIgnoreCase("exit")) break;

            List<Train> results = ticketService.searchTrains(query);
            if (results.isEmpty()) {
                System.out.println(ColorUtils.RED + "No trains found matching '" + query + "'." + ColorUtils.RESET);
            } else {
                System.out.println("-------------------------------------------------------------------------------------------------");
                System.out.printf("| %-5s | %-25s | %-10s | %-10s | %-6s | %-6s |\n",
                        "ID", "Name", "Source", "Destination", "Total", "Avail");
                System.out.println("-------------------------------------------------------------------------------------------------");
                for (Train t : results) {
                    System.out.printf("| %-5s | %-25s | %-10s | %-10s | %-6d | %-6d |\n",
                            t.getId(), t.getName(), t.getSource().getCode(), t.getDestination().getCode(),
                            t.getTotalSeats(), t.getAvailableSeats());
                }
                System.out.println("-------------------------------------------------------------------------------------------------");
            }
        }
    }
}
