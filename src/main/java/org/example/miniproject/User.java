package org.example.miniproject;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String address;
    private String zip;
    private String username;
    private String password;

    // Constructor
    public User(int userId, String firstName, String lastName, String address, String zip, String username, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.zip = zip;
        this.username = username;
        this.password = password;
    }

    // Getters for each property
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getZip() { return zip; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
