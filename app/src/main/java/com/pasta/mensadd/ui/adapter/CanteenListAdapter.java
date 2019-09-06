package com.pasta.mensadd.ui.adapter;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.ui.fragments.CanteenListFragment;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.model.DataHolder;

import java.util.ArrayList;
import java.util.List;


public class CanteenListAdapter extends RecyclerView.Adapter<CanteenListAdapter.ViewHolder> {

    private List<Canteen> mCanteens;
    private CanteenListFragment mFragment;
    private OnFavoriteClickListener mOnFavoriteClickListener;
    private OnCanteenClickListener mOnCanteenClickListener;
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
        if (item.isFavorite()) {
            holder.mFavorite.setImageDrawable(mFragment.getResources().getDrawable(R.drawable.ic_favorite_pink_24dp));
        } else {
            holder.mFavorite.setImageDrawable(mFragment.getResources().getDrawable(R.drawable.ic_favorite_border_grey_24dp));
        }
    }

    @Override
    public int getItemCount() {
        return mCanteens.size();
    }

    public void setCanteens(List<Canteen> canteens) {
        this.mCanteens = canteens;
        notifyDataSetChanged();
    }


    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.mOnFavoriteClickListener = listener;
    }

    public void setOnCanteenClickListener(OnCanteenClickListener listener) {
        this.mOnCanteenClickListener = listener;
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
                boolean isFavorite = mCanteens.get(getAdapterPosition()).isFavorite();
                mOnFavoriteClickListener.onFavoriteClick(mCanteens.get(getAdapterPosition()));
                int favIconId = isFavorite ? R.drawable.ic_favorite_border_grey_24dp : R.drawable.ic_favorite_pink_24dp;
                mFavorite.setImageDrawable(mFragment.getResources().getDrawable(favIconId));
                mFavorite.startAnimation(Utils.getFavoriteScaleOutAnimation(mFavorite));
            } else {
                try {
                    mOnCanteenClickListener.onCanteenClick(mCanteens.get(getAdapterPosition()));
                } catch (ArrayIndexOutOfBoundsException e) {
                    return;
                }
                /*
                if (mFragment.getActivity() != null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mFragment.getActivity().getApplicationContext());
                    int priority = prefs.getInt("priority_" + mensaId, 0);
                    priority += 1;
                    prefs.edit().putInt("priority_" + mensaId, priority).apply();
                }
                DataHolder.getInstance().getCanteen(mensaId).increasePriority();
                FragmentController.showMealWeekFragment(mFragment.getFragmentManager(), mensaId);
                */

            }
        }
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Canteen canteen);
    }

    public interface OnCanteenClickListener {
        void onCanteenClick(Canteen canteen);
    }
}