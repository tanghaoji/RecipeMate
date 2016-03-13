package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Haoji on 2016-03-12.
 */
public class Recipe implements Serializable{
    private String name;
    private String description;
    private double taste, difficulty;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private Bitmap coverPhoto;

    public Recipe() {
        ingredients = new ArrayList<>();
        steps = new ArrayList();
        coverPhoto = null;
    }

    public void addBasicInfo(String name, String description, double taste, double difficulty) {
        this.name = name;
        this.description = description;
        this.taste = taste;
        this.difficulty = difficulty;
    }

    public void addIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public void addCoverPhoto(Bitmap coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public Bitmap getCoverPhoto() {
        return coverPhoto;
    }

    public String getName() {
        return  name;
    }

    // flatten the ingredient arrayList
    public String getIngredients() {
        String ingredientsName = "";
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ingredientsName = ingredientsName + " ," + ingredient.getName();
        }
        return ingredientsName.substring(2, ingredientsName.length());
    }

    public ArrayList<Ingredient> getIngredientsList() {
        return ingredients;
    }

    public String getDescription() {
        return description;
    }

    public double getTaste() {
        return taste;
    }

    public double getDifficulty() {
        return  difficulty;
    }
}
