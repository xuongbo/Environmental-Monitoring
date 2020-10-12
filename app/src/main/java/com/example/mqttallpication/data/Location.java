package com.example.mqttallpication.data;

import java.util.Date;

public class Location {
    String Location;
    float Longitude;
    float Latitude;

    public Location(String location, float longitude, float latitude) {
        this.Location = location;
        this.Longitude = longitude;
        this.Latitude = latitude;
    }

    public Location() {
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public Float getLongitude() {
        return Longitude;
    }

    public void setLongitude(Float longitude) {
        this.Longitude = longitude;
    }

    public Float getLatitude() {
        return Latitude;
    }

    public void setLatitude(Float latitude) {
        this.Latitude = latitude;
    }

}
