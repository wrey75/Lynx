/* MemorySockets
 * A library for thread communication behaving like ordinary Java sockets
 *
 * Copyright (C) 2000 by Marcus Schiesser <info@linuxtest.de> 
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

package de.linuxtest.msockets;

import java.io.*;

class Connector {

	private Thread t;
	private Connection lastConnection;
	private int port;

	//private final static int PORTCOUNT = 10000;
	//static private Connector lookup[] = new Connector[PORTCOUNT];
        static private java.util.Hashtable lookup = new java.util.Hashtable();

	Connector(Thread t, int port){
		this.t = t;
		this.port = port;

		//lookup[port] = this;
                Integer i=new Integer(port);
                synchronized(Connector.class){
                  if(lookup.get(i)==null){
                    lookup.put(i, this);
	 	  }
		  else{
		  }
		}
	}

	synchronized Connection addConnection() throws IOException {
		lastConnection = new Connection();
		return lastConnection;
	}

	Connection getLastConnection(){
		return lastConnection;
	}

	Thread getServerThread(){
		return t;
	}

	static synchronized Connector getport(int port){
	        //return lookup[port];
  	        return (Connector)(lookup.get(new Integer(port)));
	}

	static synchronized void delport(int port){
	    lookup.remove(new Integer(port));
	}

	void setThread(Thread t){
		this.t = t;
	}
}
