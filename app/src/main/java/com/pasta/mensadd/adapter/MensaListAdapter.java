package com.pasta.mensadd.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.model.Mensa;

import java.util.ArrayList;


public class MensaListAdapter extends RecyclerView.Adapter<MensaListAdapter.ViewHolder> {

    public ArrayList<Mensa> items;
    public CanteenListFragment fragment;
    public ArrayList<Integer> headerColors;

    public MensaListAdapter(ArrayList<Mensa> items, CanteenListFragment fragment) {
        this.items = items;
        this.fragment = fragment;
        headerColors = new ArrayList<>();
        headerColors.add(R.color.tile_blue1);
        headerColors.add(R.color.tile_pink1);
        headerColors.add(R.color.tile_orange1);
        headerColors.add(R.color.tile_cyan1);
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
        //holder.mListItemHeader.setBackgroundColor(fragment.getResources().getColor(headerColors.get(colorIndex)));
        holder.mName.setText(item.getName());
        holder.mAddress.setText(item.getAddress());
        holder.mHours.setText(item.getHours());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mName;
        public TextView mAddress;
        public TextView mHours;
        public LinearLayout mListItemHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.mensaName);
            mAddress = (TextView) itemView.findViewById(R.id.mensaAddress);
            mHours = (TextView) itemView.findViewById(R.id.mensaHours);
            mListItemHeader = (LinearLayout) itemView.findViewById(R.id.mensaListItemHeader);
        }

        @Override
        public void onClick(View v) {
            int mensaId = items.get(getAdapterPosition()).getId();
            FragmentController.showMealListFragment(fragment.getFragmentManager(),mensaId);
        }
    }
}