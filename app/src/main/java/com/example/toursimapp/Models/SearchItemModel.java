package com.example.toursimapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchItemModel implements Parcelable {
    public static final Creator<SearchItemModel> CREATOR = new Creator<SearchItemModel>() {
        @Override
        public SearchItemModel createFromParcel(Parcel in) {
            return new SearchItemModel(in);
        }

        @Override
        public SearchItemModel[] newArray(int size) {
            return new SearchItemModel[size];
        }
    };
    String sdb_name, smain_place, sinner_place, s_place_name, s_place_img;

    public SearchItemModel(String sdb_name, String smain_place, String sinner_place, String s_place_name, String s_place_img) {
        this.sdb_name = sdb_name;
        this.smain_place = smain_place;
        this.sinner_place = sinner_place;
        this.s_place_name = s_place_name;
        this.s_place_img = s_place_img;
    }

    protected SearchItemModel(Parcel in) {
        sdb_name = in.readString();
        smain_place = in.readString();
        sinner_place = in.readString();
        s_place_name = in.readString();
        s_place_img = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sdb_name);
        dest.writeString(smain_place);
        dest.writeString(sinner_place);
        dest.writeString(s_place_name);
        dest.writeString(s_place_img);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getSdb_name() {
        return sdb_name;
    }

    public void setSdb_name(String sdb_name) {
        this.sdb_name = sdb_name;
    }

    public String getSmain_place() {
        return smain_place;
    }

    public void setSmain_place(String smain_place) {
        this.smain_place = smain_place;
    }

    public String getSinner_place() {
        return sinner_place;
    }

    public void setSinner_place(String sinner_place) {
        this.sinner_place = sinner_place;
    }

    public String getS_place_name() {
        return s_place_name;
    }

    public void setS_place_name(String s_place_name) {
        this.s_place_name = s_place_name;
    }

    public String getS_place_img() {
        return s_place_img;
    }

    public void setS_place_img(String s_place_img) {
        this.s_place_img = s_place_img;
    }

}
