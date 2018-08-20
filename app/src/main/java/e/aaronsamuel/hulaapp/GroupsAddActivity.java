package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsAddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView ViewUsers;
    DatabaseReference databaseContacts;
    DatabaseReference databaseGroups;
    ContactRecyclerAdapter mAdapter;
    EditText title;

    List<PushUserDb> contactList;

    public String a = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_add);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.title);

        getSupportActionBar().setTitle("Select Member");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data();
            }
        });

        databaseContacts = FirebaseDatabase.getInstance().getReference("Users");
        databaseGroups = FirebaseDatabase.getInstance().getReference("Groups");

        ViewUsers = findViewById(R.id.recycler_view_layout_recycler);
        ViewUsers.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();

        mAdapter = new ContactRecyclerAdapter();
        ViewUsers.setAdapter(mAdapter);

        databaseContacts.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contactList.clear();

                for(DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    PushUserDb contact = contactSnapshot.getValue(PushUserDb.class);
                    contactList.add(contact);
                }

                mAdapter.setContactList(contactList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void data () {
        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        String userName = preferences.getString("username",null);
        String userId = preferences.getString("userid",null);

        List<PushUserDb> memberInput;
        memberInput = new ArrayList<>();

        for(int i = 0; i < mAdapter.getItemCount(); i++) {
            boolean isChecked = mAdapter.getCheckBoxStatus(i);
            if(isChecked) {
                memberInput.add(mAdapter.getData(i));
            }
        }

        String titleInput = title.getText().toString();
        String creatorInput = userName.toString();
        String creatorIdInput = userId.toString();

        if(!TextUtils.isEmpty(titleInput)) {

            String id = databaseGroups.push().getKey();

            PushGroupDb pushGroup = new PushGroupDb(id, titleInput, creatorInput, creatorIdInput, memberInput);

            databaseGroups.child(id).setValue(pushGroup);

            Toast.makeText(this, "Event Added",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please Complete all field",Toast.LENGTH_LONG).show();
        }
    }
}
