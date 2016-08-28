package com.xcorp.teeport.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.xcorp.teeport.Teeport;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Teeport";
		config.width = 1280;
		config.height = 800;

		new LwjglApplication(new Teeport(), config);
	}
}
