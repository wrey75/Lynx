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


import java.awt.*;
import java.applet.*;

class RootWindow extends Window{
  private static final int InputOutput=1;
  Container rootwindow;
  RootWindow(Container container, Screen screen, Format[] format, Client c) {
    super(screen.rootId);
    rootwindow=container;
    client=c;
    screen.setRoot((Window)this);
    this.width=(short)(screen.width);
    this.height=(short)(screen.height);
    this.screen=screen;
    depth=screen.rootDepth;
    id=screen.rootId;
    type=DRAWABLE_WINDOW;
    x=y=0;
    origin.x=0;
    origin.y=0;
    clss=(byte)InputOutput;
    for(int i=0; i<format.length;i++){
      if(format[i].depth==screen.rootDepth){
	this.bitsPerPixel=format[i].bpp;
      }
    }
    setVisual(screen.rootVisual);
    setBackgroundIsPixel();
    background.pixel=screen.white;

    setBorderIsPixel();
    border.pixel=screen.black;
    borderWidth=0;
    Resource.add(this);
    makeOptional();
    attr&=~(1<<3); //cursorIsNone

    optional.cursor=Cursor.rootCursor;
    setColormap(screen.defaultColormap);

//  if(rootwindow instanceof JFrame){
//    rootwindow.setSize(this.width+10, this.height+30); // ??
//  }
//  else{
      rootwindow.setSize(this.width, this.height);
//  }

    try{ddxwindow=(DDXWindow)(Window.dDXWindow.newInstance());}
    catch(Exception e){ 
      System.err.println(e);
      /*ddxwindow=new DDXWindowImp();*/ 
    }

    ddxwindow.init(this);                                     
    ddxwindow.setLocation(0, 0);

    if(rootwindow instanceof Frame){
      //((Frame)rootwindow).setLayout(null);
      ((Frame)rootwindow).setResizable(false);
      ((Frame)rootwindow).setMenuBar(null);
      ((Frame)rootwindow).add((java.awt.Component)ddxwindow);
    }
    else if (rootwindow instanceof Applet){
      ((Applet)rootwindow).add((java.awt.Component)ddxwindow);
    }
/*
    else if(rootwindow instanceof JFrame){
      ((JFrame)rootwindow).getContentPane().setLayout(null);
      ((JFrame)rootwindow).setResizable(false);
      ((JFrame)rootwindow).setJMenuBar(null);
      ((JFrame)rootwindow).getContentPane().add((java.awt.Component)ddxwindow);
    }
    else if(rootwindow instanceof JWindow){
      ((JWindow)rootwindow).getContentPane().setLayout(null);
      ((JWindow)rootwindow).getContentPane().add((java.awt.Component)ddxwindow);
    }
    else if (rootwindow instanceof JApplet){
      ((JApplet)rootwindow).setJMenuBar(null);
      ((JApplet)rootwindow).getContentPane().add((java.awt.Component)ddxwindow);
    } 
*/
    else{
      rootwindow.add((java.awt.Component)ddxwindow);
    }

    if(screen.windowmode!=WeirdX.InBrowser){
      rootwindow.addNotify();
    }
    else{
      rootwindow.setVisible(true);
    }
    ddxwindow.setVisible(true);

    {
      rootwindow.validate();
      Insets insets=rootwindow.getInsets();
      rootwindow.setSize(this.width+insets.left+insets.right,
                         this.height+insets.top+insets.bottom);
      ddxwindow.setLocation(insets.left, insets.top);
      rootwindow.validate();
    }

    ddxwindow.requestFocus();
    Window.focus.win=id;               

    Window.LOCK=rootwindow.getTreeLock();
    Client.LOCK=rootwindow.getTreeLock();
    Resource.LOCK=rootwindow.getTreeLock();

    spriteTrace[0]=this;
    sprite.win=this;
  }

  public boolean contains(int xx, int yy){
    return ddxwindow.contains(xx, yy);
  }
}
