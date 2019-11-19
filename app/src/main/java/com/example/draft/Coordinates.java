package com.example.draft;

public class Coordinates {

    public String latitude;
    public String longitude;
    public String name;


    public Coordinates(){
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;    }
    public void getLatitude(String latitude) {
        this.latitude = latitude;    }
    public void getLongitude(String longitude) {
        this.longitude = longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public Object getLatitude() {
        this.latitude = latitude;
        return latitude;
}

    public Object getLongitude() {
        this.longitude = longitude;
        return longitude;
    }

    public void longitude(Object longitude) {
        
    }

    public void latitude(Object latitude) {
    }
}
