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

final class Keymodifier_gen extends Keymodifier{
  private byte[] _keys={
    0x32, 0x3e, // shift
    0x42, 0x00, // 0x25, 0x00, // lock
    0x25, 0x6d, // 0x42, 0x6d, // control
    0x40, 0x71, // mod1
    0x4d, 0x00, // mod2,
    0x00, 0x00, // mod3,
    0x00, 0x00, // mod4
    0x4e, 0x00  // mod5
  };
  Keymodifier_gen(){
    width=2;
    keys=_keys;  
  }
}
