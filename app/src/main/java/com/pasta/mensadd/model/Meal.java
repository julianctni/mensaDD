/*
 * Meal.java
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

public class Meal {
	private String name;
	private String price;
	private String detailLink;
	private Boolean vegetarian = false;
	private Boolean vegan = false;
	private Boolean pork = false;
	private Boolean beef = false;
	private Boolean garlik = false;
	private Boolean alcohol = false;

	public Meal(String name, String price, ArrayList<String> notes, String detailLink) {
		this.name = name;
		this.price = price;
		this.checkContent(notes);
		this.detailLink = detailLink;
	}

	public Meal(String name, String price, int veg, int vegan, int pork,
			int beef, int garlik, int alcohol, String detailLink) {
		this.name = name;
		this.price = price;
		this.vegetarian = isTrue(veg);
		this.vegan = isTrue(vegan);
		this.pork = isTrue(pork);
		this.beef = isTrue(beef);
		this.garlik = isTrue(garlik);
		this.alcohol = isTrue(alcohol);
		this.detailLink = detailLink;
	}

	public Boolean isTrue(int i) {
		if (i == 1)
			return true;
		else
			return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Boolean isVegetarian() {
		return vegetarian;
	}

	public Boolean isVegan() {
		return vegan;
	}

	public Boolean hasPork() {
		return pork;
	}

	public Boolean hasBeef() {
		return beef;
	}

	public Boolean hasGarlik() {
		return garlik;
	}

	public Boolean hasAlcohol() {
		return alcohol;
	}
	
	

	public String getDetailLink() {
		return detailLink;
	}

	public void setDetailLink(String detailLink) {
		this.detailLink = detailLink;
	}

	public String getStringNotes() {
		String result = "";
		if (this.isVegan())
			result += "\nMenü ist vegan";
		else if (this.isVegetarian())
			result += "\nMenü ist vegetarisch";
		if (this.hasAlcohol())
			result += "\nMenü enthält Alkohol";
		if (this.hasGarlik())
			result += "\nMenü enthält Knoblauch";
		if (this.hasPork())
			result += "\nMenü enthält Schweinefleisch";
		if (this.hasBeef())
			result += "\nMenü enthält Rindfleisch";
		return result;
	}

	public void checkContent(ArrayList<String> notes) {
		for (String temp : notes) {
			if (temp.equals("Menü enthält kein Fleisch")
					|| temp.equals("Menü ist vegan")) {
				this.vegetarian = true;
				if (temp.equals("Menü ist vegan"))
					this.vegan = true;
				return;
			} else if (temp.equals("Menü enthält Schweinefleisch"))
				this.pork = true;

			else if (temp.equals("Menü enthält Rindfleisch"))
				this.beef = true;
			if (temp.equals("Menü enthält Knoblauch"))
				this.garlik = true;
			if (temp.equals("Menü enthält Alkohol"))
				this.alcohol = true;
		}
	}

}
