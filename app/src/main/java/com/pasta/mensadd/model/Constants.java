/*
 * Constants.java
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


public class Constants {

	public static int NEUEMENSA = 1;
	public static int ALTEMENSA = 2;
	public static int SIEDEPUNKT = 3;
	public static int UBOOT = 4;
	public static int MENSOLOGIE = 5;
	public static int REICHENBACH = 6;
	public static int WUEINS = 7;
	public static int TELLERRANDT = 8;
	public static int STIMMGABEL = 9;
	public static int PALUCCA = 10;
	public static int BRUEHL = 11;
	public static int JOHANNSTADT = 12;
	public static int SPORT = 13;
	public static int KREUZ = 14;
	
	public static int NOINTERNET = 10;
	public static int MENSAREFRESHED = 20;
	
	public static String MEALTABLE = "meals";
	
	public static boolean checkSchaltJahr(int year) {
		if (year % 400 == 0)
			return true;
		else if (year % 100 == 0)
			return false;
		else if (year % 4 == 0)
			return true;
		else
			return false;
	}
	
	

}
