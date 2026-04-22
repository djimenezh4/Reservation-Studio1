package com.reservationstudio.model;

import javafx.beans.property.*;

public class Table {

    public enum TableStatus { AVAILABLE, SEATED, RESERVED }

    private final int number;
    private final int capacity;
    private final ObjectProperty<TableStatus> status = new SimpleObjectProperty<>(TableStatus.AVAILABLE);
    private final StringProperty occupantName = new SimpleStringProperty("");

    public Table(int number, int capacity) {
        this.number = number;
        this.capacity = capacity;
    }

    public int getNumber() { return number; }
    public int getCapacity() { return capacity; }

    public TableStatus getStatus() { return status.get(); }

    public static TableStatus parseStatus(String s) {
        switch (s.trim().toLowerCase()) {
            case "seated":   return TableStatus.SEATED;
            case "reserved": return TableStatus.RESERVED;
            default:         return TableStatus.AVAILABLE;
        }
    }

    public void setStatus(TableStatus s) { status.set(s); }
    public ObjectProperty<TableStatus> statusProperty() { return status; }

    public String getOccupantName() { return occupantName.get(); }
    public void setOccupantName(String n) { occupantName.set(n); }
    public StringProperty occupantNameProperty() { return occupantName; }
}
