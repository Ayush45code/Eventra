package com.eventra.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String department;
    private String rollNumber;
    private Timestamp createdAt;

    public User() {}

    public User(String username, String fullName, String email, String password, String phone) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public String getUsername()                     { return username; }
    public void setUsername(String username)        { this.username = username; }

    public String getFullName()                     { return fullName; }
    public void setFullName(String fullName)        { this.fullName = fullName; }

    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }

    public String getPassword()                     { return password; }
    public void setPassword(String password)        { this.password = password; }

    // Keep getPhone/setPhone so ApiServlet doesn't break
    public String getPhone()                        { return ""; }
    public void setPhone(String phone)              { }

    public String getDepartment()                   { return department; }
    public void setDepartment(String department)    { this.department = department; }

    public String getRollNumber()                   { return rollNumber; }
    public void setRollNumber(String rollNumber)    { this.rollNumber = rollNumber; }

    public Timestamp getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)   { this.createdAt = createdAt; }
}