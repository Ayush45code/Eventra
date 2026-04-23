package com.eventra.servlet;

import com.eventra.dao.UserDAO;
import com.eventra.model.User;
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

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
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
                // Get all users (admin only)
                List<User> users = userDAO.getAllUsers();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(new UserResponse(true, "Users retrieved successfully", users)));
            } else {
                // Get specific user by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        int userId = Integer.parseInt(pathParts[1]);
                        User user = userDAO.getUserById(userId);
                        
                        if (user != null) {
                            // Create response without password
                            UserResponseData userData = new UserResponseData(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getFullName(),
                                user.getPhone(),
                                user.getRole(),
                                user.getCreatedAt(),
                                user.getUpdatedAt()
                            );
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.print(gson.toJson(new SingleUserResponse(true, "User retrieved successfully", userData)));
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print(gson.toJson(new ApiResponse(false, "User not found")));
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gson.toJson(new ApiResponse(false, "Invalid user ID")));
                    }
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                // Update specific user by ID
                int userId = Integer.parseInt(pathInfo.split("/")[1]);
                
                // Get form data
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                
                // Get existing user
                User existingUser = userDAO.getUserById(userId);
                if (existingUser == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ApiResponse(false, "User not found")));
                    return;
                }
                
                // Update user with new values
                if (username != null && !username.isEmpty()) existingUser.setUsername(username);
                if (email != null && !email.isEmpty()) existingUser.setEmail(email);
                if (fullName != null && !fullName.isEmpty()) existingUser.setFullName(fullName);
                if (phone != null && !phone.isEmpty()) existingUser.setPhone(phone);
                
                // Update user in database
                boolean success = userDAO.updateUser(existingUser);
                
                if (success) {
                    // Create response without password
                    UserResponseData userData = new UserResponseData(
                        existingUser.getId(),
                        existingUser.getUsername(),
                        existingUser.getEmail(),
                        existingUser.getFullName(),
                        existingUser.getPhone(),
                        existingUser.getRole(),
                        existingUser.getCreatedAt(),
                        existingUser.getUpdatedAt()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(new SingleUserResponse(true, "User updated successfully", userData)));
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(gson.toJson(new ApiResponse(false, "Failed to update user")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Invalid request path")));
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ApiResponse(false, "Invalid user ID")));
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
                // Delete specific user by ID
                int userId = Integer.parseInt(pathInfo.split("/")[1]);
                
                // Delete user from database
                boolean success = userDAO.deleteUser(userId);
                
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(new ApiResponse(true, "User deleted successfully")));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ApiResponse(false, "User not found")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse(false, "Invalid request path")));
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ApiResponse(false, "Invalid user ID")));
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
    
    private static class UserResponseData {
        private int id;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private String role;
        private java.sql.Timestamp createdAt;
        private java.sql.Timestamp updatedAt;
        
        public UserResponseData(int id, String username, String email, String fullName, String phone, String role, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.phone = phone;
            this.role = role;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getRole() { return role; }
        public java.sql.Timestamp getCreatedAt() { return createdAt; }
        public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    }
    
    private static class UserResponse extends ApiResponse {
        private List<User> users;
        
        public UserResponse(boolean success, String message, List<User> users) {
            super(success, message);
            this.users = users;
        }
        
        public List<User> getUsers() { return users; }
    }
    
    private static class SingleUserResponse extends ApiResponse {
        private UserResponseData user;
        
        public SingleUserResponse(boolean success, String message, UserResponseData user) {
            super(success, message);
            this.user = user;
        }
        
        public UserResponseData getUser() { return user; }
    }
}
