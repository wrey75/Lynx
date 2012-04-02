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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
//import java.awt.image.*;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

                                

class Pixmap extends Drawable {
	private static Log LOG = LogFactory.getLog(Pixmap.class);

  static void init(Screen[] screen){
    ImageFactory.init(screen[0]);
    screen[0].pixmaps=new Pixmap[Format.format.length];
    for(int i=0; i < Format.format.length; i++){
      screen[0].pixmaps[i]=
	ResizablePixmap.createPixmap(Resource.fakeClientId(Client.clients[0]), 
				     screen[0].root, 
				     1, 1, Format.format[i].depth);
    }
  }

  ColorModel filter;
  long time=0;

  int fg=1;
  int bg=0;

  byte lpad;

  Colormap colormap;
  boolean dirty=true;

  MemoryImageSource mis=null;
  Drawable drawable=null;
  int refcnt;
  int datasize;
  Image img=null;
  Graphics imgg=null;
  byte[] data=null;

  boolean imageDirty=false;
  int imageDirtyX, imageDirtyY, imageDirtyWidth, imageDirtyHeight;

  Pixmap(int id, Drawable d,
		int width, int height, byte depth) {
    super(id, RT_PIXMAP);

    type=Drawable.DRAWABLE_PIXMAP;
    clss=0;
    this.screen=d.screen;
    this.depth=depth;
    this.bitsPerPixel=depth;
    this.drawable=d;
    this.x=0; this.y=0;
    this.width=width; this.height=height;
    refcnt=0;

    img=ImageFactory.createImage(width, height);
    img.flush();
    imgg=img.getGraphics();
    colormap=d.getColormap();
    filter=colormap.cm;
    time=System.currentTimeMillis();
  }

  void ref(){
    refcnt++;
  }

  void mkMIS(){
    mis=new MemoryImageSource(width, height, colormap.cm, data, 0, width);
    mis.setAnimated(true);
  }

  byte[] getData(){
    if(data==null){
      data=new byte[width*height];
      mkMIS();
    }
    return data;
  }

  Image getImage(){
    return img;
  }

  Image getImage(Window win){
    return getImage(win, fg, bg);
  }

  Image getImage(Window win, GC gc){
    return getImage(win, gc.fgPixel, gc.bgPixel);
  }

  Image getImage(Window win, int fgPixel, int bgPixel){
    if(img==null) return null;
    if(win==null) return getImage();
    if(imageDirty && colormap!=win.getColormap()){
      data=getData();
    }
    if(data!=null){
      if(depth!=1 && (filter!=win.getColormap().cm)){
	if(imageDirty){
	  image2data();
	}
	filter=win.getColormap().cm;
	dirty=true;
      }
      else{
      }
      if(time<win.getColormap().icmtime){
	dirty=true;
      }

      if(dirty){
	dirty=false;
	mkMIS();
	Image dataImg=Toolkit.getDefaultToolkit().createImage(mis);
	time=System.currentTimeMillis();
    	imgg.drawImage(dataImg, 0, 0, 
		       Screen.screen[0].root.ddxwindow); //??
	dataImg.flush();
      }
    }
    return getImage();
  }

  Image getImage(Window win, GC gc, int x, int y, int w, int h){
    getImage(win, gc);
    return getImage(gc, x, y, w, h);
  }

  Image getImage(GC gc, int x, int y, int w, int h){
    if(data!=null && time<colormap.icmtime ){
      mkMIS();
      Image dataImg=Toolkit.getDefaultToolkit().createImage(mis);
      time=System.currentTimeMillis();
      imgg.drawImage(dataImg, 0, 0, 
		     Screen.screen[0].root.ddxwindow); //??
      dataImg.flush();
    }

    Image i=getImage();
    if(gc!=null && gc.clip_mask!=null && gc.clip_mask instanceof ClipPixmap){
      TransparentFilter tf=new TransparentFilter(0, 0, (Pixmap)(gc.clip_mask.getMask()));
      i=Toolkit.getDefaultToolkit().
	createImage(new FilteredImageSource(i.getSource(), tf));
    }
    return i;
  }

  void draw(int x, int y, int w, int h){
  }

  void image2data(){
    image2data(imageDirtyX, imageDirtyY, imageDirtyWidth, imageDirtyHeight);
    imageDirty=false;
  }
  int[] pixels=new int[10];
  void image2data(int x, int y, int w, int h){
    if(pixels.length<w*h){
      pixels=new int[w * h];
    }
    PixelGrabber pg=new PixelGrabber(img, x, y, w, h, pixels, 0, w);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      LOG.error("interrupted waiting for pixels!");
      return;
    }
    if ((pg.getStatus() & ImageObserver.ABORT)!=0) {
      LOG.error("image fetch aborted or errored");
      return;
    }
    byte[] dt=getData();
    for(int i=0; i<h; i++){                             
      for(int j=0; j<w; j++){                           
	dt[(y+i)*width + x+j]=(byte)colormap.rgb2pixel(pixels[i*w+j]);
      }
    }
    time=0;
  }

  void reset(){ }

  static void reqFreePixmap(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=c.length;
    foo=io.readInt();
    Resource o=Resource.lookupIDByType(foo, RT_PIXMAP);
    c.length-=2;
    if(o==null || !(o instanceof Pixmap)){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return;
    }
    Resource.freeResource(foo, Resource.RT_NONE);
  }

  static void reqGetImage(Client c) throws IOException {
    int n ,foo, format;
    IO io=c.client;

    format=c.data;
    foo=io.readInt();
    Drawable d=c.lookupDrawable(foo);
    c.length-=2;
    if(d==null){
      c.errorValue=foo;
      c.errorReason=9; // BadDrawable;
      return;
    }

    int x, y, width, height;
    x=(short)io.readShort();
    y=(short)io.readShort();
    width=(short)io.readShort();
    height=(short)io.readShort();
    foo=io.readInt();
    c.length=0;
    Image img=null;
    Colormap colormap=d.getColormap();
    img=d.getImage(null, x, y, width, height);

    //
    //if(d instanceof Window &&
    //   ((Window)d)==((Window)d).screen.root){
    //  Window tmp=(Window)d;
    //  img=RepaintManager.currentManager(tmp.ddxwindow).
    //	getOffscreenBuffer(tmp.ddxwindow, tmp.width, tmp.height);
    //}
    //else{ img=d.getImage(null, x, y, width, height); }

    int[] pixels=new int[width * height];
    PixelGrabber pg=new PixelGrabber(img, x, y, width, height, 
			             pixels, 0, width);

    try {
      pg.grabPixels();
    }
    catch (InterruptedException e) {
      LOG.error("interrupted waiting for pixels!");
      for(int i=0; i<pixels.length; i++)pixels[i]=0;
    }
    if ((pg.getStatus()&ImageObserver.ABORT)!=0) {
      LOG.error("image fetch aborted or errored");
      for(int i=0; i<pixels.length; i++)pixels[i]=0;
    }

    if(d instanceof Window){
      if(((Window)d)!=((Window)d).screen.root &&
	 img!=((Window)d).getImage()){
	img.flush();
      }
    }
    else{
      if(img!=((Pixmap)d).getImage()){
	img.flush();
      }
    }

    int i;
    int ww;
    if(d.depth==1){
      int www=(width%32)/8;
      int wwww=(width%32)%8;

      synchronized(io){
        io.writeByte((byte)1);
        io.writeByte((byte)1);
        io.writeShort(c.seq);
        io.writeInt(((width+31)/32)*height);
        io.writeInt(0);
        io.writePad(20);
        i=0;
        if(format==1){
          for(int hh=0; hh<height;hh++){
            ww=width;
	    while(true){
              foo=0;
	      if(32<ww){
                for(int ii=0; ii<4; ii++){            
                  foo=0;                              
		  i+=8;                               
		  for(int iii=0; iii<8; iii++){       
                    i--;                              
		    foo=(foo<<1)|((pixels[i]&0xffffff)!=0?1:0); 
	          }                                   
                  i+=8;                               
                  io.writeByte((byte)(foo&0xff));
		}                                     
		ww-=32;
	        continue;
	      }
	      if(ww!=0){
                for(int ii=0; ii<www; ii++){     
	          foo=0;                         
		  i+=8;                          
		  for(int iii=0; iii<8; iii++){  
                    i--;                         
		    foo=(foo<<1)|((pixels[i]&0xffffff)!=0?1:0);  
		  }                              
		  i+=8;                          
		  io.writeByte((byte)(foo&0xff));
		}                                
		if(wwww!=0){                     
                  foo=0;                         
		  i+=wwww;                       
		  for(int iii=0; iii<wwww; iii++){    
                    i--;                              
		    foo=(foo<<1)|((pixels[i]&0xffffff)!=0?1:0);  
		  }                              
		  i+=wwww;                       
		  io.writeByte((byte)(foo));
		  for(int ii=www+1; ii<4; ii++){ 
                    io.writeByte((byte)0);
		  }                              
		}                                
		else{                            
	          for(int ii=www; ii<4; ii++){  
                    io.writeByte((byte)0); 
		  }                              
		}                                
	      }
	      break;
	    }
	  }
        }
	else{ 
	// LSB
          for(int hh=0; hh<height;hh++){
            ww=width;
	    while(true){
              foo=0;
	      if(32<ww){
	        for(int ii=0; ii<32; ii++){
	          foo=(foo<<1)|((pixels[i]&0xffffff)!=0?1:0);
		  i++;
	          if(ii==7 || ii==15 || ii==23 || ii==31){
		    io.writeByte((byte)(bi_reverse(foo)));
		    foo=0;
		  }
		}
		ww-=32;
		continue;
	      }
	      if(ww!=0){
	        for(int ii=0; ii<ww; ii++){
	          foo=foo<<1|((pixels[i]&0xffffff)!=0?1:0);
		  i++;
		  if(ii==7 || ii==15 || ii==23 || ii==31){
                    io.writeByte((byte)(bi_reverse(foo)));
		    foo=0;
		  }
		}
		for(int ii=ww; ii<32; ii++){
                  foo=(foo<<1)|0;
	          if(ii==7 || ii==15 || ii==23 || ii==31){
                    io.writeByte((byte)(bi_reverse(foo)));
		    foo=0;
		  }
		}
	      }
	      break;
	    }
	  }
	}
	io.flush();
	return;
      }
    }
    else if (d.depth==8){
      if(format==1){

        synchronized(io){
	  io.writeByte((byte)1);
	  io.writeByte((byte)d.depth);
	  io.writeShort(c.seq);
	  n=(width+3)/4;
	  io.writeInt(n*height);
	  io.writeInt(0);
	  io.writePad(20);

	  i=0;
	  for(int hh=0; hh<height;hh++){
	    ww=width;
	    while(true){
	      foo=0;
	      if(4<ww){
	        for(int ii=0; ii<4; ii++){
		  io.writeByte((colormap.rgb2pixel(pixels[i]))&0xff);
		  i++;
		}
		ww-=4;
		continue;
	      }
	      if(ww!=0){
	        for(int ii=0; ii<ww; ii++){
                  io.writeByte((colormap.rgb2pixel(pixels[i]))&0xff);
		  i++;
		}
		ww=4-ww;
		while(ww!=0){
		  io.writeByte(0);
		  ww--;
	        }
	      }
	      break;
	    }
	  }
	  io.flush();
	  return;
	}
      }
      else{ // format==2

        synchronized(io){
          io.writeByte((byte)1);
	  io.writeByte((byte)d.depth);
	  io.writeShort(c.seq);
	  n=(width+3)/4;
	  io.writeInt(n*height);
	  io.writeInt(0);
	  io.writePad(20);
	
	  i=0;
	  for(int hh=0; hh<height;hh++){
	    ww=width;
	    while(true){
	      foo=0;
	      if(4<ww){
	        for(int ii=0; ii<4; ii++){
		  io.writeByte((colormap.rgb2pixel(pixels[i]))&0xff);
		  i++;
	        }
	        ww-=4;
	        continue;
	      }
	      if(ww!=0){
	        for(int ii=0; ii<ww; ii++){
		  io.writeByte((colormap.rgb2pixel(pixels[i]))&0xff);
		  i++;
	        }
	        ww=4-ww;
	        while(ww!=0){
		  io.writeByte(0);
		  ww--;
	        }
	      }
	      break;
	    }
	  }
	  io.flush();
	  return;
	}
      }
    }
    else if(d.depth==16){
      if(format==2){
        synchronized(io){
          io.writeByte((byte)1);
	  io.writeByte((byte)d.depth);
	  io.writeShort(c.seq);
	  n=(width/2+(width%2))*4;
	  io.writeInt(n*height/4);
	  io.writeInt(0);
	  io.writePad(20);
	  i=0;
	  int iii;
	  for(int hh=0; hh<height;hh++){
            for(int ii=0; ii<width; ii++){
              iii=pixels[i];
	      iii=((iii>>16)&0xff)/8<<11 |
		  ((iii>>8)&0xff)/4<<5 |
		  ((iii)&0xff)/8;
	      io.writeByte((iii)&0xff);
	      io.writeByte((iii>>8)&0xff);
	      i++;
	    }
	    if(width%2!=0) io.writePad(2);
	  }
	  io.flush();
	  return;
	}                     
      }
    }

    synchronized(io){
      io.writeByte((byte)0);  // error!! Implementation
      io.writeByte((byte)17);
      io.writeShort(c.seq);
      io.writePad(4);
      io.writeShort(0);
      io.writeByte((byte)73);
      io.writePad(21);
      io.flush();
    }
  }

  static void reqPutImage(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    byte format;
    short width, height, dstx, dsty;
    byte depth;
    byte lpad;
    Pixmap pixmap=null;
    format=(byte)c.data;
    n=c.length;
    foo=io.readInt();
    Drawable d=c.lookupDrawable(foo);
    if(d==null){
      c.errorValue=foo;
      c.errorReason=9; // BadDrawable;
    }
    foo=io.readInt();
    GC gc=c.lookupGC(foo);
    if(gc==null && c.errorReason==0){
      c.errorValue=foo;
      c.errorReason=13; // GC
    }
    width=(short)io.readShort();
    height=(short)io.readShort(); 
    dstx=(short)io.readShort();
    dsty=(short)io.readShort();
    lpad=(byte)io.readByte();
    depth=(byte)io.readByte(); 
    io.readPad(2);
    c.length-=6;
    n-=6;
    if(c.errorReason!=0){
      return;
    }
    if(dsty<0){
//      height+=dsty;
      dsty=0;
    }
    if(dstx<0){
//      width+=dstx;
      dstx=0;
    }

    int ddstx=dstx;
    int ddsty=dsty;

    synchronized(Pixmap.class){
      if (d instanceof Pixmap){
	pixmap=(Pixmap)d;
	if(pixmap.imageDirty){
	  pixmap.image2data();
	}
      }
      else{
	if(!((Window)d).ddxwindow.isVisible()){
	  io.readPad(n*4); 
	  return;
	}
	pixmap=null;
	Pixmap[] pixmaps=((Window)d).screen.pixmaps;
	for(int i=0; i<pixmaps.length; i++){
	  if(pixmaps[i].depth==d.depth){
	    pixmap=pixmaps[i];
	    break;
	  }
	}
	if(pixmap==null){
	}
	((Resizable)pixmap).setSize(width, height);
	((Resizable)pixmap).setColormap(d.getColormap());
	pixmap.lpad=lpad;
	dstx=0;
	dsty=0;
      }

      byte[] data=null;

      data=pixmap.getData();

      int ww=0;
      int j=0;
      int i=0;

      j=dsty;

      if(depth==1 && (pixmap.depth==1||pixmap.depth==8)){
	int www=0;
	if (d instanceof Window){ www=((Resizable)pixmap).getRealWidth(); }
	else{ www=pixmap.width; }

        if(WeirdX.imageByteOrder==1){
  	  Pixmap1.putData(c, data, www, dstx, dsty, width, lpad);
	}
        else{
	  Pixmap1.putData_LSB(c, data, www, dstx, dsty, width, lpad);
	}

	if(d.depth!=1){
	  byte f=(byte)gc.fgPixel;
	  byte b=(byte)gc.bgPixel;
	  for(i=dsty;i<height+dsty;i++){
	    for(j=dstx;j<width+dstx;j++){
	      if(data[i*www+j]==0){
		data[i*www+j]=b;
	      }
	      else{
		data[i*www+j]=f;
	      }
	    }
	  }

	}
      }
      else if (depth==8){
	if(format==2){ // ZPixmap
	  int restw=width;
	  int restww=width;
	  int resth=height;
	  if(d.width<dstx+width){
	    restw=(short)(d.width-dstx);
	  }
	  if(d.height<dsty+height){
	    resth=(short)(d.height-dsty);
	  }

	  int www=0;
	  if (d instanceof Window){ www=((Resizable)pixmap).getRealWidth(); }
	  else{ www=pixmap.width; }
	  j*=www;

	  if(width<c.bbuffer.length){
            int paddedwidthi=((width+3)/4);
            int paddedwidth=paddedwidthi*4;
            int padding=paddedwidth-restw;
	    while(n!=0){
	      if(resth>0){
		io.readByte(data, j+dstx, restw);
		if(padding!=0){
		  io.readPad(padding);
		}
	      }
	      else{
		io.readPad(paddedwidth);
	      }
	      n-=paddedwidthi;
	      j+=www;
	      resth--; 
	    }
	  }
	  else{
	    while(n!=0){
	      restww=restw;
	      ww=width;
	      i=dstx;
	      while(0<ww){
		io.readByte(c.bbuffer, 0 , 4); n--;
		if(4<=ww){
		  if(resth>0){
		    if(restww>=4){
		      System.arraycopy(c.bbuffer, 0, data, j+i, 4);
		      restww-=4;
		    }
		    else if(restww>0){
		      System.arraycopy(c.bbuffer, 0, data, j+i, restww);
		      restww=0;
		    }
		  }
		  i+=4; ww-=4;
		}
		else{
		  if(resth>0){
		    if(restww>=ww){
		      System.arraycopy(c.bbuffer, 0, data, j+i, ww);
		      restww-=ww;
		    }
		    else if(restww>0){
		      System.arraycopy(c.bbuffer, 0, data, j+i, restww);
		      restww=0;
		    }
		  }
		  i+=ww;
		  break;
		}
	      }
	      j+=www;
	      resth--; 
	    }
  	  }
	}
	else{
	  int www=0;
	  if (d instanceof Window){ www=((Resizable)pixmap).getRealWidth(); }
	  else{ www=pixmap.width; }
	  n*=4;
	  while(n!=0){
	    ww=width;
	    i=dstx;
	    while(4<ww){
	      foo=io.readInt(); n-=4;
	      data[j*www+i]=(byte)(foo&0xff); i++;
	      foo=foo>>8;
	      data[j*www+i]=(byte)(foo&0xff); i++;
	      foo=foo>>8;
	      data[j*www+i]=(byte)(foo&0xff); i++;
	      foo=foo>>8;
	      data[j*www+i]=(byte)(foo&0xff); i++;
	      ww-=4;
	    }
	    if(ww!=0){
	      foo=io.readInt(); n-=4;
	      while(0<ww){
		data[j*www+i]=(byte)(foo&0xff); i++;
		foo=foo>>8;
		ww--;
	      }
	    }
	    j++;
	  }
	}
      }
      else if(pixmap.depth==16){
	((Pixmap16)pixmap).putImage(c, gc, dstx, dsty, width, height, 
				    lpad, format, depth);
      }
      else{
	io.readPad(n*4);
      }

      if(d instanceof Window){
	Graphics g=((Window)d).getGraphics();
 	pixmap.mis.newPixels(dstx, dsty, width, height);
	Image dataImg=Toolkit.getDefaultToolkit().createImage(pixmap.mis);
	Image ii=dataImg;

	java.awt.Shape tmp=g.getClip();
	g.clipRect(ddstx, ddsty, width, height);
	g.drawImage(ii, ddstx, ddsty, Screen.screen[0].root.ddxwindow);
	if(tmp==null){ g.setClip(0, 0, d.width, d.height); }
	else{g.setClip(tmp);}
	((Window)d).draw(ddstx, ddsty, width, height);    
	dataImg.flush();
      }
      else {
	if(pixmap.time<pixmap.colormap.icmtime ){
	  pixmap.mkMIS();
	}
 	pixmap.mis.newPixels(dstx, dsty, width, height);
	Image dataImg=Toolkit.getDefaultToolkit().createImage(pixmap.mis);
        if(dstx!=0 || dsty!=0 || width!=d.width || height!=d.height){
	  java.awt.Shape tmp=pixmap.imgg.getClip();

	  pixmap.imgg.clipRect(dstx, dsty, width, height);
	  pixmap.imgg.drawImage(dataImg, 0, 0, 
				Screen.screen[0].root.ddxwindow);
	  if(tmp==null){ pixmap.imgg.setClip(0, 0, d.width, d.height); }
	  else{ pixmap.imgg.setClip(tmp); }
	}
	else{
	  pixmap.imgg.drawImage(dataImg, 0, 0, 
				Screen.screen[0].root.ddxwindow);
	}
	dataImg.flush();
	pixmap.reset();
	pixmap.time=System.currentTimeMillis();
      }
    }
  }

  static void reqCreatePixmap(Client c) throws IOException{
    int foo;
    byte depth;
    short width, height;
    int pid;
    IO io=c.client;
    depth=(byte)c.data;
    pid=io.readInt();
    foo=io.readInt();
    c.length-=3;

    Drawable d=c.lookupDrawable(foo);
    if(d==null){
      c.errorValue=foo;
      c.errorReason=9; // BadValue;
      return;
    }

    width=(short)io.readShort(); 
    height=(short)io.readShort(); 

    c.length--;

    if(width==0 || height==0){
      c.errorValue=0;
      c.errorReason=4; // BadValue;
      return;
    }

    if(depth!=1){
      Depth[] dd=d.screen.depth;
      int i=0;
      for(i=0; i<dd.length; i++){
	if(dd[i].depth==depth) break;
      }
      if(i==dd.length){
	c.errorValue=depth;
	c.errorReason=8; // BadMatch
	return;
      }
    }
    createPixmap(pid, d, width, height, depth);
  }

  static Pixmap createPixmap(int id, Drawable d, 
			     int width, int height, byte depth){
    Pixmap p=null;
    if(depth==1){ p=new Pixmap1(id, d, width, height); }
    else if(depth==16){p=new Pixmap16(id, d, width, height); }
    else{ p=new Pixmap(id, d, width, height, depth); }
    Resource.add(p);
    return p;
  }

  static int bi_reverse(int code){
    int len=8;
    int res = 0;
    do{
      res|=code&1;
      code>>>=1;
      res<<=1;
    }
    while(--len>0);
    return res>>>1;
  }

  Graphics getGraphics(){
    return imgg;
  }

  Graphics getGraphics(GC gc, int mask){
    Graphics graphics=imgg;
    if(gc==currentGC &&
       gc.time==gctime &&
       (mask&~gmask)==0){
      return graphics;
    }
    gctime=gc.time;
    currentGC=gc;
    gmask=mask;
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	return graphics;
      }
      graphics.setClip(rec.x, rec.y, rec.width, rec.height);
    }
    if((mask&GC.GCFunction)!=0){
      Color color=getColormap().getColor(gc.fgPixel);
      if(gc.function==GC.GXxor){
	gmask&=~GC.GCFunction;
	graphics.setXORMode(new Color((color.getRGB() ^ graphics.getColor().getRGB())&0xffffff));
      }
      else if(gc.function==GC.GXinvert){
	//System.out.println("Pimxpa: GXinvert");
      }
      else{
	graphics.setColor(color);
      }
    }
    if((mask&GC.GCFont)!=0){
      Font font=gc.font;
      graphics.setFont(font.getFont());
    }
    if((mask&GC.GCLineWidth)!=0 ||
       (mask&GC.GCLineStyle)!=0 ||
       (mask&GC.GCCapStyle)!=0 ||
       (mask&GC.GCJoinStyle)!=0 ){
    }
    return graphics;
  }

  void copyPlane(Pixmap dst, GC gc, 
		int sx, int sy, int dx, int dy, int w, int h){
    copyArea(dst, gc, sx, sy, dx, dy, w, h);
  }

  @SuppressWarnings("unused")
void copyArea(Pixmap dst, GC gc, 
		int sx, int sy, int dx, int dy, int w, int h){
    if((width-sx)<w) w=width-sx;
    if((dst.width-dx)<w) w=dst.width-dx;

    if((height-sy)<h) h=height-sy;
    if((dst.height-dy)<h) h=dst.height-dy;

    int s=sy*width+sx;
    int d=dy*dst.width+dx;

    Graphics g=dst.getGraphics();
    Image ii=getImage(gc, sx, sy, w, h);

    if(sx==0 && sy==0 && w==width && h==height){
      g.drawImage(ii, dx, dy, width, height,
		  Screen.screen[0].root.ddxwindow);
    }
    else{
      java.awt.Shape tmp=g.getClip();
      g.setClip(dx, dy, w, h);
      g.drawImage(ii, dx-sx, dy-sy, width, height, 
		  Screen.screen[0].root.ddxwindow);

      if(tmp==null){
	g.setClip(0, 0, dst.width, dst.height);
      }
      else{
	g.setClip(tmp);
      }
    }
    dst.draw(dx, dy, w, h);

    if(ii!=getImage()){
      ii.flush();
    }
  }

  Colormap getColormap(){
    return colormap;
  }

  void delete() throws IOException{
    if(refcnt>0){
      refcnt--;
      return;
    }
    if(img!=null){
      img.flush();
      img=null;
      imgg.dispose();
      imgg=null;
    }
    data=null;
    filter=null;
    colormap=null;
    drawable=null; 
  }
  void restoreClip(){
    imgg.setClip(0, 0, width, height);	
  }
}

class ImageFactory {
  static DDXWindow ddxwindow;
  static void init(Screen screen){
    ddxwindow=screen.root.ddxwindow;
  }
  static Image createImage(int width, int height){
    return ddxwindow.createImage(width, height);
  }
}

class FBFilter extends RGBImageFilter {
  int foreGround=Color.white.getRGB();
  int backGround=Color.black.getRGB();
  boolean through=true;

  void setFgBg(Color f, Color b){
    foreGround=f.getRGB()|0xff000000;
    backGround=b.getRGB()|0xff000000;
    if((foreGround&0x00ffffff)!=0 &&
       (backGround&0x00ffffff)==0){
      through=true;
    }
    else{
      through=false;
    }
  }

  public int filterRGB(int x, int y, int rgb) {
    rgb&=0xffffff;
    if(rgb==0) return backGround;
    return foreGround;
  }
}

class Pixmap1 extends Pixmap {
  FBFilter fbFilter=null;
  Image fgImg=null;

  Pixmap1(int id, Drawable d, int width, int height) {
    super(id, d, width, height, (byte)1);
    reset();
  }

  Image getImage(){
    if(!fbFilter.through){ return fgImg; }
    else return super.getImage();
  }

  Image getImage(Window win, int fgPixel, int bgPixel){
    setFgBg(win.getColormap(), fgPixel, bgPixel);
    return super.getImage(null, fgPixel, bgPixel);
  }

  void reset(){
    if(fgImg!=null){
      fgImg.flush();
    }
    fbFilter=new FBFilter();
    fgImg=Toolkit.getDefaultToolkit().
      createImage(new FilteredImageSource(img.getSource(), fbFilter));
  }

  static void putData(Client c, byte[] d, int width,
		      int dx, int dy, int dwidth, int lpad)
    throws IOException{
    IO io=c.client;
    int j=dy*width;
    int n=c.length;
    int foo;

    if(n*4<c.cbuffer.length){
      io.readByte(c.bbuffer, 0, n*4);
      int start=0;
      while(n!=0){
	int ww=dwidth;
	int i=dx;
	if(lpad!=0){
	  n--;
	  ww_break: 
	  {
	    int ii=lpad%8;
	    int iii=lpad/8; 
	    if(ii!=0){
	      foo=c.bbuffer[start+iii];
	      foo=foo<<ii;
	      for(;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
		i++; foo=foo<<1;
		ww--; if(ww==0) break ww_break;
	      }
	      iii++;
	    }
	    for(;iii<4;iii++){
	      foo=c.bbuffer[start+iii];
	      for(ii=0;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
		i++; foo=foo<<1;
		ww--; if(ww==0) break ww_break;
	      }
	    }
	  }
	  start+=4;
	}
	while(32<ww){
	  n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[start]; start++;
	    for(int ii=0;ii<8;ii++){
	      d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
	      i++; foo=foo<<1;
	    }
	  }
	  ww-=32;
	}
	if(ww!=0){
	  n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[start]; start++;
	    for(int ii=0;ii<8;ii++){
	      if(0<ww){
		d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
		i++; foo=foo<<1;
		ww--;
	      }
	    }
	  }
	}
	j+=width;
      }
    }
    else{
      while(n!=0){
	int ww=dwidth;
	int i=dx;
	if(lpad!=0){
	  io.readByte(c.bbuffer, 0, 4); n--;
	  ww_break: 
	  {
	    int ii=lpad % 8;
	    int iii=lpad/8; 
	    if(ii!=0){
	      foo=c.bbuffer[iii];
	      foo=foo<<ii;
	      for(;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
		i++; foo=foo<<1;
		ww--; if(ww==0) break ww_break;
	      }
	      iii++;
	    }
	    for(;iii<4;iii++){
	      foo=c.bbuffer[iii];
	      for(ii=0;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
		i++; foo=foo<<1;
		ww--; if(ww==0) break ww_break;
	      }
	    }
	  }
	}
	while(32<ww){
	  io.readByte(c.bbuffer, 0, 4); n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[iii];
	    for(int ii=0;ii<8;ii++){
	      d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
	      i++; foo=foo<<1;
	    }
	  }
	  ww-=32;
	}
	if(ww!=0){
	  io.readByte(c.bbuffer, 0, 4); n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[iii];
	    for(int ii=0;ii<8;ii++){
	      if(0<ww){
		d[j+i]=(byte)(((foo&0x80)!=0)?1:0);
		i++; foo=foo<<1;
		ww--;
	      }
	    }
	  }
	}
	j+=width;
      }
    }
  }

  static void putData_LSB(Client c, byte[] d, int width,
		      int dx, int dy, int dwidth, int lpad)
    throws IOException{
    IO io=c.client;
    int j=dy*width;
    int n=c.length;
    int foo;

    if(n*4<c.cbuffer.length){
      io.readByte(c.bbuffer, 0, n*4);
      int start=0;
      while(n!=0){
	int ww=dwidth;
	int i=dx;
	if(lpad!=0){
	  n--;
	  ww_break: 
	  {
	    int ii=lpad%8;
	    int iii=lpad/8; 
	    if(ii!=0){
	      foo=c.bbuffer[start+iii];
	      foo=foo>>>ii;
	      for(;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
		i++; foo=foo>>>1;
		ww--; if(ww==0) break ww_break;
	      }
	      iii++;
	    }
	    for(;iii<4;iii++){
	      foo=c.bbuffer[start+iii];
	      for(ii=0;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
		i++; foo=foo>>>1;
		ww--; if(ww==0) break ww_break;
	      }
	    }
	  }
	  start+=4;
	}
	while(32<ww){
	  n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[start]; start++;
	    for(int ii=0;ii<8;ii++){
	      d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
	      i++; foo=foo>>>1;
	    }
	  }
	  ww-=32;
	}
	if(ww!=0){
	  n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[start]; start++;
	    for(int ii=0;ii<8;ii++){
	      if(0<ww){
		d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
		i++; foo=foo>>>1;
		ww--;
	      }
	    }
	  }
	}
	j+=width;
      }
    }
    else{
      while(n!=0){
	int ww=dwidth;
	int i=dx;
	if(lpad!=0){
	  io.readByte(c.bbuffer, 0, 4); n--;
	  ww_break: 
	  {
	    int ii=lpad % 8;
	    int iii=lpad/8; 
	    if(ii!=0){
	      foo=c.bbuffer[iii];
	      foo=foo>>>ii;
	      for(;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
		i++; foo=foo>>>1;
		ww--; if(ww==0) break ww_break;
	      }
	      iii++;
	    }
	    for(;iii<4;iii++){
	      foo=c.bbuffer[iii];
	      for(ii=0;ii<8;ii++){
		d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
		i++; foo=foo>>>1;
		ww--; if(ww==0) break ww_break;
	      }
	    }
	  }
	}
	while(32<ww){
	  io.readByte(c.bbuffer, 0, 4); n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[iii];
	    for(int ii=0;ii<8;ii++){
	      d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
	      i++; foo=foo>>>1;
	    }
	  }
	  ww-=32;
	}
	if(ww!=0){
	  io.readByte(c.bbuffer, 0, 4); n--;
	  for(int iii=0;iii<4;iii++){
	    foo=c.bbuffer[iii];
	    for(int ii=0;ii<8;ii++){
	      if(0<ww){
		d[j+i]=(byte)(((foo&0x01)!=0)?1:0);
		i++; foo=foo>>>1;
		ww--;
	      }
	    }
	  }
	}
	j+=width;
      }
    }
  }

  void delete() throws IOException{
    if(refcnt>0){
      super.delete();
      return;
    }
    super.delete();
    fbFilter=null;
    if(fgImg!=null){
      fgImg.flush();
      fgImg=null;
    }
  }

  void setFgBg(Colormap cmap, int fgPixel, int bgPixel){
    if(img==null) return;
    if(fbFilter!=null){
      fbFilter.setFgBg(cmap.getColor(fgPixel), cmap.getColor(bgPixel));
    }
  }

  void copyArea(Pixmap dst, GC gc, 
		int sx, int sy, int dx, int dy, int w, int h){
    if(dst.depth!=1){
      setFgBg(dst.colormap, gc.fgPixel, gc.bgPixel);
    }
    super.copyArea(dst, gc, sx, sy, dx, dy, w, h);
  }
  void mkMIS(){
    mis=new MemoryImageSource(width, height, Colormap.bwicm, data, 0, width);
    mis.setAnimated(true);
  }
}

class Pixmap16 extends Pixmap {
	private static Log LOG = LogFactory.getLog(Pixmap16.class);
  int[] idata;

  Pixmap16(int id, Drawable d, int width, int height) {
    super(id, d, width, height, (byte)16);
    img=ImageFactory.createImage(width, height);
    img.flush();
    imgg=img.getGraphics();
  }

  int getScanWidth(){ return width*2; }

  byte[] getData(){
    if(data==null){
      data=new byte[width*height*2];
      idata=new int[width*height];
      mkMIS();
    }
    return data;
  }

  Image getImage(Window win){
    return getImage(win, null);
  }
  Image getImage(Window win, GC gc){
    if(img==null) return null;
    if(win==null) return getImage();

    if(imageDirty && colormap!=win.getColormap()){
      data=getData();
    }

    if(data!=null){
      if(depth!=1){
	if(imageDirty){
	  image2data();
	}
	dirty=true;
      }
      else{
      }

      if(time<win.getColormap().icmtime){
	dirty=true;
      }

      if(dirty){
	dirty=false;
	mkMIS();
	Image dataImg=Toolkit.getDefaultToolkit().createImage(mis);
	time=System.currentTimeMillis();
    	imgg.drawImage(dataImg, 0, 0, 
		       Screen.screen[0].root.ddxwindow); //??
	dataImg.flush();
      }
    }
    return getImage();
  }

  Image getImage(Window win, GC gc, int x, int y, int w, int h){
    getImage(win, gc);
    return getImage(gc, x, y, w, h);
  }
  Image getImage(GC gc, int x, int y, int w, int h){
    if(data!=null && time<colormap.icmtime){
      mkMIS();
      Image dataImg=Toolkit.getDefaultToolkit().createImage(mis);
      time=System.currentTimeMillis();
      imgg.drawImage(dataImg, 0, 0, 
		     Screen.screen[0].root.ddxwindow); //??
      dataImg.flush();
    }

    Image i=getImage();

    if(gc!=null && gc.clip_mask!=null && gc.clip_mask instanceof ClipPixmap){
      TransparentFilter tf=new TransparentFilter(0, 0, (Pixmap)(gc.clip_mask.getMask()));
      i=Toolkit.getDefaultToolkit().
	createImage(new FilteredImageSource(i.getSource(), tf));
    }
    return i;
  }

  @SuppressWarnings("unused")
void putImage(Client c, GC gc, 
		int dstx, int dsty, int dwidth, int dheight, 
		int lpad, int format, int depth)
    throws IOException{
    IO io=c.client;
    int scanWidth=getScanWidth();
    byte[] d=getData(); 

    if(depth==1){
      int j=dsty*scanWidth;
      int n=c.length;
      int foo;
      int f=gc.fgPixel;
//    byte fhi=(byte)((f>>8)&0xff);
//    byte flo=(byte)(f&0xff);
      byte fhi=(byte)(f&0xff);
      byte flo=(byte)((f>>8)&0xff);
      int b=gc.bgPixel;
//    byte bhi=(byte)((b>>8)&0xff);
//    byte blo=(byte)(b&0xff);
      byte bhi=(byte)(b&0xff);
      byte blo=(byte)((b>>8)&0xff);

      if(n*4<c.cbuffer.length){
	io.readByte(c.bbuffer, 0, n*4);
	int start=0;
	while(n!=0){
	  int ww=dwidth;
	  int i=dstx;
	  if(lpad!=0){
	    n--;
	  ww_break: 
	  {
	    int ii=lpad%8;
	    int iii=lpad/8; 
	    if(ii!=0){
	      foo=c.bbuffer[start+iii];
	      foo=foo>>ii;
	      for(;ii<8;ii++){
		if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		else{ d[j+i]=bhi; d[j+i+1]=blo; }
		i+=2; 
		foo=foo>>1;
		ww--; if(ww==0) break ww_break;
	      }
	      iii++;
	    }
	    for(;iii<4;iii++){
	      foo=c.bbuffer[start+iii];
	      for(ii=0;ii<8;ii++){
		if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		else{ d[j+i]=bhi; d[j+i+1]=blo; }
		i+=2; 
		foo=foo>>1;
		ww--; if(ww==0) break ww_break;
	      }
	    }
	  }
	  start+=4;
	  }
	  while(32<ww){
	    n--;
	    for(int iii=0;iii<4;iii++){
	      foo=c.bbuffer[start]; start++;
	      for(int ii=0;ii<8;ii++){
		if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		else{ d[j+i]=bhi; d[j+i+1]=blo; }
		i+=2; 
		foo=foo>>1;
	      }
	    }
	    ww-=32;
	  }
	
	  if(ww!=0){
	    n--;
	    for(int iii=0;iii<4;iii++){
	      foo=c.bbuffer[start]; start++;
	      for(int ii=0;ii<8;ii++){
		if(0<ww){
		  if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		  else{ d[j+i]=bhi; d[j+i+1]=blo; }
		  i+=2; 
		  foo=foo>>1;
		  ww--;
		}
	      }
	    }
	  }
	  j+=scanWidth;
	}
      }
      else{
	while(n!=0){
	  int ww=dwidth;
	  int i=dstx;
	  if(lpad!=0){
	    io.readByte(c.bbuffer, 0, 4); n--;
	  ww_break: 
	  {
	    int ii=lpad % 8;
	    int iii=lpad/8; 
	    if(ii!=0){
	      foo=c.bbuffer[iii];
	      foo=foo>>ii;
	      for(;ii<8;ii++){
		if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		else{ d[j+i]=bhi; d[j+i+1]=blo; }
		i+=2; 
		foo=foo>>1;
		ww--; if(ww==0) break ww_break;
	      }
	      iii++;
	    }
	    for(;iii<4;iii++){
	      foo=c.bbuffer[iii];
	      for(ii=0;ii<8;ii++){
		if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		else{ d[j+i]=bhi; d[j+i+1]=blo; }
		i+=2; 
		foo=foo>>1;
		ww--; if(ww==0) break ww_break;
	      }
	    }
	  }
	  }
	  while(32<ww){
	    io.readByte(c.bbuffer, 0, 4); n--;
	    for(int iii=0;iii<4;iii++){
	      foo=c.bbuffer[iii];
	      for(int ii=0;ii<8;ii++){
		if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		else{ d[j+i]=bhi; d[j+i+1]=blo; }
		i+=2; 
		foo=foo>>1;
	      }
	    }
	    ww-=32;
	  }
	  if(ww!=0){
	    io.readByte(c.bbuffer, 0, 4); n--;
	    for(int iii=0;iii<4;iii++){
	      foo=c.bbuffer[iii];
	      for(int ii=0;ii<8;ii++){
		if(0<ww){
		  if((foo&0x01)!=0){ d[j+i]=fhi; d[j+i+1]=flo; }
		  else{ d[j+i]=bhi; d[j+i+1]=blo; }
		  i+=2; 
		  foo=foo>>1;
		  ww--;
		}
	      }
	    }
	  }
	  j+=scanWidth;
	}
      }

      int resth=dheight;
      if(height<dsty+dheight){ resth=(short)(height-dsty);}
      resth+=dsty;      
      int restw=dwidth;
      if(width<dstx+dwidth){ restw=(short)(width-dstx);}
      restw+=dstx;      

      int iii=scanWidth*(dsty-1);
      int iiii=(scanWidth/2)*(dsty-1);
      int jjj;

      for(int ii=dsty; ii<resth; ii++){
	iii+=scanWidth;
	iiii+=(scanWidth/2);
	jjj=(dstx-1)*2;
	for(int jj=dstx; jj<restw; jj++){
	  jjj+=2;
          foo=((d[iii+jjj+1]<<8)&0xff00)|(d[iii+jjj]&0xff);
	  idata[iiii+jj]=0xff000000 | 
                         ((foo>>11)&0x1f)*8<<16 |
                         ((foo>>5)&0x3f)*4<<8 |
                         (foo&0x1f)*8;
	}
      }
      return;
    }

    if (depth==16){
      int ww=0;
      int i=0;
      int j=dsty;
      int n=c.length;
      int foo;

      if(format == 2){ // ZPixmap
	int restw=dwidth;
	int restww=dwidth;
	int resth=dheight;

	if(width<dstx+dwidth){ restw=(short)(width-dstx); }
	restw*=2;

	if(height<dsty+dheight){ resth=(short)(height-dsty); }

	j*=scanWidth;

	while(n!=0){
	  restww=restw;
	  ww=dwidth;
	  i=dstx*2;
	  while(0<ww){
	    io.readByte(c.bbuffer, 0 , 4); n--;
	    if(resth>0){
	      if(restww>0){
		System.arraycopy(c.bbuffer, 0, d, j+i, 2);
		restww-=2; i+=2;
	      }
	      if(restww>0){
		System.arraycopy(c.bbuffer, 2, d, j+i, 2);
		restww-=2; i+=2;
	      }
	    }
	    ww-=2;
	  }
	  j+=scanWidth;
	  resth--; 
	}

	resth=dheight;
	if(height<dsty+resth){ resth=(short)(height-dsty);}
	resth+=dsty;      

	restw=dwidth;
	if(width<dstx+restw){ restw=(short)(width-dstx);}
	restw+=dstx;      

	int iii=scanWidth*(dsty-1);
	int iiii=(scanWidth/2)*(dsty-1);
	int jjj;

	for(int ii=dsty; ii<resth; ii++){
	  iii+=scanWidth;
	  iiii+=(scanWidth/2);
	  jjj=(dstx-1)*2;
	  for(int jj=dstx; jj<restw; jj++){
	    jjj+=2;
            foo=((d[iii+jjj+1]<<8)&0xff00)|(d[iii+jjj]&0xff);
	    idata[iiii+jj]=0xff000000 | 
	                   (((foo>>11)&0x1f)*8)<<16 |
                           (((foo>>5)&0x3f)*4)<<8 |
                           (foo&0x1f)*8;
	  }
	}
      }
      else{
	n*=4;
	j=dsty*scanWidth;
	while(n!=0){
	  ww=width;
	  i=dstx;
	  while(4<ww){
	    foo=io.readInt(); n-=4;
	    data[j+i]=(byte)(foo&0xff); i++;
	    foo=foo>>8;
	    data[j+i]=(byte)(foo&0xff); i++;
	    foo=foo>>8;
	    data[j+i]=(byte)(foo&0xff); i++;
	    foo=foo>>8;
	    data[j+i]=(byte)(foo&0xff); i++;
	    ww-=4;
	  }
	  if(ww!=0){
	    foo=io.readInt(); n-=4;
	    while(0<ww){
	      data[j+i]=(byte)(foo&0xff); i++;
	      foo=foo>>8;
	      ww--;
	    }
	  }
	  j+=scanWidth;
	}
      }
    }

    else{
      int n=c.length;
      int foo;
      while(n!=0){
	foo=io.readInt(); n--;
      }
    }
  }

  void image2data(int x, int y, int w, int h){
    if(pixels.length<w*h){
      pixels = new int[w * h];
    }
    PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      LOG.error("interrupted waiting for pixels!");
      return;
    }
    if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
      LOG.error("image fetch aborted or errored");
      return;
    }

    int scanWidth=getScanWidth();

    byte[] dt=getData();
    int foo;
    for(int i=0; i<h; i++){                             
      for(int j=0; j<w; j++){
        foo=pixels[i*w+j];
        foo=(((foo>>16)&0xff)/8)<<11 |
            (((foo>>8)&0xff)/4)<<5 |
            ((foo&0xff)/8);
	dt[(y+i)*scanWidth + x+j*2]=(byte)((foo>>8)&0xff);
	dt[(y+i)*scanWidth + x+j*2+1]=(byte)(foo&0xff);
      }
    }

    int iii=scanWidth*(y-1);
    int iiii=(scanWidth/2)*(y-1);
    int jjj;
    for(int ii=y; ii<y+h; ii++){
      iii+=scanWidth;
      iiii+=(scanWidth/2);
      jjj=(x-1)*2;
      for(int jj=x; jj<x+w; jj++){
	jjj+=2;
        foo=((dt[iii+jjj]<<8)&0xff00)|(dt[iii+jjj+1]&0xff);
        idata[iiii+jj]=0xff000000 | 
                       ((foo>>11)&0x1f)*8<<16 |
                       ((foo>>5)&0x3f)*4<<8 |
                       (foo&0x1f)*8;
      }
    }
    time=0;
  }

  void mkMIS(){
    mis=new MemoryImageSource(width, height, idata, 0, width);
    mis.setAnimated(true);
  }

  void copyArea(Pixmap dst, GC gc, 
		int sx, int sy, int dx, int dy, int w, int h){
    super.copyArea(dst, gc, sx, sy, dx, dy, w, h);
  }
}

interface Resizable {
  void setColormap(Colormap colormap);
  int getRealWidth();
  int getRealHeight();
  void setSize(int w, int h);
}

class ResizablePixmap extends Pixmap implements Resizable{
  int real_width=0;
  int real_height=0;

  ResizablePixmap(int id, Drawable d, int width, int height, byte depth) {
    super(id, d, width, height, depth);
    getData();
    real_width=width;
    real_height=height;
  }

  public void setColormap(Colormap colormap){
    this.colormap=colormap;
  }

  static Pixmap createPixmap(int id, Drawable d, 
			     int width, int height, byte depth){
    Pixmap p=null;
    if(depth==1){ p=new ResizablePixmap1(id, d, width, height); }
    else if(depth==16){ p=new ResizablePixmap16(id, d, width, height); }
    else{ p=new ResizablePixmap(id, d, width, height, depth); }
    Resource.add(p);
    return p;
  }

  public void setSize(int w, int h){
    if(w<=real_width && h<=real_height){
      if(2*w < real_width && 2*h < real_height){
	real_width/=2; real_height/=2;
	data=new byte[real_width*real_height];
	mis=new MemoryImageSource(real_width, real_height, 
				  colormap.cm, 
				  data, 0, real_width);
	mis.setAnimated(true);
      }
    }
    else{
      if(real_width<w) real_width=w;
      if(real_height<h) real_height=h;
      data=new byte[real_width*real_height];
      mis=new MemoryImageSource(real_width, real_height, 
				colormap.cm, 
				data, 0, real_width);
      mis.setAnimated(true);
    }
    this.width=w; this.height=h;
  }

  public int getRealWidth(){
    return real_width;
  }
  public int getRealHeight(){
    return real_height;
  }
}

class ResizablePixmap1 extends Pixmap1  implements Resizable{
  int real_width=0;
  int real_height=0;

  ResizablePixmap1(int id, Drawable d, int width, int height) {
    super(id, d, width, height);
    getData();
    real_width=width;
    real_height=height;
  }

  public void setColormap(Colormap colormap){
  }

  public void setSize(int w, int h){
    if(w<=real_width && h<=real_height){
      if(2*w < real_width && 2*h < real_height){
	real_width/=2; real_height/=2;
	data=new byte[real_width*real_height];
	mis=new MemoryImageSource(real_width, real_height, 
				  Colormap.bwicm, 
				  data, 0, real_width);
	mis.setAnimated(true);
      }
    }
    else{
      if(real_width<w) real_width=w;
      if(real_height<h) real_height=h;
      data=new byte[real_width*real_height];
      mis=new MemoryImageSource(real_width, real_height, 
				Colormap.bwicm, 
				data, 0, real_width);
      mis.setAnimated(true);
    }
    this.width=w; this.height=h;
  }
  public int getRealWidth(){
    return real_width;
  }
  public int getRealHeight(){
    return real_height;
  }
}

class ResizablePixmap16 extends Pixmap16  implements Resizable{
  int real_width=0;
  int real_height=0;

  ResizablePixmap16(int id, Drawable d, int width, int height) {
    super(id, d, width, height);
    getData();
    real_width=width;
    real_height=height;
  }

  public void setColormap(Colormap colormap){
//    this.colormap=colormap;
  }

  public void setSize(int w, int h){
    if(w<=real_width && h<=real_height){
      if(2*w < real_width && 2*h < real_height){
	real_width/=2; real_height/=2;
	data=new byte[real_width*real_height*2];
	idata=new int[real_width*real_height];
	mis=new MemoryImageSource(real_width, real_height, idata,
				  0, real_width);
	mis.setAnimated(true);
      }
    }
    else{
      if(real_width<w) real_width=w;
      if(real_height<h) real_height=h;
      data=new byte[real_width*real_height*2];
      idata=new int[real_width*real_height];
      mis=new MemoryImageSource(real_width, real_height, 
				idata, 0, real_width);
      mis.setAnimated(true);
    }
    this.width=w; this.height=h;
  }
  public int getRealWidth(){
    return real_width;
  }
  public int getRealHeight(){
    return real_height;
  }

  int getScanWidth(){ return real_width*2; }
}
