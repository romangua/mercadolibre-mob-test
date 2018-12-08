package com.rguarino.mercadolibre_test.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Shipping implements Parcelable {
    private boolean free_shipping;

    public Shipping() {
    }

    protected Shipping(Parcel in) {
        this.free_shipping = in.readInt() == 1 ? true : false;
    }

    public static final Parcelable.Creator<Shipping> CREATOR = new Parcelable.Creator<Shipping>() {
        @Override
        public Shipping createFromParcel(Parcel in) {
            return new Shipping(in);
        }

        @Override
        public Shipping[] newArray(int size) {
            return new Shipping[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.free_shipping ? 1 : 0);
    }

    public boolean getFree_shipping() {
        return free_shipping;
    }

    public void setFree_shipping(boolean free_shipping) {
        this.free_shipping = free_shipping;
    }
}
