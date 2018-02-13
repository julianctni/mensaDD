package com.pasta.mensadd;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

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
}
