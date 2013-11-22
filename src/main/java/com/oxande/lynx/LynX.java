package com.oxande.lynx;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.weirdx.Client;
import com.jcraft.weirdx.CopyPaste;
import com.jcraft.weirdx.Depth;
import com.jcraft.weirdx.DisplaySocket;
import com.jcraft.weirdx.Extension;
import com.jcraft.weirdx.Format;
import com.jcraft.weirdx.Keyboard;
import com.jcraft.weirdx.Keymap;
import com.jcraft.weirdx.Keymodifier;
import com.jcraft.weirdx.LogoImage;
import com.jcraft.weirdx.RootWindow;
import com.jcraft.weirdx.Screen;
import com.jcraft.weirdx.Visual;
import com.jcraft.weirdx.WeirdX;
import com.jcraft.weirdx.XColormap;
import com.jcraft.weirdx.XCursor;
import com.jcraft.weirdx.XFont;
import com.jcraft.weirdx.XPixmap;
import com.jcraft.weirdx.XResource;

public class LynX {


	public static void main(String args[]) {
		// Redirect to WeirdX
		WeirdX.main(args);
	}
}
