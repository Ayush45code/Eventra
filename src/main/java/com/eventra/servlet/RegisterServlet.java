package com.eventra.servlet;

import com.eventra.dao.UserDAO;
import com.eventra.model.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
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
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");
            
            if (role == null || role.isEmpty()) {
                role = "user"; // Default role
            }
            
            // Validate input
            if (username == null || username.isEmpty() || 
                email == null || email.isEmpty() || 
                password == null || password.isEmpty() || 
                fullName == null || fullName.isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "All required fields must be filled")));
                return;
            }
            
            // Create user object
            User user = new User(username, email, password, fullName, phone, role);
            
            // Register user
            boolean success = userDAO.registerUser(user);
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.toJson(new ApiResponse(true, "User registered successfully")));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(new ApiResponse(false, "Failed to register user")));
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
