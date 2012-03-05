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

import java.awt.*;
import java.awt.image.*;

class Colormap extends Resource{
  static private final int REDMAP=0;
  static private final int GREENMAP=1;
  static private final int BLUEMAP=2;
  static private final int PSEUDOMAP=3;
  static private final int AllocPrivate=-1;
  static private final int AllocTemporary=-2;
  static private final int DynamicClass=1;

  static private final int AllocNone=0;
  static private final int AllocAll=1;

  static private final int IsDefault=1;
  static private final int AllAllocated=2;
  static private final int BeingCreated=4;

  static private final int StaticGray=0;
  static private final int GrayScale=1;
  static private final int StaticColor=2;
  static private final int PseudoColor=3;
  static private final int TrueColor=4;
  static private final int DirectColor=5;

  static private final int DoRed=(1<<0);
  static private final int DoGreen=(1<<1);
  static private final int DoBlue=(1<<2);

  static IndexColorModel bwicm=null;
  static{
    byte[] foo={(byte)0, (byte)0xff};
    bwicm=new IndexColorModel(1, 2, foo, foo, foo);
  }

  Color[] colors;
  byte[] r, g, b;
  ColorModel cm;
  long icmtime;

  Visual visual;
  Screen screen;

  int[][] pixels;
  int freeRed;
  Entry[] entries;

  int flags;

  static Colormap[] installed=null;
  static Colormap defaultColormap=null;

  Colormap(int id, Screen s, Visual v, int alloc, Client client) {
    super(id, RT_COLORMAP);

    add(this);

    this.screen=s;
    this.visual=v;

    if(v.depth.depth==16) return;

    int n=v.getColormapEntries();

    r=new byte[n];
    g=new byte[n];
    b=new byte[n];

    for(int i=0; i< n; i++){ r[i]=(byte)255; g[i]=(byte)255; b[i]=(byte)255; }

    colors=new Color[n];

    pixels=new int[Client.MAXCLIENTS][];
    entries=new Entry[n];
    for(int i=0; i < entries.length; i++){
      entries[i]=new LocalEntry();
    }
    freeRed=n;
    flags=0;

    if(id==screen.defaultColormapId){
      flags|=IsDefault;
    }

    if(alloc==AllocAll){
      if ((visual.clss & DynamicClass)!=0){
	flags |=AllAllocated;
      }
      for (int i=0; i<entries.length; i++){
	entries[i].refcnt=AllocPrivate;
      }
      freeRed=0;
      pixels[client.index]=new int[entries.length];
      for(int i=0; i < pixels[client.index].length; i++){
	pixels[client.index][i]=i;
      }
    }
  }

  static Colormap getColormap(int id, Screen s, Visual v, int alloc, Client client){
    if(v.depth.depth==16){
      return new Colormap16(id, s, v, alloc, client);
    }
    return new Colormap(id, s, v, alloc, client);
  }

  private int alloc(){
    int i=0;
    for( ; i<colors.length;i++){
      if(colors[i]==null){
	colors[i]=Color.black;
	break;
      }
    }
    if(i==colors.length)i=2;
    return i;
  }

  private void alloc(String s){
    int i=0;
    for( ; i<colors.length;i++){
      if(colors[i]==null) break;
    }
    if(i==colors.length)return; // ???
    alloc(i, (Color)rgbTable.get(s));
    mkIcm();
  }

  private void alloc(int i, Color color){
    colors[i]=color;
    r[i]=(byte)color.getRed();
    g[i]=(byte)color.getGreen();
    b[i]=(byte)color.getBlue();
  }

  private void free(int i){
    if(i<colors.length){
      colors[i]=null;
      r[i]=(byte)255;
      g[i]=(byte)255;
      b[i]=(byte)255;
    }
    else{
      //System.out.println("free error: "+i);
    }
  }

  void freeAll(){
    for(int i=0; i<entries.length; i++){
      entries[i].refcnt=0;
    }
    for(int i=0; i<colors.length;i++){
      free(i);
    }
  }

  int rgb2pixel(int rgb){
    byte red  =(byte)((rgb >> 16) & 0xff);
    byte green=(byte)((rgb >>  8) & 0xff);
    byte blue =(byte)((rgb      ) & 0xff);
    for(int i=0; i<r.length; i++){
      if(r[i]==red && g[i]==green && b[i]==blue){
	return i;
      }
    }
    //System.out.println("not found: ("+red+","+green+","+blue+")");

    int distance=1000;
    int j=0, k=0, l=0;
    for(int i=0; i<r.length; i++){
      k=0;
      l=r[i]-red; if(l<0){l=l*-1;} k+=l;
      l=g[i]-green; if(l<0){l=l*-1;} k+=l;
      l=b[i]-blue; if(l<0){l=l*-1;} k+=l;
      if(k<distance){j=i; distance=k;}
    }
    return j;
  }

  static void reqCreateColormap(Client c) throws IOException{
    int foo, n, mid, alloc;
    IO io=c.client;
    alloc=c.data;
    if(alloc!=AllocNone && alloc!=AllocAll){
      c.errorValue=alloc;
      c.errorReason=2; // BadValue;
    }
    mid=io.readInt();
    foo=io.readInt();
    Window w=c.lookupWindow(foo);
    if(c.errorReason==0 && w==null){
      c.errorValue=foo;
      c.errorReason=3; // BadWindow;
    }
    foo=io.readInt();
    c.length-=4;
    if(c.errorReason!=0){
      return;
    }

    Visual v=null;
    Visual []vv=w.screen.visual;
    for(int i=0; i<vv.length; i++){
      if(vv[i].id==foo){
	v=vv[i];
	break;
      }
    }
    if(v==null){
      c.errorValue=foo;
      c.errorReason=2; // BadValue!!
      return;
    }

    if((v.clss & DynamicClass)==0 && 
       alloc!=AllocNone && 
       c!=Client.clients[0]){
      c.errorReason=9; // BadMatch!!
      return;
    }
    getColormap(mid, w.screen, v, alloc, c);
  }

  static void reqStoreColors(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    n=c.length;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }
    if((cmap.visual.clss & DynamicClass)==0){
      c.errorReason=11; // BadAlloc
      return;
    }
    n=n-2;
    cmap.storeColors(c, n);  // ??
    if(c.errorReason!=0){
      return;
    }
    cmap.mkIcm();
  }

  static void reqUninstallColormap(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }
    if(installed.length==1) return;
    boolean notfound=true;
    for(int i=0; i<installed.length; i++){
      if(installed[i]==cmap){notfound=false; break;}
    }
    if(notfound) return;
    synchronized(installed){
      Colormap[] tmp=new Colormap[installed.length-1];
      tmp[0]=installed[0];
      for(int i=1; i<installed.length; i++){
	if(installed[i]==cmap){
	  for(int j=i+1; j<installed.length; j++){
	    tmp[j-1]=installed[j];
	  }
	  break;
	}
	tmp[i]=installed[i];
      }
      installed=tmp;
    }
  }
  static void reqInstallColormap(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }

    synchronized(installed){
      for(int i=0; i<installed.length; i++){
        if(installed[i]==cmap) return;
      }
      Colormap[] tmp=new Colormap[installed.length+1];
      System.arraycopy(installed, 0, tmp, 0, installed.length);
      tmp[installed.length]=cmap;
      installed=tmp;
    }
  }

  static void reqListInstalledColormaps(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=io.readInt();

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(1);
      io.writeShort(1);
      io.writePad(22);
      n=1;
      while(n!=0){
        io.writeInt(Screen.screen[0].defaultColormapId);
        n--;
      }
      io.flush();
    }
  }

  static void reqFreeColors(Client c) throws IOException{
    int n, foo;
    IO io=c.client;
    n=c.length;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }
    foo=io.readInt();
    c.length--;
    n=n-3;
    cmap.freeColors(c, n, foo);
  }
  static void reqAllocColorPlanes(Client c) throws IOException{
    int n, foo, cont;
    IO io=c.client;
    cont=c.data;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }

    n=io.readShort();
    int reds, greens, blues;
    reds=io.readShort();
    greens=io.readShort();
    blues=io.readShort();
    c.length=0;

    if (n==0){
      c.errorValue=0;
      c.errorReason=2; //BadValue
      return; 
    }
    cmap.allocColorPlanes(c, n, (cont==1?true:false), reds, greens, blues);
    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(n);
      io.writeShort(n);
      io.writePad(2);
      io.writeInt(0xff0000);
      io.writeInt(0xff00);
      io.writeInt(0xff);
      io.writePad(8);
      while(n!=0){
        io.writeInt(cmap.alloc());
        n--;
      }
      io.flush();
    }
  }
  static void reqAllocColorCells(Client c) throws IOException{
    int foo, n, m, cont;
    IO io=c.client;
    cont=c.data;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }
    n=(short)io.readShort();
    m=(short)io.readShort();
    c.length--;
    if (n==0){
      c.errorValue=0;
      c.errorReason=2; // BadValue
      return;
    }
    if (cont!=0 && cont !=1) {
      c.errorValue=cont;
      c.errorReason=2; // BadValue
      return;
    }

    int[] pix=null, msk=null;
    pix=new int[n];
    if(m!=0){
      msk=new int[m];
    }
    cmap.allocColorCells(c, n, m, (cont==1?true:false), pix, msk);
    if(c.errorReason!=0){
      return;
    }
    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(n+m);
      io.writeShort(n);
      io.writeShort(m);
      io.writePad(20);

      for(int i=0; i<pix.length; i++){
        io.writeInt(pix[i]);
      }

      if(m!=0){
        for(int i=0; i<msk.length; i++){
	  io.writeInt(msk[i]);
        }
      }
      io.flush();
    }
  }
  static void reqLookupColor(Client c) throws IOException{
    int foo, n;
    IO io=c.client;

    n=c.length;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }

    foo=io.readShort();
    io.readPad(2);
    io.readByte(c.bbuffer, 0, foo);
    io.readPad((-foo)&3);
    c.length=0;
    foo=chopspace(c.bbuffer, foo);
    if(foo==0){
      c.errorReason=2; // BadValue
      return;
    }
    String s=new String(c.bbuffer, 0, foo);
    Color color=(Color)rgbTable.get(s);
    if(color!=null){
      synchronized(io){
        io.writeByte(1);
        io.writePad(1);
        io.writeShort(c.seq);
        io.writeInt(0);

        foo=color.getRed();   io.writeShort(foo|(foo<<8));
        foo=color.getGreen(); io.writeShort(foo|(foo<<8));
        foo=color.getBlue();  io.writeShort(foo|(foo<<8));
        foo=color.getRed();   io.writeShort(foo|(foo<<8));
        foo=color.getGreen(); io.writeShort(foo|(foo<<8));
        foo=color.getBlue();  io.writeShort(foo|(foo<<8));
        io.writePad(12);
	io.flush();
        return;
      }
    }

    synchronized(io){
      io.writeByte((byte)0);
      io.writeByte((byte)15);
      io.writeShort(c.seq);
      io.writePad(4);
      io.writeShort(0);
      io.writeByte((byte)92);
      io.writePad(21);
      io.flush();
    }
  }

  static void reqAllocColor(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }

    int red=(short)io.readShort(); 
    int green=(short)io.readShort();
    int blue=(short)io.readShort();

    io.readPad(2);

    c.length-=2;
    Color color;
    if(((red>>8)&0xff)!=0 || ((green>>8)&0xff)!=0 || ((blue>>8)&0xff)!=0){
      red=(red>>8)&0xff;
      green=(green>>8)&0xff;
      blue=(blue>>8)&0xff;
    }
    else{
      red=red&0xff;
      green=green&0xff;
      blue=blue&0xff;
    }

    int pixel=cmap.allocColor(c, red, green, blue);
    if(c.errorReason!=0){
      return;
    }

    if(cmap.visual.depth.depth!=16){
      LocalEntry ent=(LocalEntry)cmap.entries[pixel];
      red=ent.r;
      green=ent.g;
      blue=ent.b;
      if(ent.refcnt==1){
        cmap.mkIcm();
      }
    }

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(0);

      io.writeShort(red|(red<<8));
      io.writeShort(green|(green<<8));
      io.writeShort(blue|(blue<<8));

      io.writePad(2);
      io.writeInt(pixel);
      io.writePad(12);
      io.flush();
    }
  }

  static void reqQueryColors(Client c) throws IOException{
    int n;
    int foo;
    int len;
    IO io=c.client;
    n=c.length;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }
    n=n-2;
    int[] nn=new int[n];
    for(int i=0;i<n;i++){
      nn[i]=io.readInt();
    }
    c.length=0;
    cmap.queryColors (c, nn);
  }

  static void reqStoreNamedColor(Client c) throws IOException{
    int foo, n, len, doc, pixel;
    IO io=c.client;
    doc=c.data;
    n=c.length;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }

    pixel=io.readInt();
    len=io.readShort();
    io.readPad(2);
    n=n-4;
    n*=4;
    n-=len;
    io.readByte(c.bbuffer, 0, len);
    io.readPad(n);
    c.length=0;
    len=chopspace(c.bbuffer, len);
    if(len==0){
      c.errorReason=2; // BadValue
      return;
    }

    String s=new String(c.bbuffer, 0, len);
    Color color=(Color)rgbTable.get(s);
    if(pixel==-1){
      System.out.println("?? pixel="+pixel);
      pixel=25;
    }

    int red=color.getRed(), green=color.getGreen(), blue=color.getBlue();
    Color cp=cmap.colors[pixel];
    if(cp!=null){
      if(doc!=0){
	if((doc&1)==0) red=cp.getRed();
	if((doc&2)==0) green=cp.getGreen();
	if((doc&4)==0) blue=cp.getBlue();
      }
    }
    c.errorReason=cmap.storeColor(c, pixel, red, green, blue, doc);
    if(c.errorReason==0){
      cmap.mkIcm();
    }
  }
  static void reqAllocNamedColor(Client c) throws IOException{
    int foo;
    int n;
    int len;
    IO io=c.client;
    n=c.length;
    foo=io.readInt();
    Colormap cmap=(Colormap)Resource.lookupIDByType(foo, Resource.RT_COLORMAP);
    c.length-=2;
    if(cmap==null){
      c.errorValue=foo;
      c.errorReason=12; // Colormap
      return;
    }
    len=io.readShort();
    io.readPad(2);
    n=n-3;
    n*=4;
    n-=len;
    io.readByte(c.bbuffer, 0, len);
    io.readPad(n);
    c.length=0;
    len=chopspace(c.bbuffer, len);
    if(len==0){
      c.errorReason=2; // BadValue
      return;
    }

    String s=new String(c.bbuffer, 0, len);
    Color color=(Color)rgbTable.get(s);
    if(color!=null){

      int red=color.getRed();
      int green=color.getGreen();
      int blue=color.getBlue();

      int i=cmap.allocColor(c, color.getRed(), color.getGreen(), color.getBlue());
      if(c.errorReason!=0){
	return;
      }

      if(cmap.visual.depth.depth!=16){ 
        LocalEntry ent=(LocalEntry)cmap.entries[i];
        red=ent.r;
        green=ent.g;
        blue=ent.b;
        if(ent.refcnt==1){
	  cmap.mkIcm();
        }
      }

      synchronized(io){
        io.writeByte(1);
        io.writePad(1);
        io.writeShort(c.seq);
        io.writeInt(0);
        io.writeInt(i);
        io.writeShort(red|(red<<8));
        io.writeShort(green|(green<<8));
        io.writeShort(blue|(blue<<8));
        io.writeShort(red|(red<<8));
        io.writeShort(green|(green<<8));
        io.writeShort(blue|(blue<<8));
        io.writePad(8);
	io.flush();
        return;
      }
    }

    synchronized(io){
      io.writeByte((byte)0);
      io.writeByte((byte)15);
      io.writeShort(c.seq);
      io.writePad(4);
      io.writeShort(0);
      io.writeByte((byte)85);
      io.writePad(21);
      io.flush();
    }
  }

  Color getColor(int pixel){
    if(pixel<0)pixel=255;
    if(colors.length<=pixel){
      pixel=1;
    }
    Color c=colors[pixel];
    if(c==null){
      c=colors[1];
    }
    return c;
  }

  static private int chopspace(byte[] buf, int len){
    for(int i=0; i<len;i++){
      if('A'<=buf[i] && buf[i]<='Z'){
	buf[i]=(byte)('a'+buf[i]-'A');
      }
      else if(buf[i]==' '){
	len--;
	for(int j=i; j<len;j++){
	  buf[j]=buf[j+1];
	}
	i--;
      }
    }
    return len;
  }

  static int isMapInstalled(int map, Window w){
    return 1;
  }

  static Hashtable rgbTable=null;

  static void init(){
    if(rgbTable==null){
      rgbTable=new Hashtable();
      RGBTXT.init(rgbTable);
    }
    if(installed==null){
      installed=new Colormap[1];
    }
  }

  void delete() throws IOException{
    for(int i=0; i<pixels.length; i++){
      if(pixels!=null) pixels[i]=null;
    }
    pixels=null;
    entries=null;
  }

  private void freeCell(int pix, int channel){
    Entry ent;
    switch (channel) {
      default:
      case PSEUDOMAP:
      case REDMAP:
        ent=entries[pix];
        break;
    }

    if (ent.refcnt > 1){
      ent.refcnt--;
    }
    else {
      ent.refcnt=0;
      switch (channel) {
        default:
        case PSEUDOMAP:
        case REDMAP:
	  freeRed++;
	  break;
      }
      free(pix);
    }
  }

  void freePixels(int client){
    int[] pixStart=pixels[client];
    if ((visual.clss & DynamicClass)!=0){
      if(pixStart!=null){
	for (int i=0; i<pixStart.length; i++){
	  freeCell(pixStart[i], REDMAP);
	}
      }
    }
    pixels[client]=null;
  }

  int allocColor (Client client, int red, int green, int blue)
    throws IOException{
    int pixR, pixG, pixB;
    int clss;
    int	npix;
    int pixel=0;

    if(visual.depth.depth==16) return 0;

    clss=visual.clss;
    if((flags & BeingCreated)!=0){
      clss |=DynamicClass;
    }
    switch (clss) {
    case StaticColor:
    case StaticGray:
        if(visual.getDepth().depth==1){
	  pixel=((red+green+blue)/3)==0 ? 0 : 1;
	}
	else{
	  pixel=(red+green+blue)/3;
	}
	break;
    case GrayScale:
    case PseudoColor:
	pixel=findColor(client, entries, 
		        red, green, blue, PSEUDOMAP /*, AllComp*/);
	if(client.errorReason!=0){
	  return pixel;
	}
        break;
    }

    if((pixels[client.index]!=null) &&
       (pixels[client.index].length==1) &&
       (((id & Client.CLIENTMASK) >> Client.CLIENTOFFSET)!=client.index) &&
       (flags & BeingCreated)==0){
      ClientColormap cc=new ClientColormap(Resource.fakeClientId(client),
					   client.index, id);
      add(cc);
    }
    return pixel;
  }
  
  private int findBestPixel(Entry[] entry, int red, int green, int blue, int channel){
    return 0;
  }

  private int findColor(Client client, Entry[] entries, 
		 int red, int green, int blue, int channel){
    Entry ent;
    boolean foundFree=false;
    int	pixel, Free=0;
    int	npix, count, nump;
    int pPixel=-1;

    for(int i=0; i<entries.length; i++){
      if ((ent=entries[i]).refcnt > 0){
	if (ent.eq(red, green, blue)){
	  if (client.index >=0){
	    ent.refcnt++;
	  }
	  pPixel=i;
	  break;
	}
      }
      else if (!foundFree && ent.refcnt==0){
	Free=i;
	foundFree=true;
	if((flags & BeingCreated)!=0)
	  break;
      }
    }

    if(pPixel==-1){
      if (!foundFree){
	client.errorReason=11; // BadAlloc
	return pPixel;
      }

      chkLocalEntry(Free);
      ent=entries[Free];
      ent.refcnt=(client.index >=0) ? 1 : AllocTemporary;

      switch (channel){
      case PSEUDOMAP:
	((LocalEntry)ent).r=red;
	((LocalEntry)ent).g=green;
	((LocalEntry)ent).b=blue;
	if (client.index >=0)
	  freeRed--;
	break;
      }
      pPixel=Free;	
      alloc(pPixel, new Color(red, green, blue));
    }

    if ((flags & BeingCreated)!=0 || client.index==-1){
      return pPixel;
    }

    switch (channel) {
      case PSEUDOMAP:
      case REDMAP:
        int[] foo=expand(pixels[client.index], 1);
	foo[foo.length-1]=pPixel;

	pixels[client.index]=foo;
	break;
    }
    return pPixel;
  }

  private void update(){
    if (visual.clss==DirectColor)   {
    }
    else {
      Entry ent;
      int red, green, blue;
      for (int i=0; i<entries.length; i++){
	ent=entries[i];
	if (ent.refcnt==0){
	  free(i);
	}
	if(ent.shared) {
	  red=((SharedEntry)ent).r.color;
	  green=((SharedEntry)ent).g.color;
	  blue=((SharedEntry)ent).b.color;
	}
	else {
	  red=((LocalEntry)ent).r;
	  green=((LocalEntry)ent).g;
	  blue=((LocalEntry)ent).b;
	}
	alloc(i, new Color(red, green, blue));
      }
    }
  }

  private void allocColorPlanes (Client c, int colors, boolean contig, 
				 int r, int g, int b) throws IOException{
    boolean ok;
    int	mask;
    int shift;
    IO io=c.client;

    if ((visual.clss & DynamicClass)==0){
      c.errorReason=11; // BadAlloc
      return;
    }

    ClientColormap cc=null;

    int oldcount=(pixels[c.index]==null ? 0 : pixels[c.index].length);
    int[] ppix=new int[colors];

    if (visual.clss==DirectColor){
    }
    if (oldcount==0 && 
        (((id & Client.CLIENTMASK) >> Client.CLIENTOFFSET)!=c.index)){
      cc=new ClientColormap(Resource.fakeClientId(c), c.index, id);
    }

    if (visual.clss==DirectColor) {
/*
        ok=allocDirect (c, pmap, colors, r, g, b, contig, ppix,
			  rmask, gmask, bmask);
*/
    }
    else {
      int[] pMask=new int[1];
      ok=allocPseudo (c, colors, r + g + b, contig, ppix, pMask);
      if(ok){
	int rmask, gmask, bmask;
	rmask=gmask=bmask=0;
	shift=1;
	for (int i=r; --i >=0; shift +=shift){
	  while ((pMask[0] & shift)==0){
	    shift +=shift;
	  }
	  rmask |=shift;
	}
	for (int i=g; --i >=0; shift +=shift) {
	  while ((pMask[0] & shift)==0){
	    shift +=shift;
	  }
	  gmask |=shift;
	}
	for (int i=b; --i >=0; shift +=shift)   {
	  while ((pMask[0] & shift)==0){
	    shift +=shift;
	  }
	  bmask |=shift;
	}
	if (!allocShared(c, ppix, colors, r, g, b,
			 rmask, gmask, bmask, oldcount)) {
	  freeColors(c, ppix, pMask[0]);
	  c.errorReason=11;
	}
	else{
          synchronized(io){
	    io.writeByte(1);
	    io.writePad(1);
	    io.writeShort(c.seq);
	    io.writeInt(colors);
	    io.writeShort(colors);
	    io.writePad(2);
	    io.writeInt(rmask);
	    io.writeInt(gmask);
	    io.writeInt(bmask);
	    io.writePad(8);
	    for(int i=0; i<pixels.length;i++){
	      io.writeInt(ppix[i]);
	    }
	    io.flush();
	  }
	}
      }
    }

    if (c.errorReason==0 && cc!=null){
      add(cc);
    }
  }

  private boolean allocShared (Client client, int[] ppix, 
		       int c, int r, int g, int b, 
		       int rmask, int gmask, int bmask, int start){

    int		npix, z, npixClientNew, npixShared;
    int         basemask, base, bits, common;
    SharedColor[] sharedList;
    SharedColor shared;
    int sindex;
    int[] pptr=pixels[client.index];
    int pix;
    npixClientNew=c << (r + g + b);
    npixShared=(c << r) + (c << g) + (c << b);
    sharedList=new SharedColor[npixShared];

    for (int i=0; i<sharedList.length; i++){
      sharedList[i]=new SharedColor();
    }
    sindex=0;
    for(int i=0; i<c; i++){
      basemask=~(gmask | bmask);
      common=ppix[i] & basemask;
      if (rmask!=0) {
	bits=0;
	base=lowbit (rmask);
	while(true){
	  shared=sharedList[sindex]; sindex++;
	  for (int j=0; j < npixClientNew; j++){
	    if(((pix=pptr[start+j])&basemask)==(common | bits)){
	      chkShareEntry(pix);
	      ((SharedEntry)entries[pix]).r=shared;
	    }
	  }
	  //GetNextBitsOrBreak(bits, rmask, base);
	  if(bits==rmask){	break; }
	  bits +=base;
	  while((bits & ~rmask)!=0){
	    bits +=(bits & ~rmask);	
	  }
	}
      }
      else {
	shared=sharedList[sindex]; sindex++;
	for (int j=0; j < npixClientNew; j++){
	  if(((pix=pptr[start+j])&basemask)==common){
	    chkShareEntry(pix);
	    ((SharedEntry)entries[pix]).r=shared;
	  }
	}
      }
      basemask=~(rmask | bmask);
      common=ppix[i] & basemask;
      if (gmask!=0) {
	bits=0;
	base=lowbit (gmask);
	while(true){
	  shared=sharedList[sindex]; sindex++;
	  for (int j=0; j < npixClientNew; j++){
	    if(((pix=pptr[start+j])&basemask)==(common | bits)){
	      chkShareEntry(pix);
	      ((SharedEntry)entries[pix]).g=shared;
	    }
	  }
	  //GetNextBitsOrBreak(bits, gmask, base);
	  if(bits==gmask){	break; }
	  bits +=base;
	  while((bits & ~gmask)!=0){
	    bits +=(bits & ~gmask);	
	  }
	}
      }
      else {
	shared=sharedList[sindex]; sindex++;
	for (int j=0; j < npixClientNew; j++){
	  if(((pix=pptr[start+j])&basemask)==common){
	    chkShareEntry(pix);
	    ((SharedEntry)entries[pix]).g=shared;
	  }
	}
      }
      basemask=~(rmask | gmask);
      common=ppix[i] & basemask;
      if (bmask!=0) {
	bits=0;
	base=lowbit (bmask);
	while(true){
	  shared=sharedList[sindex]; sindex++;
	  for (int j=0; j < npixClientNew; j++){
	    if(((pix=pptr[start+j])&basemask)==(common | bits)){
	      chkShareEntry(pix);
	      ((SharedEntry)entries[pix]).b=shared;
	    }
	  }
	  if(bits==bmask){	break; }
	  bits +=base;
	  while((bits & ~bmask)!=0){
	    bits +=(bits & ~bmask);	
	  }
	}
      }
      else {
	shared=sharedList[sindex]; sindex++;
	for (int j=0; j < npixClientNew; j++){
	  if(((pix=pptr[start+j])&basemask)==common){
	    chkShareEntry(pix);
	    ((SharedEntry)entries[pix]).b=shared;
	  }
	}
      }
    }
    return true;
  }

  private void chkShareEntry(int i){
    if(!(entries[i].shared)){
      entries[i]=new SharedEntry();
    }
  }

  private void chkLocalEntry(int i){
    if((entries[i].shared)){
      entries[i]=new LocalEntry();
    }
  }
  
  private void allocColorCells (Client client, int colors, int planes, boolean contig, 
			int[] ppix, int[] masks){
    int rmask, gmask, bmask, ppixFirst, r, g, b;
    int		n, clss;
    int		oldcount;
    ClientColormap cc=null;

    if ((visual.clss & DynamicClass)==0){
      client.errorReason=11; // BadAlloc
      return;
    }
    if (pixels[client.index]==null && 
	(((id & Client.CLIENTMASK) >> Client.CLIENTOFFSET)!=client.index)){
      cc=new ClientColormap(Resource.fakeClientId(client),
			    client.index, id);
    }

    if (visual.clss==DirectColor){

    }
    else {
      int[] pMask=new int[1];
      boolean ok=allocPseudo (client, colors, planes, contig, ppix, pMask);
      if(ok){
	int i=0;
	for (r=1, n=planes; --n >=0; r +=r) {
	  while((pMask[0] & r)==0){
	    r +=r;
	  }
	  masks[i]=r; i++;
	}
      }
    }
    if((client.errorReason==0) && cc!=null){
      add(cc);
    }
  }

  private boolean allocPseudo (Client client, int c, int r, boolean contig, 
			       int[] pDst, int[] pMask){
    int npix=c << r;
    if ((r >=32) || (npix > freeRed) || (npix < c)){
      client.errorReason=11; // BadAlloc
      return false;
    }

    int[] ppixTemp=new int[npix];
    boolean ok=allocCP(client, entries, c, r, contig, ppixTemp, pMask);
    if(ok){
      int[] foo=expand(pixels[client.index], npix);
      System.arraycopy(ppixTemp, 0, foo, foo.length-npix, npix);
      pixels[client.index]=foo;
      freeRed -=npix;
      System.arraycopy(ppixTemp, 0, pDst, 0, c);
    }
    return ok;
  }

  private boolean allocCP (Client client, Entry[] entries, 
			   int count, int planes, 
			   boolean contig, int[] pixels, int[] pmask){
    Entry ent;
    int	pixel, base, maxp, save;
    int	dplanes, found;
    int[] ppix;
    int	mask;
    int	finalmask;

    dplanes=visual.nplanes;

    if (planes==0){
      int j=0;
      for(int i=0; i<count; i++){
	while(j<entries.length && (ent=entries[j]).refcnt!=0) j++;
	if(j==entries.length){
	  j=-1;
	  break;
	}
      }
      if(j==-1){
	//System.out.println("not enough entries");
	client.errorReason=11; // BadAlloc
	return false;
      }
      j=0;
      for(int i=0; i<count; i++){
	while((ent=entries[j]).refcnt!=0) j++;
	chkLocalEntry(j);
	ent.refcnt=AllocPrivate;
	pixels[i]=j;
      }
      pmask[0]=0;
      return true;
    }
    else if (planes > dplanes){
      client.errorReason=11; // BadAlloc
      return false;
    }
    for(mask=(1 << planes) - 1, 
	base=1, 
	dplanes -=(planes - 1);
         --dplanes >=0;
         mask +=mask, base +=base){

      int ppixi=0;
      ppix=pixels;

      found=0;
      pixel=0;

      int entcount=visual.colormapEntries - mask;

      while (pixel < entcount) {
	save=pixel;
	maxp=pixel + mask + base;

	while (pixel !=maxp && entries[pixel].refcnt==0){
	  pixel +=base;
	}
	if (pixel==maxp) {
	  ppix[ppixi]=save; ppixi++;
	  found++;
	  if (found==count) {
	    while (--count >=0) {
	      pixel=pixels[count];
	      maxp=pixel + mask;
	      while (true) {

		chkLocalEntry(pixel);

		entries[pixel].refcnt=AllocPrivate;
		entries[pixel].shared=false;
		if (pixel==maxp){
		  break;
		}
		pixel +=base;
		ppix[ppixi]=pixel; ppixi++;
	      }
	    }
	    pmask[0]=mask;
	    return true;
	  }
	}
	pixel=save + 1;
	if ((pixel & mask)!=0)
	  pixel +=mask;
      }
    }

    dplanes=visual.nplanes;
    if (contig || planes==1 || dplanes < 3){
      return false;
    }

    finalmask=
        (((1<<(planes-1)) - 1) << (dplanes-planes+1)) +
	  (1<<(dplanes-planes-1));

    for (mask=(3 << (planes -1)) - 1; mask <=finalmask; mask++) {
        pixel=(mask >> 1) & 033333333333;
        pixel=mask - pixel - ((pixel >> 1) & 033333333333);
        if ((((pixel + (pixel >> 3)) & 030707070707) % 077) !=planes)
    	    continue;
        ppix=pixels;
	int ppixi=0;

        found=0;
        int entcount=visual.colormapEntries - mask;
        base=lowbit (mask);
        for (pixel=0; pixel < entcount; pixel++) {
	  if ((pixel & mask)!=0)
	    continue;
	  maxp=0;
	  while (entries[pixel + maxp].refcnt==0) {
	    if(maxp==mask){	break; }
	    maxp +=base;
	    while((maxp & ~mask)!=0){
	      maxp +=(maxp & ~mask);	
	    }
	  }
	  if ((maxp < mask) || (entries[pixel + mask].refcnt !=0)){
	    continue;
	  }
	  ppix[ppixi]=pixel; ppixi++;
	  found++;
	  if (found < count){
	    continue;
	  }
	  while (--count >=0) {
	    pixel=(pixels)[count];
	    maxp=0;
	    while (true) {
	      chkLocalEntry(pixel+maxp);
	      entries[pixel + maxp].refcnt=AllocPrivate;
	      entries[pixel + maxp].shared=false;
	      if(maxp==mask){	break; }
	      maxp +=base;
	      while((maxp & ~mask)!=0){
		maxp +=(maxp & ~mask);	
	      }
	      ppix[ppixi]=pixel + maxp; ppixi++;
	    }
	  }
	  pmask[0]=mask;
	  return true;
	}
    }
    return false;
  }

  private void freeColors (Client c, int rest, int mask) throws IOException{
    IO io=c.client;
    if ((flags & AllAllocated)!=0){
      c.errorReason=10; // BadAccess
      return;
    }
    if(rest==0){ return; }
    int[] foo=new int[rest];
    for(int i=0; i<foo.length; i++){
      foo[i]=io.readInt();
    }
    c.length=0;
    freeColors(c, foo, mask);
  }

  void freeColors (Client c, int[] foo, int mask) throws IOException{
    int	rmask=0;
    if ((visual.clss | DynamicClass)==DirectColor) {
      rmask=0; // ??
    }
    else {
      rmask=mask & ((1 << visual.nplanes) - 1);
      freeCo(c, PSEUDOMAP, foo, rmask);
    }
    if ((mask !=rmask) /*&& foo.length!=0*/) {
      c.errorValue=foo[0] | mask;
      c.errorReason=2; // BadValue;
    }
    return;
  }

  private void freeCo (Client client, int channel, int[] pixIn, int mask){
    int[] pixClient;
    int pixTest, npixClient, npix;
    int	bits, base, cmask, rgbbad;
    int[] pptr, cptr;
    int 	n, zapped;
    int		errVal=0;
    int		offset, numents;

    cmask=0xff;
    offset=0;
    pixClient=null;

    bits=0;
    zapped=0;
    base=lowbit (mask);
    switch(channel){
      default:
      case PSEUDOMAP:
	cmask=~0;
	rgbbad=0;
	offset=0;
	numents=visual.colormapEntries;
	pixClient=pixels[client.index];
	break;
    }

    while (true) {
      for (int i=0; i<pixIn.length; i++){
	pixTest=((pixIn[i] | bits) & cmask) >> offset;
	if ((pixTest >=numents) || (pixIn[i] & rgbbad)!=0) {
	  client.errorValue=pixIn[i] | bits;
	  errVal=2; //BadValue;
	  continue;
	}
	if(pixClient==null){
	  errVal=10; //BadAccess
	}
	else{
	  int j=0;
	  for (;j<pixClient.length && pixClient[j]!=pixTest; j++);
	  if (j!=pixClient.length){
	    if ((visual.clss & DynamicClass)!=0){
	      freeCell(pixTest, channel);
	      pixClient[j]=~0;
	      zapped++;
	    }
	    else{
	      errVal=10; //BadAccess
	    }
	  }
	}
      }

      if(bits==mask){	break; }
      bits +=base;
      while((bits & ~mask)!=0){
	bits +=(bits & ~mask);	
      }
    }

    if (zapped!=0){
      int npixNew=pixClient.length - zapped;
      if (npixNew!=0) {
	pptr=cptr=pixClient;
	for(int i=0, j=0; i< pixClient.length && j<npixNew; i++){
	  if (cptr[i] !=~0) {
	    pptr[j]=cptr[i];
	    j++;
	  }
	}
	int[] foo=new int[npixNew];
	System.arraycopy(pixClient, 0, foo, 0, npixNew);
	pixClient=foo;
      }
      else {
	pixClient=null;
      }
      switch(channel) {
      case PSEUDOMAP:
      case REDMAP:
	pixels[client.index]=pixClient;
	break;
      }
    }
    if(errVal!=0){
      client.errorReason=errVal;
    }
    return;
  }

  private void storeColors (Client c, int rest) throws IOException{
    int	errVal=0;
    IO io=c.client;

    if((visual.clss & DynamicClass)==0 && 
       (flags & BeingCreated)==0){
      c.errorReason=11;
      return;
    }

    int idef=0;

    int pixel;
    int red, green, blue, doc;
    boolean ok;
    while(rest!=0){
      pixel=io.readInt(); rest--;
      red=(short)io.readShort();
      green=(short)io.readShort(); rest--;
      blue=(short)io.readShort();
      doc=io.readByte();
      io.readPad(1); rest--;

      if(((red>>8)&0xff)!=0 || ((green>>8)&0xff)!=0 || ((blue>>8)&0xff)!=0){
	red=(red>>8)&0xff;
	green=(green>>8)&0xff;
	blue=(blue>>8)&0xff;
      }
      else{
	red=red&0xff;
	green=green&0xff;
	blue=blue&0xff;
      }
      errVal=storeColor(c, pixel, red, green, blue, doc);
    }
    c.errorReason=errVal;
    return;
  }

  private int storeColor(Client c, int pixel, 
		  int red, int green, int blue, int doc)
    throws IOException{
    int errVal=0;

    if((visual.clss | DynamicClass)==DirectColor) {
    }
    else {
      boolean ok=true;
      if (pixel >=visual.colormapEntries) {
	c.errorValue=pixel;
	errVal=2; //BadValue;
	ok=false;
      }
      else if (entries[pixel].refcnt !=AllocPrivate) {
	errVal=11; //BadAccess;
	ok=false;
      }
      if(ok) {
      }
      else{
	return errVal;
      }

      Entry pent=entries[pixel];

      if((doc & DoRed)!=0) {
	if(pent.shared){
	}
	else{ ((LocalEntry)pent).r=red; }
      }
      else {
	if(pent.shared){
	}
	else{
	  red=((LocalEntry)pent).r;
	}
      }
      if((doc & DoGreen)!=0){
	if(pent.shared){
	}
	else{ ((LocalEntry)pent).g=green; }
      }
      else {
	if(pent.shared){
	}
	else{
	  green=((LocalEntry)pent).g;
	}
      }
      if((doc & DoBlue)!=0){
	if(pent.shared){
	}
	else{ 
	  ((LocalEntry)pent).b=blue; 
	}
      }
      else {
	if(pent.shared){
	}
	else{
	  blue=((LocalEntry)pent).b;
	}
      }
      alloc(pixel, new Color(red, green, blue));
    }
    return errVal;
  }

  private void queryColors (Client c, int[] ppixel) throws IOException{
    IO io=c.client;
    if ((visual.clss & DynamicClass) == 0 &&
        visual.depth.depth==16){
      int n=ppixel.length;

      synchronized(io){
        io.writeByte(1);
        io.writePad(1);
        io.writeShort(c.seq);
        io.writeInt(n*2);
        io.writeShort(n);
        io.writePad(22);
        int ii, foo;
        for(int i=0;i<n;i++){
	  ii=ppixel[i];
	  foo=((ii>>11)&0x1f)*8;
	  io.writeShort(foo|(foo<<8));
	  foo=((ii>>5)&0x3f)*4;
	  io.writeShort(foo|(foo<<8));
	  foo=(ii&0x1f)*8;
	  io.writeShort(foo|(foo<<8));
	  io.writePad(2);
        }
	io.flush();
        return;
      }
    }

    if ((visual.clss | DynamicClass)==DirectColor) {
    }
    else {
      for(int i=0; i<ppixel.length; i++){
	if (ppixel[i] >=visual.colormapEntries){
	  c.errorValue=ppixel[i];
	  c.errorReason=2; //BadValue;
	  return;
	}
      }

      synchronized(io){
        io.writeByte(1);
        io.writePad(1);
        io.writeShort(c.seq);
        io.writeInt(ppixel.length*2);
        io.writeShort(ppixel.length);
        io.writePad(22);

        for(int i=0; i<ppixel.length; i++){
	  Entry pent=entries[ppixel[i]];
	  if (pent.shared) {
	    //System.out.println("queryColors: shared");
	  }
	  else {
	    LocalEntry le=(LocalEntry)pent;
	    io.writeShort(le.r|(le.r<<8));
	    io.writeShort(le.g|(le.g<<8));
	    io.writeShort(le.b|(le.b<<8));
	    io.writePad(2);
	  }
        }
	io.flush();
      }
    }
    return;
  }

  private static int lowbit(int mask){
    int result=1;
    for(int i=0; i<32; i++){
      if((mask & 1)!=0){result<<=i; break;}
      mask>>=1;
    }
    return result;
  }

  private int[] expand(int[] foo, int i){
    int[] bar=new int[i+((foo==null) ? 0 : foo.length)];
    if(foo!=null){ System.arraycopy(foo, 0, bar, 0, foo.length); }
    return bar;
  }

  void mkIcm(){
    if ((visual.clss & DynamicClass)!=0 ||
	(flags & BeingCreated)!=0){
      if(visual.depth.depth==16) return;
      cm=new IndexColorModel((r.length==256 ? 8 : 1), r.length, r, g, b);
    }
    icmtime=System.currentTimeMillis();
  }
}

class Entry{
  boolean shared;
  int refcnt;
  Entry(){
    refcnt=0;
  }
  boolean eq(int r, int g, int b){
    return true;
  }
  void copy(Entry ent){
    shared=ent.shared;
    refcnt=ent.refcnt;
    if((this instanceof LocalEntry) && 
       (ent instanceof LocalEntry)){
      ((LocalEntry)this).r=((LocalEntry)ent).r;
      ((LocalEntry)this).g=((LocalEntry)ent).g;
      ((LocalEntry)this).b=((LocalEntry)ent).b;
    }
  }
  public String toString(){
    return "Entry: refcnt="+refcnt+", shared="+shared ;
  }
}

class LocalEntry extends Entry{
  int r;
  int g; 
  int b;
  LocalEntry(){
    shared=false;
  }
  boolean eq(int r, int g, int b){
    return this.r==r && this.g==g && this.b==b;
  }
}

class SharedEntry extends Entry{
  SharedColor r, g, b;
  SharedEntry(){
    shared=true;
  }
  boolean eq(int r, int g, int b){
    return this.r.color==r && this.g.color==g && this.b.color==b;
  }
}
class SharedColor {
  int color;
}

class ClientColormap extends Resource{
  int mapid;
  int client;

  ClientColormap(int id, int client, int mapid){
    super(id, Resource.RT_CMAPENTRY);
    this.client=client;
    this.mapid=mapid;
  }
  void delete() throws IOException{
    Colormap cmap=(Colormap)Resource.lookupIDByType(mapid, Resource.RT_COLORMAP);
    if(cmap!=null){cmap.freePixels(client);}
  }
}

class RGBTXT{
 static String rgbtxt=null;
 private static String []_rgbtxt={
"255 250 250	snow\n",
"248 248 255	GhostWhite\n",
"245 245 245	WhiteSmoke\n",
"220 220 220	gainsboro\n",
"255 250 240	FloralWhite\n",
"253 245 230	OldLace\n",
"250 240 230	linen\n",
"250 235 215	AntiqueWhite\n",
"255 239 213	PapayaWhip\n",
"255 235 205	BlanchedAlmond\n",
"255 228 196	bisque\n",
"255 218 185	PeachPuff\n",
"255 222 173	NavajoWhite\n",
"255 228 181	moccasin\n",
"255 248 220	cornsilk\n",
"255 255 240	ivory\n",
"255 250 205	LemonChiffon\n",
"255 245 238	seashell\n",
"240 255 240	honeydew\n",
"245 255 250	MintCream\n",
"240 255 255	azure\n",
"240 248 255	AliceBlue\n",
"230 230 250	lavender\n",
"255 240 245	LavenderBlush\n",
"255 228 225	MistyRose\n",
"255 255 255	white\n",
" 0 0 0	black\n",
" 47 79 79	DarkSlateGray\n",
" 47 79 79	DarkSlateGrey\n",
"105 105 105	DimGray\n",
"105 105 105	DimGrey\n",
"112 128 144	SlateGray\n",
"112 128 144	SlateGrey\n",
"119 136 153	LightSlateGray\n",
"119 136 153	LightSlateGrey\n",
"190 190 190	gray\n",
"190 190 190	grey\n",
"211 211 211	LightGrey\n",
"211 211 211	LightGray\n",
" 25 25 112	MidnightBlue\n",
" 0 0 128	navy\n",
" 0 0 128	NavyBlue\n",
"100 149 237	CornflowerBlue\n",
" 72 61 139	DarkSlateBlue\n",
"106 90 205	SlateBlue\n",
"123 104 238	MediumSlateBlue\n",
"132 112 255	LightSlateBlue\n",
" 0 0 205	MediumBlue\n",
" 65 105 225	RoyalBlue\n",
" 0 0 255	blue\n",
" 30 144 255	DodgerBlue\n",
" 0 191 255	DeepSkyBlue\n",
"135 206 235	SkyBlue\n",
"135 206 250	LightSkyBlue\n",
" 70 130 180	SteelBlue\n",
"176 196 222	LightSteelBlue\n",
"173 216 230	LightBlue\n",
"176 224 230	PowderBlue\n",
"175 238 238	PaleTurquoise\n",
" 0 206 209	DarkTurquoise\n",
" 72 209 204	MediumTurquoise\n",
" 64 224 208	turquoise\n",
" 0 255 255	cyan\n",
"224 255 255	LightCyan\n",
" 95 158 160	CadetBlue\n",
"102 205 170	MediumAquamarine\n",
"127 255 212	aquamarine\n",
" 0 100 0	DarkGreen\n",
" 85 107 47	DarkOliveGreen\n",
"143 188 143	DarkSeaGreen\n",
" 46 139 87	SeaGreen\n",
" 60 179 113	MediumSeaGreen\n",
" 32 178 170	LightSeaGreen\n",
"152 251 152	PaleGreen\n",
" 0 255 127	SpringGreen\n",
"124 252 0	LawnGreen\n",
" 0 255 0	green\n",
"127 255 0	chartreuse\n",
" 0 250 154	MediumSpringGreen\n",
"173 255 47	GreenYellow\n",
" 50 205 50	LimeGreen\n",
"154 205 50	YellowGreen\n",
" 34 139 34	ForestGreen\n",
"107 142 35	OliveDrab\n",
"189 183 107	DarkKhaki\n",
"240 230 140	khaki\n",
"238 232 170	PaleGoldenrod\n",
"250 250 210	LightGoldenrodYellow\n",
"255 255 224	LightYellow\n",
"255 255 0	yellow\n",
"255 215 0	gold\n",
"238 221 130	LightGoldenrod\n",
"218 165 32	goldenrod\n",
"184 134 11	DarkGoldenrod\n",
"188 143 143	RosyBrown\n",
"205 92 92	IndianRed\n",
"139 69 19	SaddleBrown\n",
"160 82 45	sienna\n",
"205 133 63	peru\n",
"222 184 135	burlywood\n",
"245 245 220	beige\n",
"245 222 179	wheat\n",
"244 164 96	SandyBrown\n",
"210 180 140	tan\n",
"210 105 30	chocolate\n",
"178 34 34	firebrick\n",
"165 42 42	brown\n",
"233 150 122	DarkSalmon\n",
"250 128 114	salmon\n",
"255 160 122	LightSalmon\n",
"255 165 0	orange\n",
"255 140 0	DarkOrange\n",
"255 127 80	coral\n",
"240 128 128	LightCoral\n",
"255 99 71	tomato\n",
"255 69 0	OrangeRed\n",
"255 0 0	red\n",
"255 105 180	HotPink\n",
"255 20 147	DeepPink\n",
"255 192 203	pink\n",
"255 182 193	LightPink\n",
"219 112 147	PaleVioletRed\n",
"176 48 96	maroon\n",
"199 21 133	MediumVioletRed\n",
"208 32 144	VioletRed\n",
"255 0 255	magenta\n",
"238 130 238	violet\n",
"221 160 221	plum\n",
"218 112 214	orchid\n",
"186 85 211	MediumOrchid\n",
"153 50 204	DarkOrchid\n",
"148 0 211	DarkViolet\n",
"138 43 226	BlueViolet\n",
"160 32 240	purple\n",
"147 112 219	MediumPurple\n",
"216 191 216	thistle\n",
"255 250 250	snow1\n",
"238 233 233	snow2\n",
"205 201 201	snow3\n",
"139 137 137	snow4\n",
"255 245 238	seashell1\n",
"238 229 222	seashell2\n",
"205 197 191	seashell3\n",
"139 134 130	seashell4\n",
"255 239 219	AntiqueWhite1\n",
"238 223 204	AntiqueWhite2\n",
"205 192 176	AntiqueWhite3\n",
"139 131 120	AntiqueWhite4\n",
"255 228 196	bisque1\n",
"238 213 183	bisque2\n",
"205 183 158	bisque3\n",
"139 125 107	bisque4\n",
"255 218 185	PeachPuff1\n",
"238 203 173	PeachPuff2\n",
"205 175 149	PeachPuff3\n",
"139 119 101	PeachPuff4\n",
"255 222 173	NavajoWhite1\n",
"238 207 161	NavajoWhite2\n",
"205 179 139	NavajoWhite3\n",
"139 121 94	NavajoWhite4\n",
"255 250 205	LemonChiffon1\n",
"238 233 191	LemonChiffon2\n",
"205 201 165	LemonChiffon3\n",
"139 137 112	LemonChiffon4\n",
"255 248 220	cornsilk1\n",
"238 232 205	cornsilk2\n",
"205 200 177	cornsilk3\n",
"139 136 120	cornsilk4\n",
"255 255 240	ivory1\n",
"238 238 224	ivory2\n",
"205 205 193	ivory3\n",
"139 139 131	ivory4\n",
"240 255 240	honeydew1\n",
"224 238 224	honeydew2\n",
"193 205 193	honeydew3\n",
"131 139 131	honeydew4\n",
"255 240 245	LavenderBlush1\n",
"238 224 229	LavenderBlush2\n",
"205 193 197	LavenderBlush3\n",
"139 131 134	LavenderBlush4\n",
"255 228 225	MistyRose1\n",
"238 213 210	MistyRose2\n",
"205 183 181	MistyRose3\n",
"139 125 123	MistyRose4\n",
"240 255 255	azure1\n",
"224 238 238	azure2\n",
"193 205 205	azure3\n",
"131 139 139	azure4\n",
"131 111 255	SlateBlue1\n",
"122 103 238	SlateBlue2\n",
"105 89 205	SlateBlue3\n",
" 71 60 139	SlateBlue4\n",
" 72 118 255	RoyalBlue1\n",
" 67 110 238	RoyalBlue2\n",
" 58 95 205	RoyalBlue3\n",
" 39 64 139	RoyalBlue4\n",
" 0 0 255	blue1\n",
" 0 0 238	blue2\n",
" 0 0 205	blue3\n",
" 0 0 139	blue4\n",
" 30 144 255	DodgerBlue1\n",
" 28 134 238	DodgerBlue2\n",
" 24 116 205	DodgerBlue3\n",
" 16 78 139	DodgerBlue4\n",
" 99 184 255	SteelBlue1\n",
" 92 172 238	SteelBlue2\n",
" 79 148 205	SteelBlue3\n",
" 54 100 139	SteelBlue4\n",
" 0 191 255	DeepSkyBlue1\n",
" 0 178 238	DeepSkyBlue2\n",
" 0 154 205	DeepSkyBlue3\n",
" 0 104 139	DeepSkyBlue4\n",
"135 206 255	SkyBlue1\n",
"126 192 238	SkyBlue2\n",
"108 166 205	SkyBlue3\n",
" 74 112 139	SkyBlue4\n",
"176 226 255	LightSkyBlue1\n",
"164 211 238	LightSkyBlue2\n",
"141 182 205	LightSkyBlue3\n",
" 96 123 139	LightSkyBlue4\n",
"198 226 255	SlateGray1\n",
"185 211 238	SlateGray2\n",
"159 182 205	SlateGray3\n",
"108 123 139	SlateGray4\n",
"202 225 255	LightSteelBlue1\n",
"188 210 238	LightSteelBlue2\n",
"162 181 205	LightSteelBlue3\n",
"110 123 139	LightSteelBlue4\n",
"191 239 255	LightBlue1\n",
"178 223 238	LightBlue2\n",
"154 192 205	LightBlue3\n",
"104 131 139	LightBlue4\n",
"224 255 255	LightCyan1\n",
"209 238 238	LightCyan2\n",
"180 205 205	LightCyan3\n",
"122 139 139	LightCyan4\n",
"187 255 255	PaleTurquoise1\n",
"174 238 238	PaleTurquoise2\n",
"150 205 205	PaleTurquoise3\n",
"102 139 139	PaleTurquoise4\n",
"152 245 255	CadetBlue1\n",
"142 229 238	CadetBlue2\n",
"122 197 205	CadetBlue3\n",
" 83 134 139	CadetBlue4\n",
" 0 245 255	turquoise1\n",
" 0 229 238	turquoise2\n",
" 0 197 205	turquoise3\n",
" 0 134 139	turquoise4\n",
" 0 255 255	cyan1\n",
" 0 238 238	cyan2\n",
" 0 205 205	cyan3\n",
" 0 139 139	cyan4\n",
"151 255 255	DarkSlateGray1\n",
"141 238 238	DarkSlateGray2\n",
"121 205 205	DarkSlateGray3\n",
" 82 139 139	DarkSlateGray4\n",
"127 255 212	aquamarine1\n",
"118 238 198	aquamarine2\n",
"102 205 170	aquamarine3\n",
" 69 139 116	aquamarine4\n",
"193 255 193	DarkSeaGreen1\n",
"180 238 180	DarkSeaGreen2\n",
"155 205 155	DarkSeaGreen3\n",
"105 139 105	DarkSeaGreen4\n",
" 84 255 159	SeaGreen1\n",
" 78 238 148	SeaGreen2\n",
" 67 205 128	SeaGreen3\n",
" 46 139 87	SeaGreen4\n",
"154 255 154	PaleGreen1\n",
"144 238 144	PaleGreen2\n",
"124 205 124	PaleGreen3\n",
" 84 139 84	PaleGreen4\n",
" 0 255 127	SpringGreen1\n",
" 0 238 118	SpringGreen2\n",
" 0 205 102	SpringGreen3\n",
" 0 139 69	SpringGreen4\n",
" 0 255 0	green1\n",
" 0 238 0	green2\n",
" 0 205 0	green3\n",
" 0 139 0	green4\n",
"127 255 0	chartreuse1\n",
"118 238 0	chartreuse2\n",
"102 205 0	chartreuse3\n",
" 69 139 0	chartreuse4\n",
"192 255 62	OliveDrab1\n",
"179 238 58	OliveDrab2\n",
"154 205 50	OliveDrab3\n",
"105 139 34	OliveDrab4\n",
"202 255 112	DarkOliveGreen1\n",
"188 238 104	DarkOliveGreen2\n",
"162 205 90	DarkOliveGreen3\n",
"110 139 61	DarkOliveGreen4\n",
"255 246 143	khaki1\n",
"238 230 133	khaki2\n",
"205 198 115	khaki3\n",
"139 134 78	khaki4\n",
"255 236 139	LightGoldenrod1\n",
"238 220 130	LightGoldenrod2\n",
"205 190 112	LightGoldenrod3\n",
"139 129 76	LightGoldenrod4\n",
"255 255 224	LightYellow1\n",
"238 238 209	LightYellow2\n",
"205 205 180	LightYellow3\n",
"139 139 122	LightYellow4\n",
"255 255 0	yellow1\n",
"238 238 0	yellow2\n",
"205 205 0	yellow3\n",
"139 139 0	yellow4\n",
"255 215 0	gold1\n",
"238 201 0	gold2\n",
"205 173 0	gold3\n",
"139 117 0	gold4\n",
"255 193 37	goldenrod1\n",
"238 180 34	goldenrod2\n",
"205 155 29	goldenrod3\n",
"139 105 20	goldenrod4\n",
"255 185 15	DarkGoldenrod1\n",
"238 173 14	DarkGoldenrod2\n",
"205 149 12	DarkGoldenrod3\n",
"139 101 8	DarkGoldenrod4\n",
"255 193 193	RosyBrown1\n",
"238 180 180	RosyBrown2\n",
"205 155 155	RosyBrown3\n",
"139 105 105	RosyBrown4\n",
"255 106 106	IndianRed1\n",
"238 99 99	IndianRed2\n",
"205 85 85	IndianRed3\n",
"139 58 58	IndianRed4\n",
"255 130 71	sienna1\n",
"238 121 66	sienna2\n",
"205 104 57	sienna3\n",
"139 71 38	sienna4\n",
"255 211 155	burlywood1\n",
"238 197 145	burlywood2\n",
"205 170 125	burlywood3\n",
"139 115 85	burlywood4\n",
"255 231 186	wheat1\n",
"238 216 174	wheat2\n",
"205 186 150	wheat3\n",
"139 126 102	wheat4\n",
"255 165 79	tan1\n",
"238 154 73	tan2\n",
"205 133 63	tan3\n",
"139 90 43	tan4\n",
"255 127 36	chocolate1\n",
"238 118 33	chocolate2\n",
"205 102 29	chocolate3\n",
"139 69 19	chocolate4\n",
"255 48 48	firebrick1\n",
"238 44 44	firebrick2\n",
"205 38 38	firebrick3\n",
"139 26 26	firebrick4\n",
"255 64 64	brown1\n",
"238 59 59	brown2\n",
"205 51 51	brown3\n",
"139 35 35	brown4\n",
"255 140 105	salmon1\n",
"238 130 98	salmon2\n",
"205 112 84	salmon3\n",
"139 76 57	salmon4\n",
"255 160 122	LightSalmon1\n",
"238 149 114	LightSalmon2\n",
"205 129 98	LightSalmon3\n",
"139 87 66	LightSalmon4\n",
"255 165 0	orange1\n",
"238 154 0	orange2\n",
"205 133 0	orange3\n",
"139 90 0	orange4\n",
"255 127 0	DarkOrange1\n",
"238 118 0	DarkOrange2\n",
"205 102 0	DarkOrange3\n",
"139 69 0	DarkOrange4\n",
"255 114 86	coral1\n",
"238 106 80	coral2\n",
"205 91 69	coral3\n",
"139 62 47	coral4\n",
"255 99 71	tomato1\n",
"238 92 66	tomato2\n",
"205 79 57	tomato3\n",
"139 54 38	tomato4\n",
"255 69 0	OrangeRed1\n",
"238 64 0	OrangeRed2\n",
"205 55 0	OrangeRed3\n",
"139 37 0	OrangeRed4\n",
"255 0 0	red1\n",
"238 0 0	red2\n",
"205 0 0	red3\n",
"139 0 0	red4\n",
"255 20 147	DeepPink1\n",
"238 18 137	DeepPink2\n",
"205 16 118	DeepPink3\n",
"139 10 80	DeepPink4\n",
"255 110 180	HotPink1\n",
"238 106 167	HotPink2\n",
"205 96 144	HotPink3\n",
"139 58 98	HotPink4\n",
"255 181 197	pink1\n",
"238 169 184	pink2\n",
"205 145 158	pink3\n",
"139 99 108	pink4\n",
"255 174 185	LightPink1\n",
"238 162 173	LightPink2\n",
"205 140 149	LightPink3\n",
"139 95 101	LightPink4\n",
"255 130 171	PaleVioletRed1\n",
"238 121 159	PaleVioletRed2\n",
"205 104 137	PaleVioletRed3\n",
"139 71 93	PaleVioletRed4\n",
"255 52 179	maroon1\n",
"238 48 167	maroon2\n",
"205 41 144	maroon3\n",
"139 28 98	maroon4\n",
"255 62 150	VioletRed1\n",
"238 58 140	VioletRed2\n",
"205 50 120	VioletRed3\n",
"139 34 82	VioletRed4\n",
"255 0 255	magenta1\n",
"238 0 238	magenta2\n",
"205 0 205	magenta3\n",
"139 0 139	magenta4\n",
"255 131 250	orchid1\n",
"238 122 233	orchid2\n",
"205 105 201	orchid3\n",
"139 71 137	orchid4\n",
"255 187 255	plum1\n",
"238 174 238	plum2\n",
"205 150 205	plum3\n",
"139 102 139	plum4\n",
"224 102 255	MediumOrchid1\n",
"209 95 238	MediumOrchid2\n",
"180 82 205	MediumOrchid3\n",
"122 55 139	MediumOrchid4\n",
"191 62 255	DarkOrchid1\n",
"178 58 238	DarkOrchid2\n",
"154 50 205	DarkOrchid3\n",
"104 34 139	DarkOrchid4\n",
"155 48 255	purple1\n",
"145 44 238	purple2\n",
"125 38 205	purple3\n",
" 85 26 139	purple4\n",
"171 130 255	MediumPurple1\n",
"159 121 238	MediumPurple2\n",
"137 104 205	MediumPurple3\n",
" 93 71 139	MediumPurple4\n",
"255 225 255	thistle1\n",
"238 210 238	thistle2\n",
"205 181 205	thistle3\n",
"139 123 139	thistle4\n",
" 0 0 0	gray0\n",
" 0 0 0	grey0\n",
" 3 3 3	gray1\n",
" 3 3 3	grey1\n",
" 5 5 5	gray2\n",
" 5 5 5	grey2\n",
" 8 8 8	gray3\n",
" 8 8 8	grey3\n",
" 10 10 10 	gray4\n",
" 10 10 10 	grey4\n",
" 13 13 13 	gray5\n",
" 13 13 13 	grey5\n",
" 15 15 15 	gray6\n",
" 15 15 15 	grey6\n",
" 18 18 18 	gray7\n",
" 18 18 18 	grey7\n",
" 20 20 20 	gray8\n",
" 20 20 20 	grey8\n",
" 23 23 23 	gray9\n",
" 23 23 23 	grey9\n",
" 26 26 26 	gray10\n",
" 26 26 26 	grey10\n",
" 28 28 28 	gray11\n",
" 28 28 28 	grey11\n",
" 31 31 31 	gray12\n",
" 31 31 31 	grey12\n",
" 33 33 33 	gray13\n",
" 33 33 33 	grey13\n",
" 36 36 36 	gray14\n",
" 36 36 36 	grey14\n",
" 38 38 38 	gray15\n",
" 38 38 38 	grey15\n",
" 41 41 41 	gray16\n",
" 41 41 41 	grey16\n",
" 43 43 43 	gray17\n",
" 43 43 43 	grey17\n",
" 46 46 46 	gray18\n",
" 46 46 46 	grey18\n",
" 48 48 48 	gray19\n",
" 48 48 48 	grey19\n",
" 51 51 51 	gray20\n",
" 51 51 51 	grey20\n",
" 54 54 54 	gray21\n",
" 54 54 54 	grey21\n",
" 56 56 56 	gray22\n",
" 56 56 56 	grey22\n",
" 59 59 59 	gray23\n",
" 59 59 59 	grey23\n",
" 61 61 61 	gray24\n",
" 61 61 61 	grey24\n",
" 64 64 64 	gray25\n",
" 64 64 64 	grey25\n",
" 66 66 66 	gray26\n",
" 66 66 66 	grey26\n",
" 69 69 69 	gray27\n",
" 69 69 69 	grey27\n",
" 71 71 71 	gray28\n",
" 71 71 71 	grey28\n",
" 74 74 74 	gray29\n",
" 74 74 74 	grey29\n",
" 77 77 77 	gray30\n",
" 77 77 77 	grey30\n",
" 79 79 79 	gray31\n",
" 79 79 79 	grey31\n",
" 82 82 82 	gray32\n",
" 82 82 82 	grey32\n",
" 84 84 84 	gray33\n",
" 84 84 84 	grey33\n",
" 87 87 87 	gray34\n",
" 87 87 87 	grey34\n",
" 89 89 89 	gray35\n",
" 89 89 89 	grey35\n",
" 92 92 92 	gray36\n",
" 92 92 92 	grey36\n",
" 94 94 94 	gray37\n",
" 94 94 94 	grey37\n",
" 97 97 97 	gray38\n",
" 97 97 97 	grey38\n",
" 99 99 99 	gray39\n",
" 99 99 99 	grey39\n",
"102 102 102 	gray40\n",
"102 102 102 	grey40\n",
"105 105 105 	gray41\n",
"105 105 105 	grey41\n",
"107 107 107 	gray42\n",
"107 107 107 	grey42\n",
"110 110 110 	gray43\n",
"110 110 110 	grey43\n",
"112 112 112 	gray44\n",
"112 112 112 	grey44\n",
"115 115 115 	gray45\n",
"115 115 115 	grey45\n",
"117 117 117 	gray46\n",
"117 117 117 	grey46\n",
"120 120 120 	gray47\n",
"120 120 120 	grey47\n",
"122 122 122 	gray48\n",
"122 122 122 	grey48\n",
"125 125 125 	gray49\n",
"125 125 125 	grey49\n",
"127 127 127 	gray50\n",
"127 127 127 	grey50\n",
"130 130 130 	gray51\n",
"130 130 130 	grey51\n",
"133 133 133 	gray52\n",
"133 133 133 	grey52\n",
"135 135 135 	gray53\n",
"135 135 135 	grey53\n",
"138 138 138 	gray54\n",
"138 138 138 	grey54\n",
"140 140 140 	gray55\n",
"140 140 140 	grey55\n",
"143 143 143 	gray56\n",
"143 143 143 	grey56\n",
"145 145 145 	gray57\n",
"145 145 145 	grey57\n",
"148 148 148 	gray58\n",
"148 148 148 	grey58\n",
"150 150 150 	gray59\n",
"150 150 150 	grey59\n",
"153 153 153 	gray60\n",
"153 153 153 	grey60\n",
"156 156 156 	gray61\n",
"156 156 156 	grey61\n",
"158 158 158 	gray62\n",
"158 158 158 	grey62\n",
"161 161 161 	gray63\n",
"161 161 161 	grey63\n",
"163 163 163 	gray64\n",
"163 163 163 	grey64\n",
"166 166 166 	gray65\n",
"166 166 166 	grey65\n",
"168 168 168 	gray66\n",
"168 168 168 	grey66\n",
"171 171 171 	gray67\n",
"171 171 171 	grey67\n",
"173 173 173 	gray68\n",
"173 173 173 	grey68\n",
"176 176 176 	gray69\n",
"176 176 176 	grey69\n",
"179 179 179 	gray70\n",
"179 179 179 	grey70\n",
"181 181 181 	gray71\n",
"181 181 181 	grey71\n",
"184 184 184 	gray72\n",
"184 184 184 	grey72\n",
"186 186 186 	gray73\n",
"186 186 186 	grey73\n",
"189 189 189 	gray74\n",
"189 189 189 	grey74\n",
"191 191 191 	gray75\n",
"191 191 191 	grey75\n",
"194 194 194 	gray76\n",
"194 194 194 	grey76\n",
"196 196 196 	gray77\n",
"196 196 196 	grey77\n",
"199 199 199 	gray78\n",
"199 199 199 	grey78\n",
"201 201 201 	gray79\n",
"201 201 201 	grey79\n",
"204 204 204 	gray80\n",
"204 204 204 	grey80\n",
"207 207 207 	gray81\n",
"207 207 207 	grey81\n",
"209 209 209 	gray82\n",
"209 209 209 	grey82\n",
"212 212 212 	gray83\n",
"212 212 212 	grey83\n",
"214 214 214 	gray84\n",
"214 214 214 	grey84\n",
"217 217 217 	gray85\n",
"217 217 217 	grey85\n",
"219 219 219 	gray86\n",
"219 219 219 	grey86\n",
"222 222 222 	gray87\n",
"222 222 222 	grey87\n",
"224 224 224 	gray88\n",
"224 224 224 	grey88\n",
"227 227 227 	gray89\n",
"227 227 227 	grey89\n",
"229 229 229 	gray90\n",
"229 229 229 	grey90\n",
"232 232 232 	gray91\n",
"232 232 232 	grey91\n",
"235 235 235 	gray92\n",
"235 235 235 	grey92\n",
"237 237 237 	gray93\n",
"237 237 237 	grey93\n",
"240 240 240 	gray94\n",
"240 240 240 	grey94\n",
"242 242 242 	gray95\n",
"242 242 242 	grey95\n",
"245 245 245 	gray96\n",
"245 245 245 	grey96\n",
"247 247 247 	gray97\n",
"247 247 247 	grey97\n",
"250 250 250 	gray98\n",
"250 250 250 	grey98\n",
"252 252 252 	gray99\n",
"252 252 252 	grey99\n",
"255 255 255 	gray100\n",
"255 255 255 	grey100\n",
"169 169 169	DarkGrey\n",
"169 169 169	DarkGray\n",
"0  0 139	DarkBlue\n",
"0 139 139	DarkCyan\n",
"139 0 139	DarkMagenta\n",
"139 0 0	DarkRed\n",
"144 238 144	LightGreen\n"
};

  static void init(Hashtable table){
    if(_rgbtxt==null) return;
    StringBuffer foo=new StringBuffer();
    for(int i=0; i< _rgbtxt.length; i++){
      foo.append(_rgbtxt[i]);
    }
    rgbtxt=foo.toString();
    _rgbtxt=null;
    foo=null;

    try{
      InputStream is=new ByteArrayInputStream(RGBTXT.rgbtxt.getBytes());
//      StreamTokenizer st=new StreamTokenizer(is);
      BufferedReader br=new BufferedReader(new InputStreamReader(is));
      StreamTokenizer st=new StreamTokenizer(br);

      st.ordinaryChar('!');
      //    st.ordinaryChar('\n');
      //    st.ordinaryChar('\t');
      String token=null;
      char c;
      int r,g,b;
      byte[] buf=new byte[1024];
      while(st.nextToken()!=st.TT_EOF){
	//System.out.println("type="+st.ttype+", "+st.sval);
	if(st.ttype=='!'){
//	  while((c=(char)is.read())!='\n');
	  while((c=(char)br.read())!='\n');
	  continue;
	}
	if(st.ttype==st.TT_NUMBER){
	  r=(int)st.nval;
	  st.nextToken(); g=(int)st.nval;
	  st.nextToken(); b=(int)st.nval;
	  //System.out.print("r, g, b="+r+", "+g+", "+b);
	  int i=0;
//	  while((c=(char)is.read())!='\n'){
	  while((c=(char)br.read())!='\n'){
	    if(c=='\t')continue;
	    if(c==' ')continue;
	    if('A'<=c && c<='Z'){
	      c=(char)('a'+c-'A');
	    }
	    buf[i]=(byte)c; i++;
	  }
	  table.put(new String(buf, 0, i), new Color(r,g,b));
	  //System.out.println(" -> "+new String(buf, 0, i));
	  continue;
	}
      }
      st=null;
      buf=null;
//    table.put("slategrey", rgbTable.get("slate grey"));
//    table.put("Black", rgbTable.get("black"));
//    table.put("White", rgbTable.get("white"));
    }
    catch(Exception e){
      //System.out.println(e);
    }
  }
}

class Colormap16 extends Colormap{
  Colormap16(int id, Screen s, Visual v, int alloc, Client client) {
    super(id, s, v, alloc, client);
  }
  int allocColor (Client client, int red, int green, int blue)
    throws IOException{
    return ((red/8)<<11)|((green/4)<<5)|(blue/8);
  }

  Color getColor(int pixel){ 
    if(pixel==1) return Color.white;

    int r=(((pixel>>11)&0x1f)*8);
    if(r==0xf8)r=0xff;
    int g=(((pixel>>5)&0x3f)*4);
    if(g==0xfc)g=0xff;
    int b=(((pixel)&0x1f)*8);
    if(b==0xf8)b=0xff;

    return  new Color((r<<16) | (g<<8) | b);
//  return new Color(((((pixel>>11)&0x1f)*8)<<16) |
//                   ((((pixel>>5)&0x3f)*4)<<8) |
//                   ((pixel&0x1f)*8)); 
  }
  void mkIcm(){ }
  void freePixels(int client){}
  void freeAll(){}
  int rgb2pixel(int rgb){ 
    return (((rgb&0xff0000)>>16)/8)<<11|
           (((rgb&0xff00)>>8)/4)<<5|
           ((rgb&0xff))/8; 
  }
  void delete() throws IOException{
  }
}
