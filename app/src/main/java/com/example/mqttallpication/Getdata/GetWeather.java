package com.example.mqttallpication.Getdata;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetWeather extends AsyncTask<Void,Void,String> {
    String location;


    @Override
    protected String doInBackground(Void... voids) {

//        Document document = null;
//        try {
//            document = Jsoup.connect("https://weather.com/weather/today/l/5217c2dc73b218e291cd6b14caaf944821ea545970bf0c6bc393af8b56d793bb").get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Elements elements = document.getElementsByClass("_-_-node_modules-@wxu-components-src-organism-CurrentConditions-CurrentConditions--phraseValue--2xXSr");
//        for (Element element: elements) {
//            location = element.text();
//            break;
//        }

        return location;
    }

    public String getStatusNow(){
        return this.doInBackground();
    }
}
