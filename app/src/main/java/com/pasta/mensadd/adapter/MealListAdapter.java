package com.pasta.mensadd.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.fragments.MealDayFragment;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.Mensa;

import java.util.ArrayList;


public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.ViewHolder> {

    public ArrayList<Meal> items;
    public MealDayFragment fragment;
    public ArrayList<Integer> headerColors;

    public MealListAdapter(ArrayList<Meal> items, MealDayFragment fragment) {
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
                R.layout.meal_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Meal item = items.get(position);
        Log.i("ADAPTER",item.getName());
        holder.mName.setText(item.getName());
        holder.mPrice.setText(item.getPrice());
        holder.mMealContent.setText(item.getDetails());
        if (item.isVegan()) holder.mVegan.setVisibility(View.VISIBLE);
        else holder.mVegan.setVisibility(View.GONE);

        if (item.isVegetarian()) holder.mVegetarian.setVisibility(View.VISIBLE);
        else holder.mVegetarian.setVisibility(View.GONE);

        if (item.containsPork()) holder.mPork.setVisibility(View.VISIBLE);
        else holder.mPork.setVisibility(View.GONE);

        if (item.containsBeef()) holder.mBeef.setVisibility(View.VISIBLE);
        else holder.mBeef.setVisibility(View.GONE);

        if (item.containsGarlic()) holder.mGarlic.setVisibility(View.VISIBLE);
        else holder.mGarlic.setVisibility(View.GONE);

        if (item.containsAlcohol()) holder.mAlcohol.setVisibility(View.VISIBLE);
        else holder.mAlcohol.setVisibility(View.GONE);

        if (item.isVegan() || item.isVegetarian()) {
            holder.mHeaderLayout.setBackgroundColor(Color.parseColor("#7fb29b"));
            holder.mName.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.mHeaderLayout.setBackgroundColor(Color.parseColor("#F1F1F1"));
            holder.mName.setTextColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public LinearLayout mHeaderLayout;
        public TextView mName;
        public TextView mPrice;
        public ImageView mPork;
        public ImageView mBeef;
        public TextView mVegan;
        public ImageView mVegetarian;
        public ImageView mAlcohol;
        public ImageView mGarlic;
        public TextView mMealContent;
        public LinearLayout mListItemHeader;
        public LinearLayout mMealDetails;
        public FloatingActionButton mShareButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeaderLayout = (LinearLayout) itemView.findViewById(R.id.mealListItemHeader);
            mMealDetails = (LinearLayout) itemView.findViewById(R.id.mealDetails);
            mName = (TextView) itemView.findViewById(R.id.mealName);
            mPrice = (TextView) itemView.findViewById(R.id.mealPrice);
            mPork = (ImageView) itemView.findViewById(R.id.pork);
            mBeef = (ImageView) itemView.findViewById(R.id.beef);
            mVegan = (TextView) itemView.findViewById(R.id.vegan);
            mVegetarian = (ImageView) itemView.findViewById(R.id.vegetarian);
            mAlcohol = (ImageView) itemView.findViewById(R.id.alcohol);
            mGarlic = (ImageView) itemView.findViewById(R.id.garlic);
            mMealContent = (TextView) itemView.findViewById(R.id.mealContent);
            mListItemHeader = (LinearLayout) itemView.findViewById(R.id.mensaListItemHeader);
            mShareButton = (FloatingActionButton) itemView.findViewById(R.id.shareButton);
            mShareButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff4b66")));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mMealDetails.getVisibility() == View.GONE)
                expandLayout(mMealDetails);
            else
                collapseLayout(mMealDetails);
        }


        public void expandLayout(View v) {
            v.setVisibility(View.VISIBLE);
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(widthSpec, heightSpec);
            ValueAnimator mAnimator = slideAnimator(v, v.getHeight(), v.getMeasuredHeight());
            mAnimator.setDuration(250);
            ScaleAnimation showAnim = new ScaleAnimation(0,1,0,1,50,50);
            showAnim.setDuration(250);
            mShareButton.setVisibility(View.VISIBLE);
            mShareButton.startAnimation(showAnim);
            mAnimator.start();
        }


        private void collapseLayout(final View v) {
            int finalHeight = v.getHeight();
            final ValueAnimator mAnimator = slideAnimator(v, finalHeight, 0);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ScaleAnimation showAnim = new ScaleAnimation(1,0,1,0,50,50);
                    showAnim.setDuration(250);
                    mShareButton.startAnimation(showAnim);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    v.setVisibility(View.GONE);
                    mShareButton.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mAnimator.setDuration(250);
            mAnimator.start();
        }

        private ValueAnimator slideAnimator(final View v, int start, int end) {

            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                    layoutParams.height = value;
                    v.setLayoutParams(layoutParams);
                }
            });
            return animator;
        }
    }
}