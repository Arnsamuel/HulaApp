package e.aaronsamuel.hulaapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class AttendRecyclerAdapter extends RecyclerView.Adapter<AttendRecyclerAdapter.MyViewHolder>{
    private List<String> attendList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;

        MyViewHolder(View view) {
            super(view);
            pic = view.findViewById(R.id.image_view);
        }
    }

    public AttendRecyclerAdapter() {
        this.attendList = new ArrayList<>();
    }

    public void setAttendList(List<String> attendList) {
        this.attendList.clear();
        this.attendList.addAll(attendList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_attend, parent, false);

        return new AttendRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String rawPic = attendList.get(position);

        try {
            byte[] data = Base64.decode(rawPic, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);

            if(decodedByte != null)
                holder.pic.setImageBitmap(decodedByte);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return attendList.size();
    }
}
