package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    private List<PushEventDb> eventsList;
    private Events.EventsCallback callback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, time;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
        }
    }


    public EventRecyclerAdapter(Events.EventsCallback callback) {
        this.eventsList = new ArrayList<>();
        this.callback = callback;
    }

    public void setEventsList(List<PushEventDb> eventsList) {
        this.eventsList.clear();
        this.eventsList.addAll(eventsList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_events, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final PushEventDb events = eventsList.get(position);
        holder.title.setText(events.getEventTitle());
        holder.date.setText(events.getEventDate());
        holder.time.setText(events.getEventTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null)
                    callback.openEventsDetail(events);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}