package com.pasta.mensadd;


import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewHeightAnimation extends Animation {
    int mFromHeight;
    int mToHeight;
    View mView;

    public ViewHeightAnimation(final View view, final int fromHeight, final int toHeight, int duration) {
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
