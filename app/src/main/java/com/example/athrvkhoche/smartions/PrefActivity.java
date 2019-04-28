package com.example.athrvkhoche.smartions;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class PrefActivity extends AppCompatActivity {

    Button closeButton;
    Button saveButton;

    Intent mainIntent;
    EditText mac;
    EditText server;
    EditText username;
    EditText password;
    EditText port;
    SeekBar costBar;
    SeekBar envBar;
    SeekBar societyBar;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        closeButton = findViewById(R.id.close);
        saveButton = findViewById(R.id.save);

        mac = findViewById(R.id.mac);
        server = findViewById(R.id.server);
        username = findViewById(R.id.username);
        password =  findViewById(R.id.password);
        port  = findViewById(R.id.port);

        costBar = findViewById(R.id.costBar);
        costBar.setMax(10);
        costBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        editor.putString("cost", String.valueOf(i));
                        editor.commit();

                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                }
        );
        envBar = findViewById(R.id.envBar);
        envBar.setMax(10);
        envBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        editor.putString("env", String.valueOf(i));
                        editor.commit();


                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                }
        );
        societyBar = findViewById(R.id.societyBar);
        societyBar.setMax(10);
        societyBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        Toast.makeText(PrefActivity.this, String.valueOf(i), Toast.LENGTH_LONG).show();
                        editor.putString("society", String.valueOf(i));
                        editor.commit();
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                }
        );

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mac.setText(sharedPreferences.getString("mac","evse_sim"));
        server.setText(sharedPreferences.getString("server","m10.cloudmqtt.com"));
        username.setText(sharedPreferences.getString("username", "dkpljrty"));
        password.setText(sharedPreferences.getString("password","ZJDsxMVKRjoR"));
        port.setText(sharedPreferences.getString("port", "17934"));


        costBar.setProgress(Integer.parseInt(sharedPreferences.getString("cost", "5")));
        envBar.setProgress(Integer.parseInt(sharedPreferences.getString("env", "5")));
        societyBar.setProgress(Integer.parseInt(sharedPreferences.getString("society", "5")));



        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setCloseButton(); }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setSaveButton(); }
        });


    }


    public void setCloseButton(){
        mainIntent = new Intent(PrefActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }


    public void setSaveButton(){
        Toast.makeText(this, "Saving", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mac", mac.getText().toString());
        editor.putString("server", server.getText().toString());
        editor.putString("username", username.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putString("port", port.getText().toString());
        editor.commit();



    }
}
