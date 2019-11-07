package com.example.draft;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Users extends AppCompatActivity {
    //for the scrollable textview to see people three key data, heart rate, oxygen, and confidence https://www.youtube.com/watch?v=E6vE8fqQPTE
    private String Username;
    private String HeartRate;
    private String Oxygen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public Users(String Username, String HeartRate,  String Oxygen) {
        this.HeartRate = HeartRate;
        this.Username = Username;
        this.Oxygen = Oxygen;
    }

    public String getHeartRate() {
        return HeartRate;
    }

    public void setHeartRate(String HeartRate) {
        this.HeartRate = HeartRate;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getOxygen() {
        return Oxygen;
    }

    public void setOxygen(String Oxygen) {
        this.Oxygen = Oxygen;
    }

}

