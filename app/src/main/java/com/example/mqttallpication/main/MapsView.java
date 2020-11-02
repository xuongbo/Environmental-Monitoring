package com.example.mqttallpication.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mqttallpication.Getdata.GenerateData;
import com.example.mqttallpication.Getdata.GetLocation;
import com.example.mqttallpication.Getdata.GetParameter;
import com.example.mqttallpication.Getdata.GetPm25;
import com.example.mqttallpication.Getdata.GetWeather;
import com.example.mqttallpication.R;
import com.example.mqttallpication.data.Location;
import com.example.mqttallpication.data.Parameter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.ui.IconGenerator;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class MapsView extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private View myView;
    private MqttAndroidClient client;
    public Integer oldDate;
    private static final String TAG = "mqtt";

    public int[] time = new int[12];
    public Float[][] m0 = new Float[2][12];
    public Float[][] m1 = new Float[2][12];
    public Float[][] m2 = new Float[2][12];

    public Location location = new Location();
    public Parameter parameter = new Parameter();
    public GetLocation getLocation = new GetLocation();
    public GetParameter getParameter = new GetParameter();

    //Auto create data for a while
    private Runnable runner = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                swipeData2();
                swipeData3();
                oldDate = Integer.parseInt(new GenerateData().getDate());
                time = new GenerateData().swipeDate(oldDate);
            }
        }
    };

    //Start connect to MqttCloud
    //It will be start immediately when open application
    public void startConnect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("fzmbcwzw");
        options.setPassword("EJFGo18DD7_3".toCharArray());
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://m21.cloudmqtt.com:18741",
                        clientId);
        /* Establish an MQTT connection */
        try {
            IMqttToken token = client.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    setSubscribe("Location");
                    setSubscribe("Parameter");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    exception.printStackTrace();

                }
            });
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String string = new String(message.getPayload());
                    switch (topic) {
                        case "Location":
                            location = getLocation.getLocation(string);
                            break;
                        case "Parameter":
                            parameter = getParameter.getParameter(string);
                            break;
                    }
                    swipeData1();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Subscribe topic
    private void setSubscribe(String topic) {
        try {
            client.subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onStart() {
        super.onStart();
        startConnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_view);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myView = findViewById(R.id.my_view);
        getSupportActionBar().hide();

        //Get random number and get information from websites
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new GetPm25().execute();
        String[] pm2 = new GetPm25().getPm2Now();

        // initialize as invisible (could also do in xml)
        // Set data in arrays
        // Only one location is real data, remains are fake.
        for (int i = 0; i < 12; i++) {
            m0[0][i] = 0f;
            m0[1][i] = 0f;
        }
        for (int i = 0; i < 11; i++) {
            m1[0][i] = Float.parseFloat(new GenerateData().generatePm());
            m2[0][i] = Float.parseFloat(new GenerateData().generatePm());
        }
        //Set real Pm 2.5 index for fake location.
        m1[0][11] = Float.parseFloat(pm2[0].split(" ")[1]);
        m2[0][11] = Float.parseFloat(pm2[1].split(" ")[1]);
        for (int i = 0; i < 12; i++) {
            m1[1][i] = Float.parseFloat(new GenerateData().generatePm());
            m2[1][i] = Float.parseFloat(new GenerateData().generatePm());
        }
        oldDate = Integer.parseInt(new GenerateData().getDate());
        time = new GenerateData().swipeDate(oldDate);
        bottomSheetBehavior = BottomSheetBehavior.from(myView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        autoGenerate();

        //Get weather information and add it into view.
        new GetWeather().execute();
        weather();
        swipeData1();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add Pm 2.5 index to array to make it easier for set number on marker.
        Queue<Float[][]> queue = new LinkedList<>();
        queue.add(m0);
        queue.add(m1);
        queue.add(m2);

        //Put three locations on the map
        HashMap<Double, Double> testLocation = new HashMap<>();
        testLocation.put(21.006111, 105.843056);
        testLocation.put(21.006956, 105.8336606);
        testLocation.put(21.002641, 105.815678);
        int i = 0;
        //Add Pm 2.5 on marker and make it look serious
        for (Map.Entry<Double, Double> entry : testLocation.entrySet()) {
            Float[][] m = queue.poll();
            int mc = Math.round(m[0][11]);
            LatLng currentLocation = new LatLng(entry.getKey(), entry.getValue());
            float zoomLevel = 15.5f;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
            IconGenerator iconGenerator = new IconGenerator(MapsView.this);

            //Depends on Pm 2.5 index of range, it will show marker with different color
            if (mc >= 0 && mc < 12) iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
            else if (mc >= 12 && mc < 55.4) iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
            else if (mc >= 55.4 && mc < 150.5) iconGenerator.setStyle(IconGenerator.STYLE_RED);
            else iconGenerator.setStyle(IconGenerator.STYLE_PURPLE);

            //Set number on marker.
            Bitmap bitmap = iconGenerator.makeIcon(String.valueOf(mc));
            mMap.addMarker(new MarkerOptions().position(currentLocation).snippet(String.valueOf(mc))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.setOnMarkerClickListener(this);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    myView = findViewById(R.id.my_view);
                    bottomSheetBehavior = BottomSheetBehavior.from(myView);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                }
            });
        }

    }

    //Open the activity which show specific information of location.
    //All data was added, but only show for each location which is being clicked
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMarkerClick(final Marker marker) {
        //Get id of View components
        myView = findViewById(R.id.my_view);
        bottomSheetBehavior = BottomSheetBehavior.from(myView);
        TextView location1 = findViewById(R.id.location);
        TextView temperature = findViewById(R.id.temperature);
        TextView humid = findViewById(R.id.humid);
        TextView pm2 = findViewById(R.id.pm2);
        TextView pm10 = findViewById(R.id.pm10);

        //Set data for marker, it will show different information depend on marker was picked
        if (marker.getId().equals("m0")) {
            DecimalFormat numberFormat = new DecimalFormat("#0.00");

            location1.setText(location.getLocation());
            temperature.setText(parameter.getTemperature() + " °C");
            humid.setText(parameter.getHumid() + " %");
            pm2.setText(numberFormat.format(parameter.getPm2()) + " µg/m3");
            pm10.setText(numberFormat.format(parameter.getPm10()) + " µg/m3");
            barChart(m0[0], m0[1]);

        } else if (marker.getId().equals("m1")) {
            location1.setText("Trường mầm non Kim Liên");
            temperature.setText((new Random().nextInt(10 + 5) + 25) + " °C");
            humid.setText((new Random().nextInt(20 + 1) + 80) + " %");
            pm2.setText(m1[0][11] + " µg/m3");
            pm10.setText(m1[1][11] + " µg/m3");
            barChart(m1[0], m1[1]);
        } else {
            location1.setText("Royal City");
            temperature.setText((new Random().nextInt(10 + 5) + 25) + " °C");
            humid.setText((new Random().nextInt(20 + 1) + 80) + " %");
            pm2.setText(m2[0][11] + " µg/m3");
            pm10.setText(m2[1][11] + " µg/m3");
            barChart(m2[0], m2[1]);
        }
        //Set peek height for view, it will only being seen for initial data.
        myView.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setPeekHeight(700);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return false;
    }


    //Create bar charts and show recorded data in one day.
    public void barChart(Float[] m1, Float[] m2) {
        BarChart chart1 = findViewById(R.id.barchart1);
        LineChart chart2 = findViewById(R.id.linechart);

        //Create data for charts
        List<BarEntry> bar1 = new ArrayList<>();
        List<Entry> bar2 = new ArrayList<>();
        int k = 0;
        for (Float list : m1) {
            bar1.add(new BarEntry(k++, list));
        }
        k = 0;
        for (Float list : m2) {
            bar2.add(new BarEntry(k++, list));
        }
        ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int hour : time) {
            xAxisLabel.add(hour + "h");
        }
        //Create charts for showing and show animation by delaying for 1000ms.
        BarDataSet barDataSet1 = new BarDataSet(bar1, "pm 2.5");
        LineDataSet barDataSet2 = new LineDataSet(bar2, "pm 10");

        chart1.animateXY(1000, 1000);
        chart2.animateXY(1000, 1000);
        chart1.setClickable(false);
        chart2.setClickable(false);
        chart1.getLegend().setTextColor(Color.WHITE);
        chart2.getLegend().setTextColor(Color.WHITE);

        //Create first chart to show Pm 2.5 status
        XAxis xAxis1 = chart1.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        xAxis1.setLabelCount(12);
        //Set config for X and Y Axis
        YAxis yAxis1Right = chart1.getAxisRight();
        YAxis yAxis1Left = chart1.getAxisLeft();
        yAxis1Right.setAxisMinimum(0f);
        yAxis1Left.setAxisMinimum(0f);

        //Set Color for Text of Axis
        xAxis1.setTextColor(Color.WHITE);
        yAxis1Left.setTextColor(Color.WHITE);
        yAxis1Right.setTextColor(Color.WHITE);

        //Create second chart to show Pm 10 status
        XAxis xAxis2 = chart2.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis2.setLabelCount(11);
        //Set config for X and Y Axis
        YAxis yAxis2 = chart2.getAxisRight();
        YAxis yAxis2Left = chart2.getAxisLeft();
        yAxis2.setAxisMinimum(0f);

        yAxis2Left.setAxisMinimum(0f);
        //Set Color for text of Axis
        yAxis2.setTextColor(Color.WHITE);
        yAxis2Left.setTextColor(Color.WHITE);
        xAxis2.setTextColor(Color.WHITE);

        //Set title for both charts
        Description description1 = new Description();
        Description description2 = new Description();
        description1.setText("Pm 2.5");
        description2.setText("Pm 10");
        //Set color for text of label in chart 2
        description2.setTextColor(Color.WHITE);

        chart1.setDescription(description1);
        chart2.setDescription(description2);

        //Add data to charts and show it to the screen.
        BarData barData1 = new BarData(barDataSet1);
        barData1.setValueTextColor(Color.WHITE);
        LineData barData2 = new LineData(barDataSet2);
        barData2.setValueTextColor(Color.WHITE);
        chart1.setData(barData1);
        chart2.setData(barData2);
    }

    //Change thread priority and start.
    public void autoGenerate() {
        Thread thread = new Thread(runner);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    //Push old data back to the left and add new data to the right by swipe data.
    //Each method using for each location.
    public void swipeData2() {
        //auto generate Data for Location 2
        String[] pm2 = new GetPm25().getPm2Now();
        String pm_2 = pm2[0].split(" ")[1];
        for (int i = 0; i < 11; i++) {
            m1[0][i] = m1[0][i + 1];
        }
        m1[0][11] = Float.parseFloat(pm_2);
        String pm_10 = new GenerateData().generatePm();
        for (int i = 0; i < 11; i++) {
            m1[1][i] = m1[1][i + 1];
        }
        m1[1][11] = Float.parseFloat(pm_10);
    }

    public void swipeData3() {
        //auto generate Data for Location 3
        String[] pm2 = new GetPm25().getPm2Now();
        String pm_2 = pm2[1].split(" ")[1];
        System.arraycopy(m2[0], 1, m2[0], 0, 11);
        m2[0][11] = Float.parseFloat(pm_2);
        String pm_10 = new GenerateData().generatePm();
        System.arraycopy(m2[1], 1, m2[1], 0, 11);
        m2[1][11] = Float.parseFloat(pm_10);
    }

    public void swipeData1() {
        System.arraycopy(m0[0], 1, m0[0], 0, 11);
        m0[0][11] = parameter.getPm2();

        System.arraycopy(m0[1], 1, m0[1], 0, 11);
        m0[1][11] = parameter.getPm10();
    }

    //Set weather image in realtime
    //It will be split into day and night.
    public void weather() {
        ImageView weather = findViewById(R.id.weather);
        String status = null;
        try {
            status = new GetWeather().getStatusNow();
            if (status.equals("null")) status = "default";
        } catch (Exception e) {
            status = "default";
        }
        if (oldDate <= 18 && oldDate >= 6) {

            //Show status of weather on the day
            switch (status) {
                case "Cloudy":
                case "Partly Cloudy":
                    weather.setImageResource(R.drawable.cloudy);
                    break;
                case "Scattered Thunderstorms":
                    weather.setImageResource(R.drawable.day_rain_thunder);
                    break;
                case "Rain":
                    weather.setImageResource(R.drawable.rain);
                    break;
                case "Thunderstorms":
                    weather.setImageResource(R.drawable.rain_thunder);
                    break;
                case "Showers":
                    weather.setImageResource(R.drawable.day_rain);
                    break;
                case "Mostly Cloudy":
                    weather.setImageResource(R.drawable.day_partial_cloud);
                    break;
                case "PM Thunderstorms":
                    weather.setImageResource(R.drawable.thunder);
                    break;
                default:
                    weather.setImageResource(R.drawable.mist);
                    break;
            }
        } else {

            //Show weather status of the night
            switch (status) {
                case "Cloudy":
                    weather.setImageResource(R.drawable.cloudy);
                    break;
                case "Scattered Thunderstorms":
                    weather.setImageResource(R.drawable.night_rain_thunder);
                    break;
                case "Rain":
                    weather.setImageResource(R.drawable.rain);
                    break;
                case "Thunderstorms":
                    weather.setImageResource(R.drawable.rain_thunder);
                    break;
                case "Showers":
                    weather.setImageResource(R.drawable.night_rain);
                    break;
                case "Mostly Cloudy":
                    weather.setImageResource(R.drawable.night_partial_cloud);
                    break;
                case "PM Thunderstorms":
                    weather.setImageResource(R.drawable.thunder);
                case "Partly Cloudy":
                    weather.setImageResource(R.drawable.cloudy);
                    break;
                default:
                    weather.setImageResource(R.drawable.mist);
                    break;
            }
        }
    }
}