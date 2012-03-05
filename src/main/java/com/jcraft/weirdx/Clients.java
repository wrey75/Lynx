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

class Clients extends Resource{
  Clients next;
  int resource;
  Clients(int id){
    super(id, RT_OTHERCLIENT);
    this.id=id;
    this.resource=id;
  }
  final boolean sameClient(Client c){
    Client cc=getClient();
    if(cc==null)return false;
    return cc.index==c.index;
  }
  final Client getClient(){
    return Client.clients[((resource & Client.CLIENTMASK) >> Client.CLIENTOFFSET)];
  }
}
