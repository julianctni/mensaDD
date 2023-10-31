package com.pasta.mensadd;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by julian on 13.02.18.
 */

public class Utils {

    public static ScaleAnimation getFavoriteScaleOutAnimation(final View view) {
        final ScaleAnimation scaleOut = new ScaleAnimation(0.0f, 1.3f, 0.0f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation scaleIn = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleIn.setDuration(150);
        scaleOut.setDuration(150);
        scaleOut.setInterpolator(new AccelerateInterpolator());
        scaleOut.setInterpolator(new DecelerateInterpolator());
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return scaleOut;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static ValueAnimator createPullToRefreshAnimator(RecyclerView recyclerView) {
        ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getPaddingTop(), 0);
        animator.addUpdateListener(valueAnimator -> recyclerView.setPadding(0, (int) valueAnimator.getAnimatedValue(), 0, 0));
        animator.setDuration(200);
        return animator;
    }

    public static String formatTimestamp(Context context, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String date;
        if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            date = context.getString(R.string.today);
        } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1) {
            date = context.getString(R.string.yesterday);
        } else {
            date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(new Date(timestamp));
        }
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        return date + ", " + timeFormat.format(new Date(timestamp));
    }
}
