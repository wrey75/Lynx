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

final class Keymodifier_gb extends Keymodifier{
  private byte[] _keys={
    (byte)16, (byte)151,     // shift
    (byte)20, (byte)0x00,    // lock
    (byte)17, (byte)11,      // control
    (byte)18, (byte)0,       // mod1
    (byte)148, (byte)0x00,   // mod2,
    (byte)0x00, (byte)0x00,  // mod3,
    (byte)0x00, (byte)0x00,  // mod4
    (byte)145, (byte)0x00    // mod5
  };

  Keymodifier_gb(){
    width=2;
    keys=_keys;  
  }
}
