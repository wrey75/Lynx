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
import java.awt.event.KeyEvent;
import java.net.*;

class Keymap implements Serializable{
  static Keymap km=null;
  int start;
  int width;
  int count;
  int[] map;
  static byte state=0;

  Keymap(){
  }
  Keymap(int start, int width, int count){
    this.start=start;
    this.width=width;
    this.count=count;
    map=new int[count*width];
  }

  int getCode(KeyEvent e){
    if(e.isShiftDown()) state|=1;
    if(e.isControlDown()) state|=4;
    if(e.isAltDown()) state|=8;
    int key=e.getKeyCode();
    if(key!=0){
      switch(key){
      case KeyEvent.VK_A:
      case KeyEvent.VK_B:
      case KeyEvent.VK_C:
      case KeyEvent.VK_D:
      case KeyEvent.VK_E:
      case KeyEvent.VK_F:
      case KeyEvent.VK_G:
      case KeyEvent.VK_H:
      case KeyEvent.VK_I:
      case KeyEvent.VK_J:
      case KeyEvent.VK_K:
      case KeyEvent.VK_L:
      case KeyEvent.VK_M:
      case KeyEvent.VK_N:
      case KeyEvent.VK_O:
      case KeyEvent.VK_P:
      case KeyEvent.VK_Q:
      case KeyEvent.VK_R:
      case KeyEvent.VK_S:
      case KeyEvent.VK_T:
      case KeyEvent.VK_U:
      case KeyEvent.VK_V:
      case KeyEvent.VK_W:
      case KeyEvent.VK_X:
      case KeyEvent.VK_Y:
      case KeyEvent.VK_Z:
	key=key+0x20;
	break;
      case KeyEvent.VK_0:
      case KeyEvent.VK_1:
      case KeyEvent.VK_2:
      case KeyEvent.VK_3:
      case KeyEvent.VK_4:
      case KeyEvent.VK_5:
      case KeyEvent.VK_6:
      case KeyEvent.VK_7:
      case KeyEvent.VK_8:
      case KeyEvent.VK_9:
       break;
      case KeyEvent.VK_ENTER:
	key=0xff0d; break;
      case KeyEvent.VK_BACK_SPACE:
	key=0xff08; break;
      case KeyEvent.VK_TAB:
	key=0xff09; break;
//      case KeyEvent.VK_CANCEL:
//	key=0xff69;
//	break;
//      case KeyEvent.VK_CLEAR:
//	key=0xff0b;
//	break;
      case KeyEvent.VK_COMMA:
      case KeyEvent.VK_PERIOD:
      case KeyEvent.VK_SLASH:
      case KeyEvent.VK_SEMICOLON:
      case KeyEvent.VK_EQUALS:
      case KeyEvent.VK_OPEN_BRACKET:
      case KeyEvent.VK_BACK_SLASH:
      case KeyEvent.VK_CLOSE_BRACKET:
      case KeyEvent.VK_SPACE:
	break;
      case KeyEvent.VK_BACK_QUOTE:
	key=0x60; break;
      case KeyEvent.VK_QUOTE:
	key=0x27; break;
      case KeyEvent.VK_SHIFT:
	key=0xffe1; break;
      case KeyEvent.VK_CONTROL:
	key=0xffe3; break;
      case KeyEvent.VK_ALT:
	key=0xffe9; break;
      case KeyEvent.VK_PAUSE:
	key=0xff13; break;
      case KeyEvent.VK_CAPS_LOCK:
	key=0xffe5; break;
      case KeyEvent.VK_ESCAPE:
	key=0xff1b; break;
      case KeyEvent.VK_PAGE_UP:
	key=0xff55; break;
      case KeyEvent.VK_PAGE_DOWN:
	key=0xff56; break;
      case KeyEvent.VK_END:
	key=0xff57; break;
      case KeyEvent.VK_HOME:
	key=0xff50; break;
      case KeyEvent.VK_LEFT:
	key=0xff51; break;
      case KeyEvent.VK_UP:
	key=0xff52; break;
      case KeyEvent.VK_RIGHT:
	key=0xff53; break;
      case KeyEvent.VK_DOWN:
	key=0xff54; break;
      case KeyEvent.VK_NUMPAD0:
	key=0xffb0; break;
      case KeyEvent.VK_NUMPAD1:
	key=0xffb1; break;
      case KeyEvent.VK_NUMPAD2:
	key=0xffb2; break;
      case KeyEvent.VK_NUMPAD3:
	key=0xffb3; break;
      case KeyEvent.VK_NUMPAD4:
	key=0xffb4; break;
      case KeyEvent.VK_NUMPAD5:
	key=0xffb5; break;
      case KeyEvent.VK_NUMPAD6:
	key=0xffb6; break;
      case KeyEvent.VK_NUMPAD7:
	key=0xffb7; break;
      case KeyEvent.VK_NUMPAD8:
	key=0xffb8; break;
      case KeyEvent.VK_NUMPAD9:
	key=0xffb9; break;
      case KeyEvent.VK_MULTIPLY:
	key=0xffaa; break;
      case KeyEvent.VK_ADD:
	key=0xffab; break;
      case KeyEvent.VK_SEPARATER:
	key=0xffac; break;
      case KeyEvent.VK_SUBTRACT:
	key=0xffad; break;
      case KeyEvent.VK_DECIMAL:
	key=0xffae; break;
      case KeyEvent.VK_DIVIDE:
	key=0xffaf; break;
      case KeyEvent.VK_F1:
	key=0xffbe; break;
      case KeyEvent.VK_F2:
	key=0xffbf; break;
      case KeyEvent.VK_F3:
	key=0xffc0; break;
      case KeyEvent.VK_F4:
	key=0xffc1; break;
      case KeyEvent.VK_F5:
	key=0xffc2; break;
      case KeyEvent.VK_F6:
	key=0xffc3; break;
      case KeyEvent.VK_F7:
	key=0xffc4; break;
      case KeyEvent.VK_F8:
	key=0xffc5; break;
      case KeyEvent.VK_F9:
	key=0xffc6; break;
      case KeyEvent.VK_F10:
	key=0xffc7; break;
      case KeyEvent.VK_F11:
	key=0xffc8; break;
      case KeyEvent.VK_F12:
	key=0xffc9; break;
      case KeyEvent.VK_DELETE:
	key=0xffff; break;
      case KeyEvent.VK_NUM_LOCK:
	key=0xff7f; break;
      case KeyEvent.VK_SCROLL_LOCK:
	key=0xff14; break;
      case KeyEvent.VK_PRINTSCREEN:
	key=0xff61; break;
      case KeyEvent.VK_INSERT:
	key=0xff63; break;
      case KeyEvent.VK_HELP:
	key=0xff6a; break;
      case KeyEvent.VK_META:
	key=0xffe7; break;
//      case KeyEvent.VK_KP_UP:
//	key=0xff97; break;
//      case KeyEvent.VK_KP_DOWN:
//	key=0xff99; break;
//      case KeyEvent.VK_KP_LEFT:
//	key=0xff96; break;
//      case KeyEvent.VK_KP_RIGHT:
//	key=0xff98; break;
      default:
	key=e.getKeyChar();
      }
    }
    else{
      key=e.getKeyChar();
    }

    int s=10;
    if(km!=null){
      int i=0;
      int j=0;
      s=km.start;
      while(i<km.count*km.width){
	if(km.map[i]==key)break;
	i++;
	j++;
	if(j==km.width){
	  j=0;
	  s++;
	}
      }
    }
    return s;
  }
}
