package org.example.ticket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a train in the railway system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Train {

    private String id;                   // Unique train ID
    private String name;                 // Train name
    private Station source;              // Source station
    private Station destination;         // Destination station
    private List<Coach> coaches = new ArrayList<>();
    private Set<DayOfWeek> runningDays = new HashSet<>();

    // Custom constructor for minimal init
    public Train(String id, String name, Station source, Station destination) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
    }

    // ------------------- Helper Methods -------------------

    public int getTotalSeats() {
        return (coaches != null) ? coaches.stream().mapToInt(Coach::getTotalSeats).sum() : 0;
    }

    public int getAvailableSeats() {
        return (coaches != null) ? coaches.stream().mapToInt(Coach::getAvailableSeats).sum() : 0;
    }

    public void addCoach(Coach coach) {
        if (coach == null) {
            throw new IllegalArgumentException("Coach cannot be null");
        }
        if (!coach.getTrainId().equals(this.id)) {
            throw new IllegalArgumentException("Coach does not belong to this train");
        }
        coaches.add(coach);
    }

    public void addRunningDay(DayOfWeek day) {
        runningDays.add(day);
    }

    public void removeRunningDay(DayOfWeek day) {
        runningDays.remove(day);
    }

    public boolean runsOn(DayOfWeek day) {
        return runningDays.contains(day);
    }

    public boolean bookSeat(String coachId, int seatNumber, Passenger passenger) {
        if (coaches == null) return false;
        return coaches.stream()
                .filter(c -> c.getId().equals(coachId))
                .findFirst()
                .map(c -> c.bookSeat(seatNumber, passenger))
                .orElse(false);
    }

    public boolean cancelSeat(String coachId, int seatNumber) {
        if (coaches == null) return false;
        return coaches.stream()
                .filter(c -> c.getId().equals(coachId))
                .findFirst()
                .map(c -> c.cancelSeat(seatNumber))
                .orElse(false);
    }
}
