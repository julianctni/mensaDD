package com.pasta.mensadd.adapter;

import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.MensaListFragment;
import com.pasta.mensadd.model.Mensa;

import java.util.ArrayList;


public class MensaListAdapter extends RecyclerView.Adapter<MensaListAdapter.ViewHolder> {

    public ArrayList<Mensa> items;
    public MensaListFragment fragment;
    public ArrayList<Integer> headerColors;

    public MensaListAdapter(ArrayList<Mensa> items, MensaListFragment fragment) {
        this.items = items;
        this.fragment = fragment;
        headerColors = new ArrayList<>();
        headerColors.add(R.color.tile_blue);
        headerColors.add(R.color.tile_pink);
        headerColors.add(R.color.tile_orange);
        headerColors.add(R.color.tile_cyan);
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
        int colorIndex = position%(headerColors.size());
        Log.i("MENSALIST", colorIndex+"");
        holder.header.setBackgroundColor(fragment.getResources().getColor(headerColors.get(colorIndex)));
        holder.name.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public LinearLayout header;
        public TextView address;
        public TextView distance;
        public ImageView openSign;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.mensaName);
            card = (CardView) itemView.findViewById(R.id.mensaCardView);
            header = (LinearLayout) itemView.findViewById(R.id.mensaListItemHeader);
        }
    }
}