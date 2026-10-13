package com.simbarashe.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.simbarashe.smartpantrymanager.data.AppDatabase;
import com.simbarashe.smartpantrymanager.data.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/** Screen for adding a new pantry item or editing an existing one. */
public class AddEditItemActivity extends AppCompatActivity {

    /** Intent extra holding the id of the item being edited (absent when adding). */
    public static final String EXTRA_ITEM_ID = "item_id";

    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs", "tbsp", "tsp", "cup"};

    private EditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;
    private AppDatabase db;
    private PantryItem editingItem = null;   // null means we are adding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppDatabase.getInstance(this);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button buttonSave = findViewById(R.id.buttonSave);

        spinnerUnit.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        // If an item id was passed in the Intent, we are in edit mode.
        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            editingItem = db.pantryDao().getById(itemId);
        }
        if (editingItem != null) {
            getSupportActionBar().setTitle("Edit Ingredient");
            editName.setText(editingItem.name);
            editQuantity.setText(formatQuantity(editingItem.quantity));
            editExpiry.setText(editingItem.expiryDate == null ? "" : editingItem.expiryDate);
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(editingItem.unit)) {
                    spinnerUnit.setSelection(i);
                }
            }
        } else {
            getSupportActionBar().setTitle("Add Ingredient");
        }

        buttonSave.setOnClickListener(v -> saveItem());
    }

    /** Validates the form; only saves when every field is acceptable. */
    private void saveItem() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Please enter an ingredient name");
            editName.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Please enter a valid number");
            editQuantity.requestFocus();
            return;
        }
        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            editQuantity.requestFocus();
            return;
        }

        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            editExpiry.setError("Use the format yyyy-MM-dd (e.g. 2026-11-30)");
            editExpiry.requestFocus();
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiryValue = expiry.isEmpty() ? null : expiry;

        if (editingItem == null) {
            db.pantryDao().insert(new PantryItem(name, quantity, unit, expiryValue));   // Create
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            editingItem.name = name;
            editingItem.quantity = quantity;
            editingItem.unit = unit;
            editingItem.expiryDate = expiryValue;
            db.pantryDao().update(editingItem);                                          // Update
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();   // go back to the pantry list
    }

    private boolean isValidDate(String text) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);
        try {
            format.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** Shows 500 instead of 500.0 for whole numbers. */
    private String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}