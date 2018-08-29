package e.aaronsamuel.hulaapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    private List<PushEventDb> eventsList;
    private EventsActivity.EventsCallback callback;

    String userId;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, time, edit;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            edit = view.findViewById(R.id.edit);
        }
    }


    public EventRecyclerAdapter(EventsActivity.EventsCallback callback, Context context) {
        this.eventsList = new ArrayList<>();
        this.callback = callback;

        SharedPreferences preferences = context.getSharedPreferences("USERNAME", MODE_PRIVATE);
        userId = preferences.getString("userid",null);
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

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null)
                    callback.openEditEvent(events);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null)
                    callback.openEventsDetail(events);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to delete this Event?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null)
                            callback.deleteEvent(events);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}