package com.example.athrvkhoche.smartions;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity{

    int x;
    Button prefButton;
    TextView status;
    Intent prefIntent;
    SharedPreferences sharedPreferences;
    TextView option1Info;
    TextView option2Info;
    TextView option3Info;
    TextView option4Info;
    TextView duration1;
    TextView duration2;
    TextView duration3;
    TextView duration4;
    TextView endView1;
    TextView endView2;
    TextView endView3;
    TextView endView4;
    Button goButton1;
    Button goButton2;
    Button goButton3;
    Button goButton4;
    TextView envScore1;
    TextView socScore1;
    TextView envScore2;
    TextView socScore2;
    TextView envScore3;
    TextView socScore3;


    ArrayList scheduleids = new ArrayList();


    JSONObject jsonObj = new JSONObject();



    private static final String EVSE_POST = "http://pluto.calit2.uci.edu:8082/v1/evse";
    private String GET_URL = "";
    private String GET_URL_1 = "http://pluto.calit2.uci.edu:8082/v1/schedule/new?evse=";
    private String GET_URL_2 =  "&env=100&cost=100&society=100";

    String device_MAC_ID = "240AC4110540";
    final String mqtt_user = "dkpljrty";
    final String mqtt_pwd = "ZJDsxMVKRjoR";
    final String mqtt_server = "m10.cloudmqtt.com";
    final String mqtt_port = "17934";

    final String SUBSC_TOPIC_PREFIX = "out/devices/";
    final String PUBL_TOPIC_PREFIX = "in/devices/";

    final String TOPIC_POWER_TOGGLE = "/1/OnOff/Toggle";
    final String TOPIC_READ_LOAD_STATUS = "1/OnOff/OnOff";
    final String TOPIC_READ_CURRENT = "/1/SimpleMeteringServer/RmsCurrent";
    final String TOPIC_SEND_CURRENT = "/1/SimpleMeteringServer/RequestCurrent";
    final String TOPIC_READ_POWER = "/1/SimpleMeteringServer/InstantaneousDemand";
    final String TOPIC_READ_CHARGE_LEVEL = "/1/SimpleMeteringServer/SUPLevel";
    final String TOPIC_READ_CHARGE_STATE = "/1/SimpleMeteringServer/ChargeState";
    final String TOPIC_GEN_FAULT = "/1/SimpleMeteringServer/GeneralFault";
    final String MQTT_TOPICS[] = {TOPIC_POWER_TOGGLE,
            TOPIC_READ_LOAD_STATUS,
            TOPIC_READ_CURRENT,
            TOPIC_SEND_CURRENT,
            TOPIC_READ_POWER,
            TOPIC_READ_CHARGE_LEVEL,
            TOPIC_READ_CHARGE_STATE,
            TOPIC_GEN_FAULT};

    HttpURLConnection con;
    MqttClient client;


    String power = "";
    String deviceCurrent = "";
    String chargeLevel = "";
    String chargeState = "";
    String genFault = "";
    String requestCurrent ="";
    String relayToggleResponse = "";
    final String deviceResponseLabel = "Device Current: \n" +
            "Device Power: \n" +
            "Charge State: \n" +
            "Charge Level: \n" +
            "\nCurrent Request Response: \n" +
            "General Fault: \n"+
            "Relay Response: \n";
    String deviceResponse = "";

    NotificationCompat.Builder notification;
    private static final int uniqueId = 123;



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if ( sharedPreferences.getString("mac", "").equals("") ){
            Toast.makeText(this, "Set User Info", Toast.LENGTH_LONG).show();
        }

        GET_URL = GET_URL_1 + sharedPreferences.getString("mac", "") + GET_URL_2;

        prefButton = findViewById(R.id.prefButton);
        status =  findViewById(R.id.StatusView2);

        option1Info =  findViewById(R.id.OptionInfo1);
        option2Info =  findViewById(R.id.OptionInfo2);
        option3Info =  findViewById(R.id.OptionInfo3);
        option4Info =  findViewById(R.id.OptionInfo4);
        duration1 = findViewById(R.id.duration1);
        duration2 = findViewById(R.id.duration2);
        duration3 = findViewById(R.id.duration3);
        duration4 = findViewById(R.id.duration4);
        endView1 = findViewById(R.id.endTime1);
        endView2 = findViewById(R.id.endTime2);
        endView3 = findViewById(R.id.endTime3);
        endView4 = findViewById(R.id.endTime4);
        goButton1 = findViewById(R.id.goButton1);
        goButton2 = findViewById(R.id.goButton2);
        goButton3 = findViewById(R.id.goButton3);
        goButton4  = findViewById(R.id.goButton4);
        envScore1 = findViewById(R.id.envScore1);
        envScore2 = findViewById(R.id.envScore2);
        envScore3 = findViewById(R.id.envScore3);
        socScore1 = findViewById(R.id.socScore1);
        socScore2 = findViewById(R.id.socScore2);
        socScore3 = findViewById(R.id.socScore3);


        prefButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickPrefButton();
            }
        });

        final String clientId = MqttClient.generateClientId();
        try {
            client = new MqttClient("tcp://" + sharedPreferences.getString("server", mqtt_server) + ":"+sharedPreferences.getString("port", mqtt_port), clientId, new MemoryPersistence());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    processMQTT(topic, message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(sharedPreferences.getString("username", mqtt_user));
            connOpts.setPassword( sharedPreferences.getString("password", mqtt_pwd).toCharArray() );
            client.connect(connOpts);
            subscribeTopics(client);
            try {
                sleep(1000);
                Log.d("MainActivity", "Paused");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishTopics(client);


        } catch (MqttException e) {
            e.printStackTrace();
        }
        post_evse();
        getRequest();

        goButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickGoButton(0);
            }
        });
        goButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickGoButton(1);
            }
        });
        goButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickGoButton(2);
            }
        });
        goButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickGoButton4();
            }
        });




    }

    public void clickGoButton(int index){
        RequestQueue queue = Volley.newRequestQueue(this);
        x = index;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, GET_URL,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        sendButtonInfo(response, x);
                        Log.d("Request", "success");
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG ).show();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("request", String.valueOf(error));
                Toast.makeText(MainActivity.this, "Request Error", Toast.LENGTH_LONG).show();
                prefIntent = new Intent(MainActivity.this, PrefActivity.class);
                startActivity(prefIntent);

            }
        });

        queue.add(stringRequest);




    }

    public void sendButtonInfo(JSONObject obj, int index){
        String schedId = "";
        try {
            schedId =  String.valueOf(obj.getJSONArray("schedules").getJSONObject(index).get("schedId"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(this);
        Toast.makeText(MainActivity.this, schedId, Toast.LENGTH_LONG).show() ;
        String url = "http://pluto.calit2.uci.edu:8082/v1/schedule/" + schedId +  "select/evse/evse_sim";
        StringRequest postRequest = new StringRequest(Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("macAddress", "evse_sim");
                params.put("mqttAddress", "mqtt://m10.cloudmqtt.com");
                params.put("mqttPort", "17934");
                params.put("mqttUser", "dkpljrty");
                params.put("mqttPass", "ZJDsxMVKRjoR");
                return params;

            }
        };
        queue.add(postRequest);
    }


    public void clickGoButton4(){
        try {
            client.publish(PUBL_TOPIC_PREFIX + "evse_sim" + "1/OnOff/Off", new MqttMessage("{\"method\": \"post\",\"params\":{}}".getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    public void post_evse(){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Method.POST, EVSE_POST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("macAddress", "evse_sim");
                params.put("mqttAddress", "mqtt://m10.cloudmqtt.com");
                params.put("mqttPort", "17934");
                params.put("mqttUser", "dkpljrty");
                params.put("mqttPass", "ZJDsxMVKRjoR");
                return params;

            }
        };
        queue.add(postRequest);


    }



    public void getRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, GET_URL,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJSON(response);
                        Log.d("Request", "success");
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG ).show();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("request", String.valueOf(error));
                Toast.makeText(MainActivity.this, "Request Error", Toast.LENGTH_LONG).show();
                prefIntent = new Intent(MainActivity.this, PrefActivity.class);
                startActivity(prefIntent);

            }
        });

        queue.add(stringRequest);




    }

    private void parseJSON(JSONObject response) {
        try {
            String opt1 = (String) response.getJSONArray("schedules").getJSONObject(0).get("startTime");
            option1Info.setText(opt1);
            String opt2 = (String) response.getJSONArray("schedules").getJSONObject(1).get("startTime");
            option2Info.setText(opt2);
            String opt3 = (String) response.getJSONArray("schedules").getJSONObject(2).get("startTime");
            option3Info.setText(opt3);

            duration1.setText("5 hrs");
            duration2.setText("5 hrs");
            duration3.setText("5 hrs");

            String endTime1 = String.valueOf(Integer.parseInt(opt1.substring(0,opt1.indexOf(':'))) +  5) + ":00";
            String endTime2 = String.valueOf(Integer.parseInt(opt2.substring(0, opt2.indexOf(':'))) +  5) + ":00";
            String endTime3 = String.valueOf(Integer.parseInt(opt3.substring(0,opt3.indexOf(':'))) +  5) + ":00";

            endView1.setText(endTime1);
            endView2.setText(endTime2);
            endView3.setText(endTime3);

            socScore1.setText(String.valueOf(response.getJSONArray("schedules").getJSONObject(0).get("society")).substring(0,3));
            socScore2.setText(String.valueOf(response.getJSONArray("schedules").getJSONObject(1).get("society")).substring(0,3));
            socScore3.setText(String.valueOf(response.getJSONArray("schedules").getJSONObject(2).get("society")).substring(0,3));

            envScore1.setText(String.valueOf(response.getJSONArray("schedules").getJSONObject(0).get("env")).substring(0,3) + "  ");
            envScore2.setText(String.valueOf(response.getJSONArray("schedules").getJSONObject(1).get("env")).substring(0,3) + "  ");
            envScore3.setText(String.valueOf(response.getJSONArray("schedules").getJSONObject(2).get("env")).substring(0,3) + "  ");




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void processMQTT(String topic, String message){

        if(topic.equals("out/devices/evse_sim/1/SimpleMeteringServer/chargeState") ){
            if(message.equals("0") ){
                status.setText("Not Connected");
            }
            else if(message.equals("1") ){
                status.setText("Charging");
                notification = new NotificationCompat.Builder(this);
                notification.setAutoCancel(true);
                notification.setSmallIcon(R.mipmap.ic_launcher);
                notification.setTicker("Vehicle is Charging");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("Your Vehicle is Charging");
                notification.setContentText("Click to set charge preferences");
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification.setSound(alarmSound);

                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent );

                NotificationManager nm  = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(uniqueId, notification.build());

            }
            else if(message.equals("2") ){
                status.setText("Connected");

                notification = new NotificationCompat.Builder(this);
                notification.setAutoCancel(true);
                notification.setSmallIcon(R.mipmap.ic_launcher);
                notification.setTicker("Your Vehicle is Plugged In");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("Your Vehicle is Plugged In");
                notification.setContentText("Click to set charge preferences");
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification.setSound(alarmSound);

                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent );

                NotificationManager nm  = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(uniqueId, notification.build());


            }
            else {
                status.setText("Not Found");
            }
        }
        else if(topic.equals("/1/SimpleSchedulingServer/vehicle_driver/uniqueID/useTime")){
            Toast.makeText(this, "here", Toast.LENGTH_LONG).show();
        }

        else {
            status.setText("boof");
            Log.d("MainActivity", topic);
        }

    }



    public void clickPrefButton(){
        prefIntent = new Intent(MainActivity.this, PrefActivity.class);
        startActivity(prefIntent);
    }

    public void subscribeTopics(MqttClient client){
        try {
            client.subscribe(SUBSC_TOPIC_PREFIX + "evse_sim" + "/1/SimpleMeteringServer/chargeState");
//            client.subscribe(SUBSC_TOPIC_PREFIX + "evse_sim" + "/1/SimpleSchedulingServer/vehicle_driver/uniqueID/useTime");

//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GeneralFault");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/SUPLevel");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GFIState");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/RequestCurrent");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/RmsCurrent");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/ChargeState");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/LVoltage");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/CurrentSummation/AccumulatedDemandCharge");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/AccumulatedDemandTotal");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GROUNDOK");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/INSTCurrent");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/InstantaneousDemand");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/SaveLevel1Charge");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/SaveLevel2Charge");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GroundLeakage");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/UpdateGroundThreshold");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GeneralFault");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/OnOff/Toggle");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/OnOff/On");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/1/OnOff/Off");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset");
//            client.subscribe(SUBSC_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void publishTopics(MqttClient client){
        try {
            client.publish(PUBL_TOPIC_PREFIX + "evse_sim" + "/1/SimpleMeteringServer/chargeState", new MqttMessage( "{\"method\": \"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GeneralFault", new MqttMessage( "{\"method\": \"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/SUPLevel", new MqttMessage( "{\"method\": \"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GFIState", new MqttMessage( "{\"method\": \"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/RequestCurrent", new MqttMessage( ("{\"method\": \"get\",\"params\":{\"value\":\"Current\"}}").getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/RmsCurrent", new MqttMessage( "{\"method\": \"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/ChargeState", new MqttMessage( "{\"method\": \"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/LVoltage", new MqttMessage( "{\"method\":\"get\",\"params\":{\"value\":\"L1\"}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/CurrentSummation/AccumulatedDemandCharge", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/AccumulatedDemandTotal", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GROUNDOK", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/INSTCurrent", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/InstantaneousDemand", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/SaveLevel1Charge", new MqttMessage( "{\"method\":\"post\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/SaveLevel2Charge", new MqttMessage( "{\"method\":\"post\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/GroundLeakage", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/SimpleMeteringServer/UpdateGroundThreshold", new MqttMessage( "{\"method\":\"get\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/OnOff/Toggle", new MqttMessage( "{\"method\":\"post\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/OnOff/On", new MqttMessage( "{\"method\":\"post\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/1/OnOff/Off", new MqttMessage( "{\"method\":\"post\",\"params\":{}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset", new MqttMessage( "{\"method\":\"post\",\"params\":{\"value\":\"all\"}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset", new MqttMessage( "{\"method\":\"get\",\"params\":{\"value\":\"wifi\"}{\"wifiname:password\"}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset", new MqttMessage( "{\"method\":\"get\",\"params\":{\"value\":\"mqtt\"}{\"mqttuser:mqttpassword:mqttserver: mqttport\"}}".getBytes()));
//            client.publish(PUBL_TOPIC_PREFIX + device_MAC_ID + "/0/cdo/reset", new MqttMessage( "{\"method\":\"get\",\"params\":{\"value\":\"device\"}}".getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}

