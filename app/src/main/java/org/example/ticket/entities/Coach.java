package org.example.ticket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Coach in a Train.
 * Each coach belongs to a train, has a type, and manages its seats.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coach {
    private String id;
    private String trainId;
    private String type;
    private int totalSeats;
    private double price;
    private Map<Integer, Passenger> seats = new HashMap<>();

    // All-args constructor
    public Coach(String id, String trainId, String type, int totalSeats, double price) {
        this.id = id;
        this.trainId = trainId;
        this.type = type;
        this.totalSeats = totalSeats;
        this.price = price;
        this.seats = new HashMap<>();
    }

    // ------------------- Helper Methods -------------------

    public int getAvailableSeats() {
        return totalSeats - seats.size();
    }

    public boolean bookSeat(int seatNumber, Passenger passenger) {
        if (seatNumber < 1 || seatNumber > totalSeats) {
            System.out.println("❌ Invalid seat number!");
            return false;
        }
        if (seats.containsKey(seatNumber)) {
            System.out.println("❌ Seat already booked!");
            return false;
        }
        seats.put(seatNumber, passenger);
        System.out.println("✅ Seat " + seatNumber + " booked for " + passenger.getName()
                + " | Price: ₹" + price);
        return true;
    }

    public boolean cancelSeat(int seatNumber) {
        if (!seats.containsKey(seatNumber)) {
            System.out.println("❌ No booking found for this seat.");
            return false;
        }
        seats.remove(seatNumber);
        System.out.println("✅ Seat " + seatNumber + " cancelled.");
        return true;
    }

    public boolean isSeatAvailable(int seatNumber) {
        return !seats.containsKey(seatNumber);
    }
}
