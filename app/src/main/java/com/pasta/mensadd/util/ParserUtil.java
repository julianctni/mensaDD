/*
 * ParserUtil.java
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pasta.mensadd.model.Constants;
import com.pasta.mensadd.model.Meal;

public class ParserUtil {
	Context context;
	private String downloadUrl = "http://www.studentenwerk-dresden.de/mensen/speiseplan/";
	Calendar cal;


	public ParserUtil(Context context) {
		this.context = context;
		cal = Calendar.getInstance(Locale.GERMANY);
	}

	public Elements parseHtml(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (Exception e) {
			return null;
		}
		return doc.select("table.speiseplan tbody");
	}

	public HashMap<Integer, ArrayList<Meal>> createMeals(String mensaUrl, int mensaId, int week) {
		Elements elements = new Elements();
		int dayNr = cal.get(Calendar.DAY_OF_YEAR);
		
		// setting dayNr to monday
		if (cal.get(Calendar.DAY_OF_WEEK) > 2)
			dayNr = dayNr - cal.get(Calendar.DAY_OF_WEEK) + 2;
		else if (cal.get(Calendar.DAY_OF_WEEK) == 1)
			dayNr = dayNr - 6;
		int thisWeek = cal.get(Calendar.WEEK_OF_YEAR);
		
		if (week == thisWeek) {
			elements = parseHtml((this.downloadUrl + mensaUrl + ".html"));
		} else {
			Log.i("PARSING","parsing week "+week);
			dayNr += 7;         
			if (dayNr != 366 && Constants.checkSchaltJahr(cal.get(Calendar.YEAR)))
				dayNr = dayNr%366;
			else if (dayNr != 365 && !Constants.checkSchaltJahr(cal.get(Calendar.YEAR)))
				dayNr = dayNr%365;
			
			elements = parseHtml((this.downloadUrl + mensaUrl + "-w1.html"));
		}
		HashMap<Integer, ArrayList<Meal>> foodMap = new HashMap<Integer, ArrayList<Meal>>();
		if (elements == null)
			return foodMap;

		DatabaseManager dbMan = new DatabaseManager(context);
		SQLiteDatabase db = dbMan.getWritableDatabase();
		for (Element day : elements) {
			ArrayList<Meal> tempList = new ArrayList<Meal>();
			for (Element food : day.children()) {
				Meal meal;
				if (food.children().size() == 3) {
					if (mensaId == 1
							&& getFoodName(food).startsWith("Beilagentheke"))
						Log.i("Parsing", "Delete Beilagentheke!");
					else {
						meal = new Meal(getFoodName(food), getFoodPrice(food),
								getFoodNotes(food),getDetailLink(food));
						tempList.add(meal);
						ContentValues values = new ContentValues();
						Log.i("Parsing", "Storing meal of week "+week);
						values.put("dayOfYear", dayNr);
						values.put("week", week);
						values.put("name", meal.getName());
						values.put("price", meal.getPrice());
						values.put("mensa", mensaId);
						values.put("detailLink", meal.getDetailLink());
						values.put("vegan", meal.isVegan());
						values.put("vegetarian", meal.isVegetarian());
						values.put("pork", meal.hasPork());
						values.put("beef", meal.hasBeef());
						values.put("alcohol", meal.hasAlcohol());
						values.put("garlik", meal.hasGarlik());
						db.insert(Constants.MEALTABLE, null, values);
					}
				}
			}
			Log.i("PARSING","parsing day nr "+dayNr);
			foodMap.put(dayNr, tempList);
			dayNr++;
			if (dayNr != 366 && Constants.checkSchaltJahr(cal.get(Calendar.YEAR)))
				dayNr = dayNr%366;
			else if (dayNr != 365 && !Constants.checkSchaltJahr(cal.get(Calendar.YEAR)))
				dayNr = dayNr%365;
		}
		db.close();
		return foodMap;
	}

	public String getFoodName(Element element) {
		return element.getElementsByClass("text").first().text();
	}

	public String getFoodPrice(Element element) {
		if (element.getElementsByClass("preise").first().text().length() == 1)
			return "keine Preisangaben vorhanden";
		else
			return element.getElementsByClass("preise").first().text();
	}
	
	public String getDetailLink(Element element) {
		return element.getElementsByClass("text").first().getElementsByTag("a").first().attr("href");
	}
	
	public ArrayList<String> getFoodNotes(Element element) {
		Elements temp = element.getElementsByClass("stoffe").first()
				.getElementsByTag("img");
		ArrayList<String> notes = new ArrayList<String>();
		if (temp.isEmpty())
			return notes;
		else {
			for (Element x : temp) {
				notes.add(x.attr("title"));
			}
			return notes;
		}
	}
}
