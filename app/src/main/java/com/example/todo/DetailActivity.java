package com.example.todo;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.todo.model.Todo;
import com.example.todo.util.ApiHandler;
import com.example.todo.util.DatabaseHandler;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private ApiHandler apiHandler;
    private Date date;
    private long timeInMilli;
    private int min;
    private int hour;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Todo todo = (Todo) getIntent().getSerializableExtra(ARG_ITEM);
        EditText itemName = findViewById(R.id.name);
        EditText itemDescription = findViewById(R.id.description);
        CheckBox itemDone = findViewById(R.id.checkbox_done);
        CheckBox itemFavourite = findViewById(R.id.checkbox_favorite);
        CardView dueDateWrapper = findViewById(R.id.due_date_wrapper);
        TextView selectedDueDate = findViewById(R.id.selected_due_date);
        CardView dueTimeWrapper = findViewById(R.id.due_time_wrapper);
        TextView selectedDueTime = findViewById(R.id.selected_due_time);
        FloatingActionButton fabSave = findViewById(R.id.fab_save);

        apiHandler = new ApiHandler();

        DatabaseHandler db = new DatabaseHandler(this);
        db.openDatabase();

        if (todo != null) {
            itemName.setText(todo.getName());
            itemDescription.setText(todo.getDescription());
            itemDone.setChecked(todo.isDone());
            itemFavourite.setChecked(todo.isFavourite());

            if (todo.getExpiry() != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfDate = new SimpleDateFormat("dd.MM.yyyy");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfTime = new SimpleDateFormat("HH:mm");
                Date parsedDate = new Date(Long.parseLong(todo.getExpiry()));
                selectedDueDate.setText(sfDate.format(parsedDate));
                selectedDueTime.setText(sfTime.format(parsedDate));

                date = parsedDate;
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(parsedDate);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);
            } else {
                selectedDueDate.setVisibility(View.GONE);
                selectedDueTime.setVisibility(View.GONE);
                Date currentDate = new Date();
                date = currentDate;
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(currentDate);
                hour = calendar.get(Calendar.HOUR_OF_DAY) + 2;
                min = calendar.get(Calendar.MINUTE);
            }
        }

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker().setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        materialDateBuilder.setTitleText("Datum auswählen");
        materialDateBuilder.setSelection(date.getTime());
        MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();
        materialTimeBuilder.setTitleText("Zeit auswählen");
        materialTimeBuilder.setTimeFormat(TimeFormat.CLOCK_24H).setHour(hour).setMinute(min);
        MaterialTimePicker materialTimePicker = materialTimeBuilder.build();

        dueDateWrapper.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            timeInMilli = selection;
            String date = DateFormat.format("dd.MM.yyyy", timeInMilli).toString();
            selectedDueDate.setText(date);
            selectedDueDate.setVisibility(View.VISIBLE);
        });

        dueTimeWrapper.setOnClickListener(v -> materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER"));

        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            hour = materialTimePicker.getHour();
            min = materialTimePicker.getMinute();
            Calendar timeDate = Calendar.getInstance();
            timeDate.setTimeInMillis(timeInMilli);
            timeDate.set(Calendar.HOUR_OF_DAY, hour);
            timeDate.set(Calendar.MINUTE, min);
            timeInMilli = timeDate.getTimeInMillis();

            String date = DateFormat.format("HH:mm", timeDate).toString();
            selectedDueTime.setText(date);
            selectedDueTime.setVisibility(View.VISIBLE);
        });

        fabSave.setOnClickListener(v -> {
            assert todo != null;
            todo.setName(itemName.getText().toString());
            todo.setDescription(itemDescription.getText().toString());
            todo.setDone(itemDone.isChecked());
            todo.setFavourite(itemFavourite.isChecked());
            todo.setExpiry(String.valueOf(timeInMilli));
            db.updateTodo(todo.getId(), todo);
            apiHandler.updateTodo(todo.getId(), todo);

            Intent mainIntent = new Intent(DetailActivity.this, MainActivity.class);
            DetailActivity.this.startActivity(mainIntent);
        });
    }
}
