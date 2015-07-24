/*
 * MainActivity.java
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

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.guthaben.DesfireException;
import com.pasta.mensadd.guthaben.DesfireProtocol;
import com.pasta.mensadd.guthaben.Readers;
import com.pasta.mensadd.guthaben.ValueData;
import com.pasta.mensadd.model.Constants;
import com.pasta.mensadd.model.MensaRepo;
import com.pasta.mensadd.util.NetworkUtil;
import com.pasta.mensadd.util.ParserTask;

public class MainActivity extends ActionBarActivity {

	SharedPreferences sharedPref;
	int version = 0;
	Dialog dialog;

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private static final String TAG = MainActivity.class.getName();
	private ValueData value;
	private boolean hasNFC = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setLogo(R.drawable.banner);
		toolbar.setTitle("");
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		if (sharedPref.getBoolean("first_start", true)) {
			sharedPref.edit().putBoolean("first_start", false).commit();
			this.showAboutOverlay(true);
		}

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter != null)
			hasNFC = true;

		ParserTask startTask = new ParserTask(this, false, true, false);
		startTask.execute(this.checkPreferences());
	}

	public void onPause() {
		super.onPause();
		if (mAdapter != null)
			mAdapter.disableForegroundDispatch(this);
	}

	public Integer[] checkPreferences() {
		ArrayList<Integer> paramsForTask = new ArrayList<Integer>();
		if (sharedPref.getBoolean("pref_key_neue_mensa", false))
			paramsForTask.add(Constants.NEUEMENSA);
		if (sharedPref.getBoolean("pref_key_alte_mensa", false))
			paramsForTask.add(Constants.ALTEMENSA);
		if (sharedPref.getBoolean("pref_key_siedepunkt", false))
			paramsForTask.add(Constants.SIEDEPUNKT);
		if (sharedPref.getBoolean("pref_key_uboot", false))
			paramsForTask.add(Constants.UBOOT);
		if (sharedPref.getBoolean("pref_key_mensologie", false))
			paramsForTask.add(Constants.MENSOLOGIE);
		if (sharedPref.getBoolean("pref_key_reichenbach", false))
			paramsForTask.add(Constants.REICHENBACH);
		if (sharedPref.getBoolean("pref_key_wueins", false))
			paramsForTask.add(Constants.WUEINS);
		if (sharedPref.getBoolean("pref_key_tellerrandt", false))
			paramsForTask.add(Constants.TELLERRANDT);
		if (sharedPref.getBoolean("pref_key_stimmgabel", false))
			paramsForTask.add(Constants.STIMMGABEL);
		if (sharedPref.getBoolean("pref_key_palucca", false))
			paramsForTask.add(Constants.PALUCCA);
		if (sharedPref.getBoolean("pref_key_bruehl", false))
			paramsForTask.add(Constants.BRUEHL);
		if (sharedPref.getBoolean("pref_key_johannstadt", false))
			paramsForTask.add(Constants.JOHANNSTADT);
		if (sharedPref.getBoolean("pref_key_sport", false))
			paramsForTask.add(Constants.SPORT);
		if (sharedPref.getBoolean("pref_key_kreuz", false))
			paramsForTask.add(Constants.KREUZ);

		Integer[] params = new Integer[paramsForTask.size()];
		for (int i = 0; i < paramsForTask.size(); i++) {
			params[i] = paramsForTask.get(i);
		}
		return params;
	}

	public void shareThisApp() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				"mensaDD - die neue Mensa App für Dresden!\n- goo.gl/8cXEhx");
		startActivity(Intent.createChooser(shareIntent, "Teilen"));
	}

	public void contactDeveloper() {
		Intent email = new Intent(Intent.ACTION_SEND);
		String mailSubject = "mensaDD Feedback [V:";
		try {
			mailSubject += getApplicationContext().getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		mailSubject += "/API:" + Build.VERSION.SDK_INT + "]";
		email.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "turbojulian@ymail.com" });
		email.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		email.putExtra(Intent.EXTRA_TEXT, "");
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Mail"));
	}

	public void showAboutOverlay(boolean firstStart) {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.overlay_about);
		dialog.setCanceledOnTouchOutside(true);
		dialog.findViewById(R.id.about_share).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						shareThisApp();
					}
				});
		dialog.findViewById(R.id.about_contact).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						contactDeveloper();
					}
				});
		if (firstStart) {
			TextView content = (TextView) dialog
					.findViewById(R.id.about_content);
			content.setText(R.string.welcome_text);
			TextView title = (TextView) dialog.findViewById(R.id.about_title);
			title.setText("Heyho!");
			content.setText(R.string.welcome_text);
			dialog.findViewById(R.id.about_buttons).setVisibility(View.GONE);
		} else {
			TextView sourcecode = (TextView) dialog
					.findViewById(R.id.about_sourcecode);
			sourcecode.setMovementMethod(LinkMovementMethod.getInstance());
			sourcecode.setText(Html
					.fromHtml(getString(R.string.about_sourcecode)));
			// sourcecode.setMovementMethod(LinkMovementMethod.getInstance());
		}
		dialog.show();
	}

	public void showGuthabenOverlay() {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.overlay_guthabencheck);
		dialog.setCanceledOnTouchOutside(true);
		if (mPendingIntent != null) {
			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					mPendingIntent.cancel();
				}
			});
		}
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.main_mensacard);
		if (!hasNFC) {
			item.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.main_settings):
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		case (R.id.main_about):
			showAboutOverlay(false);
			return true;

		case (R.id.main_mensacard):
			this.setUpGuthabenCheck();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* BEGINN GUTHABEN STUFF */
	@Override
	public void onNewIntent(Intent intent) {
		Log.i(TAG, "Foreground dispatch");
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.i(TAG, "Discovered tag with intent: " + intent);
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			loadCard(tag);
		}
	}

	private void toast(String text) {

		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_LONG);
		toast.show();
	}

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private String moneyStr(int i) {
		int euros = i / 100;
		int cents = i % 100;

		String centsStr = Integer.toString(cents);
		if (cents < 10)
			centsStr = "0" + centsStr;
		return euros + "," + centsStr + "\u20AC"; // Last one is euro sign
	}

	private void updateView(ValueData value) {
		String current = "aktuelles Guthaben: " + moneyStr(value.value);
		if (value.lastTransaction != null)
			current += "\n\nletzte Abbuchung: "
					+ moneyStr(value.lastTransaction);
		if (dialog.isShowing()) {
			TextView content = (TextView) dialog
					.findViewById(R.id.guthabencheck_content);
			content.setText(current);
		}
	}

	private void loadCard(Tag tag) {
		Log.i(TAG, "Loading tag");
		IsoDep tech = IsoDep.get(tag);
		try {
			tech.connect();
		} catch (IOException e) {
			// Tag was removed. We fail silently.
			e.printStackTrace();
			return;
		}
		try {
			DesfireProtocol desfireTag = new DesfireProtocol(tech);

			value = Readers.getInstance().readCard(desfireTag);
			if (value != null)
				updateView(value);
			else
				toast("card_not_supported");
			tech.close();
		} catch (DesfireException ex) {
			ex.printStackTrace();
			toast("communication_fail");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUpGuthabenCheck() {
		if (!mAdapter.isEnabled())
			this.alertNoNfc();
		else {
			this.showGuthabenOverlay();
			mPendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			IntentFilter tech = new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED);
			mFilters = new IntentFilter[] { tech, };
			mTechLists = new String[][] { new String[] {
					IsoDep.class.getName(), NfcA.class.getName() } };

			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					mTechLists);
		}
	}

	private void alertNoNfc() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.nfc_disabled)).setCancelable(
				false);
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			builder.setPositiveButton("Einstellungen",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent callNFCSettingIntent = new Intent(
									android.provider.Settings.ACTION_NFC_SETTINGS);
							startActivity(callNFCSettingIntent);
						}
					});
		}
		builder.setNegativeButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/*
	 * Image button trigger stuff!
	 */
	public void showMealsOfMensa(int mensaId) {
		if (MensaRepo.getMensaRepo().getMensaMap().get(mensaId).getmealMap()
				.isEmpty()) {
			if (NetworkUtil.getConnectivityStatus(this) > 0) {
				ParserTask startTask = new ParserTask(this, false, false, true);
				startTask.execute(new Integer[] { mensaId });
			} else {
				this.toast("Um Daten für diese Mensa herunterladen zu wollen, musst du mit dem Internet verbunden sein.");
			}
		} else {
			Intent intent = new Intent(this, MealsActivity.class);
			intent.putExtra("mensaId", mensaId);
			startActivity(intent);
		}
	}

	public void showAlteMensa(View v) {
		showMealsOfMensa(Constants.ALTEMENSA);
	}

	public void showNeueMensa(View v) {
		showMealsOfMensa(Constants.NEUEMENSA);
	}

	public void showSiedepunkt(View v) {
		showMealsOfMensa(Constants.SIEDEPUNKT);
	}

	public void showMensologie(View v) {
		showMealsOfMensa(Constants.MENSOLOGIE);
	}

	public void showJohannstadt(View v) {
		showMealsOfMensa(Constants.JOHANNSTADT);
	}

	public void showWuEins(View v) {
		showMealsOfMensa(Constants.WUEINS);
	}

	public void showBruehl(View v) {
		showMealsOfMensa(Constants.BRUEHL);
	}

	public void showUBoot(View v) {
		showMealsOfMensa(Constants.UBOOT);
	}

	public void showTellerrandt(View v) {
		showMealsOfMensa(Constants.TELLERRANDT);
	}

	public void showReichenbach(View v) {
		showMealsOfMensa(Constants.REICHENBACH);
	}

	public void showPalucca(View v) {
		showMealsOfMensa(Constants.PALUCCA);
	}

	public void showStimmgabel(View v) {
		showMealsOfMensa(Constants.STIMMGABEL);
	}

	public void showSport(View v) {
		showMealsOfMensa(Constants.SPORT);
	}

	public void showKreuz(View v) {
		showMealsOfMensa(Constants.KREUZ);
	}
}
