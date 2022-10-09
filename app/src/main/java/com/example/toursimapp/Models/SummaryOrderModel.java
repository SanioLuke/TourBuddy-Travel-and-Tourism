package com.example.toursimapp.Models;

import java.io.Serializable;
import java.util.Date;

public class SummaryOrderModel implements Serializable {

    String dbname, main_place_id, inner_place_id, order_id, hotel_id, hotel_name, place_name, hotel_img,
            s_am_pm, e_am_pm, reference_id, error_msg, hotel_contact_no, hotel_website, hotel_emailID;
    long no_days, total_no_guests, no_rooms, main_price, tax_gst, service_fee, total_final_price;
    boolean order_summary_status;
    Date startDate, endDate, bookedDate;

    public SummaryOrderModel(String dbname, String main_place_id, String inner_place_id, String order_id, String hotel_id, String hotel_name,
                             String place_name, String hotel_img, long main_price, long tax_gst, long service_fee, long total_final_price,
                             long no_days, long total_no_guests, long no_rooms, String hotel_contact_no, String hotel_website, String hotel_emailID,
                             Date startDate, Date endDate, String s_am_pm, String e_am_pm, Date bookedDate, boolean order_summary_status,
                             String reference_id, String error_msg) {
        this.dbname = dbname;
        this.main_place_id = main_place_id;
        this.inner_place_id = inner_place_id;
        this.order_id = order_id;
        this.hotel_id = hotel_id;
        this.hotel_name = hotel_name;
        this.place_name = place_name;
        this.hotel_img = hotel_img;
        this.main_price = main_price;
        this.tax_gst = tax_gst;
        this.service_fee = service_fee;
        this.total_final_price = total_final_price;
        this.no_days = no_days;
        this.total_no_guests = total_no_guests;
        this.no_rooms = no_rooms;
        this.hotel_contact_no = hotel_contact_no;
        this.hotel_website = hotel_website;
        this.hotel_emailID = hotel_emailID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.s_am_pm = s_am_pm;
        this.e_am_pm = e_am_pm;
        this.bookedDate = bookedDate;
        this.order_summary_status = order_summary_status;
        this.reference_id = reference_id;
        this.error_msg = error_msg;
    }

    public SummaryOrderModel(String dbname, String main_place_id, String inner_place_id, String order_id, String hotel_id, String hotel_name, String place_name,
                             String hotel_img, long main_price, long tax_gst, long service_fee, long total_final_price, long no_days, long total_no_guests,
                             long no_rooms, String hotel_contact_no, String hotel_website, String hotel_emailID,
                             Date startDate, Date endDate, String s_am_pm, String e_am_pm, Date bookedDate, String reference_id) {
        this.dbname = dbname;
        this.main_place_id = main_place_id;
        this.inner_place_id = inner_place_id;
        this.order_id = order_id;
        this.hotel_id = hotel_id;
        this.hotel_name = hotel_name;
        this.place_name = place_name;
        this.hotel_img = hotel_img;
        this.main_price = main_price;
        this.tax_gst = tax_gst;
        this.service_fee = service_fee;
        this.total_final_price = total_final_price;
        this.no_days = no_days;
        this.total_no_guests = total_no_guests;
        this.no_rooms = no_rooms;
        this.hotel_contact_no = hotel_contact_no;
        this.hotel_website = hotel_website;
        this.hotel_emailID = hotel_emailID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.s_am_pm = s_am_pm;
        this.e_am_pm = e_am_pm;
        this.bookedDate = bookedDate;
        this.reference_id = reference_id;
    }

    public String getDbname() {
        return dbname;
    }

    public String getMain_place_id() {
        return main_place_id;
    }

    public String getInner_place_id() {
        return inner_place_id;
    }

    public String getHotel_img() {
        return hotel_img;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getReference_id() {
        return reference_id;
    }

    public String getError_msg() {
        return error_msg;
    }

    public boolean isOrder_summary_status() {
        return order_summary_status;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getS_am_pm() {
        return s_am_pm;
    }

    public String getE_am_pm() {
        return e_am_pm;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public long getTax_gst() {
        return tax_gst;
    }

    public long getMain_price() {
        return main_price;
    }

    public long getService_fee() {
        return service_fee;
    }

    public long getTotal_final_price() {
        return total_final_price;
    }

    public long getNo_days() {
        return no_days;
    }

    public long getTotal_no_guests() {
        return total_no_guests;
    }

    public long getNo_rooms() {
        return no_rooms;
    }

    public String getHotel_contact_no() {
        return hotel_contact_no;
    }

    public String getHotel_website() {
        return hotel_website;
    }

    public String getHotel_emailID() {
        return hotel_emailID;
    }
}
