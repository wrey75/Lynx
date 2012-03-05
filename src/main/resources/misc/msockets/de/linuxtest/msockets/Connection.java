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

class Connection {

	private InputStream client_input;
	private OutputStream server_output;
	private InputStream server_input;
	private OutputStream client_output;

	Connection() throws IOException {
	    /*
		FastPipedInputStream ci = new FastPipedInputStream();
		FastPipedInputStream si = new FastPipedInputStream();
		FastPipedOutputStream co = new FastPipedOutputStream();
		FastPipedOutputStream so = new FastPipedOutputStream(); 
	    */
		PipedInputStream ci = new PipedInputStream();
		PipedInputStream si = new PipedInputStream();
		PipedOutputStream co = new PipedOutputStream();
		PipedOutputStream so = new PipedOutputStream();
		ci.connect(so);
		si.connect(co);
		client_input = ci;
		client_output = co;
		server_input = si;
		server_output = so;
	}

	InputStream getClientInputStream(){
		return client_input;
	}
	OutputStream getClientOutputStream(){
		return client_output;
	}
	InputStream getServerInputStream(){
		return server_input;
	}
	OutputStream getServerOutputStream(){
		return server_output;
	}

        void close() throws IOException{
          if(server_output!=null)server_output.close();
          if(server_input!=null)server_input.close();
          if(client_input!=null)client_input.close();
          if(client_output!=null)client_output.close();
	}

}
