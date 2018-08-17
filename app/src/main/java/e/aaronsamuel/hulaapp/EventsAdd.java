package e.aaronsamuel.hulaapp;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventsAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    private Toolbar toolbar;
    EditText title;
    Button btn;
    TextView date;
    TextView time;

    DatabaseReference databaseEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_add);

        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.title_input);

        // Convert textView into selected date

        date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // convert textView into selected time

        time = findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventsAdd.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minutes);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH" + ":" + "mm");
                        time.setText(sdf.format(cal.getTime()));
                    }
                }, 00, 00, true);

                timePickerDialog.show();
            }
        });

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data();
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

    private void data(){
        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        String userName = preferences.getString("username",null);

        String titleInput = title.getText().toString();
        String dateInput = date.getText().toString();
        String timeInput = time.getText().toString();
        String creator = userName.toString();

        if(!TextUtils.isEmpty(titleInput) && !TextUtils.isEmpty(dateInput) && !TextUtils.isEmpty(timeInput)) {
            String id = databaseEvents.push().getKey();

            PushEventDb pushEvent = new PushEventDb(id, titleInput, dateInput, timeInput, creator);

            databaseEvents.child(id).setValue(pushEvent);
            Toast.makeText(this, "Event Added",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please Complete all field",Toast.LENGTH_LONG).show();
        }
    }
}
