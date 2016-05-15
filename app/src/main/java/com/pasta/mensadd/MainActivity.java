package com.pasta.mensadd;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.MensaListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RelativeLayout mainContainer;
    private RelativeLayout cardCheckContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainContainer = (RelativeLayout) findViewById(R.id.mainContainer);
        cardCheckContainer = (RelativeLayout) findViewById(R.id.cardCheckContainer);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getFragmentManager().findFragmentById(R.id.mainContainer) == null) {
            MensaListFragment fragment = new MensaListFragment();
            getFragmentManager().beginTransaction().add(R.id.mainContainer, fragment, "MensaList").commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.nfcCheck){
            float translation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
            FragmentController.showCardCheckFragment(getFragmentManager());
            Animation animation;
            if (cardCheckContainer.getVisibility() == View.GONE) {
                animation = new SlideAnimation(cardCheckContainer, 0, (int)translation);
            } else {
                animation = new SlideAnimation(cardCheckContainer, (int)translation, 0);
            }
            cardCheckContainer.setAnimation(animation);
            cardCheckContainer.startAnimation(animation);
        }


        return super.onOptionsItemSelected(item);
    }

    public class SlideAnimation extends Animation {

        int mFromHeight;
        int mToHeight;
        View mView;

        public SlideAnimation(final View view, final int fromHeight, final int toHeight) {
            this.mView = view;
            this.mFromHeight = fromHeight;
            this.mToHeight = toHeight;
            this.setDuration(200);
            this.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (fromHeight == 0)
                        view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (toHeight == 0)
                        view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_map){
            FragmentController.showMapFragment(getFragmentManager());
        } else if (id == R.id.nav_mensa){
            FragmentController.showMensaListFragment(getFragmentManager());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
