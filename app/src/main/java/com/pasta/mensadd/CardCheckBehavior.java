package com.pasta.mensadd;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by julian on 09.02.18.
 */

public class CardCheckBehavior extends CoordinatorLayout.Behavior<CoordinatorLayout> {
    public CardCheckBehavior() {
        super();
    }

    public CardCheckBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(final CoordinatorLayout parent, final CoordinatorLayout child, final View dependency) {
        if (BottomNavigation.class.isInstance(dependency)) {
            return true;
        } else if (Snackbar.SnackbarLayout.class.isInstance(dependency)) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(
            final CoordinatorLayout parent, final CoordinatorLayout child, final View dependency) {

        final List<View> list = parent.getDependencies(child);

        float t = 0;
        boolean result = false;

        for (View dep : list) {
            if (Snackbar.SnackbarLayout.class.isInstance(dep)) {
                t += dep.getTranslationY() - dep.getHeight();
                result = true;
            } else if (BottomNavigation.class.isInstance(dep)) {
                BottomNavigation navigation = (BottomNavigation) dep;
                t += navigation.getTranslationY() - navigation.getHeight();
                result = true;
            }
        }

        child.setTranslationY(t);
        return result;
    }

    @Override
    public void onDependentViewRemoved(
            final CoordinatorLayout parent, final CoordinatorLayout child, final View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }
}
