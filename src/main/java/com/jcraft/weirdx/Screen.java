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



final class Screen {
  static Screen[] screen;
  Window root;
  int rootId;
  Colormap defaultColormap;
  int defaultColormapId;
  int white;
  int black;
  int currentInputMasks;
  short width;
  short height;
  short width_mm;
  short height_mm;
  int minInstalledMaps;
  int maxInstalledMaps;
  int rootVisual;
  int backingStores;
  int saveUnders;
  byte rootDepth;  

  int windowmode=WeirdX.InBrowser;

  Depth depth[];
  Visual visual[];

  Pixmap[] pixmaps;

//  private static final int visibilityOffset=17;
//  private static final int visibility=(3<<visibilityOffset);
//  private static final int VisibilityUnobscured=0;
//  private static final int VisibilityPartiallyObscured=1;
//  private static final int VisibilityFullyObscured=2;
//  private static final int VisibilityNotViewable=3;

  static Screen[] init(){
    screen=new Screen[1];
    return screen;
  }

  int getRootId() { return rootId; }
  void setRootId(int r) { this.rootId=r; }
  byte getRootDepth() { return rootDepth; }
  void setRootDepth(byte r) { this.rootDepth=r; }

  int getRootVisual() { return rootVisual; }
  void setRootVisual(int r) { this.rootVisual=r; }

  Window getRoot() { return root; }
  void setRoot(Window root) { this.root=root; }

  Depth[] getDepth() { return depth; }
  void setDepth(Depth[] d) { depth=d; }

  int depths() { return depth.length; }

  Colormap defaultColormap() { return defaultColormap; }
  int defaultColormapId() { return defaultColormapId; }

  int getWhite() { return white; }
  void setWhite(int w) { white=w; }

  int getBlack() { return black; }
  void setBlack(int b) { black=b; }

  int getCurrentInputMasks() { return currentInputMasks; }
  void setCurrentInputMasks(int c) { currentInputMasks=c; }

  int getMinInstalledMaps() { return minInstalledMaps; }
  void setMinInstalledMaps(int m) { minInstalledMaps=m; }

  int getMaxInstalledMaps() { return maxInstalledMaps; }
  void setMaxInstalledMaps(int m) { maxInstalledMaps=m; }
    
  int getBackingStores() { return backingStores; }
  void setBackingStores(int b) { backingStores=b; }

  int getSaveUnders() { return saveUnders; }
  void setSaveUnders(int s) { saveUnders=s; }

  Screen(int rootId,
	 int dColormap,
	 int white,
	 int black,
	 int cInputMasks,
	 int width,
	 int height,
	 int width_mm,
	 int height_mm,
	 int minInstalledMaps,
	 int maxInstalledMaps,
	 int rootVisual,
	 int bStores,
	 int sUnders,
	 int rootDepth,
	 Depth[] depth) {
    this.rootId=rootId;
    this.defaultColormapId=dColormap;
    this.white=white;
    this.black=black;
    this.currentInputMasks=cInputMasks;
    this.width=(short)width;
    this.height=(short)height;
    this.width_mm=(short)width_mm;
    this.height_mm=(short)height_mm;
    this.minInstalledMaps=minInstalledMaps;
    this.maxInstalledMaps=maxInstalledMaps;
    this.rootVisual=rootVisual;
    this.backingStores=bStores;
    this.saveUnders=sUnders;
    this.rootDepth=(byte)rootDepth;
    this.depth=depth;
  }

  void writeByte(IO out) throws java.io.IOException{
    out.writeInt(rootId);
    out.writeInt(defaultColormapId);
    out.writeInt(white);
    out.writeInt(black);
    currentInputMasks=root.eventMask | root.getOtherEventMask();
    out.writeInt(currentInputMasks);
    out.writeShort(width);
    out.writeShort(height);
    out.writeShort(width_mm);
    out.writeShort(height_mm);
    out.writeShort(minInstalledMaps);
    out.writeShort(maxInstalledMaps);
    out.writeInt(rootVisual);
    out.writeByte(backingStores);
    out.writeByte(saveUnders);
    out.writeByte(rootDepth);
    out.writeByte(depth.length);
    for (int i=0; i < depth.length; i++) {
      depth[i].writeByte(out);
    }
  }

  int getLength(){
    int i=10;
    if(depth!=null){
      for(int j=0; j<depth.length; j++){
	i+=depth[j].getLength();
      }
    }
    return i;
  }

  static void resetScreen(int scrn){
    Client.closeDownAll();
    Colormap cmap=screen[0].defaultColormap;
    if((cmap.visual.clss & 1/*DynamicClass*/)!=0){
      cmap.flags|=4/*Colormap.BeingCreated*/;
      cmap.freeAll();
      try{
	cmap.allocColor(Client.clients[0], 0, 0, 0);
	cmap.allocColor(Client.clients[0], 255, 255, 255);
      }
      catch(Exception e){}
      cmap.mkIcm();
      cmap.flags&=~4/*Colormap.BeingCreated*/;
    }
    screen[0].root.initAttr();
  }
}
