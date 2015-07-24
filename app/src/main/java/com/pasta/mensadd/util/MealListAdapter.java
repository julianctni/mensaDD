/*
 * MealListAdapter.java
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

package com.pasta.mensadd.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.model.Meal;

public class MealListAdapter extends BaseAdapter {

	private ArrayList<Meal> mensen;
	private LayoutInflater mInflater;

	public MealListAdapter(Context context, ArrayList<Meal> results) {
		mensen = results;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mensen.size();
	}

	public Object getItem(int position) {
		return mensen.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.meal_listitem_layout, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView
					.findViewById(R.id.listitem_name);
			holder.txtPrices = (TextView) convertView
					.findViewById(R.id.listitem_prices);
			holder.txtNotes = (TextView) convertView
					.findViewById(R.id.listitem_notes);
			holder.veganIcon = (TextView) convertView
					.findViewById(R.id.vegan_icon);
			holder.porkIcon = (ImageView) convertView
					.findViewById(R.id.pork_icon);
			holder.beefIcon = (ImageView) convertView
					.findViewById(R.id.beef_icon);
			holder.vegIcon = (ImageView) convertView
					.findViewById(R.id.veg_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LinearLayout ll1 = (LinearLayout) convertView
				.findViewById(R.id.listitem);
		Meal meal = mensen.get(position);
		holder.porkIcon.setVisibility(View.GONE);
		holder.beefIcon.setVisibility(View.GONE);
		holder.veganIcon.setVisibility(View.GONE);
		holder.vegIcon.setVisibility(View.GONE);
		if (meal.isVegetarian()) {
			ll1.setBackgroundColor(Color.parseColor("#cc5aa02c"));
			holder.vegIcon.setVisibility(View.VISIBLE);
			if (meal.isVegan())
				holder.veganIcon.setVisibility(View.VISIBLE);
		} else {
			ll1.setBackgroundColor(Color.parseColor("#cc01aaad"));
			if (meal.hasBeef())
				holder.beefIcon.setVisibility(View.VISIBLE);

			if (meal.hasPork())
				holder.porkIcon.setVisibility(View.VISIBLE);
		}
		holder.txtName.setText(mensen.get(position).getName());
		holder.txtPrices.setText(mensen.get(position).getPrice());
		holder.txtNotes.setText(mensen.get(position).getStringNotes());
		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtPrices;
		TextView txtNotes;
		TextView veganIcon;
		ImageView beefIcon;
		ImageView porkIcon;
		ImageView vegIcon;
	}

}
