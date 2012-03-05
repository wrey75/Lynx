/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Guess.
 *
 * Copyright (C) 2000-2004 JCraft, Inc.
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
import java.awt.event.*;
import java.awt.datatransfer.*;

final class CopyPaste implements ClipboardOwner{
  private static Clipboard clipboard=null;
  private static CopyPaste copypaste=null;
  private static boolean isOwner=true;
  static void init(){
    if(clipboard==null){
      try{ clipboard=Toolkit.getDefaultToolkit().getSystemClipboard(); }
      catch(Exception e){
	//System.out.println(e);
	clipboard=null;
      }
    }
    if(clipboard!=null && copypaste == null){
      copypaste=new CopyPaste();
      setString(" ");
    }
  }

  static void setString(String str){
    if(clipboard==null) return;
    StringSelection contents=new StringSelection(str);
    isOwner=true;
    clipboard.setContents(contents, copypaste);
  }

  static String getString(){
    if(clipboard==null) return null;
    Transferable content=clipboard.getContents(copypaste);
    if(content != null){
      try{ return (String)(content.getTransferData(DataFlavor.stringFlavor)); }
      catch(Exception e){ 
        //System.out.println(e);
      }
    }
    return null;
  }

  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    isOwner=false;
  }

  static boolean isOwner(){ return isOwner; }
  //static void setOwner(boolean foo){ isOwner=foo; }
}
