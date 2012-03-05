/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - This class is used in clipping-pixmap operation.
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
import java.awt.*;
import java.awt.image.*;

final class TransparentFilter extends RGBImageFilter {
  int width;
  int height;
  byte[] pixels;
  int cx;
  int cy;

  TransparentFilter(int cx, int cy, Pixmap pixmap){
    this.cx=cx;
    this.cy=cy;

    this.width=pixmap.width;
    this.height=pixmap.height;

    pixels=new byte[pixmap.width*pixmap.height];
    if(pixmap.data!=null){
      for(int i=0; i<pixmap.height; i++){
	for(int j=0; j<pixmap.width; j++){
	  if(pixmap.data[i*pixmap.width+j]==0){
	    pixels[i*pixmap.width+j]=0;
	  }
	  else{ pixels[i*pixmap.width+j]=1; }
	}
      }
    }
    else{
      int[] ipixels=new int[pixmap.width*pixmap.height];
      Image img=pixmap.img;
      PixelGrabber pg=null;
      pg=new PixelGrabber(img, 0, 0, 
			  pixmap.width, pixmap.height,
			  ipixels, 0, pixmap.width);
      try {
	pg.grabPixels();
      } 
      catch (InterruptedException e) {
	//System.err.println("interrupted waiting for pixels!");
	return;
      }

      if ((pg.getStatus()&ImageObserver.ABORT)!=0) {
	System.err.println("image fetch aborted or errored");
	return;
      }

      for(int i=0; i<pixmap.height; i++){
	for(int j=0; j<pixmap.width; j++){
	  if(ipixels[i*pixmap.width+j]==0xff000000){
	    pixels[i*pixmap.width+j]=0;
	  }
	  else{ pixels[i*pixmap.width+j]=1; }
	}
      }
      ipixels=null;
    }
  }

  public int filterRGB(int x, int y, int rgb) {
    if(cx<=x && x<cx+width && cy<=y && y<cy+height){
      if(pixels[(y-cy)*width+(x-cx)]==0) rgb=0x00ffffff;
    }
    return rgb;
  }
}
