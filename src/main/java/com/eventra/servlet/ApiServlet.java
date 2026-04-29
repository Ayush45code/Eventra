package com.eventra.servlet;

import com.eventra.dao.UserDAO;
import com.eventra.model.Event;
import com.eventra.model.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = normalizePath(request.getPathInfo());

        try {
            if ("/".equals(path) || "/health".equals(path) || "/test".equals(path)) {
                writeJson(response, HttpServletResponse.SC_OK, apiResponse(true, "Backend is running", null));
                return;
            }

            if ("/events".equals(path)) {
                List<Event> events = userDAO.getAllEvents();
                Map<String, Object> payload = apiResponse(true, "Events loaded", events);
                payload.put("events", events);
                writeJson(response, HttpServletResponse.SC_OK, payload);
                return;
            }

            if ("/registrations".equals(path)) {
                String email = request.getParameter("email");
                List<UserDAO.RegistrationRecord> registrations = userDAO.getRegistrations(email);
                Map<String, Object> payload = apiResponse(true, "Registrations loaded", registrations);
                payload.put("registrations", registrations);
                writeJson(response, HttpServletResponse.SC_OK, payload);
                return;
            }

            if ("/users".equals(path)) {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    int userId = Integer.parseInt(idParam);
                    User user = userDAO.getUserById(userId);
                    if (user == null) {
                        writeJson(response, HttpServletResponse.SC_NOT_FOUND, apiResponse(false, "User not found", null));
                        return;
                    }
                    user.setPassword(null);
                    writeJson(response, HttpServletResponse.SC_OK, apiResponse(true, "User loaded", user));
                    return;
                }

                List<User> users = userDAO.getAllUsers();
                for (User user : users) {
                    user.setPassword(null);
                }
                writeJson(response, HttpServletResponse.SC_OK, apiResponse(true, "Users loaded", users));
                return;
            }

            if (path.startsWith("/users/")) {
                int userId = Integer.parseInt(path.substring("/users/".length()));
                User user = userDAO.getUserById(userId);
                if (user == null) {
                    writeJson(response, HttpServletResponse.SC_NOT_FOUND, apiResponse(false, "User not found", null));
                    return;
                }

                user.setPassword(null);
                writeJson(response, HttpServletResponse.SC_OK, apiResponse(true, "User loaded", user));
                return;
            }

            if ("/stats".equals(path)) {
                Map<String, Integer> stats = new LinkedHashMap<>();
                stats.put("users", userDAO.countUsers());
                stats.put("events", userDAO.countEvents());
                stats.put("registrations", userDAO.countRegistrations());
                Map<String, Object> payload = apiResponse(true, "Stats loaded", stats);
                payload.put("stats", stats);
                writeJson(response, HttpServletResponse.SC_OK, payload);
                return;
            }

            writeJson(response, HttpServletResponse.SC_NOT_FOUND, apiResponse(false, "Unknown endpoint", null));
        } catch (NumberFormatException ex) {
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, apiResponse(false, "Invalid numeric value", null));
        } catch (Exception ex) {
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, apiResponse(false, ex.getMessage(), null));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = normalizePath(request.getPathInfo());

        try {
            if ("/register".equals(path)) {
                String username = trim(firstNonEmpty(request.getParameter("username"), request.getParameter("roll")));
                String fullName = trim(firstNonEmpty(request.getParameter("fullName"), request.getParameter("name"), request.getParameter("regName")));
                String email = trim(firstNonEmpty(request.getParameter("email"), request.getParameter("regEmail")));
                String password = trim(firstNonEmpty(request.getParameter("password"), request.getParameter("regPassword")));
                String phone = trim(firstNonEmpty(request.getParameter("phone"), request.getParameter("regPhone")));
                String department = trim(request.getParameter("department"));
                String roll = trim(request.getParameter("roll"));

                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    writeJson(response, HttpServletResponse.SC_BAD_REQUEST, apiResponse(false, "Full name, email, and password are required", null));
                    return;
                }

                User newUser = new User(username, fullName, email, password, phone);
                newUser.setDepartment(department);
                newUser.setRollNumber(roll);
                boolean created = userDAO.registerUser(newUser);
                if (created) {
                    writeJson(response, HttpServletResponse.SC_CREATED, apiResponse(true, "User registered successfully", null));
                } else {
                    writeJson(response, HttpServletResponse.SC_CONFLICT, apiResponse(false, "Email already exists or user could not be saved", null));
                }
                return;
            }

            if ("/login".equals(path)) {
                String username = trim(firstNonEmpty(request.getParameter("username"), request.getParameter("email"), request.getParameter("loginEmail"), request.getParameter("loginUsername")));
                String password = trim(firstNonEmpty(request.getParameter("password"), request.getParameter("loginPassword")));

                String loginValue = username;

                if (loginValue.isEmpty() || password.isEmpty()) {
                    writeJson(response, HttpServletResponse.SC_BAD_REQUEST, apiResponse(false, "Username or email and password are required", null));
                    return;
                }

                User user = userDAO.loginUser(loginValue, password);
                if (user == null) {
                    writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, apiResponse(false, "Invalid login credentials", null));
                    return;
                }

                user.setPassword(null);
                Map<String, Object> payload = apiResponse(true, "Login successful", user);
                payload.put("user", user);
                writeJson(response, HttpServletResponse.SC_OK, payload);
                return;
            }

            if ("/event-registration".equals(path) || "/events/register".equals(path)) {
                String fullName = trim(firstNonEmpty(request.getParameter("fullName"), request.getParameter("name")));
                String email = trim(request.getParameter("email"));
                String phone = trim(request.getParameter("phone"));
                String eventTitle = trim(firstNonEmpty(request.getParameter("eventTitle"), request.getParameter("event")));

                if (eventTitle.isEmpty()) {
                    String eventIdParam = trim(request.getParameter("eventId"));
                    String userIdParam = trim(request.getParameter("userId"));

                    if (!eventIdParam.isEmpty() && !userIdParam.isEmpty()) {
                        int eventId = Integer.parseInt(eventIdParam);
                        int userId = Integer.parseInt(userIdParam);

                        Event event = userDAO.getEventById(eventId);
                        User user = userDAO.getUserById(userId);
                        if (event == null || user == null) {
                            writeJson(response, HttpServletResponse.SC_NOT_FOUND, apiResponse(false, "User or event not found", null));
                            return;
                        }

                        fullName = user.getFullName();
                        email = user.getEmail();
                        phone = user.getPhone();
                        eventTitle = event.getTitle();
                    }
                }

                if (fullName.isEmpty() || email.isEmpty() || eventTitle.isEmpty()) {
                    writeJson(response, HttpServletResponse.SC_BAD_REQUEST, apiResponse(false, "Full name, email, and event title are required", null));
                    return;
                }

                boolean saved = userDAO.registerForEvent(fullName, email, phone, eventTitle);
                if (saved) {
                    writeJson(response, HttpServletResponse.SC_CREATED, apiResponse(true, "Registration saved successfully", null));
                } else {
                    writeJson(response, HttpServletResponse.SC_CONFLICT, apiResponse(false, "You may already be registered for this event", null));
                }
                return;
            }

            if ("/profile".equals(path)) {
                String email = trim(request.getParameter("email"));
                String fullName = trim(request.getParameter("fullName"));
                String phone = trim(request.getParameter("phone"));

                if (email.isEmpty()) {
                    writeJson(response, HttpServletResponse.SC_BAD_REQUEST, apiResponse(false, "Email is required", null));
                    return;
                }

                boolean updated = userDAO.updateProfile(email, fullName, phone);
                if (updated) {
                    writeJson(response, HttpServletResponse.SC_OK, apiResponse(true, "Profile saved", null));
                } else {
                    writeJson(response, HttpServletResponse.SC_NOT_FOUND, apiResponse(false, "User not found", null));
                }
                return;
            }

            writeJson(response, HttpServletResponse.SC_NOT_FOUND, apiResponse(false, "Unknown endpoint", null));
        } catch (Exception ex) {
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, apiResponse(false, ex.getMessage(), null));
        }
    }

    private Map<String, Object> apiResponse(boolean success, String message, Object data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", success);
        payload.put("message", message);
        payload.put("data", data);
        return payload;
    }

    private void writeJson(HttpServletResponse response, int statusCode, Object body) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(gson.toJson(body));
    }

    private String normalizePath(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty()) {
            return "/";
        }
        return pathInfo;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonEmpty(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }
}
