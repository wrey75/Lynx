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
import java.awt.*;
import java.io.*;

public abstract class Drawable extends Resource{
  public static final byte DRAWABLE_WINDOW=0;
  public static final byte DRAWABLE_PIXMAP=1;
  public static final byte UNDRAWABLE_WINDOW=2;
  public static final byte DRAWABLE_BUFFER=3;

  public byte type;
  public int clss;
  public byte depth;
  public byte bitsPerPixel;
  public int x, y, width, height;

  public Screen	screen;
  public long serialNumber;
  private static int serial=0;
  private static final int max= 1<<28;

  int gctime=0;
  int gmask=0;
  GC currentGC=null;

  Drawable(int id, int type){
    super(id, type);
    if(++serial > max) serial=1;
    serialNumber=serial;
  }

  abstract Graphics getGraphics();
  abstract void restoreClip();

  abstract Graphics getGraphics(GC gc, int mask);   
  abstract Colormap getColormap();
  abstract Image getImage(GC gc, int x, int y, int width, int height);
}
