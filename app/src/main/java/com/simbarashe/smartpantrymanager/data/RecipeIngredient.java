package com.simbarashe.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** One ingredient line required by a recipe (belongs to exactly one Recipe). */
@Entity(
        tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("recipeId")}
)
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int recipeId;      // links to Recipe.id
    public String name;       // e.g. "tomato"
    public double quantity;   // amount required
    public String unit;       // "g", "ml", "pcs", ...

    public RecipeIngredient(int recipeId, String name, double quantity, String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}