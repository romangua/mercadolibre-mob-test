package com.rguarino.mercadolibre_test.entity;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Installments {
    private Integer quantity;
    private Float amount;
    private Float rate;
    private String currency_id;

    public Installments() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }
}
