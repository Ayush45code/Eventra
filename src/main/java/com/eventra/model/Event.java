package com.eventra.model;

import java.sql.Timestamp;
import java.util.List;

public class Event {
    private int id;
    private String title;
    private String description;
    private String category;
    private String date;
    private String time;
    private String location;
    private int maxParticipants;
    private int currentParticipants;
    private double price;
    private String imageUrl;
    private int organizerId;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Event() {}
    
    public Event(String title, String description, String category, String date, String time, 
                 String location, int maxParticipants, double price, String imageUrl, int organizerId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.price = price;
        this.imageUrl = imageUrl;
        this.organizerId = organizerId;
        this.status = "upcoming";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
