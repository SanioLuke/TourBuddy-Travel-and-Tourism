package com.example.toursimapp.Models;

import java.util.ArrayList;

public class CategoryModel {

    private final String place_id;
    private final String description;
    private final String attractions;
    private final String best_time;
    private final String rate_place;
    private final String climate;
    private final String reach_method;
    private final Double longitude;
    private final Double latitude;
    private final ArrayList<String> array_images;
    private String place_name;
    private String db_name;
    private String firstImage;

    public CategoryModel(String place_id, String place_name, String description,
                         String attractions, String best_time, String rate_place,
                         String climate, String reach_method, Double longitude,
                         Double latitude, ArrayList<String> array_images, String db_name) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.description = description;
        this.attractions = attractions;
        this.best_time = best_time;
        this.rate_place = rate_place;
        this.climate = climate;
        this.reach_method = reach_method;
        this.longitude = longitude;
        this.latitude = latitude;
        this.array_images = array_images;
        this.db_name = db_name;
    }

    public CategoryModel(String place_id, String place_name, String description,
                         String attractions, String best_time, String rate_place,
                         String climate, String reach_method, Double longitude,
                         Double latitude, ArrayList<String> array_images, String db_name, String firstImage) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.description = description;
        this.attractions = attractions;
        this.best_time = best_time;
        this.rate_place = rate_place;
        this.climate = climate;
        this.reach_method = reach_method;
        this.longitude = longitude;
        this.latitude = latitude;
        this.array_images = array_images;
        this.db_name = db_name;
        this.firstImage = firstImage;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getDescription() {
        return description;
    }

    public String getAttractions() {
        return attractions;
    }

    public String getBest_time() {
        return best_time;
    }

    public String getRate_place() {
        return rate_place;
    }

    public String getClimate() {
        return climate;
    }

    public String getReach_method() {
        return reach_method;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public ArrayList<String> getArray_images() {
        return array_images;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }
}
