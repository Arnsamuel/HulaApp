package e.aaronsamuel.hulaapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {

    private List<PushEventDb> eventsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, creator, time;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            creator = view.findViewById(R.id.creator);
            time = view.findViewById(R.id.time);
        }
    }

    public MainRecyclerAdapter() {
        this.eventsList = new ArrayList<>();
    }

    public void setEventsList(List<PushEventDb> eventsList) {
        this.eventsList.clear();
        this.eventsList.addAll(eventsList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_main, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PushEventDb events = eventsList.get(position);
        holder.title.setText(events.getEventTitle());
        holder.creator.setText(events.getEventCreator());
        holder.time.setText(events.getEventTime());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
