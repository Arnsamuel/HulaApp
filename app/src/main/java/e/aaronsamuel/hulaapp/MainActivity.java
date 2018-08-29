package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView ViewEvents;
    DatabaseReference databaseEvents;
    MainRecyclerAdapter mAdapter;

    List<PushEventDb> eventsList;
    List<PushEventDb> filteredEventList;
    String picCode;

    private Calendar currSelectedCal;

    DatabaseReference databaseUser;

    interface EventsCallback {
        void openMainDetail(PushEventDb event);
    }

    private MainActivity.EventsCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set Drawer on activity
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set FAB uses
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EventsAddActivity.class));
            }
        });

        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        String userName = preferences.getString("username",null);
        final String userId = preferences.getString("userid",null);

        callback = new MainActivity.EventsCallback() {
            @Override
            public void openMainDetail(PushEventDb event) {
                Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
                intent.putExtra("EXTRA_EVENT", event);
                startActivity(intent);
            }
        };

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");

        // Set database and RecyclerView
        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
        ViewEvents = findViewById(R.id.recycler_view_layout_recycler);
        ViewEvents.setLayoutManager(new LinearLayoutManager(this));

        eventsList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

        mAdapter = new MainRecyclerAdapter(callback, this);
        ViewEvents.setAdapter(mAdapter);

        if(userName != null) {
            TextView textView = navigationView.getHeaderView(0).findViewById(R.id.userName);
            textView.setText(userName);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                }
            });
        }


        //GET PROFILE PIC BASE64 FROM DB
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                picCode = dataSnapshot.child(userId).child("profilepic").getValue(String.class);

                //SET PROFILE PICTURE INSIDE DRAWER
                if(!TextUtils.isEmpty(picCode)) {
                    ImageView imageView = findViewById(R.id.imageView);
                    try {
                        byte[] data = Base64.decode(picCode, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);

                        imageView.setImageBitmap(decodedByte);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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

                filterEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void filterEvents() {

        for(PushEventDb event : eventsList) {

            if(event.eventGroup != null && event.eventGroup.groupMember != null) {

                SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
                String userId = preferences.getString("userid",null);

                if(userId.equals(event.eventCreatorId))
                    filteredEventList.add(event);
                else {
                    PushUserDb currentUser = new PushUserDb(userId);

                    if(event.eventGroup.groupMember.contains(currentUser))
                        filteredEventList.add(event);
                }
            }
        }
    }

    private void getSelectedDateEvents() {

        ArrayList<PushEventDb> selectedEvents = new ArrayList();

        for(PushEventDb event : filteredEventList) {

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            Intent intent = new Intent(this, GroupsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_signout) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.exit(1);
                }
            });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
