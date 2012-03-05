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
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;

final class Draw{
  static void reqPolyPoint(Client c, Drawable d, GC gc) throws IOException{
    int foo;
    int n=c.length;
    IO io=c.client;
    Graphics graphics=d.getGraphics(gc, GC.GCFunction|GC.GCSubwindowMode);
    if(graphics==null){
      io.readPad(n*4); 
      return;
    }
    boolean coor=false;
    if(c.data!=0) coor=true;
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	io.readPad(4*n); 
	return;
      }
    }
    int x, y;
    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;
    if(coor){
      x=(short)io.readShort(); y=(short)io.readShort();
      n--;
      graphics.drawLine(x, y, x, y);

      if(x<=sx)sx=x;
      if(x>=lx)lx=x;
      if(y<=sy)sy=y;
      if(y>=ly)ly=y;

      while(n!=0){
	x+=(short)io.readShort(); y+=(short)io.readShort(); n--;
        graphics.drawLine(x, y, x, y);

	if(x<=sx)sx=x;
	if(x>=lx)lx=x;
	if(y<=sy)sy=y;
	if(y>=ly)ly=y;

      }
    }
    else{
      while(n!=0){
	x=(short)io.readShort(); y=(short)io.readShort(); n--;
        graphics.drawLine(x, y, x, y);

	if(x<=sx)sx=x;
	if(x>=lx)lx=x;
	if(y<=sy)sy=y;
	if(y>=ly)ly=y;

      }
    }
    if(sx<0)sx=0;
    if(sy<0)sy=0;
    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }
    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqFillPolyArc(Client c, Drawable d, GC gc) 
    throws IOException{
    int foo;
    int n=c.length;
    IO io=c.client;

    Graphics graphics=d.getGraphics(gc, GC.GCFunction|GC.GCSubwindowMode);
    if(graphics==null){
      io.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	io.readPad(n*4); 
	return;
      }
    }
    n*=4;
    short x, y, a1, a2;
    int width, height;
    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;
    while(n!=0){
      x=(short)io.readShort(); y=(short)io.readShort();
      if(x<sx) sx=x;
      if(y<sy) sy=y;

      width=io.readShort();
      height=io.readShort();

      if(lx<x+width) lx=x+width;
      if(ly<y+height) ly=y+height;

      a1=(short)io.readShort(); a2=(short)io.readShort();
      n-=12;
      graphics.fillArc(x, y, width, height, a1/64, a2/64);
    }

    if(d instanceof Window){
      if(sx<0) sx=0;
      if(sy<0) sy=0;
      ((Window)d).draw(sx, sy, lx-sx+2, ly-sy+2);
    }

    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqPolyArc(Client c, Drawable d, GC gc) throws IOException{
    int foo;
    int n=c.length;
    IO io=c.client;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction|GC.GCSubwindowMode
				    |GC.GCLineWidth
				    );
    if(graphics==null){
      io.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	io.readPad(n*4); 
	return;
      }
    }

    n*=4;

    short x, y, a1, a2;
    int width, height;

    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;

    while(n!=0){
      x=(short)io.readShort(); y=(short)io.readShort();
      if(x<sx) sx=x;
      if(y<sy) sy=y;

      width=/*(short)*/io.readShort(); height=/*(short)*/io.readShort();
      if(lx<x+width) lx=x+width;
      if(ly<y+height) ly=y+height;

      a1=(short)io.readShort(); a2=(short)io.readShort();
      n-=12;
      graphics.drawArc(x, y, width, height, a1/64, a2/64);
    }

    if(d instanceof Window){
      if(sx<0) sx=0;
      if(sy<0) sy=0;
      ((Window)d).draw(sx, sy, lx-sx+2, ly-sy+2);
    }
    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqPolyText16(Client c, Drawable d, GC gc, int x, int y) 
    throws IOException{
    int n=c.length;
    int len;
    int foo;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction | GC.GCFont
				    );
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(4); 
	  n--;
	}
	return;
      }
    }

    Font font=gc.font;
    int delta;
    n=n*4;

    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;

    while(n!=0){
      if(n<2) break;
      foo=c.client.readByte(); n--;
      if(foo==255){
	int i=0;
	foo=c.client.readByte(); n--; i=foo&0xff;
	foo=c.client.readByte(); n--; i=((i<<8)&0xffff)| foo&0xff;
	foo=c.client.readByte(); n--; i=((i<<8)&0xffffff)| foo&0xff;
	foo=c.client.readByte(); n--; i=(i<<8) | foo&0xff;
	Font tmp=(Font)Resource.lookupIDByType(i, Resource.RT_FONT);
	if(tmp!=null) { font=tmp; graphics.setFont(font.getFont()); }
	else{ System.out.println("font is null!!");  }
	continue;
      }
      delta=c.client.readByte(); n--;
      x+=delta;
      if(foo>0){
	foo*=2;
	c.client.readByte(c.bbuffer, 0, foo);
	n-=foo;
	if(font.encoding!=null){
	  foo=font.encode(c.bbuffer, 0, foo, c.cbuffer);
	  if(foo==0){
 	    c.length=n;
 	    return;
 	  }
	}
	else{
	  for(int i=0; i<foo; i++){
	    c.cbuffer[i]=(char)(c.bbuffer[i]&0xff);
	  }
	  for(int i=0; i<foo; i++){
	    if(c.cbuffer[i]!=0){
	      c.cbuffer[i/2]=c.cbuffer[i];
	    }
	  }
	  foo/=2; // ????
	}

	graphics.drawChars(c.cbuffer, 0, foo, x, y);
	if(x<sx)sx=x;
	foo=font.charsWidth(c.cbuffer, 0, foo)+x;
	x=foo;
	if(lx<foo) lx=foo;
	if((y-font.ascent)<sy) sy=y-font.ascent;
	if(ly<y+font.descent) ly=y+font.descent;
      }
    }

    if(n!=0){ 
      c.client.readPad(n);
    }

    if(sx<0)sx=0;
    if(sy<0)sy=0;
    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }

    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqImageText16(Client c, Drawable d, GC gc, int x, int y) 
    throws IOException{
    int len=c.data;
    int n=c.length;
    int foo;
    Graphics graphics=d.getGraphics(gc, GC.GCFunction|GC.GCFont);
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(4); 
	  n--;
	}
	return;
      }
    }

    Font font=gc.font;

    n*=4;

    c.client.readByte(c.bbuffer, 0, n);

    len*=2;
    if(font.encoding!=null){
      len=font.encode(c.bbuffer, 0, len, c.cbuffer);
      if(len==0){
	return;
      }
    }
    else{
      for(int i=0; i<len; i++){
	c.cbuffer[i]=(char)(c.bbuffer[i]&0xff);
      }
      for(int i=0; i<len; i++){
	if(c.cbuffer[i]!=0){
	  c.cbuffer[i/2]=c.cbuffer[i];
	}
      }
      len/=2;
    }

    {
      Color tmp=graphics.getColor();
      graphics.setColor(d.getColormap().getColor(gc.bgPixel));
      graphics.fillRect(x, y-(font.ascent), 
			font.charsWidth(c.cbuffer, 0, len),
			font.ascent+font.descent);

      graphics.setColor(tmp);
      graphics.drawChars(c.cbuffer, 0, len, x, y);
    }

    if(d instanceof Window){
      ((Window)d).draw(x, y-(font.ascent), 
			font.charsWidth(c.cbuffer, 0, len),
			font.ascent+font.descent);
    }

    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqPolyText8(Client c, Drawable d, GC gc, int x, int y) 
    throws IOException{
    int n=c.length;
    int len;
    int foo;
    IO io=c.client;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction | GC.GCFont
				    );
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	io.readPad(n*4);
	return;
      }
    }

    Font font=gc.font;

    int delta;
    n=n*4;

    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;

    while(n!=0){
      if(n<2) break;
      foo=io.readByte(); n--;
      if(foo==255){
	int i=0;
	foo=io.readByte(); n--; i=foo&0xff;
	foo=io.readByte(); n--; i=((i<<8)&0xffff)| foo&0xff;
	foo=io.readByte(); n--; i=((i<<8)&0xffffff)| foo&0xff;
	foo=io.readByte(); n--; i=(i<<8) | foo&0xff;
	Font tmp=(Font)Resource.lookupIDByType(i, Resource.RT_FONT);
	if(tmp!=null) { font=tmp; graphics.setFont(font.getFont()); }
	else{ System.out.println("font is null!!");  }
	continue;
      }

      delta=io.readByte(); n--;
      x+=delta;
      if(foo>0){
	io.readByte(c.bbuffer, 0, foo);
	n-=foo;

	if(font.encoding!=null){
          foo=font.encode(c.bbuffer, 0, foo, c.cbuffer);
	}
	else{
          char ccc=0;
          for(int i=0; i<foo ;i++){
            ccc=c.cbuffer[i]=(char)(c.bbuffer[i]&0xff);
            if(ccc<0x20){
              if(ccc==11)c.cbuffer[i]=(char)'+';
              else if(ccc==12)c.cbuffer[i]=(char)'+';
              else if(ccc==13)c.cbuffer[i]=(char)'+';
              else if(ccc==14)c.cbuffer[i]=(char)'+';
              else if(ccc==15)c.cbuffer[i]=(char)'+';
              else if(ccc==18)c.cbuffer[i]=(char)'-';
              else if(ccc==21)c.cbuffer[i]=(char)'+';
              else if(ccc==22)c.cbuffer[i]=(char)'+';
              else if(ccc==23)c.cbuffer[i]=(char)'+';
              else if(ccc==24)c.cbuffer[i]=(char)'+';
              else if(ccc==25)c.cbuffer[i]=(char)'|';
              else c.cbuffer[i]=(char)0x20;
            }
	  }
	}
	graphics.drawChars(c.cbuffer, 0, foo, x, y);

	if(x<sx)sx=x;
	foo=font.charsWidth(c.cbuffer, 0, foo)+x;

	x=foo;

	if(lx<foo) lx=foo;
	if((y-font.ascent)<sy) sy=y-font.ascent;
	if(ly<y+font.descent) ly=y+font.descent;
      }
    }

    if(n!=0){ 
      io.readPad(n);
    }

    if(sx<0)sx=0;
    if(sy<0)sy=0;
    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }
    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqImageText8(Client c, Drawable d, GC gc, int x, int y) 
    throws IOException{
    int len=c.data;
    int n=c.length;
    int foo;
    IO io=c.client;

    Graphics graphics=d.getGraphics(gc, GC.GCFunction|GC.GCFont);
    if(graphics==null){
      io.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	io.readPad(4*n); 
	return;
      }
    }

    Font font=gc.font;

    {
      io.readByte(c.bbuffer, 0, len);
      //java.awt.FontMetrics metric=graphics.getFontMetrics();

      Color tmp=graphics.getColor();
      graphics.setColor(d.getColormap().getColor(gc.bgPixel));

      if(len>0){
	io.readPad((-len)&3);
      }

      if(font.encoding!=null){
        len=font.encode(c.bbuffer, 0, len, c.cbuffer);
      }
      else{
        char ccc=0;
        for(int i=0; i<len ;i++){
          ccc=c.cbuffer[i]=(char)(c.bbuffer[i]&0xff);
          if(ccc<0x20){
            if(ccc==11)c.cbuffer[i]=(char)'+';
            else if(ccc==12)c.cbuffer[i]=(char)'+';
            else if(ccc==13)c.cbuffer[i]=(char)'+';
            else if(ccc==14)c.cbuffer[i]=(char)'+';
            else if(ccc==15)c.cbuffer[i]=(char)'+';
            else if(ccc==18)c.cbuffer[i]=(char)'-';
            else if(ccc==21)c.cbuffer[i]=(char)'+';
            else if(ccc==22)c.cbuffer[i]=(char)'+';
            else if(ccc==23)c.cbuffer[i]=(char)'+';
            else if(ccc==24)c.cbuffer[i]=(char)'+';
            else if(ccc==25)c.cbuffer[i]=(char)'|';
            else c.cbuffer[i]=(char)0x20;
          }
        }
      }
      graphics.fillRect(x, y-(font.ascent), 
			font.charsWidth(c.cbuffer, 0, len),
			font.ascent+font.descent);
      graphics.setColor(tmp);
      graphics.drawChars(c.cbuffer, 0, len, x, y);
    }

    if(d instanceof Window){
      ((Window)d).draw(x, y-(font.ascent), 
			font.charsWidth(c.cbuffer, 0, len),
			font.ascent+font.descent);
    }
    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqPolySegment(Client c, Drawable d, GC gc) 
    throws IOException{
    int foo;
    int n=c.length;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction|GC.GCSubwindowMode
				    |GC.GCLineWidth
				    );

    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(4); 
	  n--;
	}
	return;
      }
    }

    n/=2;
    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;
    while(n!=0){
      foo=c.xarray[0]=(short)c.client.readShort(); 
      if(foo<=sx)sx=foo;
      if(foo>=lx)lx=foo;
      foo=c.yarray[0]=(short)c.client.readShort();
      if(foo<=sy)sy=foo;
      if(foo>=ly)ly=foo;
      foo=c.xarray[1]=(short)c.client.readShort();
      if(foo<=sx)sx=foo;
      if(foo>=lx)lx=foo;
      foo=c.yarray[1]=(short)c.client.readShort(); 
      if(foo<=sy)sy=foo;
      if(foo>=ly)ly=foo;
      if(gc.lineWidth<=1)
	graphics.drawPolyline(c.xarray, c.yarray, 2);
      else{
	if(c.yarray[0]==c.yarray[1]){
	  graphics.drawPolyline(c.xarray, c.yarray, 2);
	  c.yarray[0]-=(gc.lineWidth/2);
	  c.yarray[1]-=(gc.lineWidth/2);
	  for(int i=0; i<gc.lineWidth; i++){
	    graphics.drawPolyline(c.xarray, c.yarray, 2);
	    c.yarray[0]++;
	    c.yarray[1]++;
	  }
	}
	else{
	  drawThickLine(graphics, c.xarray[0], c.yarray[0],
			c.xarray[1], c.yarray[1], gc.lineWidth);
	}
      }
      n--;
    }
    if(sx<0)sx=0;
    if(sy<0)sy=0;

    if(d instanceof Window){
      if((gc.attr&GC.IncludeInferiors)==0){
	((Window)d).draw(sx, sy, lx-sx+1, ly-sy+1);
      }
    }
    else{
      ((Pixmap)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }

    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  private static void drawThickLine(Graphics graphics, int x1, int y1,
			    int x2, int y2, int linewidth){
    --linewidth;
    int lw2=linewidth/2;
    graphics.fillOval(x1-lw2,y1-lw2,linewidth,linewidth);
    if(x1==x2&&y1==y2)
      return;
    if(Math.abs(x2-x1)>Math.abs(y2-y1)){
      int dy,srow;
      int dx,col,row,prevrow;
      if(x2>x1)
	dx = 1;
      else
	dx = -1;
      dy=(y2-y1)*8192/Math.abs(x2-x1);
      prevrow=row=y1;
      srow=row*8192+8192/2;
      col=x1;
      while(true){
        if(row!=prevrow){
	  graphics.drawOval(col-lw2,prevrow-lw2,linewidth,linewidth);
	  prevrow=row;
	}
	graphics.drawOval(col-lw2,row-lw2,linewidth,linewidth);
	if(col == x2)
	  break;
	srow+=dy;
	row=srow/8192;
	col+=dx;
      }
    }
    else{
      int dx,scol;
      int dy,col,row,prevcol;
      if(y2>y1)
	dy=1;
      else
	dy=-1;
      dx=(x2-x1)*8192/Math.abs(y2-y1);
      row=y1;
      prevcol=col=x1;
      scol=col*8192+8192/2;
      while(true){
	if(col!=prevcol){
	  graphics.drawOval(prevcol-lw2,row-lw2,linewidth,linewidth);
	  prevcol=col;
	}
	graphics.drawOval(col-lw2,row-lw2,linewidth,linewidth);
	if(row==y2)
	  break;
	row+=dy;
	scol+=dx;
	col=scol/8192;
      }
    }
  }

  static void reqPolyLine(Client c, Drawable d, GC gc) throws IOException{
    int n=c.length;
    int foo;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction|GC.GCSubwindowMode
				    |GC.GCLineWidth
				    );
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    int mode=c.data;

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(4); 
	  n--;
	}
	return;
      }
    }

    short x, y;
    if(c.xarray.length<n){
      c.xarray=new int[n];
      c.yarray=new int[n];
    }
    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;
    foo=c.xarray[0]=(short)c.client.readShort(); 
    if(foo<=sx)sx=foo;
    if(foo>=lx)lx=foo;
    foo=c.yarray[0]=(short)c.client.readShort();
    if(foo<=sy)sy=foo;
    if(foo>=ly)ly=foo;
    for(int i=1;i<n;i++){
      c.xarray[i]=(short)c.client.readShort(); 
      c.yarray[i]=(short)c.client.readShort();
      if(mode==1){
	c.xarray[i]+=c.xarray[i-1]; c.yarray[i]+=c.yarray[i-1];
      }
      foo=c.xarray[i];
      if(foo<=sx)sx=foo;
      if(foo>=lx)lx=foo;
      foo=c.yarray[i];
      if(foo<=sy)sy=foo;
      if(foo>=ly)ly=foo;
    }

    if(gc.lineWidth>1&&n>1){
      --n;
      for(int i=0;i<n;i++){
        int j=i+1;
        drawThickLine(graphics, c.xarray[i], c.yarray[i],
		      c.xarray[j], c.yarray[j], gc.lineWidth);
      }
    }
    else
      graphics.drawPolyline(c.xarray, c.yarray, n);

    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }
    else{
      ((Pixmap)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }

    if(gc.function==GC.GXxor || gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqFillPoly(Client c, Drawable d, GC gc) throws IOException{
    int n=c.length;
    int foo;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction|GC.GCSubwindowMode
				    );
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    byte shape;
    shape=(byte)c.client.readByte();
    byte cmode;
    cmode=(byte)c.client.readByte();
    c.client.readPad(2);

    n--;

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(4); 
	  n--;
	}
	return;
      }
    }

    short x, y;

    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;

    if(c.xarray.length<n){
      c.xarray=new int[n];
      c.yarray=new int[n];
    }

    foo=c.xarray[0]=(short)c.client.readShort(); 
    if(foo<sx)sx=foo;
    if(lx<foo)lx=foo;

    foo=c.yarray[0]=(short)c.client.readShort();
    if(foo<sy)sy=foo;
    if(ly<foo)ly=foo;

    for(int i=1;i<n;i++){
      c.xarray[i]=(short)c.client.readShort(); 
      c.yarray[i]=(short)c.client.readShort();
      if(cmode==1){
	c.xarray[i]+=c.xarray[i-1];
	c.yarray[i]+=c.yarray[i-1];
      }
      foo=c.xarray[i];
      if(foo<sx)sx=foo;
      if(lx<foo)lx=foo;

      foo=c.yarray[i];
      if(foo<sy)sy=foo;
      if(ly<foo)ly=foo;
    }
    graphics.fillPolygon(c.xarray, c.yarray, n);
    if(sx<0)sx=0;
    if(sy<0)sy=0;
    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+1, ly-sy+1);
    }
    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqPolyRectangle(Client c, Drawable d, GC gc) 
    throws IOException{

    int n=c.length;
    int foo;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction|GC.GCSubwindowMode
				    );
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(4); 
	  n--;
	}
	return;
      }
    }

    n/=2;

    short x, y;
    int ww, hh;

    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;
    
    while(n!=0){
      x=(short)c.client.readShort(); y=(short)c.client.readShort();
      ww=c.client.readShort(); hh=c.client.readShort();

      if(x<sx)sx=x;
      if(lx<x+ww)lx=x+ww;
      if(y<sy)sy=y;
      if(ly<y+hh)ly=y+hh;

      if(gc.lineWidth>1){
	int l2=gc.lineWidth/2;
	x-=l2; y-=l2;
	ww+=gc.lineWidth; hh+=gc.lineWidth;
	for(int i=0; i<gc.lineWidth; i++){
	  ww-=2; hh-=2; x+=1; y+=1;
	  graphics.drawRect(x, y, ww, hh);
	}
      }
      else{
	graphics.drawRect(x, y, ww, hh);
      }
      n--;
    }

    if(sx<0)sx=0;
    if(sy<0)sy=0;
    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+2, ly-sy+2);
    }
    if(gc.function==GC.GXxor ||
       gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }
    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }

  static void reqPolyFillRectangle(Client c, Drawable d, GC gc) 
    throws IOException{
    int n=c.length;
    int foo;

    Graphics graphics=d.getGraphics(gc, 
				    GC.GCFunction|GC.GCSubwindowMode
				    );
    if(graphics==null){
      c.client.readPad(n*4); 
      return;
    }

    if(gc.clip_mask!=null &&
       gc.clip_mask instanceof ClipPixmap &&
       (d instanceof Pixmap) &&
       ((Pixmap)d).data!=null
       ){
      Pixmap p=(Pixmap)d;
      byte[] data=p.data;

      Pixmap cpixmap=(Pixmap)(gc.clip_mask.getMask());

      short x, y;
      int ww, hh;

      byte f=(byte)gc.fgPixel;

      while(n!=0){
	x=(short)c.client.readShort(); 
	y=(short)c.client.readShort();
	ww=c.client.readShort(); 
	hh=c.client.readShort();
	n-=2;

        if(x<0){ww+=x; x=0;}
        if(y<0){hh+=y; y=0;}
        if(d.width<=x || d.height<=y) continue;
        if(ww<=0 || hh<=0) continue;
//      if((x+ww)<=0 || (y+hh)<=0) continue;

	if(ww>cpixmap.width) ww=cpixmap.width;
	if(hh>cpixmap.height) hh=cpixmap.height;
	if(ww>d.width) ww=d.width;
	if(hh>d.height) hh=d.height;
	for(int i=0;i<hh;i++){
	  for(int j=0;j<ww;j++){
	    if(p.data[(i+y)*p.width+x+j]==0){
	      p.data[(i+y)*p.width+x+j]=f;
	    }
	  }
	}
      }
      return;
    }

/*
    if(gc.clip_mask!=null &&
       (d instanceof Pixmap) &&
       gc.clip_mask.width==d.width &&
       gc.clip_mask.height==d.height &&
       ((Pixmap)d).data!=null){

      Pixmap p=(Pixmap)d;

//      for(int i=0;i<p.height;i++){
//	for(int j=0;j<p.width;j++){
//	  System.out.print(p.data[i*p.width+j]+" ");
//	}
//	System.out.println("");
//      }

      byte f=(byte)gc.fgPixel;
      for(int i=0;i<gc.clip_mask.height;i++){
	for(int j=0;j<gc.clip_mask.width;j++){
	  if(p.data[i*gc.clip_mask.width+j]==0){
	    p.data[i*gc.clip_mask.width+j]=f;
	  }
	}
      }

//      for(int i=0;i<((Pixmap)d).height;i++){
//	for(int j=0;j<((Pixmap)d).width;j++){
//	  System.out.print(((Pixmap)d).data[i*((Pixmap)d).width+j]+" ");
//	}
//	System.out.println("");
//      }

      while(n!=0){
	c.client.readPad(8); n-=2;
      }
      return;
    }
*/

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      java.awt.Rectangle rec=(Rectangle)(gc.clip_mask.getMask());
      if(rec==null){
	while(n>0){
	  c.client.readPad(8); 
	  n-=2;
	}
	return;
      }
    }

    Pixmap p=null;
    if(((gc.attr&GC.fillStyle)==GC.FillOpaqueStippled) ||
       ((gc.attr&GC.fillStyle)==GC.FillStippled)){
      p=gc.stipple;
    }
    if(((gc.attr&GC.fillStyle)==GC.FillTiled)){
      p=gc.tile.pixmap;
    }

    short x, y;
    int ww, hh;

    int sx=d.width;
    int sy=d.height;
    int lx=0;
    int ly=0;

    while(n!=0){
      x=(short)c.client.readShort(); 
      y=(short)c.client.readShort();
      ww=c.client.readShort(); 
      hh=c.client.readShort();

      n-=2;

      if(x<0){ww+=x; x=0;}
      if(y<0){hh+=y; y=0;}
      if(d.width<=x || d.height<=y) continue;
      if(ww<=0 || hh<=0) continue;

      if(x<sx)sx=x;;
      if(lx<x+ww)lx=x+ww;
      if(y<sy)sy=y;
      if(ly<y+hh)ly=y+hh;

      if(((gc.attr&GC.fillStyle)==GC.FillOpaqueStippled) ||
	 ((gc.attr&GC.fillStyle)==GC.FillStippled) ||
	 ((gc.attr&GC.fillStyle)==GC.FillTiled)){

        java.awt.Shape tmp=null;
	ww+=x; hh+=y; 
        if((p.width>ww-x)||(p.height>hh-y)){
	  tmp=graphics.getClip();
	  graphics.clipRect(x, y, ww-x, hh-y);
	}
	graphics.drawImage(p.img, x, y, Screen.screen[0].root.ddxwindow);
	if(tmp!=null) graphics.setClip(tmp);
	for(int i=x+p.width;i<ww;i+=p.width){
	  int www=p.width;
	  int hhh=p.height;
	  if(i+www>ww)  www=ww-i;
	  if(y+hhh>=hh) hhh=hh-y;
	  graphics.copyArea(x, y, www, hhh, i-x, 0);
	}
	for(int j=y+p.height;j<hh;j+=p.height){
	  int hhh=p.height;
	  if(j+hhh>=hh) hhh=hh-j;
	  graphics.copyArea(x, y, ww-x, hhh, 0, j-y);
	}
      }
      else{
	graphics.fillRect(x, y, ww, hh);
      }
    }

    if(sx<0)sx=0;
    if(sy<0)sy=0;
    if(d instanceof Window){
      ((Window)d).draw(sx, sy, lx-sx+2, ly-sy+2);
    }

    if(gc.function==GC.GXxor || gc.function==GC.GXinvert){
      graphics.setPaintMode();
    }

    if(gc.clip_mask!=null && gc.clip_mask instanceof ClipRectangles){
      d.restoreClip();
    }
  }
}
