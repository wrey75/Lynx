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

class Font_CharSet_GB2312 implements Font_CharSet{
  static int min_byte1=0x21;
  static int max_byte1=0x77;
  static int min_char_or_byte2=0x21;
  static int max_char_or_byte2=0x7e;
  static int default_char=0x2121;
  static String encoding="GB2312";
  static String charset="gb2312.1980";

  public int getMinByte1(){return min_byte1;}
  public int getMaxByte1(){return max_byte1;}
  public int getMinCharOrByte2(){return min_char_or_byte2;}
  public int getMaxCharOrByte2(){return max_char_or_byte2;}
  public int getDefaultChar(){return default_char;}
  public String getEncoding(){return encoding;}
  public String getCharset(){return charset;}

  private static String[] _flist={
    "-isas-fangsong ti-medium-r-normal--0-0-72-72-c-0-gb2312.1980-0",
    "-isas-fangsong ti-medium-r-normal--16-160-72-72-c-160-gb2312.1980-0",
    "-isas-fangsong ti-medium-r-normal--24-240-72-72-c-240-gb2312.1980-0",
    "-isas-song ti-medium-r-normal--16-160-72-72-c-160-gb2312.1980-0",
    "-isas-song ti-medium-r-normal--24-240-72-72-c-240-gb2312.1980-0"//,
  };

//  private static String[] _aliases={
//  };

  public void init(){
    if(_flist==null)return;
    Font.addFont(_flist);
    _flist=null;
  }
  public int encode(byte[] bbuffer, int start, int len, char[] cbuffer){
    int foo=0;
    try{
	String s=new String(bbuffer, start, len, encoding);
	foo=s.length();
	s.getChars(0, foo, cbuffer, 0);
    }
    catch(Exception e){
    }
    return foo;
  }
}
