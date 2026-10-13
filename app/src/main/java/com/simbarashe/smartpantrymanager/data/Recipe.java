package com.simbarashe.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** A recipe in the pre-loaded collection. Its ingredients are stored in RecipeIngredient. */
@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String steps;   // preparation steps, one per line

    public Recipe(String name, String steps) {
        this.name = name;
        this.steps = steps;
    }
}