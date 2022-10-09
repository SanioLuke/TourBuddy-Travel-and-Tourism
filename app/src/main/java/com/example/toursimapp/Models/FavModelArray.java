package com.example.toursimapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class FavModelArray implements Parcelable {

    public static final Creator<FavModelArray> CREATOR = new Creator<FavModelArray>() {
        @Override
        public FavModelArray createFromParcel(Parcel in) {
            return new FavModelArray(in);
        }

        @Override
        public FavModelArray[] newArray(int size) {
            return new FavModelArray[size];
        }
    };
    private final String fav_db_name;
    private final String fav_main_place_id;
    private final String fav_inner_place_id;
    private final String fav_place_name;

    public FavModelArray(String fav_db_name, String fav_main_place_id, String fav_inner_place_id, String fav_place_name) {
        this.fav_db_name = fav_db_name;
        this.fav_main_place_id = fav_main_place_id;
        this.fav_inner_place_id = fav_inner_place_id;
        this.fav_place_name = fav_place_name;
    }

    protected FavModelArray(Parcel in) {
        fav_db_name = in.readString();
        fav_main_place_id = in.readString();
        fav_inner_place_id = in.readString();
        fav_place_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fav_db_name);
        dest.writeString(fav_main_place_id);
        dest.writeString(fav_inner_place_id);
        dest.writeString(fav_place_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFav_db_name() {
        return fav_db_name;
    }

    public String getFav_main_place_id() {
        return fav_main_place_id;
    }

    public String getFav_inner_place_id() {
        return fav_inner_place_id;
    }

}
