package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Ingredient;
import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Recipe;

import java.util.ArrayList;


public class Ingredients extends AppCompatActivity {

    private ArrayList<Ingredient> ingredients = new ArrayList();
    private IngredientsListAdapter adapter;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ingredients);
        ListView ingredientListView = (ListView)findViewById(R.id.listView);
        adapter = new IngredientsListAdapter(Ingredients.this, R.layout.ingredience_single_line, ingredients);
        ingredientListView.setAdapter(adapter);

        adapter.insert(new Ingredient("", 0, ""), 0);

        Intent i = getIntent();
        recipe = (Recipe)i.getSerializableExtra("newRecipe");

//        if (ingredients.size() == 1) {
//            ImageButton remove = (ImageButton)findViewById(R.id.ingredients_removeIngredients);
//            remove.setVisibility(View.GONE);
//        }

    }

    public void removeIngredientsOnClickHandler(View v) {
        Ingredient itemToRemove = (Ingredient)v.getTag();
        adapter.remove(itemToRemove);

//        if (ingredients.size() == 1) {
//            ImageButton remove = (ImageButton)findViewById(R.id.ingredients_removeIngredients);
//            remove.setVisibility(View.GONE);
//        }
    }

    public void addIngredientsOnClickHandler(View v) {
        adapter.insert(new Ingredient("", 0, ""), 0);
    }


    public void goToCookSteps(View view) {
        recipe.addIngredients(ingredients);
        Intent intent = new Intent(Ingredients.this, CookSteps.class);
        intent.putExtra("newRecipe", recipe);
        startActivity(intent);
    }

}
