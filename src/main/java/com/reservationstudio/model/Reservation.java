package com.reservationstudio.model;

import javafx.beans.property.*;

public class Reservation {

    public enum Status { SEATED, RESERVATION }

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();
    private final IntegerProperty guests = new SimpleIntegerProperty();
    private final IntegerProperty tableNumber = new SimpleIntegerProperty();
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>();
    private final StringProperty notes = new SimpleStringProperty();

    public Reservation(String name, String time, int guests, int tableNumber, Status status, String notes) {
        this.name.set(name);
        this.time.set(time);
        this.guests.set(guests);
        this.tableNumber.set(tableNumber);
        this.status.set(status);
        this.notes.set(notes == null ? "" : notes);
    }

    // CSV format: name,time,guests,tableNumber,status,notes
    public String toCsv() {
        return String.join(",",
                getName(),
                getTime(),
                String.valueOf(getGuests()),
                String.valueOf(getTableNumber()),
                getStatus().name(),
                getNotes().replace(",", ";")
        );
    }

    public static Reservation fromCsv(String line) {
        String[] parts = line.split(",", 6);
        if (parts.length < 5) return null;
        try {
            return new Reservation(
                    parts[0].trim(),
                    parts[1].trim(),
                    Integer.parseInt(parts[2].trim()),
                    Integer.parseInt(parts[3].trim()),
                    Status.valueOf(parts[4].trim()),
                    parts.length == 6 ? parts[5].trim() : ""
            );
        } catch (Exception e) {
            return null;
        }
    }

    //   Properties
    public StringProperty nameProperty() { return name; }
    public StringProperty timeProperty() { return time; }
    public IntegerProperty guestsProperty() { return guests; }
    public IntegerProperty tableNumberProperty() { return tableNumber; }
    public ObjectProperty<Status> statusProperty() { return status; }
    public StringProperty notesProperty() { return notes; }

    //   Getters
    public String getName() { return name.get(); }
    public String getTime() { return time.get(); }
    public int getGuests() { return guests.get(); }
    public int getTableNumber() { return tableNumber.get(); }
    public Status getStatus() { return status.get(); }
    public String getNotes() { return notes.get(); }

    //   Setters
    public void setName(String v) { name.set(v); }
    public void setTime(String v) { time.set(v); }
    public void setGuests(int v) { guests.set(v); }
    public void setTableNumber(int v) { tableNumber.set(v); }
    public void setStatus(Status v) { status.set(v); }
    public void setNotes(String v) { notes.set(v); }

    @Override
    public String toString() {
        return getName() + " | " + getTime() + " | " + getGuests() + "G | Table " + getTableNumber();
    }
}
