package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Events extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView ViewEvents;
    DatabaseReference databaseEvents;
    EventRecyclerAdapter mAdapter;

    List<PushEventDb> eventsList;

    interface EventsCallback {
        void openEventsDetail(PushEventDb event);
    }

    private EventsCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("My Events");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventsList = new ArrayList<>();

        // Set database and RecyclerView
        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
        ViewEvents = findViewById(R.id.recycler_view_layout_recycler);

        callback = new EventsCallback() {
            @Override
            public void openEventsDetail(PushEventDb event) {
                Intent intent = new Intent(Events.this, EventDetail.class);
                intent.putExtra("EXTRA_EVENT", event);
                startActivity(intent);
            }
        };

        mAdapter = new EventRecyclerAdapter(callback);
        ViewEvents.setLayoutManager(new LinearLayoutManager(this));
        ViewEvents.setAdapter(mAdapter);

        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
                String userName = preferences.getString("username",null);


                eventsList.clear();

                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    PushEventDb event = eventSnapshot.getValue(PushEventDb.class);

                    String dbUserName = event.eventCreator;

                    if (userName.equals(dbUserName))
                        eventsList.add(event);
                }
                mAdapter.setEventsList(eventsList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home) {
            onBackPressed();
            return true;
        }

        switch(item.getItemId()) {
            case R.id.menu_add:
                Intent intent = new Intent(this, EventsAdd.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

}