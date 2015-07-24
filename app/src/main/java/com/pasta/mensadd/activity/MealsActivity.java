/*
 * MealsActivity.java
 *
 * Copyright (C) 2014 Julian Catoni
 *
 * Authors:
 * Julian Catoni <turbojulian@ymail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pasta.mensadd.activity;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.model.MensaRepo;
import com.pasta.mensadd.util.NetworkUtil;
import com.pasta.mensadd.util.ParserTask;

public class MealsActivity extends ActionBarActivity {

	Calendar calendar = Calendar.getInstance();
	int mensaId;
	ViewPager mPager;
	TextView tv;
	int currentPosition;
	ImageView arrowLeft;
	ImageView arrowRight;
	ScreenSlidePagerAdapter mPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mensaId = getIntent().getIntExtra("mensaId", 0);
		setContentView(R.layout.activity_meals);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setLogo(R.drawable.banner);
		toolbar.setTitle("");
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		mPagerAdapter = new ScreenSlidePagerAdapter(
				getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);
		arrowLeft = (ImageView) findViewById(R.id.arrow_left);
		arrowRight = (ImageView) findViewById(R.id.arrow_right);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

				if (0.0 != positionOffset && positionOffset <= 0.25f) {
					if (position == 12)
						arrowRight.setVisibility(View.VISIBLE);
					if (position == 0)
						arrowLeft.setVisibility(View.INVISIBLE);
					arrowLeft.setAlpha(1 - 4 * positionOffset);
					arrowRight.setAlpha(1 - 4 * positionOffset);
				} else if (0.0 != positionOffset && positionOffset >= 0.75f) {
					if (position == 0)
						arrowLeft.setVisibility(View.VISIBLE);
					if (position == 12)
						arrowRight.setVisibility(View.INVISIBLE);
					arrowRight.setAlpha((-0.75f + positionOffset) * 4);
					arrowLeft.setAlpha((-0.75f + positionOffset) * 4);
				}
			}

			@Override
			public void onPageSelected(int position) {
			}
		});
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1)
			dayOfWeek = 6;
		else
			dayOfWeek -= 2;
		int goToPage = getIntent().getIntExtra("currentPosition", dayOfWeek);
		mPager.setCurrentItem(goToPage, true);
		
		if (goToPage == 0)
			arrowLeft.setVisibility(View.INVISIBLE);
		if (goToPage == 13)
			arrowRight.setVisibility(View.INVISIBLE);
		tv = (TextView) findViewById(R.id.mensaName);
		tv.setText(MensaRepo.getMensaRepo().getMensaMap().get(mensaId)
				.getName());
	}

	public void refreshList() {
		Intent refresh = new Intent(this, MealsActivity.class);
		refresh.putExtra("mensaId", mensaId);
		refresh.putExtra("mensaTitle", tv.getText().toString());
		refresh.putExtra("currentPosition", mPager.getCurrentItem());
		startActivity(refresh);
		this.finish();
	}

	public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return MealsFragment.create(mensaId, position);
		}

		@Override
		public int getCount() {
			return 14;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mensa_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.action_about):
			Intent intent = new Intent(this, MensaInfoActivity.class);
			intent.putExtra("mensaId", mensaId);
			startActivity(intent);
			return true;
		case (R.id.meals_refresh):
			if (NetworkUtil.getConnectivityStatus(this) > 0) {
				ParserTask refreshTask = new ParserTask(this, true, false, false);
				refreshTask.execute(new Integer[] { mensaId });
				return true;
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.parse_no_internet_single),
						Toast.LENGTH_LONG).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
