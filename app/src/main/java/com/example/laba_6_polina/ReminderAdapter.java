package com.example.laba_6_polina;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    private MainActivity mainActivity; // Добавлено для доступа к методу deleteReminder

    public ReminderAdapter(@NonNull Context context, @NonNull List<Reminder> reminders) {
        super(context, 0, reminders);
        this.mainActivity = (MainActivity) context; // Приведение контекста к MainActivity
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Reminder reminder = getItem(position);
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.reminder_item, parent, false);
        }

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        if (reminder != null) {
            titleTextView.setText(reminder.getTitle());
            messageTextView.setText(reminder.getMessage());
            titleTextView.setTextColor(Color.WHITE); // Установка белого цвета текста
            messageTextView.setTextColor(Color.WHITE); // Установка белого цвета текста

            deleteButton.setOnClickListener(v -> {
                mainActivity.deleteReminder(reminder.getId()); // Вызов метода удаления из MainActivity
                Toast.makeText(getContext(), "Напоминание удалено", Toast.LENGTH_SHORT).show();
            });
        } else {
            titleTextView.setText("");
            messageTextView.setText("");
            deleteButton.setVisibility(View.GONE); // Скрываем кнопку, если напоминания нет
        }

        return view;
    }
}
