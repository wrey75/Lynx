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
import java.io.*;

final class Keymodifier_de extends Keymodifier{
  private byte[] _keys={
	(byte)0x32, (byte)0x3e,     // shift
    (byte)0x42, (byte)0,       // lock
    (byte)0x25, (byte)0x6d,      // control
    (byte)0x40, (byte)0,       // mod1
    (byte)0x4d,(byte)0,       // mod2
    (byte)0x71, (byte)0,    // mod3
    (byte)0, (byte)0,  // mod4
    (byte)0x4e, (byte)0   // mod5
  };

  Keymodifier_de(){
    width=2;
    keys=_keys;  
  }
}
