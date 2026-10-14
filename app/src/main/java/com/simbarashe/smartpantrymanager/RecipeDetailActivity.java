package com.simbarashe.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.simbarashe.smartpantrymanager.data.AppDatabase;
import com.simbarashe.smartpantrymanager.data.Recipe;
import com.simbarashe.smartpantrymanager.data.RecipeIngredient;

import java.util.List;

/** Shows the full ingredient list and method for one recipe. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Read the recipe id that was passed through the Intent
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        AppDatabase db = AppDatabase.getInstance(this);
        Recipe recipe = db.recipeDao().getRecipeById(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        getSupportActionBar().setTitle(recipe.name);

        List<RecipeIngredient> ingredients = db.recipeDao().getIngredientsForRecipe(recipeId);
        StringBuilder text = new StringBuilder();
        for (RecipeIngredient ing : ingredients) {
            text.append("\u2022 ")
                    .append(formatQuantity(ing.quantity)).append(" ")
                    .append(ing.unit).append(" ")
                    .append(ing.name).append("\n");
        }

        ((TextView) findViewById(R.id.textIngredients)).setText(text.toString().trim());
        ((TextView) findViewById(R.id.textSteps)).setText(recipe.steps);
    }

    private String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}