package com.pasta.mensadd.ui.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.networking.MealImageLoader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class MealListAdapter extends ListAdapter<Meal, MealListAdapter.MealViewHolder> {

    private static final int TYPE_MEAL = 1;
    private static final int TYPE_LAST_UPDATE = 2;

    private static final DiffUtil.ItemCallback<Meal> DIFF_CALLBACK = new DiffUtil.ItemCallback<Meal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Meal o, @NonNull Meal n) {
            return o.getId().equals(n.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Meal o, @NonNull Meal n) {
            return (o.isAlcohol() && n.isAlcohol()) &&
                    (o.isBeef() && n.isBeef()) &&
                    (o.isGarlic() && n.isGarlic()) &&
                    (o.isPork() && n.isPork()) &&
                    (o.isVegetarian() && n.isVegetarian()) &&
                    (o.isVegan() && n.isVegan()) &&
                    o.getDetails().equals(n.getDetails()) &&
                    o.getName().equals(n.getName()) &&
                    o.getPrice().equals(n.getPrice());
        }
    };
    private Context mContext;
    private PreferenceService mPreferenceService;
    private SparseBooleanArray mExpandStates = new SparseBooleanArray();
    private long mLastUpdate;

    public MealListAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
        mPreferenceService = new PreferenceService(context);
    }

    @NotNull
    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == TYPE_LAST_UPDATE ? R.layout.item_meal_list_last : R.layout.item_meal_list;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                layoutId, parent, false);
        return new MealViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getCurrentList().size() - 1) {
            return TYPE_LAST_UPDATE;
        } else {
            return TYPE_MEAL;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MealListAdapter.MealViewHolder holder, int position) {
        Meal item = getItem(position);
        holder.mName.setText(item.getName());
        holder.mPrice.setText(item.getPrice());
        if (item.getDetails().isEmpty()) {
            holder.mMealContent.setVisibility(View.GONE);
        } else {
            holder.mMealContent.setVisibility(View.VISIBLE);
            holder.mMealContent.setText(item.formatDetails(item.getDetails()));
        }

        if (item.getLocation().length() > 0) {
            holder.mLocation.setText(item.getLocation());
            holder.mLocation.setVisibility(View.VISIBLE);
        } else
            holder.mLocation.setVisibility(View.GONE);

        if (item.isVegan()) holder.mVegan.setVisibility(View.VISIBLE);
        else holder.mVegan.setVisibility(View.GONE);

        if (item.isVegetarian()) holder.mVegetarian.setVisibility(View.VISIBLE);
        else holder.mVegetarian.setVisibility(View.GONE);

        if (item.isPork()) holder.mPork.setVisibility(View.VISIBLE);
        else holder.mPork.setVisibility(View.GONE);

        if (item.isBeef()) holder.mBeef.setVisibility(View.VISIBLE);
        else holder.mBeef.setVisibility(View.GONE);

        if (item.isGarlic()) holder.mGarlic.setVisibility(View.VISIBLE);
        else holder.mGarlic.setVisibility(View.GONE);

        if (item.isAlcohol()) holder.mAlcohol.setVisibility(View.VISIBLE);
        else holder.mAlcohol.setVisibility(View.GONE);

        if (item.getName().contains("Bauchspeck") && mPreferenceService.isBaconFeatureEnabled())
            holder.mBacon.setVisibility(View.VISIBLE);
        else
            holder.mBacon.setVisibility(View.GONE);

        if (mPreferenceService.isGreenVeggieMealsEnabled() && (item.isVegan() || item.isVegetarian())) {
            holder.mHeaderLayout.setBackgroundColor(mContext.getResources().getColor(R.color.card_header_vegeterian));
            holder.mName.setTextColor(mContext.getResources().getColor(R.color.card_text_light));
            holder.mLocation.setTextColor(mContext.getResources().getColor(R.color.card_text_light));
        } else {
            holder.mHeaderLayout.setBackgroundColor(mContext.getResources().getColor(R.color.card_header));
            holder.mName.setTextColor(mContext.getResources().getColor(R.color.card_header_text));
            holder.mLocation.setTextColor(mContext.getResources().getColor(R.color.card_header_text));
        }

        if (mExpandStates.indexOfKey(position) >= 0 && mExpandStates.get(position)) {
            holder.mMealDetails.setVisibility(View.VISIBLE);
            holder.mShareButton.setVisibility(View.VISIBLE);
        } else {
            holder.mMealDetails.setVisibility(View.GONE);
            holder.mShareButton.setVisibility(View.GONE);
        }

        if (holder.mLastUpdate != null) {
            DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            holder.mLastUpdate.setText(mContext.getString(R.string.last_server_check, dateFormat.format(new Date(mLastUpdate))));
        }
    }

    public void setLastMealUpdate(long lastMealUpdate) {
        mLastUpdate = lastMealUpdate;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout mHeaderLayout;
        private TextView mName;
        private TextView mLocation;
        private TextView mPrice;
        private TextView mMealImageStatus;
        private ImageView mPork;
        private ImageView mBeef;
        private TextView mVegan;
        private ImageView mVegetarian;
        private ImageView mAlcohol;
        private ImageView mGarlic;
        private ImageView mBacon;
        private TextView mMealContent;
        private LinearLayout mMealDetails;
        private FloatingActionButton mShareButton;
        private ImageView mMealImage;
        private ProgressBar mMealImageProgress;
        private TextView mLastUpdate;

        private MealViewHolder(View itemView) {
            super(itemView);
            CardView mealCard = itemView.findViewById(R.id.mealCard);
            mHeaderLayout = itemView.findViewById(R.id.mealListItemHeader);
            mMealDetails = itemView.findViewById(R.id.mealDetails);
            mName = itemView.findViewById(R.id.mealName);
            mLocation = itemView.findViewById(R.id.mealLocation);
            mPrice = itemView.findViewById(R.id.mealPrice);
            mMealImageStatus = itemView.findViewById(R.id.mealImageStatus);
            mPork = itemView.findViewById(R.id.pork);
            mBeef = itemView.findViewById(R.id.beef);
            mVegan = itemView.findViewById(R.id.vegan);
            mVegetarian = itemView.findViewById(R.id.vegetarian);
            mAlcohol = itemView.findViewById(R.id.alcohol);
            mGarlic = itemView.findViewById(R.id.garlic);
            mBacon = itemView.findViewById(R.id.bacon);
            mMealContent = itemView.findViewById(R.id.mealContent);
            mMealImage = itemView.findViewById(R.id.mealImage);
            mMealImageProgress = itemView.findViewById(R.id.mealImageProgressBar);
            mShareButton = itemView.findViewById(R.id.shareButton);
            mShareButton.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.pink)));
            mShareButton.setOnClickListener(this);
            //mMealImage.setOnClickListener(this);
            mealCard.setOnClickListener(this);
            mLastUpdate = itemView.findViewById(R.id.lastCanteenUpdateText);
        }


        @SuppressLint("SetWorldReadable")
        private void shareMeal() {
            Meal meal = getItem(getAdapterPosition());
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            String shareText = meal.getName() + "\n" + meal.getPrice() + "\n#"
                    + meal.getCanteenId()
                    .replaceAll("\\s+", "") + " " + mContext.getString(R.string.content_share_hungry) + " #mensaDD";

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            Bitmap bitmap = ((BitmapDrawable) mMealImage.getDrawable()).getBitmap();
            boolean shareImagePref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(mContext.getString(R.string.pref_share_image_key), false);

            if (shareImagePref && meal.getImgLink().length() > 1) {
                try {
                    String filename = Math.abs(meal.getName().hashCode()) + ".jpeg";
                    if (mContext != null) {
                        File file = new File(mContext.getFilesDir(), filename);
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        //file.setReadable(true, false);
                        Uri fileUri = FileProvider.getUriForFile(mContext, "com.pasta.mensadd.fileprovider", file);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                shareIntent.setType("text/plain");
            }
            if (mContext != null)
                mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.content_share)));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.shareButton) {
                shareMeal();
            } else {
                mMealImage.getLayoutParams().width = mHeaderLayout.getMeasuredWidth();
                if (mMealDetails.getVisibility() == View.GONE) {
                    if (mContext != null)
                        mMealImageStatus.setText(mContext.getText(R.string.meals_loading_image));
                    String url = getItem(getAdapterPosition()).getImgLink();
                    if (url != null) {
                        expandLayout(mMealDetails);
                        mMealImageStatus.setText(mContext.getText(R.string.meals_loading_image));
                        mMealImageProgress.setVisibility(View.VISIBLE);
                        mMealImageStatus.setVisibility(View.VISIBLE);
                        mMealImage.setVisibility(View.GONE);
                        MealImageLoader.fetchImage(url, (success, bitmap) -> {
                            if (success) {
                                mMealImage.setImageBitmap(bitmap);
                                mMealImageProgress.setVisibility(View.GONE);
                                mMealImageStatus.setVisibility(View.GONE);
                                mMealImage.setVisibility(View.VISIBLE);
                            } else {
                                mMealImageProgress.setVisibility(View.GONE);
                                mMealImage.setVisibility(View.GONE);
                                mMealImageStatus.setText(mContext.getText(R.string.meals_no_image));
                                mMealImageStatus.setVisibility(View.VISIBLE);
                            }
                            expandLayout(mMealDetails);
                        });
                    } else {
                        mMealImageProgress.setVisibility(View.GONE);
                        mMealImage.setVisibility(View.GONE);
                        mMealImageStatus.setText(mContext.getText(R.string.meals_no_image));
                        mMealImageStatus.setVisibility(View.VISIBLE);
                        expandLayout(mMealDetails);
                    }
                    mExpandStates.put(getAdapterPosition(), true);
                } else {
                    mExpandStates.put(getAdapterPosition(), false);
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
            animator.addUpdateListener(valueAnimator -> {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            });
            return animator;
        }
    }
}