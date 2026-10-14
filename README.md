# Smart Pantry Manager

A Java Android app (Mobile App Development 700, Richfield) that helps reduce food waste. The user tracks the ingredients they have at home, and the app suggests recipes they can cook using **strictly** what is already in the pantry. A recipe is only suggested if every ingredient is present in at least the required quantity.

**Student:** Simbarashe Chirwa | **Student number:** 402412056

## Features

- Add, edit and delete pantry items (name, quantity, unit, optional expiry date) with input validation
- Pantry list screen (RecyclerView with a custom adapter)
- 18 preloaded recipes, seeded on first run
- Suggested Recipes screen using strict matching: a recipe with even one missing or short ingredient is excluded
- Matching handles simple real-world differences: singular/plural names (tomato / tomatoes) and unit conversion (g / kg, ml / l / tbsp / tsp / cup)
- Recipe Detail screen with the full ingredient list and method
- Friendly message when no recipes match the pantry
- Settings screen with an expiring-soon alert toggle (saved with SharedPreferences)

## Database choice: SQLite via Room

I chose **SQLite using the Room persistence library**. The data is personal to one device (a pantry and a fixed recipe list), so no server, account or internet connection is needed. Room gives compile-time checking of queries, less boilerplate than raw SQLiteOpenHelper, and data persists after the app is closed and reopened.

Tables: `pantry_items`, `recipes`, `recipe_ingredients` (linked to `recipes` by a foreign key).

## Project structure

- `MainActivity` - pantry list
- `AddEditItemActivity` - add and edit form
- `SuggestedRecipesActivity` - strict-matching results
- `RecipeDetailActivity` - ingredients and method
- `SettingsActivity` - user settings
- `data/` - Room entities, DAOs, `AppDatabase`, `RecipeSeeder` and `RecipeMatcher` (the strict-matching logic)

## Setup and run instructions

1. Install Android Studio and clone this repository.
2. Open the project folder in Android Studio and wait for Gradle to finish syncing.
3. Create an emulator in Device Manager, or connect an Android phone with USB debugging turned on.
4. Select the `app` configuration and press Run.
5. Add a few ingredients, then open the toolbar menu and choose Suggested Recipes.

Requirements: Android Studio, JDK 17 or newer, Android 7.0 (API 24) or higher. The app uses no maps, location or GPS features.