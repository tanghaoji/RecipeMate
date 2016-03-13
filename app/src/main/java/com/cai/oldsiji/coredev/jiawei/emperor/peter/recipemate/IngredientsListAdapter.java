package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate;

/**
 * Created by Haoji on 2016-03-12.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Ingredient;

public class IngredientsListAdapter extends ArrayAdapter<Ingredient> {

    protected static final String LOG_TAG = IngredientsListAdapter.class.getSimpleName();

    private List<Ingredient> items;
    private int layoutResourceId;
    private Context context;

    public IngredientsListAdapter(Context context, int layoutResourceId, List<Ingredient> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        IngredientHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new IngredientHolder();
        holder.ingredient = items.get(position);
        holder.removeIngredientsButton = (ImageButton)row.findViewById(R.id.ingredients_removeIngredients);
        holder.removeIngredientsButton.setTag(holder.ingredient);

        holder.name = (TextView)row.findViewById(R.id.ingredients_name);
        setNameTextChangeListener(holder);
        holder.value = (TextView)row.findViewById(R.id.ingredients_amount);
        setValueTextListeners(holder);
        holder.unit = (TextView)row.findViewById(R.id.ingredients_unit);
        setUnitTextChangeListener(holder);

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(IngredientHolder holder) {
        holder.name.setText(holder.ingredient.getName());
        holder.value.setText(String.valueOf(holder.ingredient.getValue()));
        holder.unit.setText(holder.ingredient.getUnit());
    }

    public static class IngredientHolder {
        Ingredient ingredient;
        TextView name;
        TextView value;
        TextView unit;
        ImageButton removeIngredientsButton;
    }

    private void setNameTextChangeListener(final IngredientHolder holder) {
        holder.name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.ingredient.setName(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setValueTextListeners(final IngredientHolder holder) {
        holder.value.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    holder.ingredient.setValue(Double.parseDouble(s.toString()));
                }catch (NumberFormatException e) {
                    Log.e(LOG_TAG, "error reading double value: " + s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setUnitTextChangeListener(final IngredientHolder holder) {
        holder.unit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.ingredient.setUnit(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}
