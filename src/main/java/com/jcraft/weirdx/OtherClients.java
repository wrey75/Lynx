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

import java.io.IOException;

final class OtherClients extends Clients{
  int mask;
  Window window;
  OtherClients(int id){super(id);}

  void delete() throws IOException{
    if(window==null)return;
    Client c=getClient();
    OtherClients prev=null;
    OtherClients other=(window.optional==null) ? null : window.optional.otherClients;
    for(; other!=null; other=(OtherClients)other.next) {
      if(other.getClient()==c){
	if(prev!=null){ prev.next=other.next; }
	else{ window.optional.otherClients=(OtherClients)other.next; }
	window.recalculateDeliverableEvents();
      }
      prev=other;
    }
  }
}
