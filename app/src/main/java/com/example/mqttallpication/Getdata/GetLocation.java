package com.example.mqttallpication.Getdata;

import com.example.mqttallpication.data.Location;

import java.text.DecimalFormat;

public class GetLocation {

    public Location getLocation(String message){

        Location location = new Location();
        String[] messages = message.split("@");
        location.setLocation(messages[0]);
        location.setLongitude(Float.parseFloat(messages[1]));
        location.setLatitude(Float.parseFloat(messages[2]));
        return location;
    }

}
