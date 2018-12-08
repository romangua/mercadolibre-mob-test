package com.rguarino.mercadolibre_test.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Installment implements Parcelable {
    private int quantity;
    private float amount;
    private float rate;
    private String currency_id;

    public Installment() {
    }

    protected Installment(Parcel in) {
        this.quantity = in.readInt();
        this.amount = in.readFloat();
        this.rate = in.readFloat();
        this.currency_id = in.readString();
    }

    public static final Parcelable.Creator<Installment> CREATOR = new Parcelable.Creator<Installment>() {
        @Override
        public Installment createFromParcel(Parcel in) {
            return new Installment(in);
        }

        @Override
        public Installment[] newArray(int size) {
            return new Installment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.quantity);
        parcel.writeFloat(this.amount);
        parcel.writeFloat(this.rate);
        parcel.writeString(this.currency_id);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }
}
