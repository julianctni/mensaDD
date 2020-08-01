package com.pasta.mensadd.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.database.entity.Canteen;


public class CanteenListAdapter extends ListAdapter<Canteen, CanteenListAdapter.ViewHolder> {

    private OnFavoriteClickListener mOnFavoriteClickListener;
    private OnCanteenClickListener mOnCanteenClickListener;
    private Context mContext;

    private static final DiffUtil.ItemCallback<Canteen> DIFF_CALLBACK = new DiffUtil.ItemCallback<Canteen>() {
        @Override
        public boolean areItemsTheSame(@NonNull Canteen o, @NonNull Canteen n) {
            return o.getId().equals(n.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Canteen o, @NonNull Canteen n) {
            return o.getName().equals(n.getName()) &&
                    o.getAddress().equals(n.getAddress()) &&
                    o.getHours().equals(n.getHours()) &&
                    o.getListPriority() == n.getListPriority();
        }
    };

    public CanteenListAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_canteen_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Canteen item = getItem(position);
        holder.mName.setText(item.getName());
        holder.mAddress.setText(item.getAddress());
        holder.mHours.setText(item.getHours());
        if (item.isFavorite()) {
            holder.mFavorite.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_24));
            holder.mFavorite.setColorFilter(ContextCompat.getColor(mContext, R.color.pink_dark));
        } else {
            holder.mFavorite.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
            holder.mFavorite.setColorFilter(ContextCompat.getColor(mContext, R.color.card_header_text));
        }
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
                boolean isFavorite = getItem(getAdapterPosition()).isFavorite();
                mOnFavoriteClickListener.onFavoriteClick(getItem(getAdapterPosition()));
                int favIconId = isFavorite ? R.drawable.ic_baseline_favorite_border_24 : R.drawable.ic_baseline_favorite_24;
                int favIconColor = isFavorite ? R.color.card_header_text : R.color.pink_dark;

                mFavorite.setImageDrawable(mContext.getDrawable(favIconId));
                mFavorite.setColorFilter(ContextCompat.getColor(mContext, favIconColor));
                mFavorite.startAnimation(Utils.getFavoriteScaleOutAnimation(mFavorite));
            } else {
                try {
                    mOnCanteenClickListener.onCanteenClick(getItem(getAdapterPosition()));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
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