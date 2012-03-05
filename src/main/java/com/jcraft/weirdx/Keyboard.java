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

final class Keyboard {
  static Keyboard keyboard=null;

  public int minKeyCode;
  public int maxKeyCode;
  int keysyms_per_keycode;
  int keysym[];

  static final int noSymbol=0;
  public int keysym(int key, int n) {
    if (n < keysyms_per_keycode)
      return keysym[(key - minKeyCode)*keysyms_per_keycode + n];
    return noSymbol;
  }

  public int group1(int key, int n) {
    return keysym(key, n);
  }

  public int group2(int key, int n) {
    if (keysyms_per_keycode < 3)
      return keysym(key, n);
    return keysym(key, n+2);
  }

  Keyboard(int minKeyCode, int maxKeyCode){
    this.minKeyCode=minKeyCode;
    this.maxKeyCode=maxKeyCode;
  }

  void writeByte(IO out) throws java.io.IOException{
    out.writeByte(minKeyCode);
    out.writeByte(maxKeyCode);
  }

  public void getMap() { 
    int length=maxKeyCode - minKeyCode + 1;
  }

//  void print(PrintStream out) {
//    out.println("minKeyCode = " + minKeyCode);
//    out.println("maxKeyCode = " + maxKeyCode);
//  }

  void led(int number, int value) {
  }

  void bell(int volume) {
  }

  static void reqGrabKeyboard(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=c.data;
    foo=c.length;
    foo=io.readInt();
    foo=io.readInt();
    foo=io.readByte(); 
    foo=io.readByte();
    io.readPad(2);

    synchronized(io){
      io.writeByte(1);
      io.writeByte(0);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writePad(24);
      io.flush();
    }
  }
  static void reqGetKeyboardControl(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=c.length;

    synchronized(io){
      io.writeByte(1);
      io.writeByte((byte)1);
      io.writeShort(c.seq);
      io.writeInt(5);
      io.writeInt(0);        // led-mask
      io.writeByte((byte)0); // key-click-percent
      io.writeByte((byte)50); // bell-percent
      io.writeShort(400);      // bell-pitch
      io.writeShort(100);      // bell-duration
      io.writePad(2);
      for(int i=0; i<8; i++){
        io.writeInt(0);
      }
      io.flush();
    }
  }

  static void reqQueryKeymap(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    foo=c.length;

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(2);
      for(int i=0; i<8;i++){
        io.writeInt(0);
      }
      io.flush();
    }
  }
  static void reqChangeKeyboardMapping(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    n=c.length;
    int first=io.readByte();
    int kpk=io.readByte();
    io.readPad(2);
    n-=2;
    int i=(first-Keymap.km.start)*Keymap.km.width;
    while(n!=0){
      foo=io.readInt();
      if(kpk==1){
        Keymap.km.map[i]=foo;
      }
      n--;
    }
  }

  static void reqGetKeyboardMapping(Client c) throws IOException{
    int n;
    int foo;
    IO io=c.client;

    int i=io.readByte();
    foo=io.readByte();
    io.readPad(2);

    synchronized(io){
      io.writeByte(1);
      io.writeByte(Keymap.km.width);
      io.writeShort(c.seq);
      io.writeInt(foo*Keymap.km.width);
      io.writePad(24);

      int ii=(i-Keymap.km.start)*Keymap.km.width;
      n=foo*Keymap.km.width;
      while(n!=0){
        io.writeInt(Keymap.km.map[ii]);
        n--;
        ii++;
      }
      io.flush();
    }
  }

  static void reqSetModifierMapping(Client c) throws IOException{
    int foo, n;
    IO io=c.client;
    int kpm=c.data;
    n=c.length;

    n=n-1;
    int i=0;

    while(n!=0){
      foo=io.readInt();
      n--;
    }

    synchronized(io){
      io.writeByte(1);
      io.writeByte(0);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writePad(24);
      io.flush();
    }
  }

  static void reqGetModifierMapping(Client c) throws IOException{
    int n;
    int foo;
    IO io=c.client;
    Keymodifier kmod=null;

    synchronized(io){
      io.writeByte(1);
      io.writeByte(Keymodifier.kmod.width);
      io.writeShort(c.seq);
      io.writeInt(Keymodifier.kmod.width*2);
      io.writePad(24);

      n=Keymodifier.kmod.width*8;

      int ii=0;
      while(n!=0){
        io.writeByte(Keymodifier.kmod.keys[ii]);
        ii++;
        n--;
      }
      io.flush();
    }
  }
}
