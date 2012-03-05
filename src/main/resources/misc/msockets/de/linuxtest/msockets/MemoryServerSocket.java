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

import java.net.*;

public class MemoryServerSocket { // sorry not extended from ServerSocket as ServerSocket() is private

	private Connector connector;
        private int port;

	public MemoryServerSocket(int port){
                this.port=port;
		connector = new Connector(Thread.currentThread(), port);
	}

	public Socket accept(){
		connector.setThread(Thread.currentThread());
		try{
			while(true){
				Thread.sleep(1000);
			}
		}catch(InterruptedException e){
		}
		Connection c = connector.getLastConnection();
		return (new MemorySocket(c));
	}

	public void close(){
  	        connector.delport(port);
	}
	
}
