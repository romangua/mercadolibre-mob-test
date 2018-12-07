package com.rguarino.mercadolibre_test.entity;

import java.util.List;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Product {
    private String id;
    private String title;
    private Float price;

    /**
     * ARS
     */
    private String currency_id;
    private Integer available_quantity;
    private Integer sold_quantity;

    /**
     * used
     * new
     */
    private String condition;
    private String thumbnail;
    private Boolean accepts_mercadopago;
    private Installments installments;
    private Address address;
    private Shipping shipping;
    private Reviews reviews;
    private List<Attributes> atrAttributes;

    public Product() {
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public Integer getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(Integer available_quantity) {
        this.available_quantity = available_quantity;
    }

    public Integer getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(Integer sold_quantity) {
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

    public Boolean getAccepts_mercadopago() {
        return accepts_mercadopago;
    }

    public void setAccepts_mercadopago(Boolean accepts_mercadopago) {
        this.accepts_mercadopago = accepts_mercadopago;
    }

    public Installments getInstallments() {
        return installments;
    }

    public void setInstallments(Installments installments) {
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

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    public List<Attributes> getAtrAttributes() {
        return atrAttributes;
    }

    public void setAtrAttributes(List<Attributes> atrAttributes) {
        this.atrAttributes = atrAttributes;
    }
}
