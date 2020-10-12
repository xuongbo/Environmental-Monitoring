package com.example.mqttallpication.Getdata;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetPm25 extends AsyncTask<Void,Void,String[]> {

    String[] pm2 = new String[2];

    @Override
    protected String[] doInBackground(Void... voids) {

        Document[] document = new Document[2];
        try{
            document[0] = Jsoup.connect("https://moitruongthudo.vn/public?site_id=8").get();
        } catch (IOException e){
            e.printStackTrace();
        }
        Elements elements1 = null;
        try {
            elements1 = document[0].getElementsByClass("dailyAQI");
        } catch (Exception e) {
            pm2[0] = "33";
        }
        for (Element element: elements1){
            pm2[0] = element.text();
            break;
        }

        try{
            document[1] = Jsoup.connect("https://moitruongthudo.vn/public?site_id=30").get();
        } catch (IOException e){
            e.printStackTrace();
        }
        Elements elements2 = document[1].getElementsByClass("dailyAQI");
        for (Element element: elements2){
            pm2[1] = element.text();
            break;
        }
        return pm2;
    }

    public String[] getPm2Now(){return this.doInBackground();}
}
