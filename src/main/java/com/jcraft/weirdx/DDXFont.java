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

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.weirdx.res.XFont;


public class DDXFont {
	static private Log LOG = LogFactory.getLog(DDXFont.class);
	static Map<String, RefCount> table = new HashMap<String, DDXFont.RefCount>();

	static class RefCount {
		AtomicInteger count = new AtomicInteger(1);
		String key = null;
		Font font = null;

		RefCount(String key, Font font) {
			this.key = key;
			this.font = font;
		}
	}

    private static synchronized Font getFont(String name, int style, int size){
    	String key = name + ',' + style + ',' + size;
    	RefCount foo = (RefCount)table.get(key);
    	if( foo != null ){
    		// We increment the refence. 
    		foo.count.incrementAndGet();
    		return foo.font;
    	}
    	
    	// Create the font and store it.
    	Font f = new Font(name, style, size);
    	foo = new RefCount(key, f);
    	table.put(key, foo);
    	return f;
    }

    private static synchronized void delFont(Font f){
    	for( RefCount foo : table.values() ){
    		if( foo.font == f ){
    			int v = foo.count.decrementAndGet(); 
    			if( v == 0 ){
    				LOG.debug( "Font " + f + " removed from the cache." ); 
    				table.remove( foo.key );
    				foo.font = null;
    			}
        
    			// Stop the enumeration now!
    			return;
    		}
    	}
    }

    public void delete() {
    	delFont( font );
    	font = null;
    }

    public byte[] lfname;
    Font font;
    public FontMetrics metric;
    int[] prop;

    public int min_char_or_byte2 = 32;
    public int max_char_or_byte2 = 255;
    public int min_byte1 = 0;
    public int max_byte1 = 0;
    public int default_char = 32;

    public int min_width;
    public int max_width;

    public String encoding = null;
  	public Font_CharSet charset = null;

  	public DDXFont() {
  		// Nothing to do
  	}

  	public void init( byte[] lfname ) throws UnsupportedEncodingException {
  		if( encoding != null ){
  			if( charset==null ) return;
  			int tmp = default_char;
  			int i = 0;
  			while( tmp != 0){
  				i++;
  				tmp >>= 8; 
  				tmp &= 0xffffff;
  			}
  			byte[] btmp = new byte[i];
  			tmp = default_char;
  			i--;
  			while( tmp != 0 ){
  				btmp[i] = (byte)(tmp&0xff);
  				tmp >>= 8; 
  				tmp &= 0xffffff;
  				i--; 
  			}
  			char[] ctmp = new char[1];
  			if( charset.encode(btmp, 0, btmp.length, ctmp) == 0 ){
  				return;
  			}
  		}
  		this.lfname=lfname;
  	}


  	public Font getFont(){
  		if(font!=null) return font;
  		int size=12;
  		try {
  			int tmp = Integer.parseInt(getFontSize());
  			if( tmp != 0 ){
  				size=tmp;
  			}
  		}
  		catch( Exception e ){
  			LOG.error(e);
  		}
  		
  		int style=Font.PLAIN;
  		if( getWeight().equals("bold") ) style|=Font.BOLD;
  		if( getStyle().equals("i") ) style|=Font.ITALIC;
        if( getFamily().equals("times")
        			|| getFamily().equals("times new roman")
       				|| getFamily().equals("new century schoolbook")){
        	font = getFont("Serif", style, size);
        }
        else if( getFamily().equals("helvetica") 
        			|| getFamily().equals("helvetic") 
        			|| getFamily().equals("courier") ){
        	font = getFont("SansSerif", style, size);
        }
        else {
        	font = getFont("Monospaced", style, size);
        }

        metric = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);

        String reg=getCharsetRegistry();
        String enc=getCharsetEncoding();

		for( Font_CharSet foo : XFont.charSets ){
			if (reg.equals(foo.getCharset()) || enc.equals(foo.getCharset())) {
				min_byte1 = foo.getMinByte1();
				max_byte1 = foo.getMaxByte1();
				min_char_or_byte2 = foo.getMinCharOrByte2();
				max_char_or_byte2 = foo.getMaxCharOrByte2();
				default_char = foo.getDefaultChar();
				encoding = foo.getEncoding();
				charset = foo;
				break;
			}
		}

		min_width = getMaxAdvance();
		max_width = getMaxAdvance();

		if (encoding != null) {
			int tmp = default_char;
			int i = 0;
			while (tmp != 0) {
				i++;
				tmp >>= 8;
				tmp &= 0xffffff;
			}
			byte[] btmp = new byte[i];
			tmp = default_char;
			i--;
			while (tmp != 0) {
				btmp[i] = (byte) (tmp & 0xff);
				tmp >>= 8;
				tmp &= 0xffffff;
				i--;
			}
			char[] ctmp = new char[1];
			if (charset != null)
				charset.encode(btmp, 0, btmp.length, ctmp);
			max_width = min_width = metric.charWidth(ctmp[0]);
			if (getSpace().equals("p")) {
				min_width = 0;
			}
		} else {
			char[] ctmp = new char[1];
			ctmp[0] = '@';
			max_width = metric.charsWidth(ctmp, 0, 1);
			ctmp[0] = ' ';
			min_width = metric.charsWidth(ctmp, 0, 1);
		}
		return font;
  }

  public int[] getProp(){
    if(prop==null)
      initprop();
    return prop;
  }
  
  void initprop(){
    prop=new int[2];
    prop[0]=XAtom.make("FONT", true);
    prop[1]=XAtom.make(new String(lfname), true);
  }
  public int getAscent(){ return metric.getAscent(); }
  public int getDescent(){ return metric.getDescent(); }
  int getLeading(){ return metric.getLeading(); }
  int getHeight(){ return metric.getHeight(); }
  int getMaxAdvance(){ return metric.charWidth('@'); }
  public int getMaxAscent(){ return metric.getAscent(); }
  public int getMaxDescent(){ return metric.getDescent(); }
  public int[] getWidths(){ return metric.getWidths(); }
  public int charWidth(char c){ return metric.charWidth(c); }

  String getFamily(){ return chop(1); }
  String getWeight(){ return chop(2); }
  String getStyle(){ return chop(3); }
  String getFontSize(){ return chop(6); }
  String getSpace(){ return chop(10); }
  String getCharsetRegistry(){ return chop(12); }
  String getCharsetEncoding(){ return chop(13); }

  private String chop(int i){
    int s,e;
    s=1;
    for(;i!=0;i--){ s=skip(s); s++; }
    e=skip(s);
    return new String(lfname, s, e-s);
  }
  private int skip(int i){
    while(i<lfname.length && lfname[i]!='-')i++;
    return i;
  }

  public boolean getScalable(){
    int tmp=0;
    try{ tmp=Integer.parseInt(getFontSize()); }
    catch(Exception e){}
    return tmp==0;
  }

  public DDXFont getScalableFont(byte[] name){
    DDXFont f=null;
    try{f=new DDXFont(); f.init(XFont.genScaleName(lfname, name));}
    catch(Exception e){}
    return f;
  }
}
