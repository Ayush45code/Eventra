package com.eventra.servlet;

import com.eventra.dao.EventDAO;
import com.eventra.model.Event;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/events/*")
public class EventServlet extends HttpServlet {
    
    private EventDAO eventDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        eventDAO = new EventDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all events
                List<Event> events = eventDAO.getAllEvents();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(new EventResponse(true, "Events retrieved successfully", events)));
            } else {
                // Get specific event by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        int eventId = Integer.parseInt(pathParts[1]);
                        Event event = eventDAO.getEventById(eventId);
                        
                        if (event != null) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.print(gson.toJson(new SingleEventResponse(true, "Event retrieved successfully", event)));
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print(gson.toJson(new ApiResponse(false, "Event not found")));
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gson.toJson(new ApiResponse(false, "Invalid event ID")));
                    }
                } else if (pathParts.length == 3 && pathParts[1].equals("category")) {
                    // Get events by category
                    String category = pathParts[2];
                    List<Event> events = eventDAO.getEventsByCategory(category);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(new EventResponse(true, "Events by category retrieved successfully", events)));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(new ApiResponse(false, "Invalid request path")));
                }
            }
            
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Database error: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Server error: " + e.getMessage())));
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Get form data
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String category = request.getParameter("category");
            String date = request.getParameter("date");
            String time = request.getParameter("time");
            String location = request.getParameter("location");
            String maxParticipantsStr = request.getParameter("maxParticipants");
            String priceStr = request.getParameter("price");
            String imageUrl = request.getParameter("imageUrl");
            String organizerIdStr = request.getParameter("organizerId");
            
            // Validate input
            if (title == null || title.isEmpty() || 
                description == null || description.isEmpty() || 
                category == null || category.isEmpty() || 
                date == null || date.isEmpty() || 
                time == null || time.isEmpty() || 
                location == null || location.isEmpty() || 
                maxParticipantsStr == null || maxParticipantsStr.isEmpty() || 
                priceStr == null || priceStr.isEmpty() || 
                organizerIdStr == null || organizerIdStr.isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "All required fields must be filled")));
                return;
            }
            
            // Parse numeric values
            int maxParticipants = Integer.parseInt(maxParticipantsStr);
            double price = Double.parseDouble(priceStr);
            int organizerId = Integer.parseInt(organizerIdStr);
            
            // Create event object
            Event event = new Event(title, description, category, date, time, location, maxParticipants, price, imageUrl, organizerId);
            
            // Create event
            boolean success = eventDAO.createEvent(event);
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(new SingleEventResponse(true, "Event created successfully", event)));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(new ApiResponse(false, "Failed to create event")));
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ApiResponse(false, "Invalid numeric format")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Database error: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Server error: " + e.getMessage())));
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                // Update specific event by ID
                int eventId = Integer.parseInt(pathInfo.split("/")[1]);
                
                // Get form data
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String category = request.getParameter("category");
                String date = request.getParameter("date");
                String time = request.getParameter("time");
                String location = request.getParameter("location");
                String maxParticipantsStr = request.getParameter("maxParticipants");
                String priceStr = request.getParameter("price");
                String imageUrl = request.getParameter("imageUrl");
                String status = request.getParameter("status");
                
                // Get existing event
                Event existingEvent = eventDAO.getEventById(eventId);
                if (existingEvent == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ApiResponse(false, "Event not found")));
                    return;
                }
                
                // Update event with new values
                if (title != null && !title.isEmpty()) existingEvent.setTitle(title);
                if (description != null && !description.isEmpty()) existingEvent.setDescription(description);
                if (category != null && !category.isEmpty()) existingEvent.setCategory(category);
                if (date != null && !date.isEmpty()) existingEvent.setDate(date);
                if (time != null && !time.isEmpty()) existingEvent.setTime(time);
                if (location != null && !location.isEmpty()) existingEvent.setLocation(location);
                if (maxParticipantsStr != null && !maxParticipantsStr.isEmpty()) {
                    existingEvent.setMaxParticipants(Integer.parseInt(maxParticipantsStr));
                }
                if (priceStr != null && !priceStr.isEmpty()) {
                    existingEvent.setPrice(Double.parseDouble(priceStr));
                }
                if (imageUrl != null && !imageUrl.isEmpty()) existingEvent.setImageUrl(imageUrl);
                if (status != null && !status.isEmpty()) existingEvent.setStatus(status);
                
                // Update event in database
                boolean success = eventDAO.updateEvent(existingEvent);
                
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(new SingleEventResponse(true, "Event updated successfully", existingEvent)));
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(gson.toJson(new ApiResponse(false, "Failed to update event")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Invalid request path")));
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ApiResponse(false, "Invalid numeric format")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Database error: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Server error: " + e.getMessage())));
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                // Delete specific event by ID
                int eventId = Integer.parseInt(pathInfo.split("/")[1]);
                
                // Delete event from database
                boolean success = eventDAO.deleteEvent(eventId);
                
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(new ApiResponse(true, "Event deleted successfully")));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ApiResponse(false, "Event not found")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Invalid request path")));
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ApiResponse(false, "Invalid event ID")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Database error: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Server error: " + e.getMessage())));
        } finally {
            out.close();
        }
    }
    
    // Helper classes for JSON response
    private static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    private static class EventResponse extends ApiResponse {
        private List<Event> events;
        
        public EventResponse(boolean success, String message, List<Event> events) {
            super(success, message);
            this.events = events;
        }
        
        public List<Event> getEvents() { return events; }
    }
    
    private static class SingleEventResponse extends ApiResponse {
        private Event event;
        
        public SingleEventResponse(boolean success, String message, Event event) {
            super(success, message);
            this.event = event;
        }
        
        public Event getEvent() { return event; }
    }
}
