package org.example.ticket.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.ticket.entities.Ticket;
import org.example.ticket.entities.Train;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    final String FILE_PATH = "app/src/main/java/org/example/ticket/database/trains.json";
    private final ObjectMapper objectMapper;
    final UserService userService;
    final List<Train> trains;

    public TicketService(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        // --- FIX: Register JavaTimeModule for LocalDate, LocalDateTime etc ---
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.trains = loadTrains();
        System.out.println("Loaded trains: " + this.trains.size());
        for (Train t : this.trains) {
            System.out.println(t.getName() + " | " + t.getId());
        }
    }

    public List<Train> loadTrains() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.<List<Train>>readValue(file, new TypeReference<List<Train>>() {});
        } catch (IOException e) {
            System.out.println("Error loading trains from file : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Train> searchTrains(String query) {
        if (query == null) query = "";
        final String q = query.trim().toLowerCase();

        if (q.isEmpty()) return new ArrayList<>(this.trains);

        List<Train> results = new ArrayList<>();
        for (Train train : this.trains) {
            if (train.getName().toLowerCase().contains(q) || train.getId().toLowerCase().contains(q)) {
                results.add(train);
            }
        }
        return results;
    }

    public List<Ticket> getBookedTickets() {
        return userService.getCurrentUser().getTickets();
    }

    public Train getTrainByNumber(String trainNumber) {
        for (Train t : this.trains) {
            if (t.getId().equals(trainNumber)) return t;
        }
        return null;
    }

    // ------------------- NEW FUNCTION -------------------
    /**
     * Saves or updates a single train in memory and writes to JSON file.
     * If train already exists (by ID), it will replace the old one.
     * Otherwise, it will add a new train.
     *
     * @param train the train to save or update
     */
    public void saveOrUpdateTrain(Train train) {
        if (train == null || train.getId() == null || train.getId().isBlank()) {
            System.out.println("⚠ Invalid train. Cannot save/update.");
            return;
        }

        // Check if train exists
        boolean updated = false;
        for (int i = 0; i < trains.size(); i++) {
            if (trains.get(i).getId().equals(train.getId())) {
                trains.set(i, train); // update
                updated = true;
                break;
            }
        }

        if (!updated) {
            trains.add(train); // new train
        }

        // Save to file
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), trains);
            System.out.println("✅ Train " + train.getId() + " saved/updated successfully.");
        } catch (Exception e) {
            System.out.println("❌ Error saving train: " + e.getMessage());
        }
    }
}
