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

class Font_CharSet_JISX implements Font_CharSet{
  static int min_byte1=0x21;
  static int max_byte1=0x74;
  static int min_char_or_byte2=0x21;
  static int max_char_or_byte2=0x7e;
  static int default_char=0x2121;
  static String encoding="EUC-JP";
  static String charset="jisx0208.1983";

  public int getMinByte1(){return min_byte1;}
  public int getMaxByte1(){return max_byte1;}
  public int getMinCharOrByte2(){return min_char_or_byte2;}
  public int getMaxCharOrByte2(){return max_char_or_byte2;}
  public int getDefaultChar(){return default_char;}
  public String getEncoding(){return encoding;}
  public String getCharset(){return charset;}

  private static String[] _flist={
    "-jis-fixed-medium-r-normal--0-0-100-100-c-0-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--16-110-100-100-c-160-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--16-150-75-75-c-160-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--24-170-100-100-c-240-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--24-230-75-75-c-240-jisx0208.1983-0",
    "-misc-fixed-medium-r-normal--14-130-75-75-c-140-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--14-120-100-100-c-160-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--14-120-100-100-c-140-jisx0208.1983-0",
    "-jis-fixed-medium-r-normal--14-120-75-75-c-140-jisx0208.1983-0",

    "-misc-fixed-medium-r-normal--0-0-75-75-c-0-jisx0201.1976-0",
    "-misc-fixed-medium-r-normal--14-130-75-75-c-70-jisx0201.1976-0",
    "-misc-fixed-medium-r-normal--16-150-75-75-c-80-jisx0201.1976-0",

    "-sony-fixed-medium-r-normal--0-0-75-75-c-0-jisx0201.1976-0",
    "-sony-fixed-medium-r-normal--14-170-100-100-c-120-jisx0201.1976-0",
    "-sony-fixed-medium-r-normal--16-120-100-100-c-80-jisx0201.1976-0",
    "-sony-fixed-medium-r-normal--16-150-75-75-c-80-jisx0201.1976-0",
    "-sony-fixed-medium-r-normal--24-170-100-100-c-120-jisx0201.1976-0",
    "-sony-fixed-medium-r-normal--24-230-75-75-c-120-jisx0201.1976-0" //,
    };

  private static String[] _aliases={
    "k14",
    "-jis-fixed-medium-r-normal--14-120-100-100-c-160-jisx0208.1983-0",
    "r14",
     "-sony-fixed-medium-r-normal--14-170-100-100-c-120-jisx0201.1976-0",
    "rk14",
     "-sony-fixed-medium-r-normal--14-170-100-100-c-120-jisx0201.1976-0",

    "r16",
    "-sony-fixed-medium-r-normal--16-120-100-100-c-80-jisx0201.1976-0",
    "rk16",
     "-sony-fixed-medium-r-normal--16-120-100-100-c-80-jisx0201.1976-0",

    "r24",
     "-sony-fixed-medium-r-normal--24-230-75-75-c-120-jisx0201.1976-0",
    "rk24",
     "-sony-fixed-medium-r-normal--24-230-75-75-c-120-jisx0201.1976-0",

    "kana14",
     "-sony-fixed-medium-r-normal--14-170-100-100-c-120-jisx0201.1976-0",
    "8x16kana",
    "-sony-fixed-medium-r-normal--16-120-100-100-c-80-jisx0201.1976-0",
    "8x16romankana",
     "-sony-fixed-medium-r-normal--16-120-100-100-c-80-jisx0201.1976-0",

    "12x24kana", 
    "-sony-fixed-medium-r-normal--24-170-100-100-c-120-jisx0201.1976-0",
    "12x24romankana",
     "-sony-fixed-medium-r-normal--24-170-100-100-c-120-jisx0201.1976-0",

    "kanji16",
    "-jis-fixed-medium-r-normal--16-110-100-100-c-160-jisx0208.1983-0",
    "kanji24",
    "-jis-fixed-medium-r-normal--24-170-100-100-c-240-jisx0208.1983-0" //,
  };

  public void init(){
    if(_flist==null)return;
    Font.addFont(_flist);
    Font.addAlias(_aliases);
    _flist=null;
  }
  public int encode(byte[] bbuffer, int start, int len, char[] cbuffer){
    int foo=0;
    try{
      for(int i=start; i<start+len;i++){
	bbuffer[i]=(byte)(bbuffer[i]|0x80);
      }
      String s=new String(bbuffer, start, len, encoding);
      foo=s.length();
      s.getChars(0, foo, cbuffer, 0);
    }
    catch(Exception e){
    }
    return foo;
  }
}
