package com.example.toursimapp.Models;

import java.util.ArrayList;

public class InnerCategoryModel {

    private String inner_place_id, inner_place_name, inner_rating, inner_description, main_place_id, db_name;
    private Double longitude, latitude;
    private ArrayList<String> inner_images, inner_features;

    public InnerCategoryModel(String inner_place_id, String inner_place_name, String inner_rating, String inner_description, String main_place_id, String db_name, Double longitude, Double latitude, ArrayList<String> inner_images, ArrayList<String> inner_features) {
        this.inner_place_id = inner_place_id;
        this.inner_place_name = inner_place_name;
        this.inner_rating = inner_rating;
        this.inner_description = inner_description;
        this.db_name = db_name;
        this.main_place_id = main_place_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.inner_images = inner_images;
        this.inner_features = inner_features;
    }

    public String getInner_place_id() {
        return inner_place_id;
    }

    public void setInner_place_id(String inner_place_id) {
        this.inner_place_id = inner_place_id;
    }

    public String getInner_place_name() {
        return inner_place_name;
    }

    public void setInner_place_name(String inner_place_name) {
        this.inner_place_name = inner_place_name;
    }

    public String getInner_rating() {
        return inner_rating;
    }

    public void setInner_rating(String inner_rating) {
        this.inner_rating = inner_rating;
    }

    public String getInner_description() {
        return inner_description;
    }

    public void setInner_description(String inner_description) {
        this.inner_description = inner_description;
    }

    public String getMain_place_id() {
        return main_place_id;
    }

    public void setMain_place_id(String main_place_id) {
        this.main_place_id = main_place_id;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public ArrayList<String> getInner_images() {
        return inner_images;
    }

    public void setInner_images(ArrayList<String> inner_images) {
        this.inner_images = inner_images;
    }

    public ArrayList<String> getInner_features() {
        return inner_features;
    }

    public void setInner_features(ArrayList<String> inner_features) {
        this.inner_features = inner_features;
    }
}
