package com.simbarashe.smartpantrymanager.data;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

/**
 * Core business logic: decides which recipes the user can make RIGHT NOW.
 * Strict rule: a recipe qualifies only if EVERY ingredient is in the pantry
 * in at least the required quantity.
 */
public class RecipeMatcher {

    /** Returns only the recipes whose ingredients are all covered by the pantry. */
    public static List<Recipe> findSuggested(AppDatabase db) {
        Map<String, Double> pantryTotals = buildPantryTotals(db.pantryDao().getAll());
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : db.recipeDao().getAllRecipes()) {
            List<RecipeIngredient> needed = db.recipeDao().getIngredientsForRecipe(recipe.id);
            if (canMake(needed, pantryTotals)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    /** True only if every required ingredient is available in enough quantity. */
    public static boolean canMake(List<RecipeIngredient> needed, Map<String, Double> pantryTotals) {
        if (needed.isEmpty()) {
            return false;   // a recipe with no ingredients is never suggested
        }
        for (RecipeIngredient ing : needed) {
            String key = makeKey(ing.name, ing.unit);
            double have = pantryTotals.containsKey(key) ? pantryTotals.get(key) : 0;
            double need = toBase(ing.quantity, ing.unit);
            if (have + 0.000001 < need) {
                return false;   // one missing or short ingredient rules the recipe out
            }
        }
        return true;
    }

    /**
     * Adds up the pantry per ingredient, so two "egg" rows count together.
     * Quantities are converted to a base unit (g, ml or pcs) before adding.
     */
    public static Map<String, Double> buildPantryTotals(List<PantryItem> items) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : items) {
            String key = makeKey(item.name, item.unit);
            double amount = toBase(item.quantity, item.unit);
            Double existing = totals.get(key);
            totals.put(key, (existing == null ? 0 : existing) + amount);
        }
        return totals;
    }

    /** Key = cleaned ingredient name + unit family, e.g. "tomato|count". */
    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + unitFamily(unit);
    }

    /** Lower-cases, trims and singularises so "Tomatoes" matches "tomato". */
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        int lastSpace = cleaned.lastIndexOf(' ');
        if (lastSpace == -1) {
            return singularize(cleaned);
        }
        // Only the last word needs singularising ("bell peppers" -> "bell pepper")
        return cleaned.substring(0, lastSpace + 1) + singularize(cleaned.substring(lastSpace + 1));
    }

    private static String singularize(String w) {
        if (w.length() > 3 && w.endsWith("ies")) {
            return w.substring(0, w.length() - 3) + "y";          // berries -> berry
        }
        if (w.endsWith("oes") || w.endsWith("ches") || w.endsWith("shes")
                || w.endsWith("sses") || w.endsWith("xes")) {
            return w.substring(0, w.length() - 2);                // tomatoes -> tomato
        }
        if (w.endsWith("ss")) {
            return w;                                             // keep "glass"
        }
        if (w.length() > 3 && w.endsWith("s")) {
            return w.substring(0, w.length() - 1);                // eggs -> egg
        }
        return w;
    }

    /** Groups units that can be compared with each other. */
    private static String unitFamily(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "g": case "kg":
                return "mass";
            case "ml": case "l": case "tbsp": case "tsp": case "cup":
                return "volume";
            case "pcs":
                return "count";
            default:
                return u;
        }
    }

    /** Converts a quantity to its base unit: grams, millilitres or pieces. */
    private static double toBase(double quantity, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "kg":   return quantity * 1000;
            case "l":    return quantity * 1000;
            case "tbsp": return quantity * 15;
            case "tsp":  return quantity * 5;
            case "cup":  return quantity * 240;
            default:     return quantity;   // g, ml, pcs
        }
    }
}