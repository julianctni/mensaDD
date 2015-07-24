/*
 * ParserTask.java
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

import java.io.IOException;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.activity.MainActivity;
import com.pasta.mensadd.activity.MealsActivity;
import com.pasta.mensadd.model.Constants;
import com.pasta.mensadd.model.Mensa;
import com.pasta.mensadd.model.MensaRepo;

public class ParserTask extends AsyncTask<Integer, Integer, Integer> {
	ProgressDialog dialog;
	private MainActivity mainContext;
	private MealsActivity mealsContext;
	boolean refresh;
	boolean initialize;
	boolean downloadSingleMensa;
	int thisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	int nextWeek;
	SharedPreferences pref;
	ParserUtil parser;
	DatabaseManager dbMan;

	public ParserTask(MainActivity context, boolean refresh,
			boolean initialize, boolean downloadSingleMensa) {
		if (thisWeek >= 52)
			nextWeek = 1;
		else
			nextWeek = thisWeek+1;
		this.refresh = refresh;
		this.initialize = initialize;
		this.downloadSingleMensa = downloadSingleMensa;
		this.mainContext = context;
		pref = PreferenceManager.getDefaultSharedPreferences(mainContext);
		parser = new ParserUtil(mainContext);
		dbMan = new DatabaseManager(mainContext);
		dialog = new ProgressDialog(mainContext);
	}

	public ParserTask(MealsActivity context, boolean refresh,
			boolean initialize, boolean downloadSingleMensa) {
		if (thisWeek >= 52)
			nextWeek = 1;
		else
			nextWeek = thisWeek+1;
		this.refresh = refresh;
		this.initialize = initialize;
		this.downloadSingleMensa = downloadSingleMensa;
		mealsContext = context;
		pref = PreferenceManager.getDefaultSharedPreferences(mealsContext);
		parser = new ParserUtil(mealsContext);
		dbMan = new DatabaseManager(mealsContext);
		dialog = new ProgressDialog(mealsContext);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (refresh)
			dialog.setMessage("Speiseplan für die aktuelle Woche wird aktualisiert.");
		else if (initialize)
			dialog.setMessage("Speisepläne der Lieblingsmensen werden geladen.");
		else if (downloadSingleMensa)
			dialog.setMessage("Speiseplan für Mensa wird heruntergeladen.");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Abbrechen",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						cancel(true);
					}
				});
		dialog.show();
	}

	@Override
	protected Integer doInBackground(Integer... ints) {
		if (ints.length == 0) {
			cancel(true);
			dialog.dismiss();
		}
		try {
			dbMan.getDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbMan.createNewTable(Constants.MEALTABLE);

		if (refresh)
			return this.refreshData(ints);

		if (initialize) {
			dbMan.setUpMensaRepo();
			return this.initializeData(ints);
		}

		if (downloadSingleMensa) {
			return this.downloadMealsForMensa(ints);
		}
		return 1;
	}

	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if (result == Constants.MENSAREFRESHED) {
			Toast.makeText(mealsContext,
					R.string.parse_downloaded_single_mealtable,
					Toast.LENGTH_SHORT).show();
			mealsContext.refreshList();
		} else if (result >= 100)
			mainContext.showMealsOfMensa(result - 100);
		else if (result == Constants.NOINTERNET)
			Toast.makeText(mainContext, R.string.parse_no_internet_general,
					Toast.LENGTH_SHORT).show();
	}

	public void downloadNewMeals(Integer... ints) {
		
		for (int id = 1; id <= 14; id++) {
			if (pref.getBoolean("mensa" + id + "Once", false)) {
				dbMan.deleteMealsFromMensa(id);
				pref.edit().remove("mensa" + id + "Once").commit();
			}
		}
		dbMan.getMeals();

		for (int i : ints) {
			Mensa mensa = MensaRepo.getMensaRepo().getMensaMap().get(i);
			if (!(pref.getInt("nextWeek", 0) == thisWeek)
					|| mensa.getmealMap().isEmpty())
				mensa.getmealMap().putAll(
						parser.createMeals(mensa.getUrlEnding(), mensa.getId(),
								thisWeek));
			mensa.getmealMap().putAll(
					parser.createMeals(mensa.getUrlEnding(), mensa.getId(),
							nextWeek));
		}
		pref.edit().putInt("nextWeek", nextWeek).commit();
	}

	public int downloadMealsForMensa(Integer... ints) {
		for (int i : ints) {
			Mensa mensa = MensaRepo.getMensaRepo().getMensaMap().get(i);
			mensa.setMealMap(parser.createMeals(mensa.getUrlEnding(),
					mensa.getId(), thisWeek));
			mensa.getmealMap().putAll(
					parser.createMeals(mensa.getUrlEnding(), mensa.getId(),
							nextWeek));
			pref.edit().putBoolean("mensa" + i + "Once", true).commit();
		}

		return ints[0] + 100;
	}

	public int refreshData(Integer... ints) {
		for (int i : ints) {
			dbMan.deleteMealsFromMensa(i);
			Mensa mensa = MensaRepo.getMensaRepo().getMensaMap().get(i);
			mensa.setMealMap(parser.createMeals(mensa.getUrlEnding(),
					mensa.getId(), thisWeek));
			mensa.getmealMap().putAll(
					parser.createMeals(mensa.getUrlEnding(), mensa.getId(),
							nextWeek));
		}
		return Constants.MENSAREFRESHED;
	}

	public int initializeData(Integer... ints) {
		dbMan.deleteOldMeals(thisWeek);
		if (pref.getInt("nextWeek", thisWeek) == thisWeek) {
			dbMan.deleteOldMeals(thisWeek);
			if (NetworkUtil.getConnectivityStatus(mainContext) < 1) {
				return Constants.NOINTERNET;
			} else
				this.downloadNewMeals(ints);
		} else {
			dbMan.getMeals();
		}
		return 0;
	}
}
