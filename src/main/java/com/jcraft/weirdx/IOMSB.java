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
import java.util.*;

final class IOMSB extends IO {
  int readShort() throws java.io.IOException{
    if((inrest)<2){ read(2); }
    inrest-=2;
    int s=inbuffer[instart++]&0xff;
    s=((s<<8)&0xffff)|(inbuffer[instart++]&0xff);
    return s;
  }
  int readInt() throws java.io.IOException{
    if((inrest)<4){ read(4); }
    inrest-=4;
    int i=inbuffer[instart++]&0xff;
    i=((i<<8)&0xffff)|(inbuffer[instart++]&0xff);
    i=((i<<8)&0xffffff)|(inbuffer[instart++]&0xff);
    i=(i<<8)|(inbuffer[instart++]&0xff);
    return i;
  }
  void writeShort(int val) throws java.io.IOException{
    if((outbuffer.length-outindex)<2){ flush(); }
    outbuffer[outindex++]=(byte)((val >> 8)&0xff);
    outbuffer[outindex++]=(byte)(val&0xff);
  }
  void writeInt(int val) throws java.io.IOException{
    if((outbuffer.length-outindex)<4){ flush(); }
    outbuffer[outindex++]=(byte)((val >> 24) & 0xff);
    outbuffer[outindex++]=(byte)((val >> 16) & 0xff);
    outbuffer[outindex++]=(byte)((val >> 8) & 0xff);
    outbuffer[outindex++]=(byte)((val) & 0xff);
  }
}
