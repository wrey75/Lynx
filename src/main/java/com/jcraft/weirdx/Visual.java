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

final class Visual {
  Depth depth;
  int id;
  int clss;
  private int bitsPerRGB;
  int colormapEntries;
  int nplanes;
  private int redMask;
  private int greenMask;
  private int blueMask;

  Depth getDepth() { return depth; }
  void setDepth(Depth d) { 
    this.depth=d; 
    nplanes=d.depth;
  }

  int getVisualClass() { return clss; }
  void setVisualClass(int v) { clss=v; }

  int getBitsPerRGB() { return bitsPerRGB; }
  void setBitsPerRGB(int b) { bitsPerRGB=b; }

  int getColormapEntries() { return colormapEntries; }
  void setColormapEntries(int c) { colormapEntries=c; }

  int getRedMask() { return redMask; }
  void setRedMask(int r) { redMask=r; }

  int getGreenMask() { return greenMask; }
  void setGreenMask(int g) { greenMask=g; }
  int getBlueMask() { return blueMask; }
  void setBlueMask(int b) { blueMask=b; }

  Visual(int id, int clss, int bitsPerRGB, int colormapEntries,
	 int redMask, int greenMask, int blueMask){
    this.id=id;
    this.clss=clss;
    this.bitsPerRGB=bitsPerRGB;
    this.colormapEntries=colormapEntries;
    this.redMask=redMask;
    this.greenMask=greenMask;
    this.blueMask=blueMask;
  }

  void writeByte(IO out) throws java.io.IOException{
    out.writeInt(id);
    out.writeByte(clss);
    out.writeByte(bitsPerRGB);
    out.writeShort(colormapEntries);
    out.writeInt(redMask);
    out.writeInt(greenMask);
    out.writeInt(blueMask);
    out.writePad(4);
  }
  static Visual[] getStaticGray1(Client c){
    Visual[] v=new Visual[1];
    v[0]=new Visual(Resource.fakeClientId(c), 0, 1, 2, 0, 0, 0);
    return v;
  }

  static Visual[] getStaticGray8(Client c){
    Visual[] v=new Visual[1];
    v[0]=new Visual(Resource.fakeClientId(c), 0, 8, 256, 0, 0, 0);
    return v;
  }

  static Visual[] getPseudoColor8(Client c){
    Vector vec=new Vector();
    vec.addElement(new Visual(Resource.fakeClientId(c), 3, 6, 256, 0, 0, 0));
    Visual[] v=new Visual[vec.size()];
    for(int i=0; i<vec.size(); i++){
      v[i]=(Visual)vec.elementAt(i);
    }
    vec.removeAllElements();
    return v;
  }
  static Visual[] getTrueColor16(Client c){
    Vector vec=new Vector();
    vec.addElement(
      new Visual(Resource.fakeClientId(c), 4, 6, 64,
		 0xf800, 0x7e0, 0x1f));
    Visual[] v=new Visual[vec.size()];
    for(int i=0; i<vec.size(); i++){
      v[i]=(Visual)vec.elementAt(i);
    }
    vec.removeAllElements();
    return v;
  }
}
