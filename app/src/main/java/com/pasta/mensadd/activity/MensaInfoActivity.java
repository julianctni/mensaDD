/*
 * MensaInfoActivity.java
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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.model.Mensa;
import com.pasta.mensadd.model.MensaRepo;

public class MensaInfoActivity extends ActionBarActivity {

	int mensaId;
	Mensa mensa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mensa_info);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setLogo(R.drawable.banner);
		toolbar.setTitle("");
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
		mensaId = getIntent().getIntExtra("mensaId", 0);
		mensa = MensaRepo.getMensaRepo().getMensaMap().get(mensaId);

		this.refreshContent();
	}

	public void refreshContent() {
		TextView mensaName = (TextView) findViewById(R.id.mensa_info_header);
		TextView where = (TextView) findViewById(R.id.mensa_where_content);
		TextView who = (TextView) findViewById(R.id.mensa_who_content);
		TextView when = (TextView) findViewById(R.id.mensa_when_content);
		TextView what = (TextView) findViewById(R.id.mensa_what_content);
		mensaName.setText(mensa.getName());
		where.setText(mensa.getAddress());
		when.setText(mensa.getTimes());
		what.setText(mensa.getInfo());
		who.setText(mensa.getContact());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mensa_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.showMap) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("geo:0,0?q=" + mensa.getLatitude() + ","
							+ mensa.getLongitude()));
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
