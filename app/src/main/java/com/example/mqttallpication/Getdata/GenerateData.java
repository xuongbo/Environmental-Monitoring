package com.example.mqttallpication.Getdata;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerateData {

    DecimalFormat numberFormat = new DecimalFormat("#0.00");

    public String generatePm(){
        return numberFormat.format(new Random().nextDouble()*40+30);
    }

    //Get current time in Hanoi
    @SuppressLint("SimpleDateFormat")
    public String getDate(){
        String time = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        Date date = new Date();
        time = simpleDateFormat.format(date);
        return time;
    }

    //Swipe timeline base on current time
    public int[] swipeDate(int currentDate){
        int[] time = new int[12];
        int i = 11;
        int hour = currentDate;
        time[i] = currentDate;
        for (int j = 10; j >0;j--){
            if (currentDate >= 0){
                time[i] = currentDate;
                currentDate-=2;
                i=j;
            }
            else{
                if (hour % 2 ==0){
                    currentDate = 24;
                    break;
                }else{
                    currentDate = 23;
                    break;
                }
            }
        }
        while (i>=0){
            time[i] = currentDate;
            currentDate-=2;
            i--;
        }

        return time;
    }

}
