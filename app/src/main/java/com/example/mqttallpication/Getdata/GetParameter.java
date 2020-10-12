package com.example.mqttallpication.Getdata;


import com.example.mqttallpication.data.Parameter;

public class GetParameter {

    public Parameter getParameter(String message){

        Parameter parameter = new Parameter();
        String[] messages = message.split("@");
        parameter.setTemperature((Integer.parseInt(messages[0]))/100);
        parameter.setHumid(Integer.parseInt(messages[1])/100);
        parameter.setPm2(Float.parseFloat(messages[2])/100);
        parameter.setPm10(Float.parseFloat(messages[3])/100);
        return parameter;

    }
}
