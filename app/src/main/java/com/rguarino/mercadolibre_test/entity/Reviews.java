package com.rguarino.mercadolibre_test.entity;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Reviews {
    private Float rating_average;
    private Integer total;

    public Reviews() {
    }

    public Float getRating_average() {
        return rating_average;
    }

    public void setRating_average(Float rating_average) {
        this.rating_average = rating_average;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
