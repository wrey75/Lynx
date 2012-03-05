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
import java.io.*;

public class MemorySocket extends Socket {

	private boolean server;
	private Connection connection;

	public MemorySocket(String address, int port) throws IOException{
		// this constructor is called on client side
		server = false;	
		// retrieve Connector
		Connector c = Connector.getport(port);
		// add Connection
		connection = c.addConnection();
		// inform ServerSocket
		c.getServerThread().interrupt();
	}

	public MemorySocket(Connection c){
		server = true;
		connection = c;
	}

        public void close()throws IOException{
          connection.close();
	}
	
	public InputStream getInputStream(){
		if(server)
			return connection.getServerInputStream();
		else
			return connection.getClientInputStream();
	}
	
	public OutputStream getOutputStream(){
		if(server)
			return connection.getServerOutputStream();
		else
			return connection.getClientOutputStream();
	}

}
