/*
 * Mensa.java
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

package com.pasta.mensadd.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Mensa {
	int id;
	String name;
	String times;
	String address;
	String info;
	String urlEnding;
	Double longitude;
	Double latitude;
	String contact;
	HashMap<Integer, ArrayList<Meal>> mealMap = new HashMap<Integer, ArrayList<Meal>>();

	public Mensa(int id, String name, String times, String address,
			String contact, Double longitude, Double latitude, String info,
			String urlEnding) {
		this.id = id;
		this.address = address;
		this.times = times;
		this.name = name;
		this.info = info;
		this.contact = contact;
		this.latitude = latitude;
		this.longitude = longitude;
		this.urlEnding = urlEnding;
	}

	public Mensa(int id, String name, String urlEnding) {
		this.id = id;
		this.name = name;
		this.urlEnding = urlEnding;
	}

	public String getUrlEnding() {
		return urlEnding;
	}

	public HashMap<Integer, ArrayList<Meal>> getmealMap() {
		return mealMap;
	}

	public void setMealMap(HashMap<Integer, ArrayList<Meal>> meal) {
		this.mealMap = meal;
	}

	public int getId() {
		return id;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

}
