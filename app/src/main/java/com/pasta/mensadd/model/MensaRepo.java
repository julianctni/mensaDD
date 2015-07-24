/*
 * MensaRepo.java
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

import android.util.SparseArray;

public class MensaRepo {
	private SparseArray<Mensa> mensaMap = new SparseArray<Mensa>();
	private static MensaRepo mensaRepo;

	public static MensaRepo getMensaRepo() {
		if (mensaRepo == null) {
			mensaRepo = new MensaRepo();
			return mensaRepo;
		} else
			return mensaRepo;
	}

	public SparseArray<Mensa> getMensaMap() {
		return mensaMap;
	}

	public void setMensaMap(SparseArray<Mensa> mensaMap) {
		this.mensaMap = mensaMap;
	}

}
