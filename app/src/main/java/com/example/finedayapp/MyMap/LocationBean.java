package com.example.finedayapp.MyMap;

public class LocationBean {

    private String latitude;
    private String longitude;

    public LocationBean() {
        super();
    }

    public LocationBean(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "坐标:" + "\n" +
                "北纬=" + latitude + "\n" +
                "东经=" + longitude + "\n";
    }

}
