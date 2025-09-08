package org.example.ticket.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.ticket.utils.ColorUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents a train ticket with booking details.
 * Each ticket can have multiple passengers and tracks its cancellation state.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Ticket {

    private final String id;                        // Unique ticket ID
    private final String trainId;                   // Associated train ID
    private final String bookerId;                  // User ID of the person who booked
    private final String securityNumber;            // For cancelling ticket securely
    private final double price;                     // Price per seat
    private final int seats;                        // Number of seats booked
    private LocalDate journeyDate;                  // Date of the journey
    private boolean hasCancelled;                   // Ticket cancellation status

    // Passenger details (key = coachNumber-seatNumber)
    private final Map<String, Passenger> passengers = new HashMap<>();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ------------------- Jackson Constructor ----------------
    @JsonCreator
    public Ticket(
            @JsonProperty("id") String id,
            @JsonProperty("trainId") String trainId,
            @JsonProperty("bookerId") String bookerId,
            @JsonProperty("securityNumber") String securityNumber,
            @JsonProperty("price") double price,
            @JsonProperty("seats") int seats,
            @JsonProperty("journeyDate") LocalDate journeyDate,
            @JsonProperty("hasCancelled") boolean hasCancelled,
            @JsonProperty("passengers") Map<String, Passenger> passengers
    ) {
        this.id = id;
        this.trainId = trainId;
        this.bookerId = bookerId;
        this.securityNumber = securityNumber;
        this.price = price;
        this.seats = seats;
        this.journeyDate = journeyDate;
        this.hasCancelled = hasCancelled;
        if (passengers != null) this.passengers.putAll(passengers);
    }

    // ------------------- Builder Constructor ----------------
    @Builder
    public Ticket(String id,
                  String trainId,
                  String bookerId,
                  String securityNumber,
                  LocalDate journeyDate,
                  double price,
                  int seats) {

        if (price <= 0) throw new IllegalArgumentException("Price must be greater than 0");
        if (seats <= 0) throw new IllegalArgumentException("Seats must be at least 1");

        this.id = Objects.requireNonNull(id, "Ticket ID cannot be null");
        this.trainId = Objects.requireNonNull(trainId, "Train ID cannot be null");
        this.bookerId = Objects.requireNonNull(bookerId, "Booker ID cannot be null");
        this.securityNumber = (securityNumber == null || securityNumber.isBlank())
                ? UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : securityNumber;

        this.journeyDate = journeyDate == null ? LocalDate.now() : journeyDate;
        this.price = price;
        this.seats = seats;
        this.hasCancelled = false;
    }

    // ------------------- Helper Methods -------------------

    /** Returns total cost of this booking. Not serialized. */
    @JsonIgnore
    public double getTotalPrice() {
        return price * seats;
    }

    /** Adds a passenger with coach and seat number. */
    public void addPassenger(String coachNumber, String seatNumber, Passenger passenger) {
        if (hasCancelled) throw new IllegalStateException("Cannot add passenger. Ticket is cancelled!");
        if (passengers.size() >= seats) throw new IllegalStateException("Cannot add more passengers than booked seats!");
        String key = coachNumber + "-" + seatNumber;
        if (passengers.containsKey(key)) throw new IllegalStateException("Seat already occupied: " + key);
        passengers.put(key, passenger);
    }

    /** Removes a passenger by seat reference (coach-seat). */
    public Passenger removePassenger(String coachNumber, String seatNumber) {
        String key = coachNumber + "-" + seatNumber;
        return passengers.remove(key);
    }

    /** Cancels the ticket and clears passengers. */
    public void cancelTicket() {
        this.hasCancelled = true;
        this.passengers.clear();
    }

    /** Returns number of passengers filled in this ticket. Not serialized. */
    @JsonIgnore
    public int getPassengerCount() {
        return passengers.size();
    }

    /** Returns a formatted date string for UI. */
    @JsonIgnore
    public String getFormattedJourneyDate() {
        return journeyDate.format(DATE_FORMATTER);
    }

    /** Returns a colored ASCII ticket for console output using ColorUtils. Not serialized. */
    @JsonIgnore
    public String getAsciiTicket() {
        StringBuilder sb = new StringBuilder();

        String[][] info = {
                {"Ticket ID", id},
                {"Train ID", trainId},
                {"Booker ID", bookerId},
                {"Journey", getFormattedJourneyDate()},
                {"Seats", String.valueOf(seats)},
                {"Price", String.format("₹%.2f", getTotalPrice())},
                {"Cancelled", hasCancelled ? "Yes" : "No"}
        };

        int leftColWidth = 12;
        int rightColWidth = 50;
        String line = "+" + "-".repeat(leftColWidth + 2) + "+" + "-".repeat(rightColWidth + 2) + "+\n";

        sb.append(line);
        sb.append("| 🎫 TRAIN TICKET").append(" ".repeat(leftColWidth + rightColWidth - 11)).append("|\n");
        sb.append(line);

        for (String[] row : info) {
            sb.append(String.format("| %-" + leftColWidth + "s | %-" + rightColWidth + "s |\n", row[0], row[1]));
        }
        sb.append(line);

        sb.append(String.format("| %-4s | %-5s | %-25s | %-3s |\n", "Seat", "Coach", "Name", "Age"));
        sb.append(line);

        for (Map.Entry<String, Passenger> entry : passengers.entrySet()) {
            Passenger p = entry.getValue();
            sb.append(String.format("| %-4d | %-5s | %-25s | %-3d |\n", p.getSeatNumber(), p.getCoach(), p.getName(), p.getAge()));
        }
        sb.append(line);

        return sb.toString();
    }

    /** Verifies if the provided security number matches this ticket's security number. */
    public boolean verifySecurityNumber(String input) {
        if (input == null) return false;
        return this.securityNumber.equalsIgnoreCase(input.trim());
    }

    /** Returns immutable view of passengers for safety. Not serialized. */
    @JsonIgnore
    public Map<String, Passenger> getPassengersSafe() {
        return Collections.unmodifiableMap(passengers);
    }
}
