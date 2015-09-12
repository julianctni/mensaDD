/*
 * MealsFragment.java
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

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.MensaRepo;
import com.pasta.mensadd.util.MealListAdapter;
import com.pasta.mensadd.util.NetworkUtil;

public class MealsFragment extends ListFragment {

	Calendar cal = Calendar.getInstance();	
	ArrayList<Meal> meals = new ArrayList<Meal>();
	Date dateOfYear;
	int mId;
	SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd.MM.yyyy",
			Locale.GERMANY);
	Dialog dialog;
	Meal currentMeal;
	int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mId = getArguments().getInt("mensaId");
		position = getArguments().getInt("position");
		meals = this.getMeals(position, mId);
		this.setListAdapter(new MealListAdapter(getActivity()
				.getApplicationContext(), meals));
	}

	public ArrayList<Meal> getMeals(int position, int mId) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1)
            dayOfWeek = 6;
        else
            dayOfWeek -= 2;

        cal.add(Calendar.DAY_OF_YEAR, (position - dayOfWeek));

        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        try {
            return MensaRepo.getMensaRepo().getMensaMap().get(mId).getmealMap().get(dayOfYear);
        } catch (Exception e){
            return new ArrayList<Meal>();
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (View) inflater.inflate(R.layout.fragment_meals,
				container, false);
		TextView tv = (TextView) rootView.findViewById(R.id.dateOfDay);
		// ImageView arrowLeft = (ImageView)
		// rootView.findViewById(R.id.arrow_left);
		// ImageView arrowRight = (ImageView)
		// rootView.findViewById(R.id.arrow_right);
		this.setDate(tv);
		TextView v = (TextView) rootView.findViewById(R.id.no_food_today);
		if (meals.isEmpty())
			v.setVisibility(View.VISIBLE);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				currentMeal = (Meal) getListView().getAdapter().getItem(
						position);
				if (!currentMeal.getDetailLink().isEmpty())
					new MealDetailParser()
							.execute("http://www.studentenwerk-dresden.de/mensen/speiseplan/"
                                    + currentMeal.getDetailLink());
			}
		});
	}

	public static MealsFragment create(int mensaId, int position) {
		MealsFragment mf = new MealsFragment();
		Bundle b = new Bundle(2);
		b.putInt("mensaId", mensaId);
		b.putInt("position", position);
		mf.setArguments(b);
		return mf;
	}

	public void setDate(TextView tv) {
		// TextView tv = (TextView)getActivity().findViewById(R.id.dateOfDay);
		tv.setText(sdf.format(cal.getTime()));
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 0, 0, "Teilen");
	}

	public void shareThisMeal() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, currentMeal.getName()
				+ "\n#"
				+ MensaRepo.getMensaRepo().getMensaMap().get(mId).getName()
						.replaceAll("\\s+", "") + " #Hunger #mensaDD");
		startActivity(Intent.createChooser(shareIntent, "Teilen"));
	}

	private class MealDetailParser extends AsyncTask<String, Integer, String> {
		TextView allergene;
		ProgressBar progress;
		ImageView foodImage;
		RelativeLayout content;
		String pictureUrl = "";
		Bitmap foodPicture;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.setContentView(R.layout.overlay_meal_detail);
			dialog.setCanceledOnTouchOutside(true);
			dialog.findViewById(R.id.share_icon).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							shareThisMeal();
						}
					});
			allergene = (TextView) dialog
					.findViewById(R.id.meal_detail_allergene);
			progress = (ProgressBar) dialog.findViewById(R.id.progress_spinner);
			foodImage = (ImageView) dialog
					.findViewById(R.id.meal_detail_picture);
			content = (RelativeLayout) dialog
					.findViewById(R.id.mensa_detail_content);
			dialog.show();

		}

		protected String doInBackground(String... args) {
			if (NetworkUtil.getConnectivityStatus(getActivity()) == 0) {
				foodImage.setVisibility(View.GONE);
				return getString(R.string.parse_no_internet_meal_detail);
			}
				
			Elements elements = parseHtml(args[0]);
			if (elements == null)
				return "error";
			String allergene = "";
			Elements details = elements.first()
					.getElementById("speiseplandetailsrechts")
					.getElementsByTag("ul");
			Elements listItems;
			for (Element detail : details) {
				listItems = detail.getElementsByTag("li");
				for (Element e : listItems) {
					allergene += "• " + e.text() + "\n";
				}
			}

			try {
				pictureUrl = elements.first().getElementById("essenfoto")
						.attr("href");
			} catch (NullPointerException e) {
			}

			if (!pictureUrl.isEmpty()) {
				try {
					foodPicture = BitmapFactory
							.decodeStream((InputStream) new URL("http:"
									+ pictureUrl).getContent());
					
					Display display = getActivity().getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
					if(width > 720){
						int newWidth = (int)(foodPicture.getWidth()*1.5);
						int newHeight = (int)(foodPicture.getHeight()*1.5);
						Bitmap b = Bitmap.createScaledBitmap(foodPicture, newWidth, newHeight, false);
						foodImage.setImageBitmap(b);
					} else if (width < 400) {
						int newWidth = (int)(foodPicture.getWidth()*0.5);
						int newHeight = (int)(foodPicture.getHeight()*0.5);
						Bitmap b = Bitmap.createScaledBitmap(foodPicture, newWidth, newHeight, false);
						foodImage.setImageBitmap(b);
					} else
						foodImage.setImageBitmap(foodPicture);
				} catch (Exception e) {
				}
			}

			return allergene;
		}

		protected void onPostExecute(String text) {
			if (text.equals("error"))
				Toast.makeText(getActivity(), getString(R.string.parse_error_mealdetail), Toast.LENGTH_LONG).show();
			progress.setVisibility(View.GONE);

			allergene.setText(text);
			content.setVisibility(View.VISIBLE);
		}
	}

	public Elements parseHtml(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
            return doc.select("#speiseplandetails");
		} catch (Exception e) {
            return null;
		}
	}

}
