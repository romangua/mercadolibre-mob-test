package com.rguarino.mercadolibre_test.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Product implements Parcelable {
    private String id;
    private String title;
    private float price;

    /**
     * ARS
     */
    private String currency_id;
    private int available_quantity;
    private int sold_quantity;

    /**
     * used
     * new
     */
    private String condition;
    private String thumbnail;
    private boolean accepts_mercadopago;
    private Installment installments;
    private Address address;
    private Shipping shipping;
    private Review reviews;
    private List<Attribute> attributes;
    private List<Picture> pictures;

    public Product() {
    }

    protected Product(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.price = in.readFloat();
        this.currency_id = in.readString();
        this.available_quantity = in.readInt();
        this.sold_quantity = in.readInt();
        this.condition = in.readString();
        this.thumbnail = in.readString();
        this.accepts_mercadopago = in.readInt() == 1 ? true : false;
        this.currency_id = in.readString();
        this.installments = in.readParcelable(Installment.class.getClassLoader());
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.shipping = in.readParcelable(Shipping.class.getClassLoader());
        this.reviews = in.readParcelable(Review.class.getClassLoader());

        if (in.readByte() == 0x01) {
            this.attributes = new ArrayList<>();
            in.readList(this.attributes, Attribute.class.getClassLoader());
        } else {
            this.attributes = null;
        }

        if (in.readByte() == 0x01) {
            this.pictures = new ArrayList<>();
            in.readList(this.pictures, Picture.class.getClassLoader());
        } else {
            this.pictures = null;
        }
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        parcel.writeFloat(this.price);
        parcel.writeString(this.currency_id);
        parcel.writeInt(this.available_quantity);
        parcel.writeInt(this.sold_quantity);
        parcel.writeString(this.condition);
        parcel.writeString(this.thumbnail);
        parcel.writeInt(this.accepts_mercadopago ? 1 : 0);
        parcel.writeString(this.currency_id);
        parcel.writeParcelable(this.installments, i);
        parcel.writeParcelable(this.address, i);
        parcel.writeParcelable(this.shipping, i);
        parcel.writeParcelable(this.reviews, i);

        if (this.attributes == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(this.attributes);
        }

        if (this.pictures == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(this.pictures);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public int getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(int available_quantity) {
        this.available_quantity = available_quantity;
    }

    public int getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(int sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isAccepts_mercadopago() {
        return accepts_mercadopago;
    }

    public void setAccepts_mercadopago(boolean accepts_mercadopago) {
        this.accepts_mercadopago = accepts_mercadopago;
    }

    public Installment getInstallments() {
        return installments;
    }

    public void setInstallments(Installment installments) {
        this.installments = installments;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Review getReviews() {
        return reviews;
    }

    public void setReviews(Review reviews) {
        this.reviews = reviews;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
