package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView ViewEvents;
    DatabaseReference databaseEvents;
    DatabaseReference databaseEventsChild;
    MainRecyclerAdapter mAdapter;

    List<PushEventDb> eventsList;

    private Calendar currSelectedCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set database and RecyclerView
        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
        databaseEventsChild = databaseEvents.child("eventId");
        ViewEvents = findViewById(R.id.recycler_view_layout_recycler);
        ViewEvents.setLayoutManager(new LinearLayoutManager(this));

        eventsList = new ArrayList<>();

        mAdapter = new MainRecyclerAdapter();
        ViewEvents.setAdapter(mAdapter);

        // Set FAB uses
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EventsAdd.class));
            }
        });

        //set Drawer on activity
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        String userName = preferences.getString("username",null);

        if(userName != null) {

            TextView textView = navigationView.getHeaderView(0).findViewById(R.id.userName);
            textView.setText(userName);
        }

        // SET TITLE BASED ON SELECTED CALENDAR
        final CalendarView calendarView = findViewById(R.id.simpleCalendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                TextView textview = findViewById(R.id.titleView);
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);

                currSelectedCal = cal;

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd");
                textview.setText(sdf.format(cal.getTime()));

                getSelectedDateEvents();
            }
        });

        // POPULATE RECYCLERVIEW
        databaseEvents.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                eventsList.clear();

                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    PushEventDb event = eventSnapshot.getValue(PushEventDb.class);
                    eventsList.add(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getSelectedDateEvents() {

        ArrayList<PushEventDb> selectedEvents = new ArrayList();

        for(PushEventDb event : eventsList) {

            String rawDate = event.eventDate;

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(rawDate));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

            if(cal.get(Calendar.YEAR) == currSelectedCal.get(Calendar.YEAR)
                    && cal.get(Calendar.MONTH) == currSelectedCal.get(Calendar.MONTH)
                    && cal.get(Calendar.DAY_OF_MONTH) == currSelectedCal.get(Calendar.DAY_OF_MONTH))
                selectedEvents.add(event);

            //convert rawDate ke Calendar
            //compare calendar ama yg selectedEvents
            //klo sama, add ke selected events
        }

        mAdapter.setEventsList(selectedEvents);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.menu_login:
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            Intent intent = new Intent(this, Groups.class);
            startActivity(intent);
        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(this, Events.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
