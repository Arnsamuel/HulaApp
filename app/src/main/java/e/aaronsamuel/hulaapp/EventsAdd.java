package e.aaronsamuel.hulaapp;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class EventsAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_add);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        TextView textView = findViewById(R.id.date);
        textView.setText(currentDateString);
    }
}
