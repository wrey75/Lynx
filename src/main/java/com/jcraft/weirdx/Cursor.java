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
import java.io.*;
import java.util.*;

final class Cursor extends Resource{
  static Cursor rootCursor;
  java.awt.Cursor cursor;

  private static int[] cursors={
    java.awt.Cursor.DEFAULT_CURSOR,
    java.awt.Cursor.CROSSHAIR_CURSOR,
    java.awt.Cursor.TEXT_CURSOR,
    java.awt.Cursor.WAIT_CURSOR,
    java.awt.Cursor.SW_RESIZE_CURSOR,
    java.awt.Cursor.SE_RESIZE_CURSOR,
    java.awt.Cursor.NW_RESIZE_CURSOR,
    java.awt.Cursor.NE_RESIZE_CURSOR,
    java.awt.Cursor.N_RESIZE_CURSOR,
    java.awt.Cursor.S_RESIZE_CURSOR,
    java.awt.Cursor.W_RESIZE_CURSOR,
    java.awt.Cursor.E_RESIZE_CURSOR,
    java.awt.Cursor.HAND_CURSOR,
    java.awt.Cursor.MOVE_CURSOR
  };
  Cursor(int id){
    super(id, RT_CURSOR);
    cursor=java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR);
  }
  Cursor(int id, Pixmap src, Pixmap msk,
	 int fr, int fg, int fb,
	 int br, int bg, int bb,
	 int hx, int hy){
    super(id, RT_CURSOR);
    cursor=java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR);
  }
  private int getType(int i){
    int j=java.awt.Cursor.DEFAULT_CURSOR;
    switch(i){
    case /*XC_X_cursor*/ 0:
    case /*XC_arrow*/ 2:
      break;
    case /*XC_based_arrow_down*/ 4:
    case /*XC_based_arrow_up*/ 6:
    case /*XC_boat*/ 8:
    case /*XC_bogosity*/ 10:
    case /*XC_bottom_left_corner*/ 12:
      j=java.awt.Cursor.SW_RESIZE_CURSOR;
      break;
    case /*XC_bottom_right_corner*/ 14:
      j=java.awt.Cursor.SE_RESIZE_CURSOR;
      break;
    case /*XC_bottom_side*/ 16:
      j=java.awt.Cursor.S_RESIZE_CURSOR;
      break;
    case /*XC_bottom_tee*/ 18:
      j=java.awt.Cursor.S_RESIZE_CURSOR;
      break;
    case /*XC_box_spiral*/ 20:
    case /*XC_center_ptr*/ 22:
    case /*XC_circle*/ 24:
      break;
    case /*XC_clock*/ 26:
      j=java.awt.Cursor.WAIT_CURSOR;
      break;
    case /*XC_coffee_mug*/ 28:
      break;
    case /*XC_cross*/ 30:
    case /*XC_cross_reverse*/ 32:
    case /*XC_crosshair*/ 34:
      j=java.awt.Cursor.CROSSHAIR_CURSOR;
      break;
    case /*XC_diamond_cross*/ 36:
    case /*XC_dot*/ 38:
    case /*XC_dotbox*/ 40:
    case /*XC_double_arrow*/ 42:
    case /*XC_draft_large*/ 44:
    case /*XC_draft_small*/ 46:
    case /*XC_draped_box*/ 48:
    case /*XC_exchange*/ 50:
      break;
    case /*XC_fleur*/ 52:
      j=java.awt.Cursor.MOVE_CURSOR;
      break;
    case /*XC_gobbler*/ 54:
    case /*XC_gumby*/ 56:
      break;
    case /*XC_hand1*/ 58:
    case /*XC_hand2*/ 60:
      j=java.awt.Cursor.HAND_CURSOR;
      break;
    case /*XC_heart*/ 62:
    case /*XC_icon*/ 64:
    case /*XC_iron_cross*/ 66:
    case /*XC_left_ptr*/ 68:
      break;
    case /*XC_left_side*/ 70:
    case /*XC_left_tee*/ 72:
      j=java.awt.Cursor.W_RESIZE_CURSOR;
      break;
    case /*XC_leftbutton*/ 74:
    case /*XC_ll_angle*/ 76:
    case /*XC_lr_angle*/ 78:
    case /*XC_man*/ 80:
    case /*XC_middlebutton*/ 82:
    case /*XC_mouse*/ 84:
    case /*XC_pencil*/ 86:
    case /*XC_pirate*/ 88:
    case /*XC_plus*/ 90:
    case /*XC_question_arrow*/ 92:
    case /*XC_right_ptr*/ 94:
      break;
    case /*XC_right_side*/ 96:
    case /*XC_right_tee*/ 98:
      j=java.awt.Cursor.E_RESIZE_CURSOR;
      break;
    case /*XC_rightbutton*/ 100:
    case /*XC_rtl_logo*/ 102:
    case /*XC_sailboat*/ 104:
    case /*XC_sb_down_arrow*/ 106:
    case /*XC_sb_h_double_arrow*/ 108:
    case /*XC_sb_left_arrow*/ 110:
    case /*XC_sb_right_arrow*/ 112:
    case /*XC_sb_up_arrow*/ 114:
    case /*XC_sb_v_double_arrow*/ 116:
    case /*XC_shuttle*/ 118:
    case /*XC_sizing*/ 120:
    case /*XC_spider*/ 122:
    case /*XC_spraycan*/ 124:
    case /*XC_star*/ 126:
    case /*XC_target*/ 128:
    case /*XC_tcross*/ 130:
    case /*XC_top_left_arrow*/ 132:
      break;
    case /*XC_top_left_corner*/ 134:
      j=java.awt.Cursor.NW_RESIZE_CURSOR;
      break;
    case /*XC_top_right_corner*/ 136:
      j=java.awt.Cursor.NE_RESIZE_CURSOR;
      break;
    case /*XC_top_side*/ 138:
    case /*XC_top_tee*/ 140:
      j=java.awt.Cursor.N_RESIZE_CURSOR;
      break;
    case /*XC_trek*/ 142:
    case /*XC_ul_angle*/ 144:
    case /*XC_umbrella*/ 146:
    case /*XC_ur_angle*/ 148:
      break;
    case /*XC_watch*/ 150:
      j=java.awt.Cursor.WAIT_CURSOR;
      break;
    case /*XC_xterm*/ 152:
      j=java.awt.Cursor.TEXT_CURSOR;
      break;
    default:
    }
    return j;
  }
  Cursor(int id, Font src, Font msk,
	 int srcc, int mskc,
	 int fr, int fg, int fb,
	 int br, int bg, int bb){
    super(id, RT_CURSOR);
    cursor=java.awt.Cursor.getPredefinedCursor(getType(srcc));
  }

  static void reqCreateGlyphCursor(Client c) throws IOException{
    int n, foo, cid, fr, fg, fb, br, bg, bb;
    int x, y;
    Resource r;
    int srcc, mskc;
    Font src, msk;
    cid=c.client.readInt();
    foo=c.client.readInt();
    c.length-=3;
    src=(Font)Resource.lookupIDByType(foo, Resource.RT_FONT);
    if(src==null){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return; 
    }
    foo=c.client.readInt();
    c.length--;
    msk=(Font)Resource.lookupIDByType(foo, Resource.RT_FONT);
    if(msk==null){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return; 
    }
    srcc=(short)c.client.readShort();
    mskc=(short)c.client.readShort();

    fr=(short)c.client.readShort();
    fg=(short)c.client.readShort();
    fb=(short)c.client.readShort();
    br=(short)c.client.readShort();
    bg=(short)c.client.readShort();
    bb=(short)c.client.readShort();

    Cursor cur=new Cursor(cid, src, msk, srcc, mskc, 
			  fr, fg, fb, br, bg, bb);
    Resource.add(cur);
  }
  static void reqFreeCursor(Client c) throws IOException{
    int foo;
    foo=c.client.readInt();
    c.length-=2;
    Cursor cur=(Cursor)Resource.lookupIDByType(foo, Resource.RT_CURSOR);
    if(cur==null){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return; 

    }
    Resource.freeResource(foo, Resource.RT_NONE);
  }
  void recolor(int fr, int fg, int fb, int br, int bg, int bb){
  }
  void delete() throws IOException{
    cursor=null;
  }
  static void reqRecolorCursor(Client c) throws IOException{
    int n, foo, cid, fr, fg, fb, br, bg, bb;
    int x, y;
    Resource r;
    Pixmap src, msk;
    foo=c.client.readInt();
    c.length-=2;
    Cursor cur=(Cursor)Resource.lookupIDByType(foo, Resource.RT_CURSOR);
    if(cur==null){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return; 
    }

    fr=(short)c.client.readShort();
    fg=(short)c.client.readShort();
    fb=(short)c.client.readShort();
    br=(short)c.client.readShort();
    bg=(short)c.client.readShort();
    bb=(short)c.client.readShort();

    cur.recolor(fr, fg, fb, br, bg, bb);
  }
  static void reqCreateCursor(Client c) throws IOException{
    int n, foo, cid, fr, fg, fb, br, bg, bb;
    int x, y;
    Resource r;
    Pixmap src, msk;

    cid=c.client.readInt();
    foo=c.client.readInt();
    c.length-=3;
    r=Resource.lookupIDByType(foo, RT_PIXMAP);
    if(r==null || !(r instanceof Pixmap)){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return; 
    }
    src=(Pixmap)r;

    foo=c.client.readInt();
    c.length--;
    r=Resource.lookupIDByType(foo, RT_PIXMAP);
    if(r==null || !(r instanceof Pixmap)){
      c.errorValue=foo;
      c.errorReason=4; // BadValue;
      return; 
    }
    msk=(Pixmap)r;

    fr=(short)c.client.readShort();
    fg=(short)c.client.readShort();
    fb=(short)c.client.readShort();
    br=(short)c.client.readShort();
    bg=(short)c.client.readShort();
    bb=(short)c.client.readShort();
    x=(short)c.client.readShort();
    y=(short)c.client.readShort();
    Cursor cur=new Cursor(cid, src, msk, fr, fg, fb, br, bg, bb, x, y);
    Resource.add(cur);
  }
}
