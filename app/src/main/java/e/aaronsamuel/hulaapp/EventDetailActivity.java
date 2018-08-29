package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    private PushEventDb events;
    private TextView creator;
    private TextView date;
    private TextView time;
    private TextView location;
    private Button btnyes;
    private Button btnno;
    private Button btnmaybe;
    String userId;

    RecyclerView viewAttend;
    AttendRecyclerAdapter mAdapter;

    DatabaseReference databaseEvents;

    private ArrayList<PushUserDb> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        userId = preferences.getString("userid",null);

        creator = findViewById(R.id.creator);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);
        btnyes = findViewById(R.id.btnyes);
        btnno = findViewById(R.id.btnno);

        mAdapter = new AttendRecyclerAdapter();
        viewAttend = findViewById(R.id.recycler_view_layout_recycler_attend);
        viewAttend.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        viewAttend.setAdapter(mAdapter);

        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");

        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<PushUserDb>> t = new GenericTypeIndicator<ArrayList<PushUserDb>>() {};

                userList.clear();
                userList = dataSnapshot.child(events.eventId).child("eventGroup").child("groupMember").getValue(t);

                getAttendList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if(getIntent() != null) {
            events = getIntent().getParcelableExtra("EXTRA_EVENT");
            getSupportActionBar().setTitle(events.eventTitle);
            creator.setText(events.eventCreator);
            date.setText(events.eventDate);
            time.setText(events.eventTime);
            location.setText(events.eventLocation);
        }

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+events.eventCoor+"?q="+events.eventCoor);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        getAttendList();

        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked(true);
            }
        });

        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked(false);
            }
        });
    }

    public void onButtonClicked(final boolean attending) {
        if(userList != null && userList.size() > 0) {
            int position = userList.indexOf(new PushUserDb(userId));

            if(position != -1) {

                PushUserDb newUser = userList.get(position);
                newUser.attending = attending;

                String routeToUpdate = events.eventId + "/eventGroup/groupMember/" + position;

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(routeToUpdate, newUser);

                databaseEvents.updateChildren(childUpdates);
            }
        }
    }

    public void getAttendList() {
        ArrayList<String> attendingListPic = new ArrayList<>();

        if(userList != null && userList.size() > 0) {
            for(PushUserDb user : userList) {
                if(user.attending)
                    attendingListPic.add(user.profilepic);
            }
        }

        mAdapter.setAttendList(attendingListPic);
        mAdapter.notifyDataSetChanged();
    };
}
