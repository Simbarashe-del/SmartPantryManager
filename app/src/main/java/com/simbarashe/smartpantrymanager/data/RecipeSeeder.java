package com.simbarashe.smartpantrymanager.data;

import java.util.ArrayList;
import java.util.List;

/** Fills the recipe tables with starter recipes the first time the app runs. */
public class RecipeSeeder {

    /** Does nothing if recipes already exist, so it only seeds once. */
    public static void seedIfEmpty(AppDatabase db) {
        RecipeDao dao = db.recipeDao();
        if (dao.countRecipes() > 0) {
            return;
        }

        // Each ingredient row is {name, quantity, unit}
        add(dao, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Melt the butter in a pan.\n3. Whisk the eggs and scramble gently.\n4. Serve on the toast.",
                new Object[][]{{"egg", 2, "pcs"}, {"bread", 2, "pcs"}, {"butter", 10, "g"}});

        add(dao, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt the butter in a pan and pour in the eggs.\n3. Add grated cheese, fold and cook until set.",
                new Object[][]{{"egg", 3, "pcs"}, {"cheese", 50, "g"}, {"butter", 10, "g"}});

        add(dao, "Tomato Pasta",
                "1. Boil the pasta.\n2. Fry chopped onion and garlic in olive oil.\n3. Add chopped tomatoes and simmer 10 minutes.\n4. Mix with the pasta.",
                new Object[][]{{"pasta", 200, "g"}, {"tomato", 4, "pcs"}, {"onion", 1, "pcs"},
                        {"garlic", 2, "pcs"}, {"olive oil", 30, "ml"}});

        add(dao, "Egg Fried Rice",
                "1. Fry the chopped onion.\n2. Add cooked rice and stir-fry.\n3. Push aside, scramble the eggs, then mix together.\n4. Season with soy sauce.",
                new Object[][]{{"rice", 200, "g"}, {"egg", 2, "pcs"}, {"onion", 1, "pcs"}, {"soy sauce", 30, "ml"}});

        add(dao, "Pancakes",
                "1. Mix the flour, sugar, milk and egg into a smooth batter.\n2. Pour small rounds into a hot pan.\n3. Flip when bubbles form.",
                new Object[][]{{"flour", 150, "g"}, {"milk", 200, "ml"}, {"egg", 1, "pcs"}, {"sugar", 20, "g"}});

        add(dao, "Cheese Toastie",
                "1. Butter the bread.\n2. Place the cheese between the slices.\n3. Toast in a pan until golden on both sides.",
                new Object[][]{{"bread", 2, "pcs"}, {"cheese", 60, "g"}, {"butter", 10, "g"}});

        add(dao, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash.\n3. Stir in the butter and warm milk.",
                new Object[][]{{"potato", 4, "pcs"}, {"butter", 30, "g"}, {"milk", 100, "ml"}});

        add(dao, "Chicken and Rice",
                "1. Brown the chicken pieces.\n2. Add chopped onion and garlic.\n3. Add the rice and enough water to cook it.\n4. Simmer until the rice is tender.",
                new Object[][]{{"chicken", 300, "g"}, {"rice", 200, "g"}, {"onion", 1, "pcs"}, {"garlic", 2, "pcs"}});

        add(dao, "Vegetable Stir-Fry",
                "1. Slice the vegetables.\n2. Stir-fry on high heat for 5 minutes.\n3. Add soy sauce and toss.",
                new Object[][]{{"carrot", 2, "pcs"}, {"onion", 1, "pcs"}, {"bell pepper", 1, "pcs"}, {"soy sauce", 30, "ml"}});

        add(dao, "Tuna Pasta",
                "1. Boil the pasta.\n2. Drain the tuna and warm it in olive oil.\n3. Toss with the pasta.",
                new Object[][]{{"pasta", 200, "g"}, {"tuna", 1, "pcs"}, {"olive oil", 20, "ml"}});

        add(dao, "Tomato Soup",
                "1. Fry the chopped onion and garlic in butter.\n2. Add chopped tomatoes and simmer 20 minutes.\n3. Blend until smooth.",
                new Object[][]{{"tomato", 6, "pcs"}, {"onion", 1, "pcs"}, {"garlic", 2, "pcs"}, {"butter", 20, "g"}});

        add(dao, "French Toast",
                "1. Whisk the eggs and milk.\n2. Dip the bread slices.\n3. Fry in butter until golden.",
                new Object[][]{{"bread", 4, "pcs"}, {"egg", 2, "pcs"}, {"milk", 100, "ml"}, {"butter", 10, "g"}});

        add(dao, "Garlic Bread",
                "1. Mash the garlic into the butter.\n2. Spread on the bread.\n3. Bake at 200C for 10 minutes.",
                new Object[][]{{"bread", 4, "pcs"}, {"garlic", 3, "pcs"}, {"butter", 40, "g"}});

        add(dao, "Egg Fried Noodles",
                "1. Boil the noodles and drain.\n2. Stir-fry the grated carrot.\n3. Add the eggs and scramble.\n4. Add the noodles and soy sauce and toss.",
                new Object[][]{{"noodles", 200, "g"}, {"egg", 2, "pcs"}, {"carrot", 1, "pcs"}, {"soy sauce", 20, "ml"}});

        add(dao, "Cheesy Baked Potatoes",
                "1. Bake the potatoes at 200C for 50 minutes.\n2. Cut open and add butter.\n3. Top with grated cheese and return to the oven for 5 minutes.",
                new Object[][]{{"potato", 4, "pcs"}, {"cheese", 80, "g"}, {"butter", 20, "g"}});

        add(dao, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix in the eggs and flour.\n3. Fry small rounds until golden on both sides.",
                new Object[][]{{"banana", 2, "pcs"}, {"egg", 2, "pcs"}, {"flour", 50, "g"}});

        add(dao, "Beans on Toast",
                "1. Toast the bread and butter it.\n2. Heat the baked beans in a pot.\n3. Pour over the toast.",
                new Object[][]{{"baked beans", 1, "pcs"}, {"bread", 2, "pcs"}, {"butter", 10, "g"}});

        add(dao, "Macaroni Cheese",
                "1. Boil the macaroni.\n2. Melt the butter, stir in the milk and grated cheese to make a sauce.\n3. Mix with the macaroni.",
                new Object[][]{{"macaroni", 200, "g"}, {"cheese", 100, "g"}, {"milk", 250, "ml"}, {"butter", 30, "g"}});
    }

    /** Inserts one recipe and then its ingredient rows. */
    private static void add(RecipeDao dao, String name, String steps, Object[][] ingredients) {
        long recipeId = dao.insertRecipe(new Recipe(name, steps));
        List<RecipeIngredient> list = new ArrayList<>();
        for (Object[] row : ingredients) {
            list.add(new RecipeIngredient(
                    (int) recipeId,
                    (String) row[0],
                    ((Number) row[1]).doubleValue(),
                    (String) row[2]));
        }
        dao.insertIngredients(list);
    }
}