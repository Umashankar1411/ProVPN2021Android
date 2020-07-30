package com.ugt.premiumvpn.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ugt.premiumvpn.R;
import com.ugt.premiumvpn.model.Apps;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    public List<Apps> apps;

    public RecyclerAdapter(List<Apps> apps)
    {
        this.apps = apps;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_apps, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Apps app= apps.get(position);
        holder.size.setText(app.getSize());
        holder.image.setImageDrawable(app.getImage());
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView size;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            size = (TextView) view.findViewById(R.id.apptext);
            image=(ImageView)view.findViewById(R.id.appimage);

        }
    }
}
