package com.simbarashe.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simbarashe.smartpantrymanager.data.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Custom adapter that turns PantryItem objects into rows for the RecyclerView. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** The activity implements this so it can react to taps on a row. */
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    /** Replaces the displayed data and redraws the list. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.textName.setText(item.name);
        holder.textQuantity.setText(formatQuantity(item.quantity) + " " + item.unit);

        // Read the user's setting from SharedPreferences
        boolean alertsOn = holder.itemView.getContext()
                .getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(SettingsActivity.KEY_EXPIRY_ALERTS, true);

        if (item.expiryDate == null || item.expiryDate.isEmpty()) {
            holder.textExpiry.setText("No expiry date");
            holder.textExpiry.setTextColor(Color.GRAY);
        } else if (alertsOn && isExpiringSoon(item.expiryDate)) {
            holder.textExpiry.setText("Expiring soon! " + item.expiryDate);
            holder.textExpiry.setTextColor(Color.RED);
        } else {
            holder.textExpiry.setText("Expires: " + item.expiryDate);
            holder.textExpiry.setTextColor(Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** True if the date is today, in the past, or within the next 3 days. */
    private boolean isExpiringSoon(String dateText) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date expiry = format.parse(dateText);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long days = (expiry.getTime() - today.getTimeInMillis()) / (24L * 60 * 60 * 1000);
            return days <= 3;
        } catch (ParseException e) {
            return false;
        }
    }

    private String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    /** Holds references to the views of one row so they are not looked up repeatedly. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textQuantity, textExpiry;
        ImageButton buttonDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}