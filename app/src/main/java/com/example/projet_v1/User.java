package com.example.projet_v1;

public class User implements Comparable<User>{
    private String name;
    private double latitude;
    private double longitude;
    private double distance;


    /**
     * Constructor to initialize a user with name, latitude, longitude and distance.
     */
    public User(String name, double latitude, double longitude, double distance) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }




    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(User otherUser) {
        return this.getName().compareTo(otherUser.getName());
    }
}
