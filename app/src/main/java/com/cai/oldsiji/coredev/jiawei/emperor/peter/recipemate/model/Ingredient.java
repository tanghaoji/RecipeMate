package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model;

/**
 * Created by Haoji on 2016-03-12.
 */
import java.io.Serializable;

public class Ingredient implements Serializable {
    private static final long serialVersionUID = -5435670920302756945L;

    private String name = "";
    private double value = 0;
    private String unit = "";

    public Ingredient(String name, double value, String unit) {
        this.setName(name);
        this.setValue(value);
        this.setUnit(unit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
