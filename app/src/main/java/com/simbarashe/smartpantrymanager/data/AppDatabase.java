package com.simbarashe.smartpantrymanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/** The single Room database for the app (a SQLite file stored on the device). */
@Database(
        entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryDao pantryDao();
    public abstract RecipeDao recipeDao();

    private static AppDatabase instance;

    /** Singleton: the whole app shares one database connection. */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_pantry.db")
                    .allowMainThreadQueries()   // acceptable for a small student app
                    .build();
            RecipeSeeder.seedIfEmpty(instance);   // load recipes on first run only
        }
        return instance;
    }
}