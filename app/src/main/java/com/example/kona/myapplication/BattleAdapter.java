package com.example.kona.myapplication;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BattleAdapter extends RecyclerView.Adapter<BattleAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private int resId;
    private long currItem;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public TextView itemName;
        public TextView held;
        public ImageView img_s;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            itemName = (TextView) v.findViewById(R.id.item_name);
            held = (TextView) v.findViewById(R.id.item_held);
            img_s = v.findViewById(R.id.icon);
        }
    }

    BattleAdapter(ArrayList<Item> items, int resId) {
        this.items = items;
        this.resId = resId;
    }
    /*
        ItemAdapter(Context context, ArrayList<Item> items) {
            this.context = context;
            this.items = items;
        }
    */
    public void add(int position, Item item) {
        items.add(position, item);
        notifyItemInserted(position);
    }


    public Item getItem(int position) {return items.get(position); }

    // Create new views (invoked by the layout manager)
    @Override
    public BattleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element

        final Item item = getItem(position);
        holder.itemName.setText(item.name);
        holder.held.setText(String.valueOf(item.amount));
        holder.img_s.setImageResource(item.resId);
        holder.layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets the recycler position
                currItem = holder.getAdapterPosition();

                // intent passes the value of currItem to the activities
                Intent intent = new Intent("pass-item");
                intent.putExtra("item",currItem);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}