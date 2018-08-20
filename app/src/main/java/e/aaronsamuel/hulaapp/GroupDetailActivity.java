package e.aaronsamuel.hulaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {

    private PushGroupDb groups;
    private TextView creator;
    private RecyclerView viewMembers;
    private GroupDetailRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        creator = findViewById(R.id.creator);

        viewMembers = findViewById(R.id.recycler_view_layout_recycler);
        viewMembers.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent() != null) {
            groups = getIntent().getParcelableExtra("EXTRA_GROUP");
            getSupportActionBar().setTitle(groups.groupTitle);
            creator.setText(groups.groupCreator);

            mAdapter = new GroupDetailRecyclerAdapter(groups);
            viewMembers.setAdapter(mAdapter);
        }


    }
}
