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
import java.awt.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//import com.sun.java.swing.*;
import javax.swing.*;                                    

class DDXWindowImpSwingAlphaBackground2D extends DDXWindowImpSwing{
  private static float balpha=0.0f;
  boolean alpha=false;

  private static boolean init=true;
         
  static AlphaComposite ac=AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
						      balpha); 
  static void setAlpha(int alpha){
    balpha=((float)alpha)/255.0f;
    ac=AlphaComposite.getInstance(AlphaComposite.SRC_OVER, balpha); 
  }

  DDXWindowImpSwingAlphaBackground2D(){
    super();
    if(init){
      String foo=WeirdX.alphaBackground;
      if(foo!=null){
        try{
          int balpha=Integer.parseInt(foo); 
          if((balpha&0xff)!=255)setAlpha(balpha&0xff);
        }
        catch(Exception e){}
      }
      init=false;
    }
  }

  public void setBackground(Color color, int x, int y, int w, int h){
    super.setBackground(color, x, y, w, h);
    if(!alpha) alpha=true;
  }

  public void paintComponent(Graphics g){
    if(window.clss==2/*InputOnly*/){
      super.paintComponent(g);
      return;
    }
    if(offi==null) return;

    if(alpha){
      ((Graphics2D)g).setComposite(ac);
      g.drawImage(offi, bw, bw, this);
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
      if(alpha) return false;
      return true;
    }
    return false;
  }

  public Window getWindow(){
    return window;
  }
}
