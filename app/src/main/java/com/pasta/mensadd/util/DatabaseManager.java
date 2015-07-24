/*
 * DatabaseManager.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.pasta.mensadd.model.Constants;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.Mensa;
import com.pasta.mensadd.model.MensaRepo;

public class DatabaseManager extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mensadd.db";
	private static String DATABASE_PATH = "";
	private static final int DATABASE_VERSION = 1;
	private Context myContext;
	private SQLiteDatabase db;


	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext = context;
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			DATABASE_PATH = myContext.getDatabasePath(
					DatabaseManager.DATABASE_NAME).toString();
			Log.i("database", "API >= 17");
		} else {
			DATABASE_PATH = "/data/data/" + context.getPackageName() + "/"
					+ "databases/" + DATABASE_NAME;
			Log.i("database", "API < 17");
		}
	}

	/*
	 * Checks if database has already been imported from assets. If not, it will
	 * call "coppyDataBase()".
	 */
	public void getDataBase() throws IOException {
		boolean dbExists = checkDataBase();
		if (dbExists && !this.checkNewVersion())
			this.openDataBase();
		else {
			this.getWritableDatabase();
			try {
				this.copyDataBase();
				this.openDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}
	
	public boolean checkNewVersion(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(myContext);
		int version = 0;
		try {
			version = this.myContext.getPackageManager().getPackageInfo(myContext.getPackageName(),0).versionCode;
		} catch (NameNotFoundException e1) {}
		
		if (version > PreferenceManager.getDefaultSharedPreferences(myContext).getInt("prev_version_db", 1)) {
			pref.edit().putInt("prev_version_db", version).commit();
			Log.i("db_version_check","new version!");
			return true;
		} else {
			pref.edit().putInt("prev_version_db", version).commit();
			Log.i("db_version_check","not a new version!");
			return false;
		}
	}

	public void copyDataBase() throws IOException {
		Log.i("database","copying new database from assets");
		myContext.deleteDatabase(DATABASE_NAME);
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
		String outFileName = DATABASE_PATH;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/*
	 * Opens the database.
	 */
	public void openDataBase() throws SQLException {
		String path = DATABASE_PATH;
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/*
	 * Checks, if there is a database file in the app's database folder.
	 */
	private boolean checkDataBase() {
		File dbFile = new File(DATABASE_PATH);
		return dbFile.exists();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	public void setUpMensaRepo() {
		Log.i("SQLite", "importing Mensa");
		String[] projection = { "mensaId", "mensaInfo", "mensaName",
				"urlEnding", "mensaAddress", "mensaTimes", "mensaContact",
				"longitude", "latitude" };
		Cursor c = db.query("mensen", projection, null, null, null, null, null);
		SparseArray<Mensa> mensaArray = new SparseArray<Mensa>();
		while (c.moveToNext()) {
			int mensaId = c.getInt(c.getColumnIndex("mensaId"));
			String mensaName = c.getString(c.getColumnIndex("mensaName"));
			String mensaUrl = c.getString(c.getColumnIndex("urlEnding"));
			String mensaInfo = c.getString(c.getColumnIndex("mensaInfo"));
			String mensaAddress = c.getString(c.getColumnIndex("mensaAddress"));
			String mensaTimes = c.getString(c.getColumnIndex("mensaTimes"));
			String mensaContact = c.getString(c.getColumnIndex("mensaContact"));
			Double longitude = Double.parseDouble(c.getString(c
					.getColumnIndex("longitude")));
			Double latitude = Double.parseDouble(c.getString(c
					.getColumnIndex("latitude")));
			mensaArray.put(mensaId, new Mensa(mensaId, mensaName, mensaTimes,
					mensaAddress, mensaContact, longitude, latitude, mensaInfo,
					mensaUrl));
		}
		MensaRepo.getMensaRepo().setMensaMap(mensaArray);
	}

	public void getMeals() {
		Log.i("database","getting stored meals");
		String[] projection = { "dayOfYear", "name", "price","detailLink", "vegan",
				"vegetarian", "pork", "beef", "alcohol", "garlik", "mensa"};
		Cursor c;
		try {
			c = db.query(Constants.MEALTABLE, projection, null, null, null, null, null);
		} catch (SQLiteException e) {
			return;
		}

		while (c.moveToNext()) {
			int mensa = c.getInt(c.getColumnIndex("mensa"));
			int vegetarian = c.getInt(c.getColumnIndex("vegetarian"));
			int vegan = c.getInt(c.getColumnIndex("vegan"));
			int alcohol = c.getInt(c.getColumnIndex("alcohol"));
			int pork = c.getInt(c.getColumnIndex("pork"));
			int beef = c.getInt(c.getColumnIndex("beef"));
			int garlik = c.getInt(c.getColumnIndex("garlik"));
			String name = c.getString(c.getColumnIndex("name"));
			String price = c.getString(c.getColumnIndex("price"));
			int dayNr = c.getInt(c.getColumnIndex("dayOfYear"));
			String detailLink = c.getString(c.getColumnIndex("detailLink"));
			Meal meal = new Meal(name, price, vegetarian, vegan, pork, beef,
					garlik, alcohol, detailLink);
			if (MensaRepo.getMensaRepo().getMensaMap().get(mensa).getmealMap()
					.containsKey(dayNr))
				MensaRepo.getMensaRepo().getMensaMap().get(mensa).getmealMap()
						.get(dayNr).add(meal);
			else {
				ArrayList<Meal> newList = new ArrayList<Meal>();
				newList.add(meal);
				MensaRepo.getMensaRepo().getMensaMap().get(mensa).getmealMap()
						.put(dayNr, newList);
			}
		}
	}

	
	public boolean isDetailLinkMissing(String tableName) {
		String[] projection = {"detailLink"};
		Cursor c;
		try {
			c = db.query(tableName, projection, null, null, null, null, null);
		} catch (SQLiteException e) {
			return true;
		}
		return false;
	}
	public void deleteTable(String name) {
		db.execSQL("DROP TABLE IF EXISTS " + name);
	}
	
	public void deleteOldMeals(int thisWeek) {
		Log.i("Database","Deleting meals older than week "+thisWeek);
		if (thisWeek < 10)
			db.execSQL("DELETE FROM " + Constants.MEALTABLE + " WHERE (week <= 52 AND week >= "+(thisWeek+2)+")");
		db.execSQL("DELETE FROM " + Constants.MEALTABLE + " WHERE week < "+thisWeek+";");
	}
	
	public void createNewTable(String name) {
		String createTable = "CREATE TABLE IF NOT EXISTS " + name + " ("
				+ "mealId" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "dayOfYear" + " INTEGER," + "week" + " INTEGER,"+ "mensa" + " INTEGER," + "name"
				+ " TEXT," + "price" + " TEXT," + "detailLink" + " TEXT, " +"vegetarian" + " INTEGER,"
				+ "vegan" + " INTEGER," + "pork" + " INTEGER," + "beef"
				+ " INTEGER," + "garlik" + " INTEGER," + "alcohol" + " INTEGER"
				+ ")";
		db.execSQL(createTable);
	}

	public void deleteMealsFromMensa(int mId) {
		db.delete(Constants.MEALTABLE, "mensa=" + mId, null);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}