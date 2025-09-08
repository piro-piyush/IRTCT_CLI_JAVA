package org.example.ticket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a system user (not the actual passenger).
 * Required fields: id, name, email, passwordHash
 * Nullable fields: phoneNumber, aadhaarUid
 */
@Data
@NoArgsConstructor // Jackson deserialization ke liye default constructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    // ------------------- Required Fields -------------------
    private String id;           // Unique user ID
    private String name;         // Full name
    private String email;        // Email ID (login)
    private String passwordHash; // Encrypted passwordl̥

    // ------------------- Nullable Fields -------------------
    private String phoneNumber;  // Optional
    private String aadhaarUid;   // Optional

    // ------------------- Collections -------------------
    private List<Ticket> tickets = new ArrayList<>(); // Tickets booked by the user

    // ------------------- All-args Constructor (Required Fields) -------------------
    public User(String id, String name, String email, String passwordHash) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
        this.tickets = new ArrayList<>();
    }

    // ------------------- Helper Methods -------------------

    /**
     * Adds a booked ticket to the user's account.
     */
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    /**
     * Cancels a ticket by removing it from user's account.
     */
    public void cancelTicket(String ticketId) {
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket.getId().equals(ticketId)) {
                ticket.cancelTicket();
                tickets.set(i, ticket);
                return;
            }
        }
    }


    public String getAadhaarNumber() {
        return  this.aadhaarUid;
    }

    /**
     * Returns whether this user has verified details (phone and Aadhaar).
     */
    public boolean hasVerified() {
        return phoneNumber != null && !phoneNumber.isEmpty()
                && aadhaarUid != null && !aadhaarUid.isEmpty();
    }

    // ------------------- toString -------------------
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + (name != null ? name : "N/A") + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + (phoneNumber != null ? phoneNumber : "N/A") + '\'' +
                ", aadhaarUid='" + (aadhaarUid != null ? aadhaarUid : "N/A") + '\'' +
                ", totalTickets=" + tickets.size() +
                ", hasVerified=" + hasVerified() +
                '}';
    }

}
