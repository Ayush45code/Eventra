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
import javax.servlet.http.HttpSession;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    
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
            // Get login credentials
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            // Validate input
            if (username == null || username.isEmpty() || 
                password == null || password.isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Username and password are required")));
                return;
            }
            
            // Authenticate user
            User user = userDAO.loginUser(username, password);
            
            if (user != null) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                
                // Create response with user data (excluding password)
                UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getRole()
                );
                
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(new LoginResponse(true, "Login successful", userResponse)));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(gson.toJson(new ApiResponse(false, "Invalid username or password")));
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
    
    private static class UserResponse {
        private int id;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private String role;
        
        public UserResponse(int id, String username, String email, String fullName, String phone, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.phone = phone;
            this.role = role;
        }
        
        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getRole() { return role; }
    }
    
    private static class LoginResponse extends ApiResponse {
        private UserResponse user;
        
        public LoginResponse(boolean success, String message, UserResponse user) {
            super(success, message);
            this.user = user;
        }
        
        public UserResponse getUser() { return user; }
    }
}
