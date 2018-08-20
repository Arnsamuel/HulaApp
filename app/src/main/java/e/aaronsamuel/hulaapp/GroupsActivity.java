package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    DatabaseReference groupsDatabase;
    RecyclerView ViewGroups;
    GroupsRecyclerAdapter mAdapter;

    List<PushGroupDb> groupList;

    interface GroupCallback {
        void onGroupClicked(PushGroupDb group);
        void onGroupLongClicked(PushGroupDb group);
    }

    private GroupCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Groups");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsActivity.this, GroupsAddActivity.class);
                startActivity(intent);
            }
        });

        callback = new GroupCallback() {
            @Override
            public void onGroupClicked(PushGroupDb group) {
                Intent intent = new Intent(GroupsActivity.this, GroupDetailActivity.class);
                intent.putExtra("EXTRA_GROUP", group);
                startActivity(intent);
            }

            public void onGroupLongClicked(PushGroupDb group) {
                groupsDatabase.getDatabase().getReference("Groups").child(group.groupId).removeValue();
            }
        };

        mAdapter = new GroupsRecyclerAdapter(callback);

        groupsDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        ViewGroups = findViewById(R.id.recycler_view_layout_recycler);
        ViewGroups.setLayoutManager(new LinearLayoutManager(this));
        ViewGroups.setAdapter(mAdapter);

        groupList = new ArrayList<>();

        groupsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                groupList.clear();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    PushGroupDb groups = groupSnapshot.getValue(PushGroupDb.class);

                    groupList.add(groups);
                }

                mAdapter.setGroupList(groupList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
