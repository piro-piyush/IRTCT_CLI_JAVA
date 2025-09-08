package org.example.ticket.utils;

import org.example.ticket.entities.Coach;
import java.util.*;

public class SeatUtils {

    /**
     * Returns list of available seat numbers for a given coach.
     * Indexing starts from 1 (not 0).
     *
     * @param coach the coach object
     * @return list of seat numbers that are not booked
     */
    public static List<Integer> getAvailableSeatNumbers(Coach coach) {
        List<Integer> availableSeats = new ArrayList<>();

        for (int seatNum = 1; seatNum <= coach.getTotalSeats(); seatNum++) {
            if (!coach.getSeats().containsKey(seatNum)) {
                availableSeats.add(seatNum); // seat not booked
            }
        }

        return availableSeats;
    }

    /**
     * Returns the first available seat number in the given coach.
     * Indexing starts from 1 (not 0).
     *
     * @param coach the coach object
     * @return first available seat number, or -1 if no seats available
     */
    public static int getFirstAvailableSeat(Coach coach) {
        for (int seatNum = 1; seatNum <= coach.getTotalSeats(); seatNum++) {
            if (!coach.getSeats().containsKey(seatNum)) {
                return seatNum; // first empty seat found
            }
        }
        return -1; // no seat available
    }
}
