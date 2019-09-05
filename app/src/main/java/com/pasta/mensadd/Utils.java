package com.pasta.mensadd;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.pasta.mensadd.model.Canteen;

/**
 * Created by julian on 13.02.18.
 */

public class Utils {

    public static int FAVORITE_PRIORITY = 999999;

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

    public static int calculateCanteenPriority(String canteenId, int priority) {
        int canteenPriority;
        if (canteenId.contains("zeltschloesschen") || canteenId.contains("alte-mensa"))
            if (priority < 2)
                canteenPriority = 2;
            else
                canteenPriority = priority;
        else if (canteenId.contains("siedepunkt") || canteenId.contains("mensa-reichenbachstrasse"))
            if (priority < 1)
                canteenPriority = 1;
            else
                canteenPriority = priority;
        else
            canteenPriority = priority;
        return canteenPriority;
    }
}
