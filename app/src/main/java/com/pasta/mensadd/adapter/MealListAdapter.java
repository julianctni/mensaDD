package com.pasta.mensadd.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.MealDayFragment;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.networking.LoadImageCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.ViewHolder> {

    private ArrayList<Meal> mMeals;
    private MealDayFragment mFragment;
    private SharedPreferences mPrefs;
    private final String COLOR_TEXT_WHITE = "#FFFFFF";
    private final String COLOR_TEXT_DARK = "#444444";
    private final String COLOR_HEADER_VEG = "#7fb29b";
    private final String COLOR_HEADER_NOT_VEG = "#F1F1F1";
    private HashMap<Integer,Boolean> mExpandStates = new HashMap<>();

    public MealListAdapter(ArrayList<Meal> items, MealDayFragment fragment) {
        mMeals = items;
        mFragment = fragment;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mFragment.getActivity().getApplicationContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_meal_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Meal item = mMeals.get(position);
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

        if (mPrefs.getBoolean(mFragment.getString(R.string.pref_veg_meals_key), true) && (item.isVegan() || item.isVegetarian())) {
            holder.mHeaderLayout.setBackgroundColor(Color.parseColor(COLOR_HEADER_VEG));
            holder.mName.setTextColor(Color.parseColor(COLOR_TEXT_WHITE));
        } else {
            holder.mHeaderLayout.setBackgroundColor(Color.parseColor(COLOR_HEADER_NOT_VEG));
            holder.mName.setTextColor(Color.parseColor(COLOR_TEXT_DARK));
        }


        if (mExpandStates.containsKey(position) && mExpandStates.get(position)) {
            holder.mMealDetails.setVisibility(View.VISIBLE);
            holder.mShareButton.setVisibility(View.VISIBLE);
        } else {
            holder.mMealDetails.setVisibility(View.GONE);
            holder.mShareButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMeals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, LoadImageCallback {
        private LinearLayout mHeaderLayout;
        private TextView mName;
        private TextView mPrice;
        private TextView mMealImageStatus;
        private ImageView mPork;
        private ImageView mBeef;
        private TextView mVegan;
        private ImageView mVegetarian;
        private ImageView mAlcohol;
        private ImageView mGarlic;
        private TextView mMealContent;
        private LinearLayout mMealDetails;
        private FloatingActionButton mShareButton;
        private ImageView mMealImage;
        private ProgressBar mMealImageProgress;

        private ViewHolder(View itemView) {
            super(itemView);
            mHeaderLayout = itemView.findViewById(R.id.mealListItemHeader);
            mMealDetails = itemView.findViewById(R.id.mealDetails);
            mName = itemView.findViewById(R.id.mealName);
            mPrice = itemView.findViewById(R.id.mealPrice);
            mMealImageStatus = itemView.findViewById(R.id.mealImageStatus);
            mPork =  itemView.findViewById(R.id.pork);
            mBeef =  itemView.findViewById(R.id.beef);
            mVegan = itemView.findViewById(R.id.vegan);
            mVegetarian =  itemView.findViewById(R.id.vegetarian);
            mAlcohol =  itemView.findViewById(R.id.alcohol);
            mGarlic =  itemView.findViewById(R.id.garlic);
            mMealContent = itemView.findViewById(R.id.mealContent);
            mMealImage =  itemView.findViewById(R.id.mealImage);
            mMealImageProgress = itemView.findViewById(R.id.mealImageProgressBar);
            mShareButton = itemView.findViewById(R.id.shareButton);
            mShareButton.setBackgroundTintList(ColorStateList.valueOf(mFragment.getResources().getColor(R.color.pink)));
            mShareButton.setOnClickListener(this);
            mMealImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }




        @SuppressLint("SetWorldReadable")
        private void shareMeal() {
            Meal meal = mMeals.get(getAdapterPosition());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //text has to be added to intent no matter what
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, meal.getName() + "\n" + meal.getPrice() + "\n#"
                    + DataHolder.getInstance().getMensa(mFragment.getCanteenId()).getName()
                    .replaceAll("\\s+", "") + " "+mFragment.getString(R.string.content_share_hungry)+" #mensaDD");


            Bitmap bitmap = ((BitmapDrawable) mMealImage.getDrawable()).getBitmap();
            boolean shareImagePref = PreferenceManager.getDefaultSharedPreferences(mFragment.getContext()).getBoolean(mFragment.getString(R.string.pref_share_image_key), false);

            //if there is a bitmap attached (the link isn't too short) and the user has enabled image sharing in prefs:
            if (shareImagePref && meal.getImgLink().length() > 1 ) {
                //additionally share image
                try {
                    //save file to cache directory
                    File file = new File(mFragment.getContext().getCacheDir(), meal.getName().hashCode() + ".jpeg");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    shareIntent.setType("image/png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mFragment.getActivity().startActivity(Intent.createChooser(shareIntent, mFragment.getString(R.string.content_share)));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.shareButton) {
                shareMeal();
            } else {
                mMealImage.getLayoutParams().width = mHeaderLayout.getMeasuredWidth();
                if (mMealDetails.getVisibility() == View.GONE) {
                    mMealImageStatus.setText(mFragment.getActivity().getApplicationContext().getText(R.string.meals_loading_image));
                    String url = mMeals.get(getAdapterPosition()).getImgLink();
                    if (url.length() > 1) {
                        expandLayout(mMealDetails);
                        NetworkController.getInstance(mFragment.getActivity().getApplicationContext()).loadMealImage(url, this);
                    } else {
                        mMealImageProgress.setVisibility(View.GONE);
                        mMealImage.setVisibility(View.GONE);
                        mMealImageStatus.setText(mFragment.getActivity().getApplicationContext().getText(R.string.meals_no_image));
                        mMealImageStatus.setVisibility(View.VISIBLE);
                        expandLayout(mMealDetails);
                    }
                    mExpandStates.put(getAdapterPosition(),true);
                } else {
                    mExpandStates.put(getAdapterPosition(),false);
                    collapseLayout(mMealDetails);
                }
            }

        }


        private void expandLayout(View v) {
            v.setVisibility(View.VISIBLE);
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(widthSpec, heightSpec);
            ValueAnimator mAnimator = slideAnimator(v, v.getHeight(), v.getMeasuredHeight());
            mAnimator.setDuration(200);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ScaleAnimation showAnim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    showAnim.setDuration(180);
                    if (mShareButton.getVisibility() == View.GONE) {
                        mShareButton.setVisibility(View.VISIBLE);
                        mShareButton.startAnimation(showAnim);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mAnimator.start();
        }


        private void collapseLayout(final View v) {
            int finalHeight = v.getHeight();
            final ValueAnimator mAnimator = slideAnimator(v, finalHeight, 0);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ScaleAnimation hideAnim = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    hideAnim.setDuration(200);
                    mShareButton.startAnimation(hideAnim);
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
            mAnimator.setDuration(200);
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

        @Override
        public void onResponseMessage(int responseType, String message, Bitmap bitmap) {
            if (responseType == NetworkController.SUCCESS) {
                mMealImage.setImageBitmap(bitmap);
                mMealImageProgress.setVisibility(View.GONE);
                mMealImageStatus.setVisibility(View.GONE);
                mMealImage.setVisibility(View.VISIBLE);
            } else {
                mMealImageProgress.setVisibility(View.GONE);
                mMealImage.setVisibility(View.GONE);
                mMealImageStatus.setText(mFragment.getActivity().getApplicationContext().getText(R.string.meals_no_image));
                mMealImageStatus.setVisibility(View.VISIBLE);
            }
            if (responseType == NetworkController.NO_INTERNET) {
                Toast.makeText(mFragment.getActivity().getApplicationContext(), mFragment.getString(R.string.img_load_no_connection), Toast.LENGTH_SHORT).show();
            }
            expandLayout(mMealDetails);
        }
    }
}