/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - managing atoms.
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

final class Atom {
  private static int lastAtom=0;

  private static String[] idTable={
    null,
    "PRIMARY", 
    "SECONDARY", 
    "ARC", 
    "ATOM", 
    "BITMAP",
    "CARDINAL", 
    "COLORMAP", 
    "CURSOR", 
    "CUT_BUFFER0", 
    "CUT_BUFFER1",
    "CUT_BUFFER2", 
    "CUT_BUFFER3", 
    "CUT_BUFFER4",
    "CUT_BUFFER5", 
    "CUT_BUFFER6", 
    "CUT_BUFFER7",
    "DRAWABLE", 
    "FONT", 
    "INTEGER",
    "PIXMAP", 
    "POINT", 
    "RECTANGLE", 
    "RESOURCE_MANAGER", 
    "RGB_COLOR_MAP", 
    "RGB_BEST_MAP", 
    "RGB_BLUE_MAP",
    "RGB_DEFAULT_MAP", 
    "RGB_GRAY_MAP", 
    "RGB_GREEN_MAP", 
    "RGB_RED_MAP",
    "STRING", 
    "VISUALID", 
    "WINDOW",
    "WM_COMMAND", 
    "WM_HINTS", 
    "WM_CLIENT_MACHINE",
    "WM_ICON_NAME", 
    "WM_ICON_SIZE", 
    "WM_NAME",
    "WM_NORMAL_HINTS", 
    "WM_SIZE_HINTS", 
    "WM_ZOOM_HINTS",
    "MIN_SPACE", 
    "NORM_SPACE", 
    "MAX_SPACE", 
    "END_SPACE",
    "SUPERSC.LPT_X", 
    "SUPERSC.LPT_Y",
    "SUBSC.LPT_X", 
    "SUBSC.LPT_Y",
    "UNDERLINE_POSITION", 
    "UNDERLINE_THICKNESS",
    "STRIKEOUT_ASCENT", 
    "STRIKEOUT_DESCENT", 
    "ITALIC_ANGLE",
    "X_HEIGHT", 
    "QUAD_WIDTH", 
    "WEIGHT", 
    "POINT_SIZE",
    "RESOLUTION", 
    "COPYRIGHT", 
    "NOTICE", 
    "FONT_NAME",
    "FAMILY_NAME", 
    "FULL_NAME", 
    "CAP_HEIGHT", 
    "WM_CLASS", 
    "WM_TRANSIENT_FOR"
  };

  private static final int lastPredefined=idTable.length-1;
  static{
    String[] foo=new String[100];
    System.arraycopy(idTable, 0, foo, 0, idTable.length);
    lastAtom=idTable.length-1;
    idTable=foo;
  }

  private static final Hashtable nameTable=nameTablePredefined();

  private static Hashtable nameTablePredefined(){
    Hashtable hash=new Hashtable();
    for (int i=1;; i++){
      if(idTable[i]!=null)
	hash.put(idTable[i], new Integer(i));
      else
	break;
    }
    return hash;
  }

  static int make(byte[] name, int start, int length, boolean makeit){
    return make(new String(name, start, length), makeit);
  }
  static int make(byte[] name, boolean makeit){
    return make(new String(name), makeit);
  }

  static synchronized int make(String name, boolean makeit){
    int id;
    if((id=find(name))!=0) return id;
    if(!makeit) return 0;
    lastAtom++;
    //System.out.println("length="+idTable.length+", lastAtom="+lastAtom);
    if(idTable.length<=lastAtom){
      String foo[];
      foo=new String[idTable.length*2];
      System.arraycopy(idTable, 0, foo, 0, idTable.length);
      idTable=foo;
    }
    idTable[lastAtom]=name;
    nameTable.put(name, new Integer(lastAtom));
    return lastAtom;
  }

  static boolean valid(int id){ return id!=0 && id <=lastAtom; }

  static String find(int id) {
    if (id==0) return null;
    if (id<=lastAtom) return idTable[id];
    return null;
  }

  static int find(String name) {
    if (name==null) return 0;
    try{
      Integer i=(Integer)nameTable.get(name);
      return i.intValue();
    }
    catch(Exception e){
    }
    return 0;
  }

  static int find(byte[] name) {
    if (name==null) return 0;
    try{
      Integer i=(Integer)nameTable.get(new String(name));
      return i.intValue();
    }
    catch(Exception e){
    }
    return 0;
  }

  static void reqInternAtom(Client c) throws IOException{
    int foo;
    int atom;
    int exp;
    int n=0;
    IO io=c.client;

    exp=c.data;
    n=io.readShort();
    io.readPad(2);
    c.length-=2;
    if(n<=0){
      c.errorReason=11; // BadAlloc
      return;
    }
    byte[] bb=c.bbuffer;
    io.readByte(bb, 0, n);
    io.readPad((-n) & 3);

    c.length=0;
    int atm=Atom.make(bb, 0, n, exp==0);

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeInt(atm);
      io.writePad(20);
      io.flush();
    }
  }

  static void reqGetAtomName(Client c) throws IOException{
    int foo;
    int atom;
    int n;
    IO io=c.client;
    atom=io.readInt();
    if(atom<0){ atom&=0x7fffffff;}
    if(!valid(atom)){         // Error
      synchronized(io){
        io.writeByte(0); 
        io.writeByte(5);
        io.writeShort(c.seq);
        io.writeInt(atom);
        io.writeShort(0);
        io.writeByte(17);
        io.writePad(21);
	io.flush();
      }
    }
    else{
      String s=find(atom);
      synchronized(io){
        io.writeByte(1);
        io.writePad(1);
        io.writeShort(c.seq);
        io.writeInt(((s.length()+3)/4));
        io.writeShort(s.length());
        io.writePad(22);
        io.writeByte(s.getBytes());
        if(s.length()>0) io.writePad((-s.length())&3);
	io.flush();
      }
    }
  }
}
