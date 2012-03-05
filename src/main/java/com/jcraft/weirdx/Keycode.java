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

final class Keycode {
  static byte keycode[];
  static{
    keycode=new byte[256];
    keycode['A']=keycode['a']=keycode[0x01]=38;
    keycode['S']=keycode['s']=39;
    keycode['D']=keycode['d']=40;
    keycode['F']=keycode['f']=41;
    keycode['G']=keycode['g']=42;
    keycode['H']=keycode['h']=43;
    keycode['J']=keycode['j']=44;
    keycode['K']=keycode['k']=45;
    keycode['L']=keycode['l']=46;

    keycode['Q']=keycode['q']=24;
    keycode['W']=keycode['w']=25;
    keycode['E']=keycode['e']=26;
    keycode['R']=keycode['r']=27;
    keycode['T']=keycode['t']=28;
    keycode['Y']=keycode['y']=29;
    keycode['U']=keycode['u']=30;
    keycode['I']=keycode['i']=31;
    keycode['O']=keycode['o']=32;
    keycode['P']=keycode['p']=33;

    keycode['Z']=keycode['z']=52;
    keycode['X']=keycode['x']=53;
    keycode['C']=keycode['c']=54;
    keycode['V']=keycode['v']=55;
    keycode['B']=keycode['b']=56;
    keycode['N']=keycode['n']=57;
    keycode['M']=keycode['m']=58;
    keycode['<']=keycode[',']=59;
    keycode['>']=keycode['.']=60;
    keycode['?']=keycode['/']=61;
    keycode['_']=keycode['\\']=123;

    keycode['1']=keycode['!']=10;
    keycode['2']=keycode['"']=11;
    keycode['3']=keycode['#']=12;
    keycode['4']=keycode['$']=13;
    keycode['5']=keycode['%']=14;
    keycode['6']=keycode['&']=15;
    keycode['7']=keycode['\'']=16;
    keycode['8']=keycode['(']=17;
    keycode['9']=keycode[')']=18;
    keycode['0']=keycode['~']=19;
    keycode['-']=keycode['=']=20;
    keycode[' ']=65;
    keycode['/']=61;

    keycode[16]=50;
    keycode[17]=66;

    keycode[8]=22; // ^H

    keycode[KeyEvent.VK_ENTER]=36;
  }
}
