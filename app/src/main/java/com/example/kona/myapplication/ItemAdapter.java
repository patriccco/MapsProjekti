package com.example.kona.myapplication;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private Transaction transaction = new Transaction();
    private int resId;
    private TextView itemInfo;
    private ImageView img_l;

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

    ItemAdapter(ArrayList<Item> items, int resId, TextView itemInfo, ImageView img_l) {
        this.items = items;
        this.resId = resId;
        this.itemInfo = itemInfo;
        this.img_l = img_l;
    }

    public void add(int position, Item item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void showInfo(ViewHolder holder, int position) {
    }

    public Item getItem(int position) {return items.get(position); }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Item item = getItem(position);
        holder.itemName.setText(item.name);
        holder.held.setText("placeholder");
        holder.img_s.setImageResource(resId);
        holder.layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                img_l.setImageResource(resId);
                itemInfo.setText(item.info);
                transaction.setCurrItem(holder.getAdapterPosition());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}