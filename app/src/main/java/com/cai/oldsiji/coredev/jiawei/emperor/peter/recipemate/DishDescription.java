package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Recipe;

public class DishDescription extends AppCompatActivity {

    private Recipe recipe = new Recipe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dish_description);
    }

    public void goToIngredients(View view) {
        String dishName = ((EditText)findViewById(R.id.dishNameInput)).getText().toString();
        String description = ((EditText)findViewById(R.id.descriptionInput)).getText().toString();
        double tasteRating = ((RatingBar) findViewById(R.id.tasteRating)).getRating();
        double difficultyRating = ((RatingBar) findViewById(R.id.difficultyRating)).getRating();

        recipe.addBasicInfo(dishName,description,tasteRating,difficultyRating);

        Intent intent = new Intent(DishDescription.this, Ingredients.class);
        intent.putExtra("newRecipe", recipe);
        startActivity(intent);
    }
}
