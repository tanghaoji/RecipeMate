package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Recipe;

import java.util.List;

/**
 * Created by Haoji on 2016-03-13.
 */
public class RecipesListAdapter extends ArrayAdapter<Recipe> {
    protected static final String LOG_TAG = RecipesListAdapter.class.getSimpleName();

    private List<Recipe> items;
    private int layoutResourceId;
    private Context context;

    public RecipesListAdapter(Context context, int layoutResourceId, List<Recipe> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecipeHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new RecipeHolder();
        holder.recipe = items.get(position);

        holder.coverPhoto = (ImageView)row.findViewById(R.id.coverPhotoView);

        holder.dishName = (TextView)row.findViewById(R.id.dishNameText);

        holder.ingredients = (TextView)row.findViewById(R.id.ingredientsText);


        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(RecipeHolder holder) {
        holder.coverPhoto.setImageBitmap(holder.recipe.getCoverPhoto());
        holder.coverPhoto.getLayoutParams().height = 320;
        holder.coverPhoto.getLayoutParams().width = 320;
        holder.dishName.setText(String.valueOf(holder.recipe.getName()));
        holder.ingredients.setText(holder.recipe.getIngredients());
    }

    public static class RecipeHolder {
        Recipe recipe;
        ImageView coverPhoto;
        TextView dishName;
        TextView ingredients;
    }
}
