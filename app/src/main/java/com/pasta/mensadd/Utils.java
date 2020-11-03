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

    public static ScaleAnimation getLastUpdateScaleAnimation(final View view) {
        final ScaleAnimation scaleOut = new ScaleAnimation(0.0f, 1.05f, 0.0f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation scaleIn = new ScaleAnimation(1.05f, 1.0f, 1.05f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //scaleOut.setStartOffset(2000);
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

    public static ViewHeightAnimation getViewHeightAnimation(View view, int fromHeight, int toHeight, int duration) {
        return new ViewHeightAnimation(view, fromHeight, toHeight, duration);
    }

    private static class ViewHeightAnimation extends Animation {
        int mFromHeight;
        int mToHeight;
        View mView;

        ViewHeightAnimation(View view, int fromHeight, int toHeight, int duration) {
            this.mView = view;
            this.mFromHeight = fromHeight;
            this.mToHeight = toHeight;
            this.setDuration(duration);
            this.setInterpolator(new AccelerateInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
            int newHeight;

            if (mView.getHeight() != mToHeight) {
                newHeight = (int) (mFromHeight + ((mToHeight - mFromHeight) * interpolatedTime));
                mView.getLayoutParams().height = newHeight;
                mView.requestLayout();
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
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
}
