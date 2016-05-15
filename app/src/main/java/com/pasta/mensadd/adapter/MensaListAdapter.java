package com.pasta.mensadd.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.MensaListFragment;
import com.pasta.mensadd.model.Mensa;

import java.util.ArrayList;


public class MensaListAdapter extends RecyclerView.Adapter<MensaListAdapter.ViewHolder> {

    public ArrayList<Mensa> items;
    public MensaListFragment fragment;

    public MensaListAdapter(ArrayList<Mensa> items, MensaListFragment fragment) {
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.mensa_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mensa item = items.get(position);
        holder.name.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView address;
        public TextView distance;
        public ImageView openSign;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.mensaName);
            card = (CardView) itemView.findViewById(R.id.mensaCardView);
        }
    }
}