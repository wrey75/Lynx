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
import java.util.List;
import java.awt.*;
import java.awt.image.*;


//import com.sun.java.swing.*;

class DDXWindowImpSwingAlphaBackground extends DDXWindowImpSwing {

  static int dalpha=0x00;
  AlphaBackground alpha=null;
  static Map<Integer, Object> ctable = new HashMap<Integer, Object>();

  private static boolean init=true;

  DDXWindowImpSwingAlphaBackground(){
    super();
    if(init){
      String foo=WeirdX.alphaBackground;
      if(foo!=null){
        try{
          int balpha=Integer.parseInt(foo); 
          if((balpha&0xff)!=255)setAlpha(balpha&0xff);
        }
        catch(Exception e){setAlpha(foo);}
      }
      init=false;
    }
  }

  static void setAlpha(int alpha){
    dalpha=alpha;
  }

  static void setAlpha(String alphas){
    byte[] array=alphas.getBytes();
    int end=array.length;
    int start=0, current=0;
    List<Integer> tmp=new ArrayList<Integer>();
    try{
      while(current<end){
        if(array[current]==' ' ||
           array[current]==',' ||
           array[current]==':'){
          tmp.add(getVal(array, start, current-start));
          current++;
          while(current<end){
            if(array[current]==' ' ||
               array[current]==',' ||
               array[current]==':'){
              current++;
   	      continue;
	    }
            break;
	  }
          if(current>=end){break;}
          start=current;
        }
        current++;
      }
      if(current!=start){
        tmp.add(getVal(array, start, current-start));
      }
    }
    catch(Exception e){
      //System.out.println(e);
    }
    if(tmp.size()==1){
      dalpha=((Integer)(tmp.get(0))).intValue();
    }
    else{
      dalpha=255;
      int i=0;
      while(i<tmp.size()){
        ctable.put(tmp.get(i), tmp.get(i+1));
        i+=2;
      }
    }
  }

  public void setSize(int w, int h){
    boolean skip=true;
    if(w!=this.width || h!=this.height) skip=false;
    if(bwc)skip=false;
    super.setSize(w, h);
    if(!skip && alpha!=null){ alpha.setImage(offi); }
  }

  public void setBackground(Color color, int x, int y, int w, int h){
    super.setBackground(color, x, y, w, h);
    if(alpha!=null){
      int foo=dalpha;
      int bar=(getBackground().getRGB()&0xffffff);
      Object o=ctable.get(new Integer(bar));
      if(o!=null) foo=((Integer)o).intValue();
      if(foo!=255){
        alpha.setColor(color, foo); 
      }
      else{
        alpha=null;
      }
    }
  }

  public void draw(int x, int y, int width, int height){
    super.draw(x, y, width, height);
    if(!isVisible()) return;
    if(alpha==null){
      if(//dalpha!=255 && 
         window!=window.screen.root && 
         (window.attr&3/*backgroundState*/)==2/*BackgroundPixel*/
        ){
        int foo=dalpha;
        int color=(getBackground().getRGB()&0xffffff);
        Object o=ctable.get(new Integer(color));
        //System.out.println(getBackground()+", "+o);
        if(o!=null) foo=((Integer)o).intValue();
        if(foo!=255){
  	  alpha=new AlphaBackground(offi, foo);
	  alpha.setColor(getBackground(), foo);
	}
      }
    }
    else{
      alpha.setImage(offi);
    }
  }

  public void draw(){
    super.draw();
    if(!isVisible()) return;
    if(alpha==null){
      if(//dalpha!=255 && 
         window!=window.screen.root && 
         (window.attr&3/*backgroundState*/)==2/*BackgroundPixel*/
        ){
        int foo=dalpha;
        int color=(getBackground().getRGB()&0xffffff);
        Object o=ctable.get(new Integer(color));
        if(o!=null) foo=((Integer)o).intValue();
        if(foo!=255){
  	  alpha=new AlphaBackground(offi, foo);
	  alpha.setColor(getBackground(), foo);
	}
      }
    }
    else{
      alpha.setImage(offi);
    }
  }

  public void paintComponent(Graphics g){
    if(window.clss==2/*InputOnly*/){
      super.paintComponent(g);
      return;
    }
    if(offi==null) return;

    if(alpha!=null){
      alpha.drawImage(g, bw, bw, this);
    }
    else{
      g.drawImage(offi, bw, bw, this);
    }

    Rectangle r=g.getClip().getBounds();
    if(bw>0 &&
       (r.x<=bw || r.y<=bw || 
	(width+bw)<=(r.width+r.x) ||
	(height+bw)<=(r.height+r.y)) ){
      if(window.isBorderPixel()){
	g.setColor(window.getColormap().getColor(window.border.pixel));
      }
      else{
	g.setColor(Color.black);
      }
      for(int i=bw-1;0<=i; i--){
	g.drawRect(i, i, width+2*bw-i*2-1, height+2*bw-i*2-1);
      }
    }
  }

  public boolean isOpaque(){
    if(isVisible()){
      if(window.clss==2/*InputOnly*/){
	return false;
      }
      if(alpha!=null) return false;
      return super.isOpaque();
    }
    return false;
  }

  public void delete() throws IOException{
    super.delete();
    if(alpha!=null){
      alpha.dispose();
      alpha=null;
    }
  }

  private static Integer getVal(byte[]array, int s, int len){
    String foo=new String(array, s, len);
    int radix=10;
    if(foo.startsWith("0x")){
      radix=16;
      foo=new String(array,s+2,len-2);
    }
    int i=0;
    try{
      i=Integer.parseInt(foo, radix);
    }
    catch(Exception e){}
    return new Integer(i);
  }

  public Window getWindow(){
    return window;
  }
}

class AlphaBackground{
  Image offi;
  Image filteredi=null;
  AlphaFilter filter=null;
  Color background=null;
  int x, y, width, height;
  int alpha;
  AlphaBackground(Image offi, int alpha){
    this.offi=offi;
    this.alpha=alpha;
    filter=new AlphaFilter(alpha);
  }
  void setImage(Image img){
    this.offi=img;
    freeImage();
    filteredi=null;
  }
  void setCrop(int x, int y, int width, int height){
    if(x==this.x && y==this.y && width==this.width && height==this.height)
      return;
    this.x=x; this.y=y; this.width=width; this.height=height;
    freeImage();
    filteredi=null;
  }
  void setColor(Color c, int alpha){
    if(background!=null && background.equals(c)) return;
    background=c;
    this.alpha=alpha;
    filter.setColor(c, alpha);
    freeImage();
    filteredi=null;
  }
  synchronized void freeImage(){
    if(filteredi!=null)filteredi.flush();
  }
  Image getImage(){
    if(filteredi==null){
      if(filter==null){ filter=new AlphaFilter(alpha); }
      filteredi=Toolkit.getDefaultToolkit().
        createImage(new FilteredImageSource(offi.getSource(), 
					    filter));
    }
    return filteredi;
  }
  void dispose(){
    finalize();
  }
  synchronized void drawImage(Graphics g, int xx, int yy, ImageObserver io){
    //Rectangle r = g.getClipBounds();
    Image i = getImage();
    g.drawImage(i, xx, yy, io);
  }
  
  public void finalize(){
    offi=null;
    freeImage();
    filteredi=null;
  }
}

class AlphaFilter extends RGBImageFilter {
  int color=0xffffffff/*Color.white.getRGB()|0xff000000*/;
  int alphacolor=(color&0xffffff);
  int alpha=0;
  AlphaFilter(int alpha){
    canFilterIndexColorModel=true; 
    this.alpha=(alpha<<24);
  }
  void setColor(Color c, int alpha){
    this.alpha=(alpha<<24);
    this.setColor(c);
  }
  void setColor(Color c){
    color=c.getRGB()|0xff000000;
    alphacolor=(color&0xffffff)|alpha;
  }
  public int filterRGB(int x, int y, int rgb){
    //System.out.println("x="+x+",y="+y);
    if(rgb==color){ return alphacolor;}
    return rgb;
  }
}
