/* -*-mode:java; c-basic-offset:2; -*- */
/* WeirdX - Guess.
 *
 * Copyright (C) 2000-2004 Marcus Schiesser
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
 *
 * Authors:  Marcus Schiesser<marcus.schiesser@student.uni-tuebingen.de>
 *
 */ 

package com.jcraft.weirdx;


import java.awt.event.KeyEvent;


final class Keymap_de extends Keymap{
  private static final int VK_ALT_GRAPH = 0x15;

  private int[] _map={
	0x0, 0x0, 0x0, 0x0, // 8
	0xff1b, 0x0, 0x0, 0x0, // 9 (Escape)	
	0x0031, 0x0021, 0x00b9, 0x00a1, // 10 (1) (exclam) (onesuperior) (exclamdown)	
	0x0032, 0x0022, 0x00b2, 0x0ac3, // 11 (2) (quotedbl) (twosuperior) (oneeighth)	
	0x0033, 0x00a7, 0x00b3, 0x00a3, // 12 (3) (section) (threesuperior) (sterling)	
	0x0034, 0x0024, 0x00bc, 0x00a4, // 13 (4) (dollar) (onequarter) (currency)	
	0x0035, 0x0025, 0x00bd, 0x0ac4, // 14 (5) (percent) (onehalf) (threeeighths)	
	0x0036, 0x0026, 0x00be, 0x0ac5, // 15 (6) (ampersand) (threequarters) (fiveeighths)	
	0x0037, 0x002f, 0x007b, 0x0ac6, // 16 (7) (slash) (braceleft) (seveneighths)	
	0x0038, 0x0028, 0x005b, 0x0ac9, // 17 (8) (parenleft) (bracketleft) (trademark)	
	0x0039, 0x0029, 0x005d, 0x00b1, // 18 (9) (parenright) (bracketright) (plusminus)	
	0x0030, 0x003d, 0x007d, 0x00b0, // 19 (0) (equal) (braceright) (degree)	
	0x00df, 0x003f, 0x005c, 0x00bf, // 20 (ssharp) (question) (backslash) (questiondown)	
	0xfe51, 0xfe50, 0xfe5b, 0xfe5c, // 21 (dead_acute) (dead_grave) (dead_cedilla) (dead_ogonek)	
	0xff08, 0x0, 0x0, 0x0, // 22 (BackSpace)	
	0xff09, 0xfe20, 0x0, 0x0, // 23 (Tab) (ISO_Left_Tab)	
	0x0071, 0x0051, 0x0040, 0x07d9, // 24 (q) (Q) (at) (Greek_OMEGA)	
	0x0077, 0x0057, 0x01b3, 0x01a3, // 25 (w) (W) (lstroke) (Lstroke)	
	0x0065, 0x0045, 0x20ac, 0x0, // 26 (e) (E) (EuroSign)	
	0x0072, 0x0052, 0x00b6, 0x00ae, // 27 (r) (R) (paragraph) (registered)	
	0x0074, 0x0054, 0x03bc, 0x03ac, // 28 (t) (T) (tslash) (Tslash)	
	0x007a, 0x005a, 0x08fb, 0x00a5, // 29 (z) (Z) (leftarrow) (yen)	
	0x0075, 0x0055, 0x08fe, 0x08fc, // 30 (u) (U) (downarrow) (uparrow)	
	0x0069, 0x0049, 0x08fd, 0x02b9, // 31 (i) (I) (rightarrow) (idotless)	
	0x006f, 0x004f, 0x00f8, 0x00d8, // 32 (o) (O) (oslash) (Ooblique)	
	0x0070, 0x0050, 0x00fe, 0x00de, // 33 (p) (P) (thorn) (THORN)	
	0x00fc, 0x00dc, 0xfe57, 0xfe58, // 34 (udiaeresis) (Udiaeresis) (dead_diaeresis) (dead_abovering)	
	0x002b, 0x002a, 0xfe53, 0xfe54, // 35 (plus) (asterisk) (dead_tilde) (dead_macron)	
	0xff0d, 0x0, 0x0, 0x0, // 36 (Return)	
	0xffe3, 0x0, 0x0, 0x0, // 37 (Control_L)	
	0x0061, 0x0041, 0x00e6, 0x00c6, // 38 (a) (A) (ae) (AE)	
	0x0073, 0x0053, 0x00df, 0x00a7, // 39 (s) (S) (ssharp) (section)	
	0x0064, 0x0044, 0x00f0, 0x00d0, // 40 (d) (D) (eth) (ETH)	
	0x0066, 0x0046, 0x01f0, 0x00aa, // 41 (f) (F) (dstroke) (ordfeminine)	
	0x0067, 0x0047, 0x03bf, 0x03bd, // 42 (g) (G) (eng) (ENG)	
	0x0068, 0x0048, 0x02b1, 0x02a1, // 43 (h) (H) (hstroke) (Hstroke)	
	0x006a, 0x004a, 0x0, 0x0, // 44 (j) (J)	
	0x006b, 0x004b, 0x03a2, 0x0026, // 45 (k) (K) (kra) (ampersand)	
	0x006c, 0x004c, 0x01b3, 0x01a3, // 46 (l) (L) (lstroke) (Lstroke)	
	0x00f6, 0x00d6, 0xfe51, 0xfe59, // 47 (odiaeresis) (Odiaeresis) (dead_acute) (dead_doubleacute)	
	0x00e4, 0x00c4, 0xfe52, 0xfe5a, // 48 (adiaeresis) (Adiaeresis) (dead_circumflex) (dead_caron)	
	0xfe52, 0x00b0, 0x00ac, 0x0, // 49 (dead_circumflex) (degree) (notsign)	
	0xffe1, 0x0, 0x0, 0x0, // 50 (Shift_L)	
	0x0023, 0x0027, 0xfe50, 0xfe55, // 51 (numbersign) (apostrophe) (dead_grave) (dead_breve)	
	0x0079, 0x0059, 0x00ab, 0x00, // 52 (y) (Y) (guillemotleft) 	
	0x0078, 0x0058, 0x00bb, 0x00, // 53 (x) (X) (guillemotright) 	
	0x0063, 0x0043, 0x00a2, 0x00a9, // 54 (c) (C) (cent) (copyright)	
	0x0076, 0x0056, 0x0ad2, 0x0060, // 55 (v) (V) (leftdoublequotemark) (grave)	
	0x0062, 0x0042, 0x0ad3, 0x0027, // 56 (b) (B) (rightdoublequotemark) (apostrophe)	
	0x006e, 0x004e, 0x0, 0x0, // 57 (n) (N)	
	0x006d, 0x004d, 0x00b5, 0x00ba, // 58 (m) (M) (mu) (masculine)	
	0x002c, 0x003b, 0x08a3, 0x00d7, // 59 (comma) (semicolon) (horizconnector) (multiply)	
	0x002e, 0x003a, 0x00b7, 0x00f7, // 60 (period) (colon) (periodcentered) (division)	
	0x002d, 0x005f, 0xfe60, 0xfe56, // 61 (minus) (underscore) (dead_belowdot) (dead_abovedot)	
	0xffe2, 0x0, 0x0, 0x0, // 62 (Shift_R)	
	0xffaa, 0x0, 0x0, 0x0, // 63 (KP_Multiply)	
	0xffe9, 0xffe7, 0x0, 0x0, // 64 (Alt_L) (Meta_L)	
	0x0020, 0x0, 0x0, 0x0, // 65 (space)	
	0xffe5, 0x0, 0x0, 0x0, // 66 (Caps_Lock)	
	0xffbe, 0x0, 0x0, 0x0, // 67 (F1)	
	0xffbf, 0x0, 0x0, 0x0, // 68 (F2)	
	0xffc0, 0x0, 0x0, 0x0, // 69 (F3)	
	0xffc1, 0x0, 0x0, 0x0, // 70 (F4)	
	0xffc2, 0x0, 0x0, 0x0, // 71 (F5)	
	0xffc3, 0x0, 0x0, 0x0, // 72 (F6)	
	0xffc4, 0x0, 0x0, 0x0, // 73 (F7)	
	0xffc5, 0x0, 0x0, 0x0, // 74 (F8)	
	0xffc6, 0x0, 0x0, 0x0, // 75 (F9)	
	0xffc7, 0x0, 0x0, 0x0, // 76 (F10)	
	0xff7f, 0xfef9, 0x0, 0x0, // 77 (Num_Lock) (Pointer_EnableKeys)	
	0xff14, 0x0, 0x0, 0x0, // 78 (Scroll_Lock)	
	0xff95, 0xffb7, 0x0, 0x0, // 79 (KP_Home) (KP_7)	
	0xff97, 0xffb8, 0x0, 0x0, // 80 (KP_Up) (KP_8)	
	0xff9a, 0xffb9, 0x0, 0x0, // 81 (KP_Prior) (KP_9)	
	0xffad, 0x0, 0x0, 0x0, // 82 (KP_Subtract)	
	0xff96, 0xffb4, 0x0, 0x0, // 83 (KP_Left) (KP_4)	
	0xff9d, 0xffb5, 0x0, 0x0, // 84 (KP_Begin) (KP_5)	
	0xff98, 0xffb6, 0x0, 0x0, // 85 (KP_Right) (KP_6)	
	0xffab, 0x0, 0x0, 0x0, // 86 (KP_Add)	
	0xff9c, 0xffb1, 0x0, 0x0, // 87 (KP_End) (KP_1)	
	0xff99, 0xffb2, 0x0, 0x0, // 88 (KP_Down) (KP_2)	
	0xff9b, 0xffb3, 0x0, 0x0, // 89 (KP_Next) (KP_3)	
	0xff9e, 0xffb0, 0x0, 0x0, // 90 (KP_Insert) (KP_0)	
	0xff9f, 0xffae, 0x0, 0x0, // 91 (KP_Delete) (KP_Decimal)	
	0xff61, 0xff15, 0x0, 0x0, // 92 (Print) (Sys_Req)	
	0x0, 0x0, 0x0, 0x0, // 93
	0x003c, 0x003e, 0x007c, 0x00a6, // 94 (less) (greater) (bar) (brokenbar)	
	0xffc8, 0x0, 0x0, 0x0, // 95 (F11)	
	0xffc9, 0x0, 0x0, 0x0, // 96 (F12)	
	0xff50, 0x0, 0x0, 0x0, // 97 (Home)	
	0xff52, 0x0, 0x0, 0x0, // 98 (Up)	
	0xff55, 0x0, 0x0, 0x0, // 99 (Prior)	
	0xff51, 0x0, 0x0, 0x0, // 100 (Left)	
	0x0, 0x0, 0x0, 0x0, // 101
	0xff53, 0x0, 0x0, 0x0, // 102 (Right)	
	0xff57, 0x0, 0x0, 0x0, // 103 (End)	
	0xff54, 0x0, 0x0, 0x0, // 104 (Down)	
	0xff56, 0x0, 0x0, 0x0, // 105 (Next)	
	0xff63, 0x0, 0x0, 0x0, // 106 (Insert)	
	0xffff, 0x0, 0x0, 0x0, // 107 (Delete)	
	0xff8d, 0x0, 0x0, 0x0, // 108 (KP_Enter)	
	0xffe4, 0x0, 0x0, 0x0, // 109 (Control_R)	
	0xff13, 0xff6b, 0x0, 0x0, // 110 (Pause) (Break)	
	0xff61, 0xff15, 0x0, 0x0, // 111 (Print) (Sys_Req)	
	0xffaf, 0x0, 0x0, 0x0, // 112 (KP_Divide)	
	0xff7e, 0xff20, 0x0, 0x0, // 113 (Mode_switch) (Multi_key)	
	0xff13, 0xff6b, 0x0, 0x0, // 114 (Pause) (Break)	
	0x0, 0x0, 0x0, 0x0, // 115
	0x0, 0x0, 0x0, 0x0, // 116
	0x0, 0x0, 0x0, 0x0, // 117
	0x0, 0x0, 0x0, 0x0, // 118
	0x0, 0x0, 0x0, 0x0, // 119
	0x0, 0x0, 0x0, 0x0, // 120
	0x0, 0x0, 0x0, 0x0, // 121
	0x0, 0x0, 0x0, 0x0, // 122
	0x0, 0x0, 0x0, 0x0, // 123
	0x0, 0x0, 0x0, 0x0, // 124
	0x0, 0x0, 0x0, 0x0, // 125
	0x0, 0x0, 0x0, 0x0, // 126
	0x0, 0x0, 0x0, 0x0, // 127
	0x0, 0x0, 0x0, 0x0, // 128
	0x0, 0x0, 0x0, 0x0, // 129
	0x0, 0x0, 0x0, 0x0, // 130
	0x0, 0x0, 0x0, 0x0, // 131
	0x0, 0x0, 0x0, 0x0, // 132
	0x0, 0x0, 0x0, 0x0, // 133
	0x0, 0x0, 0x0, 0x0, // 134
	0x0, 0x0, 0x0, 0x0, // 135
	0x0, 0x0, 0x0, 0x0, // 136
	0x0, 0x0, 0x0, 0x0, // 137
	0x0, 0x0, 0x0, 0x0, // 138
	0x0, 0x0, 0x0, 0x0, // 139
	0x0, 0x0, 0x0, 0x0, // 140
	0x0, 0x0, 0x0, 0x0, // 141
	0x0, 0x0, 0x0, 0x0, // 142
	0x0, 0x0, 0x0, 0x0, // 143
	0x0, 0x0, 0x0, 0x0, // 144
	0x0, 0x0, 0x0, 0x0, // 145
	0x0, 0x0, 0x0, 0x0, // 146
	0x0, 0x0, 0x0, 0x0, // 147
	0x0, 0x0, 0x0, 0x0, // 148
	0x0, 0x0, 0x0, 0x0, // 149
	0x0, 0x0, 0x0, 0x0, // 150
	0x0, 0x0, 0x0, 0x0, // 151
	0x0, 0x0, 0x0, 0x0, // 152
	0x0, 0x0, 0x0, 0x0, // 153
	0x0, 0x0, 0x0, 0x0, // 154
	0x0, 0x0, 0x0, 0x0, // 155
	0x0, 0x0, 0x0, 0x0, // 156
	0x0, 0x0, 0x0, 0x0, // 157
	0x0, 0x0, 0x0, 0x0, // 158
	0x0, 0x0, 0x0, 0x0, // 159
	0x0, 0x0, 0x0, 0x0, // 160
	0x0, 0x0, 0x0, 0x0, // 161
	0x0, 0x0, 0x0, 0x0, // 162
	0x0, 0x0, 0x0, 0x0, // 163
	0x0, 0x0, 0x0, 0x0, // 164
	0x0, 0x0, 0x0, 0x0, // 165
	0x0, 0x0, 0x0, 0x0, // 166
	0x0, 0x0, 0x0, 0x0, // 167
	0x0, 0x0, 0x0, 0x0, // 168
	0x0, 0x0, 0x0, 0x0, // 169
	0x0, 0x0, 0x0, 0x0, // 170
	0x0, 0x0, 0x0, 0x0, // 171
	0x0, 0x0, 0x0, 0x0, // 172
	0x0, 0x0, 0x0, 0x0, // 173
	0x0, 0x0, 0x0, 0x0, // 174
	0x0, 0x0, 0x0, 0x0, // 175
	0x0, 0x0, 0x0, 0x0, // 176
	0x0, 0x0, 0x0, 0x0, // 177
	0x0, 0x0, 0x0, 0x0, // 178
	0x0, 0x0, 0x0, 0x0, // 179
	0x0, 0x0, 0x0, 0x0, // 180
	0x0, 0x0, 0x0, 0x0, // 181
	0x0, 0x0, 0x0, 0x0, // 182
	0x0, 0x0, 0x0, 0x0, // 183
	0x0, 0x0, 0x0, 0x0, // 184
	0x0, 0x0, 0x0, 0x0, // 185
	0x0, 0x0, 0x0, 0x0, // 186
	0x0, 0x0, 0x0, 0x0, // 187
	0x0, 0x0, 0x0, 0x0, // 188
	0x0, 0x0, 0x0, 0x0, // 189
	0x0, 0x0, 0x0, 0x0, // 190
	0x0, 0x0, 0x0, 0x0, // 191
	0x0, 0x0, 0x0, 0x0, // 192
	0x0, 0x0, 0x0, 0x0, // 193
	0x0, 0x0, 0x0, 0x0, // 194
	0x0, 0x0, 0x0, 0x0, // 195
	0x0, 0x0, 0x0, 0x0, // 196
	0x0, 0x0, 0x0, 0x0, // 197
	0x0, 0x0, 0x0, 0x0, // 198
	0x0, 0x0, 0x0, 0x0, // 199
	0x0, 0x0, 0x0, 0x0, // 200
	0x0, 0x0, 0x0, 0x0, // 201
	0x0, 0x0, 0x0, 0x0, // 202
	0x0, 0x0, 0x0, 0x0, // 203
	0x0, 0x0, 0x0, 0x0, // 204
	0x0, 0x0, 0x0, 0x0, // 205
	0x0, 0x0, 0x0, 0x0, // 206
	0x0, 0x0, 0x0, 0x0, // 207
	0x0, 0x0, 0x0, 0x0, // 208
	0x0, 0x0, 0x0, 0x0, // 209
	0x0, 0x0, 0x0, 0x0, // 210
	0x0, 0x0, 0x0, 0x0, // 211
	0x0, 0x0, 0x0, 0x0, // 212
	0x0, 0x0, 0x0, 0x0, // 213
	0x0, 0x0, 0x0, 0x0, // 214
	0x0, 0x0, 0x0, 0x0, // 215
	0x0, 0x0, 0x0, 0x0, // 216
	0x0, 0x0, 0x0, 0x0, // 217
	0x0, 0x0, 0x0, 0x0, // 218
	0x0, 0x0, 0x0, 0x0, // 219
	0x0, 0x0, 0x0, 0x0, // 220
	0x0, 0x0, 0x0, 0x0, // 221
	0x0, 0x0, 0x0, 0x0, // 222
	0x0, 0x0, 0x0, 0x0, // 223
	0x0, 0x0, 0x0, 0x0, // 224
	0x0, 0x0, 0x0, 0x0, // 225
	0x0, 0x0, 0x0, 0x0, // 226
	0x0, 0x0, 0x0, 0x0, // 227
	0x0, 0x0, 0x0, 0x0, // 228
	0x0, 0x0, 0x0, 0x0, // 229
	0x0, 0x0, 0x0, 0x0, // 230
	0x0, 0x0, 0x0, 0x0, // 231
	0x0, 0x0, 0x0, 0x0, // 232
	0x0, 0x0, 0x0, 0x0, // 233
	0x0, 0x0, 0x0, 0x0, // 234
	0x0, 0x0, 0x0, 0x0, // 235
	0x0, 0x0, 0x0, 0x0, // 236
	0x0, 0x0, 0x0, 0x0, // 237
	0x0, 0x0, 0x0, 0x0, // 238
	0x0, 0x0, 0x0, 0x0, // 239
	0x0, 0x0, 0x0, 0x0, // 240
	0x0, 0x0, 0x0, 0x0, // 241
	0x0, 0x0, 0x0, 0x0, // 242
	0x0, 0x0, 0x0, 0x0, // 243
	0x0, 0x0, 0x0, 0x0, // 244
	0x0, 0x0, 0x0, 0x0, // 245
	0x0, 0x0, 0x0, 0x0, // 246
	0x0, 0x0, 0x0, 0x0, // 247
	0x0, 0x0, 0x0, 0x0, // 248
	0x0, 0x0, 0x0, 0x0, // 249
	0x0, 0x0, 0x0, 0x0, // 250
	0x0, 0x0, 0x0, 0x0, // 251
	0x0, 0x0, 0x0, 0x0, // 252
	0x0, 0x0, 0x0, 0x0, // 253
	0x0, 0x0, 0x0, 0x0, // 254
	0x0, 0x0, 0x0, 0x0  // 255
  };

  Keymap_de(){
	width = 4;
	start = 8;
	count = 248;
    map=_map;
  }

  final int getCode(KeyEvent e){
	if(e.isShiftDown()) state|=1;
	if(e.isControlDown()) state|=4;
	if(e.isAltDown()) state|=8;
	int key=e.getKeyCode();
	// check for windoze ALT+GR Style
	if(e.isAltDown() && e.isControlDown()) {
		key = VK_ALT_GRAPH;
	}
	// debug message
	//System.out.println( "Key: " + Integer.toHexString(key) );
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
/*      case KeyEvent.VK_BACK_QUOTE:
        if(e.getKeyChar()==0x27){key=0x27;}
        else if(e.getKeyChar()==0x40){key=0x40;}
        else
	key=0x60; break;
      case KeyEvent.VK_QUOTE:
        if(e.getKeyChar()==0x23){key=0x23;}
        else if(e.getKeyChar()==0x7e){key=0x7e;}
	else
	key=0x27; break; */
      case KeyEvent.VK_SHIFT:
	key=0xffe1; break;
      case KeyEvent.VK_CONTROL:
	key=0xffe3; break;
      case KeyEvent.VK_ALT:
	key=0xffe9; break;
      case VK_ALT_GRAPH:
	key=0xff7e; break;
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

	// debug message
//	System.out.println( "AfterKey: " + Integer.toHexString(key) );

    int s=32;
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
    km=new Keymap_gb();
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
}
