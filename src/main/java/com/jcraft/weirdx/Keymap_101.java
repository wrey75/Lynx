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

final class Keymap_101 extends Keymap{
  private int[] _map={
    0xff1b, 0x0, 
    0x31, 0x21, 
    0x32, 0x40, 
    0x33, 0x23, 
    0x34, 0x24, 
    0x35, 0x25, 
    0x36, 0x5e, 
    0x37, 0x26, 
    0x38, 0x2a, 
    0x39, 0x28, 
    0x30, 0x29, 
    0x2d, 0x5f, 
    0x3d, 0x2b, 
    0xff08, 0x0, 
    0xff09, 0xfe20, 
    0x71, 0x51, 
    0x77, 0x57, 
    0x65, 0x45, 
    0x72, 0x52, 
    0x74, 0x54, 
    0x79, 0x59, 
    0x75, 0x55, 
    0x69, 0x49, 
    0x6f, 0x4f, 
    0x70, 0x50, 
    0x5b, 0x7b, 
    0x5d, 0x7d, 
    0xff0d, 0x0, 
    0xffe3, 0x0, 
    0x61, 0x41, 
    0x73, 0x53, 
    0x64, 0x44, 
    0x66, 0x46, 
    0x67, 0x47, 
    0x68, 0x48, 
    0x6a, 0x4a, 
    0x6b, 0x4b, 
    0x6c, 0x4c, 
    0x3b, 0x3a, 
    0x27, 0x22, 
    0x60, 0x7e, 
    0xffe1, 0x0, 
    0x5c, 0x7c, 
    0x7a, 0x5a, 
    0x78, 0x58, 
    0x63, 0x43, 
    0x76, 0x56, 
    0x62, 0x42, 
    0x6e, 0x4e, 
    0x6d, 0x4d, 
    0x2c, 0x3c, 
    0x2e, 0x3e, 
    0x2f, 0x3f, 
    0xffe2, 0x0, 
    0xffaa, 0x0, 
    0xffe9, 0xffe7, 
    0x20, 0x0, 
    0xffe5, 0x0, 
    0xffbe, 0x0, 
    0xffbf, 0x0, 
    0xffc0, 0x0, 
    0xffc1, 0x0, 
    0xffc2, 0x0, 
    0xffc3, 0x0, 
    0xffc4, 0x0, 
    0xffc5, 0x0, 
    0xffc6, 0x0, 
    0xffc7, 0x0, 
    0xff7f, 0xfef9, 
    0xff14, 0x0, 
    0xff95, 0xffb7, 
    0xff97, 0xffb8, 
    0xff9a, 0xffb9, 
    0xffad, 0x0, 
    0xff96, 0xffb4, 
    0xff9d, 0xffb5, 
    0xff98, 0xffb6, 
    0xffab, 0x0, 
    0xff9c, 0xffb1, 
    0xff99, 0xffb2, 
    0xff9b, 0xffb3, 
    0xff9e, 0xffb0, 
    0xff9f, 0xffae, 
    0x0, 0x0, 
    0x0, 0x0, 
    0x0, 0x0, 
    0xffc8, 0x0, 
    0xffc9, 0x0, 
    0xff50, 0x0, 
    0xff52, 0x0, 
    0xff55, 0x0, 
    0xff51, 0x0, 
    0x0, 0x0, 
    0xff53, 0x0, 
    0xff57, 0x0, 
    0xff54, 0x0, 
    0xff56, 0x0, 
    0xff63, 0x0, 
    0xffff, 0x0, 
    0xff8d, 0x0, 
    0xffe4, 0x0, 
    0xff13, 0xff6b, 
    0xff61, 0xff62, 
    0xffaf, 0x0, 
    0xffea, 0xffe8, 
    0x0, 0x0, 
    0x0, 0x0, 
    0x0, 0x0, 
    0x0, 0x0};

  Keymap_101(){
    start=9;
    width=2;
    count=109;
    map=_map;
  }

  final int getCode(KeyEvent e){
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
        if(e.getKeyChar()=='_') key=0x5f;
        else key=0xffad; break;
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

/*    
      // For European keyboards 
      case KeyEvent.VK_DEAD_GRAVE:
      case KeyEvent.VK_DEAD_ACUTE:
      case KeyEvent.VK_DEAD_CIRCUMFLEX:
      case KeyEvent.VK_DEAD_TILDE:
      case KeyEvent.VK_DEAD_MACRON:
      case KeyEvent.VK_DEAD_BREVE:
      case KeyEvent.VK_DEAD_ABOVEDOT:
      case KeyEvent.VK_DEAD_DIAERESIS:
      case KeyEvent.VK_DEAD_ABOVERING:
      case KeyEvent.VK_DEAD_DOUBLEACUTE:
      case KeyEvent.VK_DEAD_CARON:
      case KeyEvent.VK_DEAD_CEDILLA:
      case KeyEvent.VK_DEAD_OGONEK:
      case KeyEvent.VK_DEAD_IOTA:
      case KeyEvent.VK_DEAD_VOICED_SOUND:
      case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:

      case KeyEvent.VK_AMPERSAND:
      case KeyEvent.VK_ASTERISK:
      case KeyEvent.VK_QUOTEDBL:
      case KeyEvent.VK_LESS:

      case KeyEvent.VK_GREATER:
      case KeyEvent.VK_BRACELEFT:
      case KeyEvent.VK_BRACERIGHT:

      // for Asian Keyboards
      case KeyEvent.VK_FINAL:
      case KeyEvent.VK_CONVERT:
      case KeyEvent.VK_NONCONVERT:
      case KeyEvent.VK_ACCEPT:
      case KeyEvent.VK_MODECHANGE:
      case KeyEvent.VK_KANA:
      case KeyEvent.VK_KANJI:

      // for Sun keyboards
      case KeyEvent.VK_CUT:
      case KeyEvent.VK_COPY:
      case KeyEvent.VK_PASTE:
      case KeyEvent.VK_UNDO:
      case KeyEvent.VK_AGAIN:
      case KeyEvent.VK_FIND:
      case KeyEvent.VK_PROPS:
      case KeyEvent.VK_STOP:
*/
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
/*
  public static int getCode(int key){
    if(key==KeyEvent.VK_BACK_SPACE){
      key=0xff08;
    }
    else if(key==KeyEvent.VK_TAB){
      key=0xff09;
    }
    else if(key==KeyEvent.VK_ENTER){
      key=0xff0d;
    }
    else if(key==KeyEvent.VK_ESCAPE){
      key=0xff1b;
    }
    else if(key==KeyEvent.VK_DELETE){
      key=0xffff;
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
  public static void main(String[] arg){
    if(km!=null){
      int i=0;
      int j=0;
      int s=km.start;
      System.out.print(s+": ");
      while(i<km.count*km.width){
	System.out.print(km.map[i]+", ");
	i++;
	j++;
	if(j==km.width){
	  j=0;
	  s++;
	  System.out.print("\n"+s+": ");
	}
      }
    }
  }
*/
}
