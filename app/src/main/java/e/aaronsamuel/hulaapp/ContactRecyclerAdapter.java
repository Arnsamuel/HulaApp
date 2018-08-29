package e.aaronsamuel.hulaapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.MyViewHolder> {

    private List<PushUserDb> contactList;
    private ArrayList<Boolean> checkBoxStatus;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView contact;
        public CheckBox cbselect;


        public MyViewHolder(View view) {
            super(view);
            contact = view.findViewById(R.id.username);
            cbselect = view.findViewById(R.id.checkbox);
        }
    }

    public ContactRecyclerAdapter() {
        this.contactList = new ArrayList<>();
        this.checkBoxStatus = new ArrayList<>();
    }

    public boolean getCheckBoxStatus(int position) {
        return checkBoxStatus.get(position);
    }

    public PushUserDb getData(int position) {
        return this.contactList.get(position);
    }

    public void setContactList(List<PushUserDb> contactList) {
        this.contactList.clear();
        this.contactList.addAll(contactList);

        for(int i = 0; i < contactList.size(); i++) {
            checkBoxStatus.add(true);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_contacts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final PushUserDb contacts = contactList.get(position);
        holder.contact.setText(contacts.getName());

        holder.cbselect.setSelected(checkBoxStatus.get(position));

        holder.cbselect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxStatus.add(position, isChecked);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}

