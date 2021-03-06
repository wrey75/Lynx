/* WeirdX - Guess
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
import java.net.Socket;

/**
 * Socket for display.
 * 
 * @author JCraft.
 *
 */
public interface DisplaySocket {

	/**
	 * Initialize a display.
	 * 
	 * @param display the display number.
	 * @throws IOException if an I/O error occured at
	 * 		during the initialization.
	 */
	public void init(int display) throws IOException;

	public Socket accept() throws IOException;

	/**
	 * Close the display.
	 * 
	 * @throws IOException if an I/O error occured.
	 */
	public void close() throws IOException;
}
