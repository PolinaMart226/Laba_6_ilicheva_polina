package com.example.laba_6_polina;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private ReminderAdapter adapter;
    private EditText titleEditText, messageEditText;
    private Calendar selectedCalendar = Calendar.getInstance(); // Хранение выбранной даты и времени
    private Button dateButton, timeButton, addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.reminderList);
        titleEditText = findViewById(R.id.titleEditText);
        messageEditText = findViewById(R.id.messageEditText);
        addButton = findViewById(R.id.addButton);
        dateButton = findViewById(R.id.dateButton); // Добавлена кнопка для выбора даты
        timeButton = findViewById(R.id.timeButton); // Добавлена кнопка для выбора времени

        loadReminders();

        addButton.setOnClickListener(v -> addReminder());

        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Reminder reminder = (Reminder) adapter.getItem(position);
            if (reminder != null) {
                deleteReminder(reminder.getId());
            }
        });
    }

    private void loadReminders() {
        List<Reminder> reminders = dbHelper.getAllReminders();
        adapter = new ReminderAdapter(this, reminders);
        listView.setAdapter(adapter);
    }

    void deleteReminder(int id) {
        boolean isDeleted = dbHelper.deleteReminder(id);
        if (isDeleted) {
            loadReminders();
            Toast.makeText(this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
        }
    }

    private void addReminder() {
        String title = titleEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d",
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH) + 1,
                selectedCalendar.get(Calendar.DAY_OF_MONTH),
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE));

        if (dbHelper.addReminder(title, message, date)) {
            scheduleNotification(title, message, date);
            loadReminders();
            Toast.makeText(this, "Напоминание добавлено", Toast.LENGTH_SHORT).show();
            titleEditText.setText("");
            messageEditText.setText("");
        } else {
            Toast.makeText(this, "Ошибка при добавлении напоминания", Toast.LENGTH_SHORT).show();
        }
    }


    private void scheduleNotification(String title, String message, String date) {
        String[] parts = date.split(" ");
        String[] dateParts = parts[0].split("-");
        String[] timeParts = parts[1].split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName())));
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year1);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateButton.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute1);
                    timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                }, hour, minute, true);
        timePickerDialog.show();
    }
}
