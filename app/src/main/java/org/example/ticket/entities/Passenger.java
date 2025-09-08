package org.example.ticket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Passenger {

    private String name;
    private int age;
    private String coach;
    private int seatNumber;

    public Passenger(String name, int age, String coach,int seatNumber) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Passenger name cannot be null or empty");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Passenger age must be greater than 0");
        }
        if (coach == null || coach.isBlank()) {
            throw new IllegalArgumentException("Coach cannot be null or empty");
        }

        this.name = name;
        this.age = age;
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public void assignSeat(int seatNumber) {
        if (seatNumber <= 0) {
            throw new IllegalArgumentException("Seat number must be greater than 0");
        }
        this.seatNumber = seatNumber;
    }
}
