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

public class PipeBuffer {

	private int buffersize;
	private byte buffer[];
	private int bytesinbuffer;
	private int writePos, readPos;

	public PipeBuffer(int size){
		buffersize = size;
		buffer = new byte[size];
		clear();
	}

	/* precondition: not full */
	public synchronized void write(byte b){
        buffer[writePos++] = b;
        if (writePos >= buffersize) {
            writePos = 0;
        }
		bytesinbuffer++;
	}

	/* precondition: not empty */
	public synchronized byte read(){
        byte ret = buffer[readPos++];
        if (readPos >= buffersize) {
            readPos = 0;
        }
		bytesinbuffer--;
		return ret;
	}

	public synchronized void clear(){
		writePos = 0;
		readPos = 0;
		bytesinbuffer = 0;
	}

	public synchronized int available(){
		return bytesinbuffer;
	}

	public synchronized boolean isFull(){
		return bytesinbuffer==buffersize;
	}

	public synchronized boolean isEmpty(){
		return bytesinbuffer==0;
	}

}
