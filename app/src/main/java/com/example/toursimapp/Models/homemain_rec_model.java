package com.example.toursimapp.Models;

import java.util.ArrayList;

public class homemain_rec_model {

    private final String trip_names;
    private final ArrayList<CategoryModel> trip_places_list;

    public homemain_rec_model(String trip_names, ArrayList<CategoryModel> trip_places_list) {
        this.trip_names = trip_names;
        this.trip_places_list = trip_places_list;
    }

    public ArrayList<CategoryModel> getTrip_places_list() {
        return trip_places_list;
    }

    public String getTrip_names() {
        return trip_names;
    }
}
