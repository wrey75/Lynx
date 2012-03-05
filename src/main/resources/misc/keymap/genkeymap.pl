#!/usr/bin/perl
#/* genkeymap.pl -- Guess
# *
# * Copyright (C) 2000 Marcus Schiesser, ymnk
# *
# * This program is free software; you can redistribute it and/or modify
# * it under the terms of the GNU General Public License as published by
# * the Free Software Foundation; either version 2 of the License, or
# * (at your option) any later version.
# *
# * This program is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# * GNU General Public License for more details.
# *
# * You should have received a copy of the GNU General Public License
# * along with this program; if not, write to the Free Software
# * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
# */
#/* 
# * Authors:  Marcus Schiesser<marcus.schiesser@student.uni-tuebingen.de>,
# *           ymnk<ymnk@jcraft.com> 
# *
# * 01-Aug-2000: This file is written by Marcus Schiesser.
# * 02-Aug-2000: ymnk modifies for supporting variable width keymap tables.
# * 21-Sep-2000: ymnk modifies for generating java classes.
# */ 
# 
# usage: xmodmap -pk -pm | ./genkeymap.pl -n name
# 

use Getopt::Std;

getopts("n:");

$name=$opt_n;

if($#ARGV>-1){
  open(STDIN, "<$ARGV[0]") or die "can't open: $!";
}

if($name eq ""){ $name=$ARGV[0]; }
if($name eq ""){ $name="tmp"; }

while(<>){

  if(/^There are (\d+) KeySyms per KeyCode; KeyCodes range from (\d+) to (\d+)/){
    open(STDOUT, ">Keymap_$name.java") or die "can't open output: $!";
    &printkeymap($name, $1, $2, $3);
    close(STDOUT);
    next;
  }

  if(/^xmodmap:  up to (\d+) keys per modifier/){
    open(STDOUT, ">Keymodifier_$name.java") or die "can't open output: $!";
    &printkeymod($name, $1);
    close(STDOUT);
    next;
  }
}
#close(STDIN);
#close(STDOUT);

sub printkeymap{
  local($name, $keysyms, $from, $to)=@_;

  &printheader;

  local($result)=&keymap($keysyms, $from, $to); 

print <<CLASS;
final class Keymap_$name extends Keymap{
  private int[] _map={
$result  };

  Keymap_$name(){
    start=$from;
    width=$keysyms;
    count=$to-$from+1;
    map=_map;
  }
  final public int getCode(KeyEvent e){
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
    int s=0;
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
CLASS
}

sub keymap{
  local($keysyms, $from, $to, $result)=@_;

  while(<>){ last if(/Value/); }
  <>;
  while(<>){
    chop;
    s/\t/ /g;
    @array=split(' ');
    $result .= "    ";
    for($i=1; $i<$#array; $i+=2){
      $result .= $array[$i];
      if($array[0]!=$to || ($i+1)/2!=$keysyms){ $result .= ", "; }
      else{ $result .=  "  "; }
    }
    $i/=2;
    if($i<$keysyms){
      for($j=0; $j<$keysyms-$i; $j++){
        $result .= "0x0000";
        if($array[0]!=$to || ($j+1)<$keysyms-$i){ $result .= ", "; }
        else{ $result .= "  "; }
      }
    }
    $result .= " // ".$array[0];
    for($i=2; $i<=$#array; $i+=2){
      $result .=  " ". $array[$i];
    }
    $result .= "\n";
  }
  return $result;
}

sub printkeymod{
  local($name, $kpm)=@_;
  &printheader;

  local($result)=&keymod($kpm); 
print <<CLASS;
final class Keymodifier_$name extends Keymodifier{
  private byte[] _keys={
$result  };
  Keymodifier_$name(){
    width=$kpm;
    keys=_keys;
  }
}
CLASS
}

sub keymod{
  local($kpm, $result)=@_;
  <>;
  while(<>){
    last if(/^$/);
    chop;
    s/\t/ /g;
    @array=split(' ');
    $result .= "    ";
    for($i=2; $i<=$#array; $i+=2){
      if($array[$i]=~/\((.+)\)/){
        $result .= "$1";
        if($array[0] ne "mod5" || ($i/2)<$kpm){ $result .=  ", "; }
        else{ $result .=  "  "; }
      }
    }

    $i/=2;

    if($i<=$kpm){
      for($j=0; $j<=$kpm-$i; $j++){
        $result .= "0x00";
        if($array[0] ne "mod5" || ($j+1)<=$kpm-$i){ $result .=  ", "; }
        else{ $result .=  "  "; }
      }
    }

    $result .=  (" // [".$array[0]."] ");
    for($i=1; $i<=$#array; $i+=2){
      $result .=  (" ". $array[$i]);
    }
    $result .=  "\n";
  }
  return $result;
}

sub printheader{
print <<HEADER;
/* WeirdX - Guess.
 *
 * Copyright (C) 1999,2000 JCraft, Inc.
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

HEADER
}
