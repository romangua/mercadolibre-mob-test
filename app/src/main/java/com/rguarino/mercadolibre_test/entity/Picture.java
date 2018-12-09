package com.rguarino.mercadolibre_test.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {

    private String id;
    private String url;

    public Picture() {
    }

    public Picture(String url) {
        this.url = url;
    }

    protected Picture(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.url);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
