package com.example.toursimapp.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class HotelDetailModel implements Serializable {

    String db_name, main_place_id, inner_place_id, hotel_id, hotel_name, place_name, hotel_rate, hotel_des;
    Double hotel_longitude, hotel_latitude;
    ArrayList<String> hotel_img, hotel_fac, hotel_contact;
    long people_no_booking, initial_price, service_fee, tax_gst_fee, people_accomodation_no;

    public HotelDetailModel(String db_name, String main_place_id, String inner_place_id, String hotel_id,
                            String hotel_name, String place_name, String hotel_rate, String hotel_des,
                            Double hotel_longitude, Double hotel_latitude, ArrayList<String> hotel_img,
                            ArrayList<String> hotel_fac, ArrayList<String> hotel_contact, long people_no_booking,
                            long initial_price, long service_fee, long tax_gst_fee, long people_accomodation_no) {
        this.db_name = db_name;
        this.main_place_id = main_place_id;
        this.inner_place_id = inner_place_id;
        this.hotel_id = hotel_id;
        this.hotel_name = hotel_name;
        this.place_name = place_name;
        this.hotel_rate = hotel_rate;
        this.hotel_des = hotel_des;
        this.hotel_longitude = hotel_longitude;
        this.hotel_latitude = hotel_latitude;
        this.hotel_img = hotel_img;
        this.hotel_fac = hotel_fac;
        this.hotel_contact = hotel_contact;
        this.people_no_booking = people_no_booking;
        this.initial_price = initial_price;
        this.service_fee = service_fee;
        this.tax_gst_fee = tax_gst_fee;
        this.people_accomodation_no = people_accomodation_no;
    }

    public String getDb_name() {
        return db_name;
    }

    public String getMain_place_id() {
        return main_place_id;
    }

    public String getInner_place_id() {
        return inner_place_id;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public String getHotel_rate() {
        return hotel_rate;
    }

    public String getHotel_des() {
        return hotel_des;
    }

    public Double getHotel_longitude() {
        return hotel_longitude;
    }

    public Double getHotel_latitude() {
        return hotel_latitude;
    }

    public ArrayList<String> getHotel_img() {
        return hotel_img;
    }

    public ArrayList<String> getHotel_fac() {
        return hotel_fac;
    }

    public ArrayList<String> getHotel_contact() {
        return hotel_contact;
    }

    public long getPeople_no_booking() {
        return people_no_booking;
    }

    public long getInitial_price() {
        return initial_price;
    }

    public long getService_fee() {
        return service_fee;
    }

    public long getTax_gst_fee() {
        return tax_gst_fee;
    }

    public long getPeople_accomodation_no() {
        return people_accomodation_no;
    }
}
