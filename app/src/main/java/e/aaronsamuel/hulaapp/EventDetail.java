package e.aaronsamuel.hulaapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EventDetail extends AppCompatActivity {

    private PushEventDb events;
    private TextView creator;
    private TextView date;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        creator = findViewById(R.id.creator);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        if(getIntent() != null) {
            events = (PushEventDb) getIntent().getSerializableExtra("EXTRA_EVENT");
            getSupportActionBar().setTitle(events.eventTitle);
            creator.setText(events.eventCreator);
            date.setText(events.eventDate);
            time.setText(events.eventTime);
        }


    }
}
