package org.example.ticket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a railway station with a unique code, name, location, and facilities.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {

    // ------------------- Public Getters -------------------
    private String code;        // Unique station code (e.g., NDLS, BCT)
    private String name;        // Full station name (e.g., New Delhi, Mumbai Central)
    private String city;        // City where the station is located
    private String state;       // State where the station is located
    private int platformCount;  // Number of platforms at the station

    // ------------------- Constructors -------------------
    public Station(String code, String name, String city, String state, int platformCount) {
        this.code = code.toUpperCase();
        this.name = name.trim();
        this.city = city.trim();
        this.state = state.trim();
        this.platformCount = Math.max(0, platformCount);
    }

    // ------------------- Public Setters -------------------
    public void setCode(String code) {
        if (code != null) this.code = code.toUpperCase();
    }

    public void setName(String name) {
        if (name != null) this.name = name.trim();
    }

    public void setCity(String city) {
        if (city != null) this.city = city.trim();
    }

    public void setState(String state) {
        if (state != null) this.state = state.trim();
    }

    public void setPlatformCount(int platformCount) {
        if (platformCount >= 0) this.platformCount = platformCount;
    }

    // ------------------- Helper Methods -------------------
    public String getFullDescription() {
        return name + " (" + code + "), " + city + ", " + state;
    }

    @Override
    public String toString() {
        return "Station{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", platformCount=" + platformCount +
                '}';
    }
}
