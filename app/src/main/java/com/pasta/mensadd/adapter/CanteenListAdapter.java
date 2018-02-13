package com.pasta.mensadd.adapter;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;

import java.util.ArrayList;


public class CanteenListAdapter extends RecyclerView.Adapter<CanteenListAdapter.ViewHolder> {

    private ArrayList<Canteen> mCanteens;
    private CanteenListFragment mFragment;

    public CanteenListAdapter(ArrayList<Canteen> items, CanteenListFragment fragment) {
        mCanteens = items;
        mFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_canteen_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Canteen item = mCanteens.get(position);
        holder.mName.setText(item.getName());
        holder.mAddress.setText(item.getAddress());
        holder.mHours.setText(item.getHours());
        if (item.isFavorite())
            holder.mFavorite.setImageDrawable(mFragment.getResources().getDrawable(R.drawable.ic_favorite_pink_24dp));
        else
            holder.mFavorite.setImageDrawable(mFragment.getResources().getDrawable(R.drawable.ic_favorite_border_grey_24dp));
    }

    @Override
    public int getItemCount() {
        return mCanteens.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mName;
        TextView mAddress;
        TextView mHours;
        ImageView mFavorite;
        RelativeLayout mListItemHeader;

        ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.mensaName);
            mAddress = itemView.findViewById(R.id.mensaAddress);
            mHours = itemView.findViewById(R.id.mensaHours);
            mFavorite = itemView.findViewById(R.id.canteenItemFav);
            mListItemHeader = itemView.findViewById(R.id.mensaListItemHeader);
            itemView.setOnClickListener(this);
            mFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.canteenItemFav) {
                if (mCanteens.get(getAdapterPosition()).isFavorite()) {
                    mFavorite.setImageDrawable(mFragment.getResources().getDrawable(R.drawable.ic_favorite_border_grey_24dp));
                    mCanteens.get(getAdapterPosition()).setAsFavorite(false, mFragment.getContext());
                    Toast.makeText(mFragment.getContext(), mFragment.getString(R.string.toast_remove_favorite), Toast.LENGTH_SHORT).show();
                } else {
                    mFavorite.setImageDrawable(mFragment.getResources().getDrawable(R.drawable.ic_favorite_pink_24dp));
                    mCanteens.get(getAdapterPosition()).setAsFavorite(true, mFragment.getContext());
                    Toast.makeText(mFragment.getContext(), mFragment.getString(R.string.toast_add_favorite), Toast.LENGTH_SHORT).show();
                }
                mFavorite.startAnimation(Utils.getFavoriteScaleOutAnimation(mFavorite));
            } else {
                String mensaId;
                try {
                    mensaId = mCanteens.get(getAdapterPosition()).getCode();
                } catch (ArrayIndexOutOfBoundsException e) {
                    return;
                }
                if (mFragment.getActivity() != null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mFragment.getActivity().getApplicationContext());
                    int priority = prefs.getInt("priority_" + mensaId, 0);
                    priority += 1;
                    prefs.edit().putInt("priority_" + mensaId, priority).apply();
                }
                DataHolder.getInstance().getCanteen(mensaId).increasePriority();
                DataHolder.getInstance().sortCanteenList();
                FragmentController.showMealWeekFragment(mFragment.getFragmentManager(), mensaId);
            }
        }
    }
}