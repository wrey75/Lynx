/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Guess.
 *
 * Copyright (C) 1999-2004 JCraft, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.jcraft.weirdx;

import java.io.IOException;

public final class Format {
	static Format[] format = null;
	byte depth;
	byte bpp;
	byte scanLinePad;

	Format(byte d, byte b, byte s) {
		depth = d;
		bpp = b;
		scanLinePad = s;
	}

	void writeByte(InputOutput out) throws IOException {
		out.writeByte(depth);
		out.writeByte(bpp);
		out.writeByte(scanLinePad);
		out.writePad(5);
	}
}
