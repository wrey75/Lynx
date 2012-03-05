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

import java.net.*;
import java.awt.image.*;

final class LogoImage{
  static Screen screen;
  static String name="weirdx.jpg";
  static Image logoimage=null;
  static int logoimagex;
  static int logoimagey;
  static int logoimagewidth;
  static int logoimageheight;

  static void init(Screen[] scrn){
    screen=scrn[0];
  }
  static void storeImage(Image img){
    logoimage=img;
    logoimagewidth=90;
    logoimageheight=30;
  }
  static boolean loadLogo(String _name){
    if(_name==null) return false;
    if(logoimage==null || !name.equals(_name)){
      name=_name;
      if(logoimage!=null) logoimage.flush();
      try{
        URL url=LogoImage.class.getResource(name);
        Image img=Toolkit.getDefaultToolkit().
                          createImage((ImageProducer)url.getContent());
        storeImage(img);
      }
      catch(Exception e){
//      System.exit(-1);
      }
    }
    return logoimage!=null;
  }
  static boolean loadLogo(){
    return loadLogo(name);
  }
  static void toggle(){
    if(logoimage==null){ up(); }
    else{ down(); }
  }
  static void up(){
    if(logoimage==null){
      loadLogo();
    }
  }
  static void down(){
    if(logoimage!=null){
      Image img=logoimage;
      logoimage=null;
      img.flush();
    }
  }
}
