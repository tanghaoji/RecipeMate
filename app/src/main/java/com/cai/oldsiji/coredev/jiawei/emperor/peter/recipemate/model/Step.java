package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model;

import android.graphics.Bitmap;

/**
 * Created by Haoji on 2016-03-12.
 */
public class Step {
    private int stepNumber;
    private String stepDetail;
    Bitmap thumbnail;

    public Step(int stepNumber, String stepDetail, Bitmap thumbnail) {
        this.stepNumber = stepNumber;
        this.stepDetail = stepDetail;
        this.thumbnail = thumbnail;
    }

    public void setStepDetail (String stepDetail) {
        this.stepDetail = stepDetail;
    }

    public void setThumbnail (Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStepDetail () {
        return stepDetail;
    }

    public int getStepNumber () {
        return stepNumber;
    }

    public Bitmap getThumbnail () {
        return thumbnail;
    }
}
