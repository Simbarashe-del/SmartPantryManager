package com.simbarashe.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.simbarashe.smartpantrymanager.data.AppDatabase;
import com.simbarashe.smartpantrymanager.data.Recipe;
import com.simbarashe.smartpantrymanager.data.RecipeMatcher;

import java.util.List;

/** Shows only the recipes the user can make right now (strict matching). */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private AppDatabase db;
    private RecipeAdapter adapter;
    private TextView textNoMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppDatabase.getInstance(this);
        textNoMatch = findViewById(R.id.textNoMatch);

        RecyclerView recycler = findViewById(R.id.recyclerRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);
    }

    /** Re-run the matching every time the screen appears, so it reflects the current pantry. */
    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> suggested = RecipeMatcher.findSuggested(db);
        adapter.setRecipes(suggested);
        textNoMatch.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}