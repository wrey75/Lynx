package com.oxande.lynx;

import java.awt.BufferCapabilities;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.Transparency;
import java.io.PrintWriter;


/**
 * This class is developed mainly to give
 * the information about the graphic devices.
 * 
 * It is intended to use it as a command line
 * 
 * @author wrey75
 *
 */
public class LXInfo {
	
	private PrintWriter out;
	private boolean showAllDisplays = false;
		
	public boolean isShowAllDisplays() {
		return showAllDisplays;
	}

	public void setShowAllDisplays(boolean showAllDisplays) {
		this.showAllDisplays = showAllDisplays;
	}

	private String tab( int level ) {
		StringBuffer tab = new StringBuffer( level*2 );
		for(int i = 0; i < level; i++ ) tab.append("  ");
		return tab.toString();
	}
	
	protected void print( int level, String capability, Object value ){
		out.println( tab(level) + capability + ": " + value.toString() );
		out.flush();
	}
	
	protected void title( int level, String str, String expr ) {
		StringBuffer copy = new StringBuffer();
		out.println( tab(level) + str );
		if( expr.length() > 0 ){
			while( copy.length() < str.length() ) copy.append( expr );
			out.println( tab(level) + copy.substring(0, str.length()) );
		}
	}
	
	protected void title( int level, String title ) {
		this.title(level, title.substring(1), title.substring(0, 1));
	}
	
	public void printDevice( int level, GraphicsDevice env ){
		StringBuffer v = new StringBuffer( );
		v.append( env.getIDstring() );
		title( level, env.getIDstring(), "--" );
		print( level, "Full screen support", env.isFullScreenSupported() );
		int accMemory = env.getAvailableAcceleratedMemory();
		print( level, "Accelerated memory", accMemory + " bytes" );
		String type = "unknown";
		switch( env.getType() ){
		case GraphicsDevice.TYPE_IMAGE_BUFFER : 
			type = "Buffer"; 
			break;
		case GraphicsDevice.TYPE_PRINTER :
			type = "Printer";
			break;
		case GraphicsDevice.TYPE_RASTER_SCREEN :
			type = "Screen";
			break;
		}
		print( level, "Type", type);
		print( level, "Display change supported", env.isDisplayChangeSupported() );
		printDisplayMode(level+1, 0, env.getDisplayMode() );
		DisplayMode[] modes = env.getDisplayModes();
		for( int i = 0; i < modes.length; i++ ){
			printDisplayMode(level+1, i+1, modes[i] );
		}
		
		GraphicsConfiguration[] confs = env.getConfigurations();
		
		int nb = 0;
		for( GraphicsConfiguration conf : confs ){
			nb++;
			printConfig( level+1, nb, conf );
		}
	}
	public void printDisplayMode( int level, int nb, DisplayMode mode ){
		if( showAllDisplays || nb == 0 ){
			title( level, "Display #" + nb + (nb == 0 ? " (current)" : ""), ":" );
			print( level, "Bit depth", mode.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI ? "multiple" : mode.getBitDepth() );
			print( level, "Size", mode.getWidth() + "x" + mode.getHeight() );
			int rate = mode.getRefreshRate();
			print( level, "Refresh rate",  rate == DisplayMode.REFRESH_RATE_UNKNOWN ? "unknown" : rate + " Hz");
		}
	}
	
	public void printInfo( GraphicsEnvironment env ){
		title( 0, "=GRAPHIC DEVICES" );
		for( GraphicsDevice device : env.getScreenDevices() ){
			printDevice( 1, device );
		}
		out.flush();
	}
	
	public String imageCapabilities( ImageCapabilities caps ){
		StringBuffer capabilities = new StringBuffer();
		if( caps.isAccelerated() ) capabilities.append( ", accelerated" );
		if( caps.isTrueVolatile() ) capabilities.append( ", true volatile" );
		if( capabilities.length() == 0 ) return "none";
		return capabilities.substring(2);
		
	}
	
	public String bufferCapabilities( BufferCapabilities caps ){
		StringBuffer capabilities = new StringBuffer();
		if( caps.isPageFlipping() ) capabilities.append(", flipping");
		if( caps.isMultiBufferAvailable() ) capabilities.append(", multiple buffers");
		if( caps.isFullScreenRequired() ) capabilities.append(", full screen required");
		if( capabilities.length() == 0 ) return "none";
		return capabilities.substring(2);
		
	}
	
	public void printConfig( int level, int numConfig, GraphicsConfiguration env ){
		title( level, "Configuration #" + numConfig, "-" );
		print( level, "Size", env.getBounds().width + "x" + env.getBounds().height );
		print( level, "ColorModel", env.getColorModel());
		print( level, "ColorModel (opaque)", env.getColorModel( Transparency.OPAQUE ));
		print( level, "ColorModel (bitmask)", env.getColorModel( Transparency.BITMASK ));
		print( level, "ColorModel (translucent)", env.getColorModel( Transparency.TRANSLUCENT ));
		print( level, "Default transform", env.getDefaultTransform() );
		print( level, "Normalizing transform", env.getNormalizingTransform() );
		print( level, "Capabilities", bufferCapabilities( env.getBufferCapabilities() ) );
		print( level, "Back buffer capabilities", imageCapabilities( env.getBufferCapabilities().getBackBufferCapabilities() ) );
		print( level, "Front buffer capabilities", imageCapabilities( env.getBufferCapabilities().getFrontBufferCapabilities() ) );
	}
	
	public void setWriter( PrintWriter out ){
		this.out = out;
	}
	
	static public void main( String[] args ){
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		LXInfo appli = new LXInfo();
		for( int i = 0; i < args.length; i++ ){
			char c = args[i].charAt(0);
			if( c == '-' && args[i].length() > 1 ){
				switch( args[i].charAt(1) ){
				case 'd' : // Show all displays
					appli.setShowAllDisplays(true);
					break;
				}
			}
		}
		appli.setWriter( new PrintWriter( System.out ) );
		appli.printInfo( env );
		
	}
}
