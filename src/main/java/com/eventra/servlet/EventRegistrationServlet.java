package com.eventra.servlet;

import com.eventra.dao.EventDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/event-registration")
public class EventRegistrationServlet extends HttpServlet {
    
    private EventDAO eventDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        eventDAO = new EventDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Get form data
            String eventIdStr = request.getParameter("eventId");
            String userIdStr = request.getParameter("userId");
            
            // Validate input
            if (eventIdStr == null || eventIdStr.isEmpty() || 
                userIdStr == null || userIdStr.isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Event ID and User ID are required")));
                return;
            }
            
            // Parse numeric values
            int eventId = Integer.parseInt(eventIdStr);
            int userId = Integer.parseInt(userIdStr);
            
            // Register user for event
            boolean success = eventDAO.registerForEvent(eventId, userId);
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(new ApiResponse(true, "Successfully registered for event")));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Failed to register for event. You may already be registered.")));
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
    
    // Helper class for JSON response
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
}
