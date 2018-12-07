package com.rguarino.mercadolibre_test.entity;

/**
 * Created by dev3 on 07/12/2018.
 */

public class Attribute {

    /*
        Marca
        Modelo
        Línea
        Condición del ítem
     */
    private String name;
    private String value_name;

    public Attribute() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue_name() {
        return value_name;
    }

    public void setValue_name(String value_name) {
        this.value_name = value_name;
    }
}
