package e.aaronsamuel.hulaapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class GroupDetailRecyclerAdapter extends RecyclerView.Adapter<GroupDetailRecyclerAdapter.MyViewHolder>{

    PushGroupDb group;
    List<PushUserDb> memberList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView memberName;

        public MyViewHolder (View view) {
            super(view);
            memberName = view.findViewById(R.id.title);
        }
    }

    public GroupDetailRecyclerAdapter(PushGroupDb group) {
        this.group = group;
    };

    public void setGroupDetailList(List<PushUserDb> memberList) {
        this.group.groupMember.clear();
        this.group.groupMember.addAll(memberList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_group_detail,parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PushUserDb member = group.groupMember.get(position);
        holder.memberName.setText(member.name);
    }

    @Override
    public int getItemCount() {
        return group.groupMember.size();
    }
}
