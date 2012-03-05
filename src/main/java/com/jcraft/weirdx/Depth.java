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

final class Depth {
  int depth;  
  Visual visual[];
  int getDepth() { return depth; }
  void setDepth(int d) { depth=d; }
  Visual[] getVisual() { return visual; }
  void setVisual(Visual[] v) { visual=v; }
  int visuals() { return visual.length; }
  Depth(int depth, Visual[] visual) {
    this.depth=depth;
    this.visual=visual;
    for(int i=0; i<visual.length; i++){
      visual[i].setDepth(this);
    }
  }

  void writeByte(IO out) throws java.io.IOException{
    out.writeByte(depth);
    out.writePad(1);
    if(visual!=null) out.writeShort(visual.length);
    else out.writeShort(0);
    out.writePad(4);
    if(visual!=null){
      for (int i=0; i < visual.length; i++){
	visual[i].writeByte(out);
      }
    }
  }
  int getLength(){
    return 2+(visual!=null ? 6*visual.length : 0);
  }
}
  


