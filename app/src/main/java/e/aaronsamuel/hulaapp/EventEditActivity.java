package e.aaronsamuel.hulaapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private PushEventDb events;
    private Toolbar toolbar;
    EditText title;
    Spinner group;
    Button btn;
    TextView date;
    TextView time;
    TextView map;

    String location;
    String mapcoor;

    DatabaseReference databaseEvents;
    DatabaseReference databaseGroups;

    private ArrayList<PushGroupDb> groupList = new ArrayList<>();
    private PushGroupDb selectedGroup;

    final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        title = findViewById(R.id.title_input);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        if(getIntent() != null) {
            events = getIntent().getParcelableExtra("EXTRA_EVENT");
            title.setText(events.eventTitle);
            date.setText(events.eventDate);
            time.setText(events.eventTime);
        }

        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Edit Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.title_input);

        group = findViewById(R.id.group_input_spinner);

        databaseGroups = FirebaseDatabase.getInstance().getReference("Groups");

        getGroup();

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        map = findViewById(R.id.map);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(builder.build(EventEditActivity.this), PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                }
            }
        });

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data();
                startActivity(new Intent(EventEditActivity.this, MainActivity.class));
            }
        });

    }

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
        String userId = preferences.getString("userid",null);

        String titleInput = title.getText().toString();
        String dateInput = date.getText().toString();
        String timeInput = time.getText().toString();
        String creatorInput = userName.toString();
        String creatorIdInput = userId.toString();
        String locationInput = location;
        String coorInput = mapcoor;
        PushGroupDb groupInput = selectedGroup;

        if(!TextUtils.isEmpty(titleInput) && !TextUtils.isEmpty(dateInput) && !TextUtils.isEmpty(timeInput)) {
            String id = databaseEvents.push().getKey();

            PushEventDb pushEvent = new PushEventDb(id, titleInput, dateInput, timeInput, creatorInput, creatorIdInput, locationInput, coorInput, groupInput);

            databaseEvents.child(id).setValue(pushEvent);
            Toast.makeText(this, "Event Added",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please Complete all field",Toast.LENGTH_LONG).show();
        }
    }

    private void getGroup() {
        databaseGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> groupTitleList = new ArrayList<>();

                groupList.clear();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    PushGroupDb groups = groupSnapshot.getValue(PushGroupDb.class);
                    groupList.add(groups);
                    groupTitleList.add(groups.groupTitle);
                }

                ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(EventEditActivity.this, android.R.layout.simple_spinner_item, groupTitleList);
                groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                group.setAdapter(groupAdapter);

                group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedGroup = groupList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, EventEditActivity.this);
                String name = String.format("%s", place.getName());
                String lat = String.format("%s",place.getLatLng().latitude);
                String lng = String.format("%s",place.getLatLng().longitude);
                map.setText(name);
                mapcoor = lat+","+lng;
                location = name;

            }
        }
    }
}
