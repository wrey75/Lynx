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
import java.util.*;

final class Font extends Resource{
  static Font dflt;
  static Vector charSets=new Vector();

  byte[] name;
  byte[] lfname;
  String encoding=null;
  DDXFont font;

  int ascent;
  int descent;

  private static String[] _flist={
    "-misc-fixed-medium-r-semicondensed--13-120-75-75-c-60-iso8859-1", // fixed

    "-misc-fixed-bold-r-normal--0-0-100-100-c-0-iso8859-1",
    "-misc-fixed-bold-r-normal--0-0-75-75-c-0-iso8859-1",

    "-misc-fixed-bold-r-normal--13-100-100-100-c-70-iso8859-1",
    "-misc-fixed-bold-r-normal--13-100-100-100-c-80-iso8859-1",
    "-misc-fixed-bold-r-normal--13-120-75-75-c-70-iso8859-1",
    "-misc-fixed-bold-r-normal--13-120-75-75-c-80-iso8859-1",
    "-misc-fixed-bold-r-normal--14-130-75-75-c-70-iso8859-1",
    "-misc-fixed-bold-r-normal--15-120-100-100-c-90-iso8859-1",
    "-misc-fixed-bold-r-normal--15-140-75-75-c-90-iso8859-1",
    "-misc-fixed-bold-r-semicondensed--13-100-100-100-c-60-iso8859-1",
    "-misc-fixed-bold-r-semicondensed--13-120-75-75-c-60-iso8859-1",

    "-misc-fixed-medium-r-normal--0-0-100-100-c-0-iso8859-1",
    "-misc-fixed-medium-r-normal--0-0-75-75-c-0-iso8859-1",

    "-misc-fixed-medium-r-normal--10-100-75-75-c-60-iso8859-1",
    "-misc-fixed-medium-r-normal--10-70-100-100-c-60-iso8859-1",
    "-misc-fixed-medium-r-normal--13-100-100-100-c-70-iso8859-1",
    "-misc-fixed-medium-r-normal--13-100-100-100-c-80-iso8859-1",
    "-misc-fixed-medium-r-normal--13-120-75-75-c-70-iso8859-1",
    "-misc-fixed-medium-r-normal--13-120-75-75-c-80-iso8859-1",
    "-misc-fixed-medium-r-normal--14-110-100-100-c-70-iso8859-1",
    "-misc-fixed-medium-r-normal--14-130-75-75-c-70-iso8859-1",
    "-misc-fixed-medium-r-normal--15-120-100-100-c-90-iso8859-1",
    "-misc-fixed-medium-r-normal--15-140-75-75-c-90-iso8859-1",
    "-misc-fixed-medium-r-normal--16-150-75-75-c-80-iso8859-1",
    "-misc-fixed-medium-r-normal--20-140-100-100-c-100-iso8859-1",
    "-misc-fixed-medium-r-normal--20-200-75-75-c-100-iso8859-1",
    "-misc-fixed-medium-r-normal--7-50-100-100-c-50-iso8859-1",
    "-misc-fixed-medium-r-normal--7-70-75-75-c-50-iso8859-1",
    "-misc-fixed-medium-r-normal--8-60-100-100-c-50-iso8859-1",
    "-misc-fixed-medium-r-normal--8-80-75-75-c-50-iso8859-1",
    "-misc-fixed-medium-r-normal--9-80-100-100-c-60-iso8859-1",
    "-misc-fixed-medium-r-normal--9-90-75-75-c-60-iso8859-1",
    "-misc-fixed-medium-r-semicondensed--12-110-75-75-c-60-iso8859-1",
    "-misc-fixed-medium-r-semicondensed--12-90-100-100-c-60-iso8859-1",
    "-misc-fixed-medium-r-semicondensed--13-100-100-100-c-60-iso8859-1",
    "-sony-fixed-medium-r-normal--16-120-100-100-c-80-iso8859-1",
    "-sony-fixed-medium-r-normal--16-150-75-75-c-80-iso8859-1",
    "-sony-fixed-medium-r-normal--24-170-100-100-c-120-iso8859-1",
    "-sony-fixed-medium-r-normal--24-230-75-75-c-120-iso8859-1",

    "-adobe-courier-bold-o-normal--0-0-100-100-m-0-iso8859-1",
    "-adobe-courier-bold-r-normal--0-0-100-100-m-0-iso8859-1",
    "-adobe-courier-medium-o-normal--0-0-100-100-m-0-iso8859-1",
    "-adobe-courier-medium-r-normal--0-0-100-100-m-0-iso8859-1",

//  "-adobe-helvetica-bold-o-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-helvetica-bold-r-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-helvetica-medium-o-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-helvetica-medium-r-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-new century schoolbook-bold-i-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-new century schoolbook-bold-r-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-new century schoolbook-medium-i-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-new century schoolbook-medium-r-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-times-bold-i-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-times-bold-r-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-times-medium-i-normal--0-0-100-100-p-0-iso8859-1",
//  "-adobe-times-medium-r-normal--0-0-100-100-p-0-iso8859-1",

    "-adobe-courier-bold-o-normal--0-0-75-75-m-0-iso8859-1",
    "-adobe-courier-bold-r-normal--0-0-75-75-m-0-iso8859-1",
    "-adobe-courier-medium-o-normal--0-0-75-75-m-0-iso8859-1",
    "-adobe-courier-medium-r-normal--0-0-75-75-m-0-iso8859-1",

//  "-adobe-helvetica-medium-o-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-helvetica-medium-r-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-new century schoolbook-bold-i-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-new century schoolbook-bold-r-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-new century schoolbook-medium-i-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-new century schoolbook-medium-r-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-times-bold-i-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-times-bold-r-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-times-medium-i-normal--0-0-75-75-p-0-iso8859-1",
//  "-adobe-times-medium-r-normal--0-0-75-75-p-0-iso8859-1",

    "-adobe-helvetica-bold-o-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-helvetica-bold-r-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-helvetica-medium-o-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-helvetica-medium-r-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-new century schoolbook-bold-i-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-new century schoolbook-bold-r-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-new century schoolbook-medium-i-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-new century schoolbook-medium-r-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-times-bold-i-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-times-bold-r-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-times-medium-i-normal--0-0-0-0-p-0-iso8859-1",
    "-adobe-times-medium-r-normal--0-0-0-0-p-0-iso8859-1"

  };

  static DDXFont[] flist=null;

  private static String[] _aliases={
    "fixed", "-misc-fixed-medium-r-semicondensed--13-120-75-75-c-60-iso8859-1",
    "7x14", "-misc-fixed-bold-r-normal--14-130-75-75-c-70-iso8859-1",
    "7x13", "-misc-fixed-medium-r-normal--13-120-75-75-c-70-iso8859-1",
    "7x13bold", "-misc-fixed-bold-r-normal--13-120-75-75-c-70-iso8859-1",
    "8x16", "-misc-fixed-medium-r-normal--16-150-75-75-c-80-iso8859-1",
    "12x24", "-sony-fixed-medium-r-normal--24-170-100-100-c-120-iso8859-1",
    "a14", "-misc-fixed-medium-r-normal--14-130-75-75-c-70-iso8859-1"
  };

  private static Alias[] aliases=null;

  static void addFont(String _name){
    byte[] name=_name.getBytes();
    DDXFont f=null;
    try{ f=new DDXFont(); f.init(name); }catch(Exception e){}
    if(f==null) return;
    DDXFont[] tmp=new DDXFont[flist.length+1];
    System.arraycopy(flist, 0, tmp, 1, flist.length);
    flist=tmp;
    flist[0]=f;
  }

  static void addFont(String[] name){
    Vector v=new Vector();
    for(int i=0; i<name.length; i++){
      try{
	DDXFont f=new DDXFont();
	f.init(name[i].getBytes());
	v.addElement(f);
      }
      catch(Exception e){}
    }
    if(v.size()==0)return;

    DDXFont[] tmp=null;
    int offset=0;
    if(flist==null){tmp=new DDXFont[v.size()]; }
    else{
      tmp=new DDXFont[flist.length+v.size()];
      System.arraycopy(flist, 0, tmp, 0, flist.length);
      offset=flist.length;
    }
    for(int i=0; i<v.size(); i++){ tmp[i+offset]=(DDXFont)v.elementAt(i); }
    flist=tmp;
  }

  static void addAlias(String[] name){
    Vector v=new Vector();
    for(int i=0; i<name.length; i+=2){
      byte[] b=name[i+1].getBytes();
      DDXFont fl=null;
      for(int j=0; j<flist.length; j++){
	if(match_aux(flist[j].lfname, 0, b, 0)){
          fl=flist[j];
	  break;
	}
      }
      if(fl==null)continue;
      v.addElement(new Alias(name[i].getBytes(), fl));
    }
    if(v.size()==0)return;

    Alias[] tmp=null;
    int offset=0;
    if(aliases==null){tmp=new Alias[v.size()]; }
    else{
      tmp=new Alias[aliases.length+v.size()];
      System.arraycopy(aliases, 0, tmp, 0, aliases.length);
      offset=aliases.length;
    }
    for(int i=0; i<v.size(); i++){
      tmp[i+offset]=(Alias)v.elementAt(i);
    }
    aliases=tmp;
  }

  static void addAlias(String name, String font){
    byte[] b=font.getBytes();
    for(int i=0; i<flist.length; i++){
      if(match_aux(flist[i].lfname, 0, b, 0)){
	if(aliases==null){
	  aliases=new Alias[1];
	}
	else{
	  Alias[] foo=new Alias[aliases.length+1];
	  System.arraycopy(aliases, 0, foo, 1, aliases.length);
	  aliases=foo;
	}
	aliases[0]=new Alias(name.getBytes(), flist[i]);
	return;
      }
    }
  }

  static byte[][] fpath={
    "/tmp".getBytes()
  };

  static void init(int id, String charset){
    if(_flist!=null){
      addFont(_flist);
      _flist=null; // go away!!
    }
    if(_aliases!=null){
      addAlias(_aliases);
      _aliases=null; // go away!!
    }
    dflt=new Font(id, "fixed");

    if(charset!=null){
      int start=0, end;
      while(true){
        if((end=charset.indexOf(',', start))>0){
          loadCharSet(charset.substring(start, end));
          start=end+1;
          continue;
	}
        if(start<charset.length()){
          loadCharSet(charset.substring(start));
	}
        break;
      }
    }
    loadCharSet("FONTSPECIFIC");
  }

  static void loadCharSet(String name){
    try{
      Class c=Class.forName("com.jcraft.weirdx.Font_CharSet_"+name);

      Object o=c.newInstance();
      charSets.addElement(o);
      ((Font_CharSet)o).init();
    }
    catch(Exception e){ }
  }

  java.awt.Font getFont(){ return font.getFont(); }

  int bytesWidth(byte[] b, int i, int j){ 
    return font.metric.bytesWidth(b, i, j);
  }

  int charsWidth(char[] b, int i, int j){ 
    return font.metric.charsWidth(b, i, j);
  }

  private static boolean match_aux(byte[] b1, int i1, byte[] b2, int i2){
    while(i1<b1.length && i2<b2.length){
      if(b1[i1]!=b2[i2]) break;
      i1++; i2++;
    }
    if(i2==b2.length) return true;
    if(b2[i2]=='*'){
      i2++;
      if(i2==b2.length) return true;
      if(i1==b1.length)	return false;  // a <-> a*-
      for(int i=i1; i<b1.length; i++){
	if(b1[i]==b2[i2]){
	  boolean res=match_aux(b1, i, b2, i2);
	  if(res) return res;
	}
      }
      return false;
    }
    if(b2[i2]=='?'){
      if(i1==b1.length)	return false;
      i2++; i1++;
      return match_aux(b1, i1, b2, i2);
    }
    return false;
  }

  private static boolean match_scalable(byte[] b1, byte[] b2){
    int i1=0;
    int i2=0;
    int i=0;
    //System.out.println("s1: "+new String(b1)+ ", s2: "+new String(b2));
    for(int j=0; j<b2.length; j++){
      if(b2[j]=='-') i++;
    }
    if(i!=14) return false;

    i=0;
    while(i1<b1.length && i2<b2.length){
      if(b1[i1]=='-') i++;
      //System.out.println("s1: "+new Character((char)b1[i1])+
      //		 ", s2: "+new Character((char)b2[i2]));
      if(b1[i1]!=b2[i2]){
	if(b2[i2]=='*' || b2[i2]=='0'){
          if(i2+1==b2.length) return true; // end
          if(b1[i1]=='-'){ i--; i1--;}     // again
          else{ 
            for(;i1<b1.length; i1++){
              if(b1[i1]=='-') { i1--; break; }
            }
            if(i1==b1.length) return false;
          }
	}
	else if(b2[i2]=='?'){
	  if(i2+1==b2.length) return true; // end
	  if(b1[i1]=='-') return false;
	  if((i1+1)>=b1.length || (b1[i1+1]!='-')){
	    return false;
	  }
	}
	else if(b1[i1]=='0' &&
		(i==7 || i==8 || i==9 || i==10 || i==11 || i==12)){
          // scalable font!!
	  if(b2[i2]<'0' || '9'<b2[i2]) return false;
	  for(;i2<b2.length; i2++){
	    if(b2[i2]=='-') { i2--; break; }
	  }
	  if(i2==b2.length) return false;
	}
	else{
	  return false;
	}
      }
      i1++; i2++;
    }
    return true;
  }

  static byte[] genScaleName(byte[] lfname, byte[] name){
    return genScaleName(lfname, name, 0);
  }

  static byte[] genScaleName(byte[] lfname, byte[] name, int size){
    //System.out.println("lfname: "+new String(lfname)+
    //", name: "+new String(name));

    if(size==0){
      size=getScalableSize(name);
      if(size==0){
	//System.out.println("scalable font error: "+new String(name));
	size=14;
      }
    }

    StringBuffer tmp=new StringBuffer();
    int i=0;
    int skip=7;
    while(i<lfname.length){
      if(lfname[i]=='-'){skip--; }
      tmp.append((char)lfname[i]);
      i++;
      if(skip==0) break;
    }
    tmp.append(size+"-"+(size*10)+"-");
    skip=2;
    while(i<lfname.length){
      if(lfname[i]=='-'){ skip--; }
      i++;
      if(skip==0) break;
    }
    skip=3;
    while(i<lfname.length){
      if(lfname[i]=='-'){ skip--; }
      tmp.append((char)lfname[i]);
      i++;
      if(skip==0) break;
    }
    tmp.append(size+"-");
    skip=1;
    while(i<lfname.length){
      if(lfname[i]=='-'){ skip--; }
      i++;
      if(skip==0) break;
    }
    while(i<lfname.length){
      tmp.append((char)lfname[i]);
      i++;
    }
    //System.out.println(tmp.toString());
    return tmp.toString().getBytes();
  }

  static int getScalableSize(byte[] name){
    int size=0;
    int i=0;
    int skip=7;
    while(i<name.length){
      if(name[i]=='-'){	skip--; }
      i++;
      if(skip==0) break;
    }
    if(i>=name.length)return 0;
    if('0'<=name[i] && name[i]<='9'){
      while(i<name.length){
	if(name[i]=='-')break;
	size=size*10+name[i]-'0';
	i++;
      }
    }
    if(size==0){
      i++;
      skip=1;
      while(i<name.length){
	if(name[i]=='-'){ skip--; }
	i++;
	if(skip==0) break;
      }
      if(i>=name.length)return 0;
      if('0'<=name[i] && name[i]<='9'){
	while(i<name.length){
	  if(name[i]=='-')break;
	  size=size*10+name[i]-'0';
	  i++;
	}
      }
      if(size>0) size=(int)(size/10);
    }
    if(size==0){
      i++;
      skip=3;
      while(i<name.length){
	if(name[i]=='-'){ skip--; }
	i++;
	if(skip==0) break;
      }
      if(i>=name.length)return 0;
      if('0'<=name[i] && name[i]<='9'){
	while(i<name.length){
	  if(name[i]=='-')break;
	  size=size*10+name[i]-'0';
	  i++;
	}
      }
    }
    return size;
  }

  Font(int id, String name) {
    super(id, RT_FONT);
    this.name=name.getBytes();
    if(name.equals("fixed") || name.equals("cursor")){
      font=flist[0];
      lfname=font.lfname;
    }
    else{
      for (int i=0; i< aliases.length; i++){
	if(match_aux(aliases[i].name, 0, this.name, 0)){
	  font=aliases[i].font;
	  lfname=font.lfname;
	}
      }
      if(lfname==null){
	for(int i=0; i<flist.length; i++){
	  if(match_aux(flist[i].lfname, 0, this.name, 0)){
	    font=flist[i];
	    lfname=font.lfname;
	    break;
	  }
	}
      }
      if(lfname==null){
	int size=getScalableSize(this.name);
	if(size>0){
	  for(int i=0; i<flist.length; i++){
	    if(flist[i].getScalable()){
	      if(match_scalable(flist[i].lfname, this.name)){
		font=flist[i].getScalableFont(this.name);
		lfname=font.lfname;
		break;
	      }
	    }
	  }
	}
      }
      if(lfname==null){
	font=flist[0];
	lfname=font.lfname;
      }
    }
    font.getFont();
    ascent=font.getAscent();
    descent=font.getDescent();
    encoding=font.encoding;
  }

  static void reqQueryTextExtents(Client c) throws IOException{
    int n, foo;
    boolean odd=false;
    IO io=c.client;
    foo=c.data;
    if(foo!=0)odd=true;
    n=c.length;
    foo=io.readInt();
    c.length-=2;
    Font f=(Font)Resource.lookupIDByType(foo, RT_FONT);
    if(f==null){
      c.errorValue=foo;
      c.errorReason=7; // BadFont;
      return;
    }
    n=n-2;
    n*=4;
    io.readByte(c.bbuffer, 0, n);

    if(odd) n-=2;

    synchronized(io){
      io.writeByte(1);
      if(f.encoding!=null){
        for(int i=0; i<n; i++){
	  c.bbuffer[i]|=0x80;
        }
        try{
	  String s=new String(c.bbuffer, 0, n, f.encoding);
	  n=s.length();
	  s.getChars(0, n, c.cbuffer, 0);
        }
        catch(Exception e){
	  System.out.println(e);
	  return;
        }
      }
      else{
        for(int i=0; i<n; i++){
	  c.cbuffer[i]=(char)c.bbuffer[i];
        }
      }

      foo=f.charsWidth(c.cbuffer, 0, n);

      io.writeByte((byte)0);
      io.writeShort(c.seq);
      io.writeInt(0);
      io.writeShort(f.font.getMaxAscent());
      io.writeShort(f.font.getMaxDescent());
      io.writeShort(f.font.getMaxAscent());
      io.writeShort(f.font.getMaxDescent());
      io.writeInt(foo);
      io.writeInt(0);
      io.writeInt(foo);
      io.writePad(4);
      io.flush();
    }
  }
  static void reqListFontsWithInfo(Client c) throws IOException{
    int foo, n;
    int maxname;
    IO io=c.client;
    n=c.length;
    maxname=io.readShort();
    foo=io.readShort();
    byte[] qqqq=null;
    String s;
    {
      qqqq=c.bbuffer;
      io.readByte(qqqq, 0, foo);
      io.readPad((-foo)&3);
      s=new String(qqqq, 0, foo);
      qqqq=s.getBytes();
    }
    byte[] pattern=qqqq;
    byte[] bb=new byte[12];
    int count=0;
    for (int i=0; i< aliases.length; i++){
      if(match_aux(aliases[i].name, 0, pattern, 0)){
	count++;
	if(maxname<count)break;
      }
    }

    if(maxname>=count){
      for(int i=0; i<flist.length;i++){
	if(match_aux(flist[i].lfname, 0, pattern, 0)){
	  count++;
	  if(maxname<count)break;
	}
      }
    }
    if(maxname>=count && count==0){
      for(int i=0; i<flist.length;i++){
	if(match_scalable(flist[i].lfname, pattern)){
	  count++;
	  if(maxname<count)break;
	}
      }
    }

    synchronized(io){
    if(count>0){
      for(int i=0; i<aliases.length;i++){
	if(match_aux(aliases[i].name, 0, pattern, 0)){
	  count--;
	  DDXFont font=aliases[i].font;
	  int[] prop=font.getProp();

	  io.writeByte(1);

  	  io.writeByte(aliases[i].name.length);
	  io.writeShort(c.seq);
	  io.writeInt(7+ (prop!=null ? prop.length/2 : 0)*2 +
		     (aliases[i].name.length+3)/4);
          // min-bounds
	  io.writeShort(0);
	  io.writeShort(0);
	  io.writeShort(font.min_width);
	  io.writeShort(0);
	  io.writeShort(0);
	  io.writeShort(0);
	  io.writePad(4);

          // max-bounds
	  io.writeShort(0);
          io.writeShort(font.max_width);
          io.writeShort(font.max_width);
	  io.writeShort(font.getMaxAscent());
	  io.writeShort(font.getMaxDescent());
	  io.writeShort(0);
	  io.writePad(4);

          io.writeShort(font.min_char_or_byte2);    // min-char-or-byte2
	  io.writeShort(font.max_char_or_byte2);    // max-char-or-byte2

	  io.writeShort(font.default_char); // default-char

	  io.writeShort(prop!=null ? prop.length/2 : 0); // m

	  io.writeByte(0);                  // draw-direction
	  io.writeByte(font.min_byte1);     // min-byte1
	  io.writeByte(font.max_byte1);     // max-byte1
	  io.writeByte(0);                  // all-char-exists

	  io.writeShort(font.getMaxAscent());  // font-ascent
	  io.writeShort(font.getMaxDescent()); // font-descent
	  io.writeInt(count); // reply-hint

          // property 
	  if(prop!=null){
	    for(int j=0; j<prop.length; j++){
	      io.writeInt(prop[j]);
	    }
	  }

	  io.writeByte(aliases[i].name);
	  io.writePad((-aliases[i].name.length)&3);

          if(count==0)break;
	}
      }

      if(count>0){
	for(int i=0; i<flist.length;i++){
	  if(match_aux(flist[i].lfname, 0, pattern, 0)){
	    count--;

	    DDXFont font=flist[i];
	    int[] prop=font.getProp();

	    io.writeByte(1);

	    io.writeByte(font.lfname.length);
	    io.writeShort(c.seq);
	    io.writeInt(7+ (prop!=null ? prop.length/2 : 0)*2 +
			    (font.lfname.length+3)/4);

	    io.writeShort(0);
	    io.writeShort(0);
	    io.writeShort(font.min_width);
	    io.writeShort(0);
	    io.writeShort(0);
	    io.writeShort(0);
	    io.writePad(4);

          // max-bounds
	    io.writeShort(0);                      // left-side-bearing
	    io.writeShort(font.max_width);      // right-side-bearing
	    io.writeShort(font.max_width);       // character-width
	    io.writeShort(font.getMaxAscent());  // ascent
	    io.writeShort(font.getMaxDescent()); // descent
	    io.writeShort(0);                        // attribute
	    io.writePad(4);

	    io.writeShort(font.min_char_or_byte2);  // min-char-or-byte2
	    io.writeShort(font.max_char_or_byte2);    // max-char-or-byte2

	    io.writeShort(font.default_char); // default-char
	    io.writeShort(prop!=null ? prop.length/2 : 0); // m
	    io.writeByte(0);  // draw-direction
	    io.writeByte(font.min_byte1); // min-byte1
	    io.writeByte(font.max_byte1); // max-byte1
	    io.writeByte(0);  // all-char-exists
	    io.writeShort(font.getMaxAscent());  // font-ascent
	    io.writeShort(font.getMaxDescent()); // font-descent
	    io.writeInt(count); // reply-hint

	    if(prop!=null){
	      for(int j=0; j<prop.length; j++){
		io.writeInt(prop[j]);
	      }
	    }

	    io.writeByte(font.lfname);
	    io.writePad((-font.lfname.length)&3);

	    if(count==0)break;
	  }
	}
      }

      if(count>0){
	for(int i=0; i<flist.length;i++){
	  if(match_scalable(flist[i].lfname, pattern)){
	    count--;

	    DDXFont font=flist[i].getScalableFont(pattern);
	    int[] prop=font.getProp();

	    io.writeByte(1);


	    io.writeByte(font.lfname.length);
	    io.writeShort(c.seq);
	    io.writeInt(7+ (prop!=null ? prop.length/2 : 0)*2 +
			    (font.lfname.length+3)/4);

          // min-bounds
	    io.writeShort(0);              // left-side-bearing 
	    io.writeShort(0);              // right-side-bearing
	    io.writeShort(font.min_width); // character-width
	    io.writeShort(0);              // ascent
	    io.writeShort(0);              // descent
	    io.writeShort(0);              // attribute
	    io.writePad(4);

          // max-bounds
	    io.writeShort(0);                      // left-side-bearing
	    io.writeShort(font.max_width);      // right-side-bearing
	    io.writeShort(font.max_width);       // character-width
	    io.writeShort(font.getMaxAscent());  // ascent
	    io.writeShort(font.getMaxDescent()); // descent
	    io.writeShort(0);                        // attribute
	    io.writePad(4);

	    io.writeShort(font.min_char_or_byte2);  // min-char-or-byte2
	    io.writeShort(font.max_char_or_byte2);    // max-char-or-byte2
	    io.writeShort(font.default_char); // default-char

	    io.writeShort(prop!=null ? prop.length/2 : 0); // m

	    io.writeByte(0);  // draw-direction
	    io.writeByte(font.min_byte1); // min-byte1
	    io.writeByte(font.max_byte1); // max-byte1
	    io.writeByte(0);  // all-char-exists

	    io.writeShort(font.getMaxAscent());  // font-ascent
	    io.writeShort(font.getMaxDescent()); // font-descent
	    io.writeInt(count); // reply-hint

          // property 
	    if(prop!=null){
	      for(int j=0; j<prop.length; j++){
		io.writeInt(prop[j]);
	      }
	    }

	    io.writeByte(font.lfname);
	    io.writePad((-font.lfname.length)&3);

	    if(count==0)break;
	  }
	}
      }
    }

    io.writeByte(1);
    io.writeByte(0);
    io.writeShort(c.seq);
    io.writeInt(7);
    io.writePad(52);
    io.flush();
    }
  }

  static void reqSetFontPath(Client c) throws IOException{
    int foo, n;
    IO io=c.client;

    n=c.length;
    foo=io.readShort();
    io.readPad(2);
    byte[][] path;
    path=new byte[foo][];
    int count=0;
    for(int i=0; i<path.length; i++){
      foo=io.readByte();
      count+=(1+foo);
      path[i]=new byte[foo];
      io.readByte(path[i], 0, foo);
    }
    io.readPad((-count)&3);
    synchronized (Font.class) {
      fpath=path;
    }
  }

  static void reqGetFontPath(Client c) throws IOException{
    int foo,n;
    IO io=c.client;
    foo=c.length;

    synchronized(io){
      io.writeByte(1);
      int length=0;
      for(int i=0; i<fpath.length;i++){
        length+=(1+fpath[i].length);
      }
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt((length+3)/4);
      io.writeShort(fpath.length);
      io.writePad(22);

      for(int i=0; i<fpath.length;i++){
        io.writeByte(fpath[i].length);
        io.writeByte(fpath[i]);
      }
      io.writePad((-length)&3);
      io.flush();
    }
  }

  static void reqListFonts(Client c) throws IOException{
    int foo, n;
    int maxname;
    IO io=c.client;
    n=c.length;
    maxname=foo=io.readShort();
    foo=io.readShort();
    byte[] pattern=null;
    {
      io.readByte(c.bbuffer, 0, foo);
      io.readPad((-foo)&3);
      for(int i=0; i<foo;i++){
	if('A'<=c.bbuffer[i] && c.bbuffer[i]<='Z')
	  c.bbuffer[i]=(byte)('a'+c.bbuffer[i]-'A');
      }
      String s=new String(c.bbuffer, 0, foo);
      pattern=s.getBytes();
    }

    synchronized(io){
      io.writeByte(1);

      int count=0;
      int length=0;

      for(int i=0; i< aliases.length; i++){
        if(match_aux(aliases[i].name, 0, pattern, 0)){
  	  count++;
	  length+=(1+aliases[i].name.length);
	  if(maxname<count)break;
        }
      }
      if(maxname>=count){
        for(int i=0; i<flist.length;i++){
	  if(match_aux(flist[i].lfname, 0, pattern, 0)){
	    count++;
	    length+=(1+flist[i].lfname.length);
	    if(maxname<count)break;
	  }
        }
      }
      if(maxname>=count && count==0){
        for(int i=0; i<flist.length;i++){
	  if(match_scalable(flist[i].lfname, pattern)){
	    count++;
	    byte[] tmp=Font.genScaleName(flist[i].lfname, pattern);
	    length+=(1+tmp.length);
	    if(maxname<count)break;
	  }
        }
      }

      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt((length+3)/4);
      io.writeShort(count);
      io.writePad(22);

      if(count>0){
        for (int i=0; i< aliases.length; i++){
  	  if(match_aux(aliases[i].name, 0, pattern, 0)){
	    count--;
	    io.writeByte(aliases[i].name.length);
	    io.writeByte(aliases[i].name);
	    if(count==0)break;
	  }
        }
      }
      if(count>0){
        for(int i=0; i<flist.length;i++){
	  if(match_aux(flist[i].lfname, 0, pattern, 0)){
	    count--;
	    io.writeByte(flist[i].lfname.length);
	    io.writeByte(flist[i].lfname);
	    if(count==0)break;
	  }
        }
      }
      if(count>0){
        for(int i=0; i<flist.length;i++){
	  if(match_scalable(flist[i].lfname, pattern)){
	    count--;
	    byte[] tmp=Font.genScaleName(flist[i].lfname, pattern);
	    io.writeByte(tmp.length);
	    io.writeByte(tmp);
	    if(count==0)break;
	  }
        }
      }
      io.writePad((-length)&3);
      io.flush();
    }
  }

  static void reqCloseFont(Client c) throws IOException{
    int foo;
    IO io=c.client;
    foo=c.length;
    foo=io.readInt();
    Resource.freeResource(foo, Resource.RT_NONE);
  }
  void delete() throws IOException{
    name=null;
    lfname=null;
    encoding=null;
    if(font!=null)font.delete();
    font=null;
  }
  static void reqOpenFont(Client c) throws IOException{
    int foo;
    int n;
    int fid;
    IO io=c.client;
    n=c.length;
    foo=io.readInt(); fid=foo;
    foo=io.readShort();
    io.readPad(2);
    byte[] qqqq=null;
    String s;
    {
      qqqq=c.bbuffer;
      io.readByte(qqqq, 0, foo);
      io.readPad((-foo)&3);
      for(int i=0; i<foo;i++){
	if('A'<=qqqq[i] && qqqq[i]<='Z')
	  qqqq[i]=(byte)('a'+qqqq[i]-'A');
      }
      s=new String(qqqq, 0, foo);
    }

    try{
      Font f=new Font(fid, s);
      Resource.add(f);
    }
    catch(Exception e){
      //System.out.println(e);
    }
  }

  static void reqQueryFont(Client c) throws IOException{
    int foo;
    int n;
    IO io=c.client;
    foo=io.readInt();
    c.length-=2;
    Font f=(Font)Resource.lookupIDByType(foo, Resource.RT_FONT);
    if(f==null){
      c.errorValue=foo;
      c.errorReason=7; // BadFont
      return;
    }

    int nn;
    int[] prop=f.getProp();
    boolean hascinfo=f.hasCharInfo();

    synchronized(io){
      io.writeByte(1);
      io.writePad(1);
      io.writeShort(c.seq);
      io.writeInt(7 + 
                (prop!=null ? prop.length/2 : 0)*2 + 
                (hascinfo ? 
                 (f.font.max_char_or_byte2-f.font.min_char_or_byte2+1)*3 :
                 0));

      // min-bounds
      io.writeShort(0);                      // left-side-bearing
      io.writeShort(0);                      // right-side-bearing
      io.writeShort(f.font.min_width);       // character-width
      io.writeShort(0);                      // ascent
      io.writeShort(0);                      // descent
      io.writeShort(0);                      // attribute
      io.writePad(4);
    
      // max-bounds
      io.writeShort(0);                      // left-side-bearing
      io.writeShort(f.font.max_width);       // right-side-bearing
      io.writeShort(f.font.max_width);       // character-width
      io.writeShort(f.font.getMaxAscent());  // ascent
      io.writeShort(f.font.getMaxDescent()); // descent
      io.writeShort(0);                      // attribute
      io.writePad(4);

      io.writeShort(f.font.min_char_or_byte2);   // min-char-or-byte2
      io.writeShort(f.font.max_char_or_byte2);   // max-char-or-byte2

      io.writeShort(f.font.default_char);        // default-char
      io.writeShort(prop!=null ? prop.length/2 : 0);

      io.writeByte(0);                           // draw direction
      io.writeByte(f.font.min_byte1);            // min-byte1
      io.writeByte(f.font.max_byte1);            // max-byte1
      io.writeByte(0); // all char exist

      io.writeShort(f.font.getMaxAscent());
      io.writeShort(f.font.getMaxDescent());
      if(hascinfo){
        io.writeInt(f.font.max_char_or_byte2-f.font.min_char_or_byte2+1); // m 
      }
      else{	
        io.writeInt(0);                          // m 
      }

      if(prop!=null){
        for(int i=0; i<prop.length; i++){
  	  io.writeInt(prop[i]);
        }
      }

      if(hascinfo){
        f.dumpCharInfo(c);                      // m 
      }

      io.flush();
    }
  }

  int[] getProp(){
    return font.getProp();
  }

  boolean hasCharInfo(){
    return (font.min_width!=font.max_width) || font.min_byte1==0; 
  }

  void dumpCharInfo(Client c) throws IOException{
    IO io=c.client;
    char w=0;
    int ma=font.getMaxAscent();
    int md=font.getMaxDescent();
    if(font.min_width==font.max_width){
      w=(char)font.min_width;
      for(int i=font.min_char_or_byte2; i<=font.max_char_or_byte2; i++){
        io.writeShort(0);                    // left-side-bearing
        io.writeShort(w);                    // right-side-bearing
        io.writeShort(w);                    // character-width
        io.writeShort(ma);                   // ascent
        io.writeShort(md);                   // descent
        io.writeShort(0);                    // attribute
      }	
      return;
    }
    if(font.encoding==null){
      int[] widths=font.getWidths();
      for(int i=font.min_char_or_byte2; i<=font.max_char_or_byte2; i++){
        w=(char)widths[i];
        io.writeShort(0);                    // left-side-bearing
        io.writeShort(w);                    // right-side-bearing
        io.writeShort(w);                    // character-width
        io.writeShort(ma);                   // ascent
        io.writeShort(md);                   // descent
        io.writeShort(0);                    // attribute
      }	
      return;
    }
    if(font.max_char_or_byte2 <= 0xff){
      byte[] src=new byte[1];	
      char[] dst=new char[1];	
      int width;
      for(int i=font.min_char_or_byte2; i<=font.max_char_or_byte2; i++){
        src[0]=(byte)i;
        encode(src, 0, 1, dst);
        w=(char)font.charWidth(dst[0]);
        io.writeShort(0);                    // left-side-bearing
        io.writeShort(w);                    // right-side-bearing
        io.writeShort(w);                    // character-width
        io.writeShort(ma);                   // ascent
        io.writeShort(md);                   // descent
        io.writeShort(0);                    // attribute
      }	
      return;
    }
    else{
      byte[] src=new byte[2];	
      char[] dst=new char[1];	
      int width;
      for(int i=font.min_char_or_byte2; i<=font.max_char_or_byte2; i++){
        src[0]=(byte)(i>>8);
        src[1]=(byte)(i);
        encode(src, 0, 2, dst);
        w=(char)font.charWidth(dst[0]);
        io.writeShort(0);                    // left-side-bearing
        io.writeShort(w);                    // right-side-bearing
        io.writeShort(w);                    // character-width
        io.writeShort(ma);                   // ascent
        io.writeShort(md);                   // descent
        io.writeShort(0);                    // attribute
      }	
      return;
    }
  }	

  int encode(byte[] bbuffer, int start, int len, char[] cbuffer){
    if(font.charset==null){
      return 0;
    }
    return font.charset.encode(bbuffer, start, len, cbuffer);
  }
}

class Alias{
  byte[] name;
  DDXFont font;
  Alias(byte[] n, DDXFont f){
    this.name=n; this.font=f;
  }
}
