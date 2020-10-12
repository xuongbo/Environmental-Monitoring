package com.example.mqttallpication.data;


public class Parameter {
    int temperature;
    int humid;
    float pm2;
    float pm10;

    public Parameter(int temperature, int humid, float pm2, float pm10) {
        this.temperature = temperature;
        this.humid = humid;
        this.pm2 = pm2;
        this.pm10 = pm10;
    }

    public Parameter() {
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumid() {
        return humid;
    }

    public void setHumid(int humid) {
        this.humid = humid;
    }

    public float getPm2() {
        return pm2;
    }

    public void setPm2(float pm2) {
        this.pm2 = pm2;
    }

    public float getPm10() {
        return pm10;
    }

    public void setPm10(float pm10) {
        this.pm10 = pm10;
    }
}
